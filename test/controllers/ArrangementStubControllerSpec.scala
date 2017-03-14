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

import java.time.LocalDate

import org.scalatestplus.play._
import play.api.libs.json.Json
import play.api.mvc._
import play.api.test.Helpers._
import testData._
import uk.gov.hmrc.ssttp.desstub.models.{Arrangement, DebitDetails, LetterAndControl, TTPArrangement}

import scala.concurrent.Future

class ArrangementStubControllerSpec extends PlaySpec with Results {

  implicit val ddReads = Json.writes[DebitDetails]
  implicit val ttpArrReads = Json.writes[TTPArrangement]
  implicit val lacReads = Json.writes[LetterAndControl]
  implicit val arrangementReads = Json.writes[Arrangement]

  val controller = new ArrangementStubController()

  "Arrangement Controller" should {
    "reject requests with no environment header for submit arrangement" in {
      val result: Future[Result] = controller.submitArrangement("1234567890").apply(fakeAuthRequest)
      val bodyText: String = contentAsString(result)
      status(result) mustBe UNAUTHORIZED
      bodyText mustBe "No environment header present"
    }

    "reject requests with no authorization header for submit arrangement" in {
      val result: Future[Result] = controller.submitArrangement("1234567890").apply(fakeEnvironmentRequest)
      val bodyText: String = contentAsString(result)
      status(result) mustBe UNAUTHORIZED
      bodyText mustBe "No authorization header present"
    }

    "return 202 for submit arrangement with valid submission" in {
      val result: Future[Result] = controller.submitArrangement("1234567890").apply(fakeAuthEnvironmentRequest.withJsonBody(validArrangementSubmission))
      val bodyText: String = contentAsString(result)
      status(result) mustBe ACCEPTED
      bodyText mustBe ""
    }

    "return 400 for submit arrangement with invalid startDate" in {
      val requiredArrangement = validArrangement.copy(ttpArrangement = validArrangement.ttpArrangement.copy(startDate = LocalDate.parse("2100-01-01")))
      val arrangementJson = Json.toJson(requiredArrangement)

      val result: Future[Result] = controller.submitArrangement("1234567890").apply(fakeAuthEnvironmentRequest.withJsonBody(arrangementJson))
      val bodyText: String = contentAsString(result)
      status(result) mustBe BAD_REQUEST
      bodyText must include ("Invalid JSON message received")
    }

    "return 400 for submit arrangement with invalid endDate" in {
      val requiredArrangement = validArrangement.copy(ttpArrangement = validArrangement.ttpArrangement.copy(endDate = LocalDate.parse("2100-01-01")))
      val arrangementJson = Json.toJson(requiredArrangement)

      val result: Future[Result] = controller.submitArrangement("1234567890").apply(fakeAuthEnvironmentRequest.withJsonBody(arrangementJson))
      val bodyText: String = contentAsString(result)
      status(result) mustBe BAD_REQUEST
      bodyText must include ("Invalid JSON message received")
    }

    "return 400 for submit arrangement with invalid firstPaymentDate" in {
      val requiredArrangement = validArrangement.copy(ttpArrangement = validArrangement.ttpArrangement.copy(firstPaymentDate = LocalDate.parse("2100-01-01")))
      val arrangementJson = Json.toJson(requiredArrangement)

      val result: Future[Result] = controller.submitArrangement("1234567890").apply(fakeAuthEnvironmentRequest.withJsonBody(arrangementJson))
      val bodyText: String = contentAsString(result)
      status(result) mustBe BAD_REQUEST
      bodyText must include ("Invalid JSON message received")
    }

    "return 400 for submit arrangement with invalid firstPaymentAmount" in {
      val requiredArrangement = validArrangement.copy(ttpArrangement = validArrangement.ttpArrangement.copy(firstPaymentAmount = "-10"))
      val arrangementJson = Json.toJson(requiredArrangement)

      val result: Future[Result] = controller.submitArrangement("1234567890").apply(fakeAuthEnvironmentRequest.withJsonBody(arrangementJson))
      val bodyText: String = contentAsString(result)
      status(result) mustBe BAD_REQUEST
      bodyText must include ("Invalid JSON message received")
    }

    "return 400 for submit arrangement with invalid regularPaymentFrequency" in {
      val requiredArrangement = validArrangement.copy(ttpArrangement = validArrangement.ttpArrangement.copy(regularPaymentFrequency = "9-Monthly"))
      val arrangementJson = Json.toJson(requiredArrangement)

      val result: Future[Result] = controller.submitArrangement("1234567890").apply(fakeAuthEnvironmentRequest.withJsonBody(arrangementJson))
      val bodyText: String = contentAsString(result)
      status(result) mustBe BAD_REQUEST
      bodyText must include ("Invalid JSON message received")
    }

    "return 400 for submit arrangement with invalid reviewDate" in {
      val requiredArrangement = validArrangement.copy(ttpArrangement = validArrangement.ttpArrangement.copy(reviewDate = LocalDate.parse("2100-01-01")))
      val arrangementJson = Json.toJson(requiredArrangement)

      val result: Future[Result] = controller.submitArrangement("1234567890").apply(fakeAuthEnvironmentRequest.withJsonBody(arrangementJson))
      val bodyText: String = contentAsString(result)
      status(result) mustBe BAD_REQUEST
      bodyText must include ("Invalid JSON message received")
    }

    "return 400 for submit arrangement with invalid initials" in {
      val requiredArrangement = validArrangement.copy(ttpArrangement = validArrangement.ttpArrangement.copy(initials = "@@@@££££---"))
      val arrangementJson = Json.toJson(requiredArrangement)

      val result: Future[Result] = controller.submitArrangement("1234567890").apply(fakeAuthEnvironmentRequest.withJsonBody(arrangementJson))
      val bodyText: String = contentAsString(result)
      status(result) mustBe BAD_REQUEST
      bodyText must include ("Invalid JSON message received")
    }

    "return 400 for submit arrangement with invalid enforcementAction" in {
      val requiredArrangement = validArrangement.copy(ttpArrangement = validArrangement.ttpArrangement.copy(enforcementAction = "Bad Enforcement Action"))
      val arrangementJson = Json.toJson(requiredArrangement)

      val result: Future[Result] = controller.submitArrangement("1234567890").apply(fakeAuthEnvironmentRequest.withJsonBody(arrangementJson))
      val bodyText: String = contentAsString(result)
      status(result) mustBe BAD_REQUEST
      bodyText must include ("Invalid JSON message received")
    }

    "return 400 for submit arrangement with invalid debitDetails" in {
      val requiredArrangement = validArrangement.copy(ttpArrangement = validArrangement.ttpArrangement.copy(debitDetails = List.empty))
      val arrangementJson = Json.toJson(requiredArrangement)

      val result: Future[Result] = controller.submitArrangement("1234567890").apply(fakeAuthEnvironmentRequest.withJsonBody(arrangementJson))
      val bodyText: String = contentAsString(result)
      status(result) mustBe BAD_REQUEST
      bodyText must include ("Invalid JSON message received")
    }

    "return 400 for submit arrangement with invalid debitType" in {
      val debitDetails = DebitDetails("@@@~~~~~#####````^^^^^", LocalDate.parse("2017-01-31"))
      val requiredArrangement = validArrangement.copy(ttpArrangement = validArrangement.ttpArrangement.copy(debitDetails = List(debitDetails)))
      val arrangementJson = Json.toJson(requiredArrangement)

      val result: Future[Result] = controller.submitArrangement("1234567890").apply(fakeAuthEnvironmentRequest.withJsonBody(arrangementJson))
      val bodyText: String = contentAsString(result)
      status(result) mustBe BAD_REQUEST
      bodyText must include ("Invalid JSON message received")
    }

    "return 400 for submit arrangement with invalid debitDueDate" in {
      val debitDetails = DebitDetails("IN1", LocalDate.parse("2100-01-01"))
      val requiredArrangement = validArrangement.copy(ttpArrangement = validArrangement.ttpArrangement.copy(debitDetails = List(debitDetails)))
      val arrangementJson = Json.toJson(requiredArrangement)

      val result: Future[Result] = controller.submitArrangement("1234567890").apply(fakeAuthEnvironmentRequest.withJsonBody(arrangementJson))
      val bodyText: String = contentAsString(result)
      status(result) mustBe BAD_REQUEST
      bodyText must include ("Invalid JSON message received")
    }

    "return 400 for submit arrangement with invalid saNote" in {
      val requiredArrangement = validArrangement.copy(ttpArrangement = validArrangement.ttpArrangement.copy(saNote = "#####@@@@@@##]][]'']]}{}{}====++++@@@@!!!!!!!%%%^^^*(*(&(&(^**^(%(%))%)%@@~~:::>>><<<<>>.....,,,||||"))
      val arrangementJson = Json.toJson(requiredArrangement)

      val result: Future[Result] = controller.submitArrangement("1234567890").apply(fakeAuthEnvironmentRequest.withJsonBody(arrangementJson))
      val bodyText: String = contentAsString(result)
      status(result) mustBe BAD_REQUEST
      bodyText must include ("Invalid JSON message received")
    }

    "return 400 for submit arrangement with valid customerName" in {
      val requiredLetterAndControl = letterAndControl.copy(customerName = Some("Bob Bob"))
      val requiredArrangement = validArrangement.copy(letterAndControl = Some(requiredLetterAndControl))
      val arrangementJson = Json.toJson(requiredArrangement)

      val result: Future[Result] = controller.submitArrangement("1234567890").apply(fakeAuthEnvironmentRequest.withJsonBody(arrangementJson))
      val bodyText: String = contentAsString(result)
      status(result) mustBe ACCEPTED
      bodyText mustBe ""
    }

    "return 400 for submit arrangement with invalid customerName" in {
      val requiredLetterAndControl = letterAndControl.copy(customerName = Some(""))
      val requiredArrangement = validArrangement.copy(letterAndControl = Some(requiredLetterAndControl))
      val arrangementJson = Json.toJson(requiredArrangement)

      val result: Future[Result] = controller.submitArrangement("1234567890").apply(fakeAuthEnvironmentRequest.withJsonBody(arrangementJson))
      val bodyText: String = contentAsString(result)
      status(result) mustBe BAD_REQUEST
      bodyText must include ("Invalid JSON message received")
    }

    "return 400 for submit arrangement with valid salutation" in {
      val requiredLetterAndControl = letterAndControl.copy(salutation = Some("Dear Customer Name"))
      val requiredArrangement = validArrangement.copy(letterAndControl = Some(requiredLetterAndControl))
      val arrangementJson = Json.toJson(requiredArrangement)

      val result: Future[Result] = controller.submitArrangement("1234567890").apply(fakeAuthEnvironmentRequest.withJsonBody(arrangementJson))
      val bodyText: String = contentAsString(result)
      status(result) mustBe ACCEPTED
      bodyText mustBe ""
    }

    "return 400 for submit arrangement with invalid salutation" in {
      val requiredLetterAndControl = letterAndControl.copy(salutation = Some(""))
      val requiredArrangement = validArrangement.copy(letterAndControl = Some(requiredLetterAndControl))
      val arrangementJson = Json.toJson(requiredArrangement)

      val result: Future[Result] = controller.submitArrangement("1234567890").apply(fakeAuthEnvironmentRequest.withJsonBody(arrangementJson))
      val bodyText: String = contentAsString(result)
      status(result) mustBe BAD_REQUEST
      bodyText must include ("Invalid JSON message received")
    }

    "return 400 for submit arrangement with valid addressLine1" in {
      val requiredLetterAndControl = letterAndControl.copy(addressLine1 = Some("123 Any Street"))
      val requiredArrangement = validArrangement.copy(letterAndControl = Some(requiredLetterAndControl))
      val arrangementJson = Json.toJson(requiredArrangement)

      val result: Future[Result] = controller.submitArrangement("1234567890").apply(fakeAuthEnvironmentRequest.withJsonBody(arrangementJson))
      val bodyText: String = contentAsString(result)
      status(result) mustBe ACCEPTED
      bodyText mustBe ""
    }

    "return 400 for submit arrangement with invalid addressLine1" in {
      val requiredLetterAndControl = letterAndControl.copy(addressLine1 = Some(""))
      val requiredArrangement = validArrangement.copy(letterAndControl = Some(requiredLetterAndControl))
      val arrangementJson = Json.toJson(requiredArrangement)

      val result: Future[Result] = controller.submitArrangement("1234567890").apply(fakeAuthEnvironmentRequest.withJsonBody(arrangementJson))
      val bodyText: String = contentAsString(result)
      status(result) mustBe BAD_REQUEST
      bodyText must include ("Invalid JSON message received")
    }

    "return 400 for submit arrangement with valid addressLine2" in {
      val requiredLetterAndControl = letterAndControl.copy(addressLine2 = Some("Kingsland High Road"))
      val requiredArrangement = validArrangement.copy(letterAndControl = Some(requiredLetterAndControl))
      val arrangementJson = Json.toJson(requiredArrangement)

      val result: Future[Result] = controller.submitArrangement("1234567890").apply(fakeAuthEnvironmentRequest.withJsonBody(arrangementJson))
      val bodyText: String = contentAsString(result)
      status(result) mustBe ACCEPTED
      bodyText mustBe ""
    }

    "return 400 for submit arrangement with invalid addressLine2" in {
      val requiredLetterAndControl = letterAndControl.copy(addressLine2 = Some(""))
      val requiredArrangement = validArrangement.copy(letterAndControl = Some(requiredLetterAndControl))
      val arrangementJson = Json.toJson(requiredArrangement)

      val result: Future[Result] = controller.submitArrangement("1234567890").apply(fakeAuthEnvironmentRequest.withJsonBody(arrangementJson))
      val bodyText: String = contentAsString(result)
      status(result) mustBe BAD_REQUEST
      bodyText must include ("Invalid JSON message received")
    }

    "return 400 for submit arrangement with valid addressLine3" in {
      val requiredLetterAndControl = letterAndControl.copy(addressLine3 = Some("Dalston"))
      val requiredArrangement = validArrangement.copy(letterAndControl = Some(requiredLetterAndControl))
      val arrangementJson = Json.toJson(requiredArrangement)

      val result: Future[Result] = controller.submitArrangement("1234567890").apply(fakeAuthEnvironmentRequest.withJsonBody(arrangementJson))
      val bodyText: String = contentAsString(result)
      status(result) mustBe ACCEPTED
      bodyText mustBe ""
    }

    "return 400 for submit arrangement with invalid addressLine3" in {
      val requiredLetterAndControl = letterAndControl.copy(addressLine3 = Some(""))
      val requiredArrangement = validArrangement.copy(letterAndControl = Some(requiredLetterAndControl))
      val arrangementJson = Json.toJson(requiredArrangement)

      val result: Future[Result] = controller.submitArrangement("1234567890").apply(fakeAuthEnvironmentRequest.withJsonBody(arrangementJson))
      val bodyText: String = contentAsString(result)
      status(result) mustBe BAD_REQUEST
      bodyText must include ("Invalid JSON message received")
    }

    "return 400 for submit arrangement with valid addressLine4" in {
      val requiredLetterAndControl = letterAndControl.copy(addressLine4 = Some("Greater London"))
      val requiredArrangement = validArrangement.copy(letterAndControl = Some(requiredLetterAndControl))
      val arrangementJson = Json.toJson(requiredArrangement)

      val result: Future[Result] = controller.submitArrangement("1234567890").apply(fakeAuthEnvironmentRequest.withJsonBody(arrangementJson))
      val bodyText: String = contentAsString(result)
      status(result) mustBe ACCEPTED
      bodyText mustBe ""
    }

    "return 400 for submit arrangement with invalid addressLine4" in {
      val requiredLetterAndControl = letterAndControl.copy(addressLine4 = Some(""))
      val requiredArrangement = validArrangement.copy(letterAndControl = Some(requiredLetterAndControl))
      val arrangementJson = Json.toJson(requiredArrangement)

      val result: Future[Result] = controller.submitArrangement("1234567890").apply(fakeAuthEnvironmentRequest.withJsonBody(arrangementJson))
      val bodyText: String = contentAsString(result)
      status(result) mustBe BAD_REQUEST
      bodyText must include ("Invalid JSON message received")
    }

    "return 400 for submit arrangement with valid addressLine5" in {
      val requiredLetterAndControl = letterAndControl.copy(addressLine5 = Some("UK"))
      val requiredArrangement = validArrangement.copy(letterAndControl = Some(requiredLetterAndControl))
      val arrangementJson = Json.toJson(requiredArrangement)

      val result: Future[Result] = controller.submitArrangement("1234567890").apply(fakeAuthEnvironmentRequest.withJsonBody(arrangementJson))
      val bodyText: String = contentAsString(result)
      status(result) mustBe ACCEPTED
      bodyText mustBe ""
    }

    "return 400 for submit arrangement with invalid addressLine5" in {
      val requiredLetterAndControl = letterAndControl.copy(addressLine5 = Some(""))
      val requiredArrangement = validArrangement.copy(letterAndControl = Some(requiredLetterAndControl))
      val arrangementJson = Json.toJson(requiredArrangement)

      val result: Future[Result] = controller.submitArrangement("1234567890").apply(fakeAuthEnvironmentRequest.withJsonBody(arrangementJson))
      val bodyText: String = contentAsString(result)
      status(result) mustBe BAD_REQUEST
      bodyText must include ("Invalid JSON message received")
    }

    "return 400 for submit arrangement with valid postcode" in {
      val requiredLetterAndControl = letterAndControl.copy(postcode = Some("E8 3PP"))
      val requiredArrangement = validArrangement.copy(letterAndControl = Some(requiredLetterAndControl))
      val arrangementJson = Json.toJson(requiredArrangement)

      val result: Future[Result] = controller.submitArrangement("1234567890").apply(fakeAuthEnvironmentRequest.withJsonBody(arrangementJson))
      val bodyText: String = contentAsString(result)
      status(result) mustBe ACCEPTED
      bodyText mustBe ""
    }

    "return 400 for submit arrangement with invalid postcode" in {
      val requiredLetterAndControl = letterAndControl.copy(postcode = Some(""))
      val requiredArrangement = validArrangement.copy(letterAndControl = Some(requiredLetterAndControl))
      val arrangementJson = Json.toJson(requiredArrangement)

      val result: Future[Result] = controller.submitArrangement("1234567890").apply(fakeAuthEnvironmentRequest.withJsonBody(arrangementJson))
      val bodyText: String = contentAsString(result)
      status(result) mustBe BAD_REQUEST
      bodyText must include ("Invalid JSON message received")
    }

    "return 400 for submit arrangement with valid totalAll" in {
      val requiredLetterAndControl = letterAndControl.copy(totalAll = Some("1000.00"))
      val requiredArrangement = validArrangement.copy(letterAndControl = Some(requiredLetterAndControl))
      val arrangementJson = Json.toJson(requiredArrangement)

      val result: Future[Result] = controller.submitArrangement("1234567890").apply(fakeAuthEnvironmentRequest.withJsonBody(arrangementJson))
      val bodyText: String = contentAsString(result)
      status(result) mustBe ACCEPTED
      bodyText mustBe ""
    }

    "return 400 for submit arrangement with invalid totalAll" in {
      val requiredLetterAndControl = letterAndControl.copy(totalAll = Some("1000.0"))
      val requiredArrangement = validArrangement.copy(letterAndControl = Some(requiredLetterAndControl))
      val arrangementJson = Json.toJson(requiredArrangement)

      val result: Future[Result] = controller.submitArrangement("1234567890").apply(fakeAuthEnvironmentRequest.withJsonBody(arrangementJson))
      val bodyText: String = contentAsString(result)
      status(result) mustBe BAD_REQUEST
      bodyText must include ("Invalid JSON message received")
    }

    "return 400 for submit arrangement with valid clmIndicateInt" in {
      val requiredLetterAndControl = letterAndControl.copy(clmIndicateInt = Some("Including interest due"))
      val requiredArrangement = validArrangement.copy(letterAndControl = Some(requiredLetterAndControl))
      val arrangementJson = Json.toJson(requiredArrangement)

      val result: Future[Result] = controller.submitArrangement("1234567890").apply(fakeAuthEnvironmentRequest.withJsonBody(arrangementJson))
      val bodyText: String = contentAsString(result)
      status(result) mustBe ACCEPTED
      bodyText mustBe ""
    }

    "return 400 for submit arrangement with invalid clmIndicateInt" in {
      val requiredLetterAndControl = letterAndControl.copy(clmIndicateInt = Some(""))
      val requiredArrangement = validArrangement.copy(letterAndControl = Some(requiredLetterAndControl))
      val arrangementJson = Json.toJson(requiredArrangement)

      val result: Future[Result] = controller.submitArrangement("1234567890").apply(fakeAuthEnvironmentRequest.withJsonBody(arrangementJson))
      val bodyText: String = contentAsString(result)
      status(result) mustBe BAD_REQUEST
      bodyText must include ("Invalid JSON message received")
    }

    "return 400 for submit arrangement with valid clmpymtString" in {
      val requiredLetterAndControl = letterAndControl.copy(clmPymtString = Some("Initial payment of 16.67 then one payments of 16.67 and final payment of 20.00"))
      val requiredArrangement = validArrangement.copy(letterAndControl = Some(requiredLetterAndControl))
      val arrangementJson = Json.toJson(requiredArrangement)

      val result: Future[Result] = controller.submitArrangement("1234567890").apply(fakeAuthEnvironmentRequest.withJsonBody(arrangementJson))
      val bodyText: String = contentAsString(result)
      status(result) mustBe ACCEPTED
      bodyText mustBe ""
    }

    "return 400 for submit arrangement with invalid clmPymtString" in {
      val requiredLetterAndControl = letterAndControl.copy(clmPymtString = Some(""))
      val requiredArrangement = validArrangement.copy(letterAndControl = Some(requiredLetterAndControl))
      val arrangementJson = Json.toJson(requiredArrangement)

      val result: Future[Result] = controller.submitArrangement("1234567890").apply(fakeAuthEnvironmentRequest.withJsonBody(arrangementJson))
      val bodyText: String = contentAsString(result)
      status(result) mustBe BAD_REQUEST
      bodyText must include ("Invalid JSON message received")
    }

    "return 400 for submit arrangement with valid officeName1" in {
      val requiredLetterAndControl = letterAndControl.copy(officeName1 = Some("HMRC"))
      val requiredArrangement = validArrangement.copy(letterAndControl = Some(requiredLetterAndControl))
      val arrangementJson = Json.toJson(requiredArrangement)

      val result: Future[Result] = controller.submitArrangement("1234567890").apply(fakeAuthEnvironmentRequest.withJsonBody(arrangementJson))
      val bodyText: String = contentAsString(result)
      status(result) mustBe ACCEPTED
      bodyText mustBe ""
    }

    "return 400 for submit arrangement with invalid officeName1" in {
      val requiredLetterAndControl = letterAndControl.copy(officeName1 = Some(""))
      val requiredArrangement = validArrangement.copy(letterAndControl = Some(requiredLetterAndControl))
      val arrangementJson = Json.toJson(requiredArrangement)

      val result: Future[Result] = controller.submitArrangement("1234567890").apply(fakeAuthEnvironmentRequest.withJsonBody(arrangementJson))
      val bodyText: String = contentAsString(result)
      status(result) mustBe BAD_REQUEST
      bodyText must include ("Invalid JSON message received")
    }

    "return 400 for submit arrangement with valid officeName2" in {
      val requiredLetterAndControl = letterAndControl.copy(officeName2 = Some("DM 440"))
      val requiredArrangement = validArrangement.copy(letterAndControl = Some(requiredLetterAndControl))
      val arrangementJson = Json.toJson(requiredArrangement)

      val result: Future[Result] = controller.submitArrangement("1234567890").apply(fakeAuthEnvironmentRequest.withJsonBody(arrangementJson))
      val bodyText: String = contentAsString(result)
      status(result) mustBe ACCEPTED
      bodyText mustBe ""
    }

    "return 400 for submit arrangement with invalid officeName2" in {
      val requiredLetterAndControl = letterAndControl.copy(officeName2 = Some(""))
      val requiredArrangement = validArrangement.copy(letterAndControl = Some(requiredLetterAndControl))
      val arrangementJson = Json.toJson(requiredArrangement)

      val result: Future[Result] = controller.submitArrangement("1234567890").apply(fakeAuthEnvironmentRequest.withJsonBody(arrangementJson))
      val bodyText: String = contentAsString(result)
      status(result) mustBe BAD_REQUEST
      bodyText must include ("Invalid JSON message received")
    }

    "return 400 for submit arrangement with valid officeNamePostCode" in {
      val requiredLetterAndControl = letterAndControl.copy(officePostCode = Some("BX5 5AB"))
      val requiredArrangement = validArrangement.copy(letterAndControl = Some(requiredLetterAndControl))
      val arrangementJson = Json.toJson(requiredArrangement)

      val result: Future[Result] = controller.submitArrangement("1234567890").apply(fakeAuthEnvironmentRequest.withJsonBody(arrangementJson))
      val bodyText: String = contentAsString(result)
      status(result) mustBe ACCEPTED
      bodyText mustBe ""
    }

    "return 400 for submit arrangement with invalid officePostCode" in {
      val requiredLetterAndControl = letterAndControl.copy(officePostCode = Some(""))
      val requiredArrangement = validArrangement.copy(letterAndControl = Some(requiredLetterAndControl))
      val arrangementJson = Json.toJson(requiredArrangement)

      val result: Future[Result] = controller.submitArrangement("1234567890").apply(fakeAuthEnvironmentRequest.withJsonBody(arrangementJson))
      val bodyText: String = contentAsString(result)
      status(result) mustBe BAD_REQUEST
      bodyText must include ("Invalid JSON message received")
    }

    "return 400 for submit arrangement with valid officePhone" in {
      val requiredLetterAndControl = letterAndControl.copy(officePhone = Some("0300 200 3822"))
      val requiredArrangement = validArrangement.copy(letterAndControl = Some(requiredLetterAndControl))
      val arrangementJson = Json.toJson(requiredArrangement)

      val result: Future[Result] = controller.submitArrangement("1234567890").apply(fakeAuthEnvironmentRequest.withJsonBody(arrangementJson))
      val bodyText: String = contentAsString(result)
      status(result) mustBe ACCEPTED
      bodyText mustBe ""
    }

    "return 400 for submit arrangement with invalid officePhone" in {
      val requiredLetterAndControl = letterAndControl.copy(officePhone = Some(""))
      val requiredArrangement = validArrangement.copy(letterAndControl = Some(requiredLetterAndControl))
      val arrangementJson = Json.toJson(requiredArrangement)

      val result: Future[Result] = controller.submitArrangement("1234567890").apply(fakeAuthEnvironmentRequest.withJsonBody(arrangementJson))
      val bodyText: String = contentAsString(result)
      status(result) mustBe BAD_REQUEST
      bodyText must include ("Invalid JSON message received")
    }

    "return 400 for submit arrangement with valid officeFax" in {
      val requiredLetterAndControl = letterAndControl.copy(officeFax = Some("01708 707502"))
      val requiredArrangement = validArrangement.copy(letterAndControl = Some(requiredLetterAndControl))
      val arrangementJson = Json.toJson(requiredArrangement)

      val result: Future[Result] = controller.submitArrangement("1234567890").apply(fakeAuthEnvironmentRequest.withJsonBody(arrangementJson))
      val bodyText: String = contentAsString(result)
      status(result) mustBe ACCEPTED
      bodyText mustBe ""
    }

    "return 400 for submit arrangement with invalid officeFax" in {
      val requiredLetterAndControl = letterAndControl.copy(officeFax = Some(""))
      val requiredArrangement = validArrangement.copy(letterAndControl = Some(requiredLetterAndControl))
      val arrangementJson = Json.toJson(requiredArrangement)

      val result: Future[Result] = controller.submitArrangement("1234567890").apply(fakeAuthEnvironmentRequest.withJsonBody(arrangementJson))
      val bodyText: String = contentAsString(result)
      status(result) mustBe BAD_REQUEST
      bodyText must include ("Invalid JSON message received")
    }

    "return 400 for submit arrangement with valid officeOpeningHours" in {
      val requiredLetterAndControl = letterAndControl.copy(officeOpeningHours = Some("Monday - Friday 08.00 to 20.00"))
      val requiredArrangement = validArrangement.copy(letterAndControl = Some(requiredLetterAndControl))
      val arrangementJson = Json.toJson(requiredArrangement)

      val result: Future[Result] = controller.submitArrangement("1234567890").apply(fakeAuthEnvironmentRequest.withJsonBody(arrangementJson))
      val bodyText: String = contentAsString(result)
      status(result) mustBe ACCEPTED
      bodyText mustBe ""
    }

    "return 400 for submit arrangement with invalid officeOpeningHours" in {
      val requiredLetterAndControl = letterAndControl.copy(officeOpeningHours = Some(""))
      val requiredArrangement = validArrangement.copy(letterAndControl = Some(requiredLetterAndControl))
      val arrangementJson = Json.toJson(requiredArrangement)

      val result: Future[Result] = controller.submitArrangement("1234567890").apply(fakeAuthEnvironmentRequest.withJsonBody(arrangementJson))
      val bodyText: String = contentAsString(result)
      status(result) mustBe BAD_REQUEST
      bodyText must include ("Invalid JSON message received")
    }

    "return 400 for submit arrangement with valid template" in {
      val requiredLetterAndControl = letterAndControl.copy(template = Some("DMTC13"))
      val requiredArrangement = validArrangement.copy(letterAndControl = Some(requiredLetterAndControl))
      val arrangementJson = Json.toJson(requiredArrangement)

      val result: Future[Result] = controller.submitArrangement("1234567890").apply(fakeAuthEnvironmentRequest.withJsonBody(arrangementJson))
      val bodyText: String = contentAsString(result)
      status(result) mustBe ACCEPTED
      bodyText mustBe ""
    }

    "return 400 for submit arrangement with invalid template" in {
      val requiredLetterAndControl = letterAndControl.copy(template = Some(""))
      val requiredArrangement = validArrangement.copy(letterAndControl = Some(requiredLetterAndControl))
      val arrangementJson = Json.toJson(requiredArrangement)

      val result: Future[Result] = controller.submitArrangement("1234567890").apply(fakeAuthEnvironmentRequest.withJsonBody(arrangementJson))
      val bodyText: String = contentAsString(result)
      status(result) mustBe BAD_REQUEST
      bodyText must include ("Invalid JSON message received")
    }

    "return 400 for submit arrangement with valid exceptionType" in {
      val requiredLetterAndControl = letterAndControl.copy(exceptionType = Some("4"))
      val requiredArrangement = validArrangement.copy(letterAndControl = Some(requiredLetterAndControl))
      val arrangementJson = Json.toJson(requiredArrangement)

      val result: Future[Result] = controller.submitArrangement("1234567890").apply(fakeAuthEnvironmentRequest.withJsonBody(arrangementJson))
      val bodyText: String = contentAsString(result)
      status(result) mustBe ACCEPTED
      bodyText mustBe ""
    }

    "return 400 for submit arrangement with invalid exceptionType" in {
      val requiredLetterAndControl = letterAndControl.copy(exceptionType = Some(""))
      val requiredArrangement = validArrangement.copy(letterAndControl = Some(requiredLetterAndControl))
      val arrangementJson = Json.toJson(requiredArrangement)

      val result: Future[Result] = controller.submitArrangement("1234567890").apply(fakeAuthEnvironmentRequest.withJsonBody(arrangementJson))
      val bodyText: String = contentAsString(result)
      status(result) mustBe BAD_REQUEST
      bodyText must include ("Invalid JSON message received")
    }

    "return 400 for submit arrangement with valid exceptionReason" in {
      val requiredLetterAndControl = letterAndControl.copy(exceptionReason = Some("Welsh required"))
      val requiredArrangement = validArrangement.copy(letterAndControl = Some(requiredLetterAndControl))
      val arrangementJson = Json.toJson(requiredArrangement)

      val result: Future[Result] = controller.submitArrangement("1234567890").apply(fakeAuthEnvironmentRequest.withJsonBody(arrangementJson))
      val bodyText: String = contentAsString(result)
      status(result) mustBe ACCEPTED
      bodyText mustBe ""
    }

    "return 400 for submit arrangement with invalid exceptionReason" in {
      val requiredLetterAndControl = letterAndControl.copy(exceptionReason = Some(""))
      val requiredArrangement = validArrangement.copy(letterAndControl = Some(requiredLetterAndControl))
      val arrangementJson = Json.toJson(requiredArrangement)

      val result: Future[Result] = controller.submitArrangement("1234567890").apply(fakeAuthEnvironmentRequest.withJsonBody(arrangementJson))
      val bodyText: String = contentAsString(result)
      status(result) mustBe BAD_REQUEST
      bodyText must include ("Invalid JSON message received")
    }

  }

}
