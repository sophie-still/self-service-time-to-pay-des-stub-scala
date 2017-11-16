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

import java.util.concurrent.atomic.AtomicReference
import javax.inject.Inject

import play.api.http.Status
import play.api.libs.json.JsString
import play.api.mvc._
import uk.gov.hmrc.play.microservice.controller.BaseController
import uk.gov.hmrc.ssttp.desstub.models.PredefinedResponse
import PredefinedResponse._
import uk.gov.hmrc.ssttp.desstub.controllers.ResponseHandling._

object EligibilityStubController {

  lazy val returns: AtomicReference[Map[String, PredefinedResponse]] =
    new AtomicReference[Map[String, PredefinedResponse]](Map(
      "0000000000" -> notFoundReturn
    ).withDefault(_ => defaultReturn))

  lazy val defaultReturn = PredefinedResponse(
    body = "/SAReturnHappy.json".resourceAsJson(),
    status = Status.OK
  )

  lazy val notFoundReturn = PredefinedResponse(
    body = JsString(""),
    status = Status.NOT_FOUND
  )

  lazy val debits: AtomicReference[Map[String, PredefinedResponse]] =
    new AtomicReference[Map[String, PredefinedResponse]](Map(
      "0000000000" -> notFoundDebit
    ).withDefault(_ => defaultDebit))

  lazy val defaultDebit = PredefinedResponse(
    body = "/SADebitHappy.json".resourceAsJson(),
    status = Status.OK
  )

  lazy val notFoundDebit = PredefinedResponse(
    body = JsString(""),
    status = Status.NOT_FOUND
  )

  lazy val communicationPreferences: AtomicReference[Map[String, PredefinedResponse]] =
    new AtomicReference[Map[String, PredefinedResponse]](Map(
      "0000000000" -> notFoundCommunicationPreferences
    ).withDefault(_ => defaultCommunicationPreferences))

  lazy val defaultCommunicationPreferences = PredefinedResponse(
    body = "/CommPreferences.json".resourceAsJson(),
    status = Status.OK
  )

  lazy val notFoundCommunicationPreferences = PredefinedResponse(
    body = JsString(""),
    status = Status.NOT_FOUND
  )

}

class EligibilityStubController @Inject()() extends BaseController {

  /**
    * Represents the getSAReturns endpoint for DES which is called by Time To Pay Taxpayer
    */
  def getReturns(utr: String): Action[AnyContent] = saAction(utr){ implicit request =>
    val predefinedResponse = EligibilityStubController.returns.get()(utr)
    predefinedResponse.body match {
      case JsString(s) => Status(predefinedResponse.status)(s)
      case j => Status(predefinedResponse.status)(j)
    }
  }

  def setReturns(utr: String): Action[PredefinedResponse] = Action(parse.json[PredefinedResponse]) { implicit request =>
      EligibilityStubController.returns.getAndUpdate(transform(_.updated(utr, request.body)))
      Ok(s"Done! Predefined response for $utr set.")
    }



  /**
    * Represents the getSADebits endpoint for DES which is called by Time To Pay Taxpayer
    */
  def getDebits(utr: String): Action[AnyContent] = saAction(utr){ implicit request =>
      val r = EligibilityStubController.debits.get()(utr)
      r.body match {
        case JsString(s) => Status(r.status)(s)
        case j => Status(r.status)(j)
      }
    }

  def setDebits(utr: String): Action[PredefinedResponse] =
    Action(parse.json[PredefinedResponse]) { implicit request =>
      EligibilityStubController.debits.getAndUpdate(transform(_.updated(utr, request.body)))
      Ok(s"Done! Predefined response for $utr set.")
    }

  /**
    * Represents the getCommPreferences endpoint for DES which is called by Time To Pay Taxpayer
    */
  def getCommunicationPreferences(utr: String): Action[AnyContent] =  saAction(utr) { implicit request =>
    val r = EligibilityStubController.communicationPreferences.get()(utr)
      r.body match {
      case JsString(s) => Status(r.status)(s)
      case j => Status(r.status)(j)
    }
  }

  def setCommunicationPreferences(utr: String): Action[PredefinedResponse] =
    Action(parse.json[PredefinedResponse]) { implicit request =>
      EligibilityStubController.communicationPreferences.getAndUpdate(transform(_.updated(utr, request.body)))
      Ok(s"Done! Predefined response for $utr set.")
    }

}
