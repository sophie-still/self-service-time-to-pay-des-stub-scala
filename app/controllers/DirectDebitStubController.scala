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

import javax.inject.Inject

import play.api.libs.json._
import play.api.mvc._

class DirectDebitStubController @Inject()() extends ResponseHandling {

  def generateDDI(credentialId: String) = Action {
    implicit request =>

    val requestingService: String =
    {request.body.asJson.get \ "requestingService"}.as[String]

    if (request.headers.get("AUTHORIZATION").isEmpty)
      Unauthorized("No authorization header present")
    else
      requestingService.toLowerCase match {
        case PreprogrammedResult(r) => r
        case _ => credentialId.toLowerCase match {
          case "cred-id-543212300016" => Ok(JsObject{Seq(
            "processingDate" -> JsString("2001-12-17T09:30:47Z"),
            "directDebitInstruction" -> JsArray(Nil)
          )})
          case "543212300016" => Ok(JsObject{Seq(
            "processingDate" -> JsString("2001-12-17T09:30:47Z"),
            "directDebitInstruction" -> JsArray(Nil)
          )})
          case "123456" => NotFound(stdBody("BP not found", "002"))
          case _ => Ok(Json.parse(getClass.getResourceAsStream("/DDI.json")))
        }
      }
  }

  def generateDDIPP(credentialId: String) = Action { implicit request =>
    val requestingService: String =
    {request.body.asJson.get \ "requestingService"}.as[String]

    if (request.headers.get("AUTHORIZATION").isEmpty)
      Unauthorized("No authorization header present")
    else
      requestingService.toLowerCase match {
        case PreprogrammedResult(r) => r
        case _ => Created(Json.parse(getClass.getResourceAsStream("/DDIPP.json")))
      }
  }
}
