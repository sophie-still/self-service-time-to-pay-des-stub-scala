/*
 * Copyright 2019 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uk.gov.hmrc.ssttp.desstub.controllers

import play.api.Logger
import play.api.http.HeaderNames
import play.api.http.HeaderNames._
import play.api.mvc.BodyParsers.parse
import play.api.libs.json._
import play.api.mvc.Results._
import play.api.mvc._
import uk.gov.hmrc.ssttp.desstub.models.Arrangement

import scala.concurrent.Future

object ResponseHandling {

  /**
    * These are two generic error responses used by all of the DES endpoints
    */
  val yourSubmissionContainsErrors = BadRequest(
    errorResponse("Your submission contains one or more errors", ""))

  val invalidJson = BadRequest(
    errorResponse("Invalid JSON message received", "")
  )

  val invalidJsonF = Future.successful(invalidJson)


  /**
    * Defines the standard body response that is returned by DES in some instances
    */
  def errorResponse(reason: String, reasonCode: String): JsObject = {
    JsObject{Seq(
      "reason" -> JsString(reason),
      "reasonCode" -> JsString(reasonCode)
    )}
  }

  implicit class LoadResourceAsJson(resource: String) {
    def resourceAsJson(): JsValue = Json.parse(
      getClass.getResourceAsStream(resource)
    ).as[JsObject]
  }

  type AAAA = Action[AnyContent] => Action[AnyContent]

  val requireAuthorisation: AAAA = f => Action.async(f.parser) {request =>
    if (request.headers.get(HeaderNames.AUTHORIZATION).isEmpty)
      Future.successful(Unauthorized("No authorization header present"))
    else
      f(request)
  }

  val requireEnvironment: AAAA = f => Action.async(f.parser) {request =>
    if (request.headers.get("Environment").isEmpty)
      Future.successful(Unauthorized("No environment header present"))
    else
      f(request)
  }

  val parseArrangement: AAAA = f => Action.async(f.parser) {request =>
    request.body.asJson.map(j => j.validate[Arrangement] match {
      case s: JsSuccess[Arrangement] if s.get.isValid => f(request)
      case s: JsSuccess[Arrangement] if !s.get.isValid => invalidJsonF
      case JsError(e) =>
        Logger.warn(("JSON Validation Error " :: e.toList.map { x => x._1 + ": " + x._2 }).mkString("\n   "))
        invalidJsonF
    }).getOrElse(invalidJsonF)
  }

  def verifyUtr[A](utr: String): AAAA = f => Action.async(f.parser) { request =>
    if (!utr.matches("^[0-9]{10}$"))
      Future.successful(BadRequest(errorResponse("Invalid UTR number", "err-code-goes-here")))
    else
      f(request)
  }

  def saAction(utr: String)(block: Request[AnyContent] => Result): Action[AnyContent] =
    (requireAuthorisation compose verifyUtr(utr)) (Action { request => block(request) })

}
