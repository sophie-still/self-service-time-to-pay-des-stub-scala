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
import uk.gov.hmrc.play.microservice.controller.BaseController
import uk.gov.hmrc.play.http.logging.MdcLoggingExecutionContext._
import play.api.mvc._
import scala.concurrent.Future

import uk.gov.hmrc.ssttp.desstub.models._

class ArrangementStubController @Inject()() extends ResponseHandling {

  def submitArrangement(utr: String) = Action { implicit request =>

    import play.api.libs.json._
    implicit val ddReads = Json.reads[DebitDetails]    
    implicit val ttpArrReads = Json.reads[TTPArrangement]
    implicit val lacReads = Json.reads[LetterAndControl]            
    implicit val arrangementReads = Json.reads[Arrangement]

    def sendArrangement(arrangement: Arrangement): Result = {
      arrangement.ttpArrangement.enforcementAction match {
        case PreprogrammedResult(r) => r
        case _ if !arrangement.isValid => yourSubmissionContainsErrors
        case _ => Accepted("")
      }
    }

    request.headers.get("Environment") match {
      case None => Unauthorized(stdBody("No authorization or environment header present", ""))
      case Some(_) => request.headers.get(AUTHORIZATION) match {
        case None => yourSubmissionContainsErrors
        case Some(_) => request.body.asJson match {
          case Some(json) => sendArrangement(json.as[Arrangement])
          case None => yourSubmissionContainsErrors
        }
      }
    }
  }
}
