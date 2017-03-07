/*
 * Copyright 2017 HM Revenue & Customs
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

import uk.gov.hmrc.play.microservice.controller.BaseController
import play.api.mvc._
import play.api.libs.json._

trait ResponseHandling extends BaseController {

  val yourSubmissionContainsErrors = BadRequest(
    stdBody("Your submission contains one or more errors", ""))

  val invalidJson = BadRequest(
    stdBody("Invalid JSON message received", ""))

  def serveFile(file: String)(utr: String) = baseResponse(utr) {
    val stream = getClass.getResourceAsStream(file)
    Ok(Json.parse(stream))
  }

  object PreprogrammedResult {
    def unapply(utr: String): Option[Result] = utr match {
      case _ if !utr.matches("^[0-9]{10}$") => Some(yourSubmissionContainsErrors)
      case "0000000000" => Some(NotFound)
      case _ => None
    }
  }

  def stdBody(reason: String, reasonCode: String): JsObject = {
    JsObject{Seq(
      "reason" -> JsString(reason),
      "reasonCode" -> JsString(reasonCode)
    )}
  }

  def baseResponse(utr: String)(otherWise: => Result) = Action {
    implicit request =>

    if (request.headers.get(AUTHORIZATION).isEmpty)
      Unauthorized("No authorization header present")
    else
      utr.toLowerCase match {
        case PreprogrammedResult(r) => r
        case _ => otherWise
      }
  }
}

