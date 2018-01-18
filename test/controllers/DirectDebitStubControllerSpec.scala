/*
 * Copyright 2018 HM Revenue & Customs
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
import play.api.test._
import testData._
import uk.gov.hmrc.ssttp.desstub.models.{DDIPPRequest, DDIRequest, DirectDebitInstruction, KnownFact, PaymentPlan}

import scala.concurrent.Future
import scala.util.Random.nextInt


class DirectDebitStubControllerSpec extends PlaySpec with Results {

  implicit val knownFactWrites = Json.writes[KnownFact]
  implicit val ddInstructionWrites = Json.writes[DirectDebitInstruction]

  implicit val paymentPlanWrites = Json.writes[PaymentPlan]

  implicit val ddiRequestWrites = Json.writes[DDIRequest]
  implicit val ddiPPRequestWrites = Json.writes[DDIPPRequest]


  val controller = new DirectDebitStubController()
  def randomUtr = "1234567890".map(_ => nextInt(10).toString.charAt(0))

  "Direct Debit Controller" should {

    "reject requests with no authorization header for generate DDI" in {
      val result: Future[Result] = controller.getInstructionsRequest(randomUtr).apply(FakeRequest().withJsonBody(ddiRequestJson))
      val bodyText: String = contentAsString(result)
      status(result) mustBe UNAUTHORIZED
      bodyText mustBe "No authorization header present"
    }

    "reject requests with no authorization header for generate DDIPP with new bank details" in {
      val result: Future[Result] = controller.generateDDIPP(randomUtr).apply(FakeRequest().withJsonBody(validNewDDIJson))
      val bodyText: String = contentAsString(result)
      status(result) mustBe UNAUTHORIZED
      bodyText mustBe "No authorization header present"
    }

    "reject requests with no authorization header for generate DDIPP with existing bank details" in {
      val result: Future[Result] = controller.generateDDIPP(randomUtr).apply(FakeRequest().withJsonBody(validExistingDDI))
      val bodyText: String = contentAsString(result)
      status(result) mustBe UNAUTHORIZED
      bodyText mustBe "No authorization header present"
    }

    "return 200 with populated ddi for generate DDI" in {
      val result: Future[Result] = controller.getInstructionsRequest(randomUtr).apply(fakeAuthRequest.withJsonBody(ddiRequestJson))
      val bodyText: String = contentAsString(result)
      status(result) mustBe OK
      bodyText mustBe ddiResponse
    }

    "return 200 with empty ddi for generate DDI" in {
      val result: Future[Result] = controller.getInstructionsRequest("543212300016").apply(fakeAuthRequest.withJsonBody(ddiRequestJson))
      val bodyText: String = contentAsString(result)
      status(result) mustBe OK
      bodyText mustBe emptyDDIResponse
    }

    "return 201 for generate DDIPP with new bank details" in {
      val result: Future[Result] = controller.generateDDIPP(randomUtr).apply(fakeAuthRequest.withJsonBody(validNewDDIJson))
      val bodyText: String = contentAsString(result)
      status(result) mustBe CREATED
      bodyText mustBe ddiPPResponse
    }

    "return 201 for generate DDIPP with existing bank details" in {
      val result: Future[Result] = controller.generateDDIPP(randomUtr).apply(fakeAuthRequest.withJsonBody(validExistingDDI))
      val bodyText: String = contentAsString(result)
      status(result) mustBe CREATED
      bodyText mustBe ddiPPResponse
    }

    "return 200 with populated DDI for generate DDI with no knownFact" in {
      val requiredRequest = ddiRequest.copy(knownFact = None)
      val requiredJson = Json.toJson(requiredRequest)

      val result: Future[Result] = controller.getInstructionsRequest(randomUtr).apply(fakeAuthRequest.withJsonBody(requiredJson))
      val bodyText: String = contentAsString(result)
      status(result) mustBe OK
      bodyText mustBe ddiResponse
    }

    "return 200 with empty DDI for generate DDI with no knownFact" in {
      val requiredRequest = ddiRequest.copy(knownFact = None)
      val requiredJson = Json.toJson(requiredRequest)

      val result: Future[Result] = controller.getInstructionsRequest("543212300016").apply(fakeAuthRequest.withJsonBody(requiredJson))
      val bodyText: String = contentAsString(result)
      status(result) mustBe OK
      bodyText mustBe emptyDDIResponse
    }

    "return 200 with populated DDI for generate DDI with empty known fact list" in {
      val requiredRequest = ddiRequest.copy(knownFact = Some(List.empty))
      val requiredJson = Json.toJson(requiredRequest)

      val result: Future[Result] = controller.getInstructionsRequest(randomUtr).apply(fakeAuthRequest.withJsonBody(requiredJson))
      val bodyText: String = contentAsString(result)
      status(result) mustBe OK
      bodyText mustBe ddiResponse
    }

    "return 200 with empty DDI for generate DDI with empty known fact list" in {
      val requiredRequest = ddiRequest.copy(knownFact = Some(List.empty))
      val requiredJson = Json.toJson(requiredRequest)

      val result: Future[Result] = controller.getInstructionsRequest("543212300016").apply(fakeAuthRequest.withJsonBody(requiredJson))
      val bodyText: String = contentAsString(result)
      status(result) mustBe OK
      bodyText mustBe emptyDDIResponse
    }

    "return 400 for generate DDI with invalid requestingService" in {
      val requiredRequest = ddiRequest.copy(requestingService = "")
      val requiredJson = Json.toJson(requiredRequest)

      val result: Future[Result] = controller.getInstructionsRequest(randomUtr).apply(fakeAuthRequest.withJsonBody(requiredJson))
      val bodyText: String = contentAsString(result)
      status(result) mustBe BAD_REQUEST
      bodyText must include("Your submission contains one or more errors")
    }

    "return 400 for generate DDI with invalid service" in {
      val knownFact = KnownFact("", None)
      val requiredRequest = ddiRequest.copy(knownFact = Some(List(knownFact)))
      val requiredJson = Json.toJson(requiredRequest)

      val result: Future[Result] = controller.getInstructionsRequest(randomUtr).apply(fakeAuthRequest.withJsonBody(requiredJson))
      val bodyText: String = contentAsString(result)
      status(result) mustBe BAD_REQUEST
      bodyText must include("Your submission contains one or more errors")
    }

    "return 400 for generate DDI with invalid value" in {
      val knownFact = KnownFact("CESA", Some(""))
      val requiredRequest = ddiRequest.copy(knownFact = Some(List(knownFact)))
      val requiredJson = Json.toJson(requiredRequest)

      val result: Future[Result] = controller.getInstructionsRequest("543212300016").apply(fakeAuthRequest.withJsonBody(requiredJson))
      val bodyText: String = contentAsString(result)
      status(result) mustBe BAD_REQUEST
      bodyText must include("Your submission contains one or more errors")
    }

    "return 400 for generate DDIPP with invalid requestingService" in {
      val requiredRequest = validNewDDI.copy(requestingService = "")
      val requiredJson = Json.toJson(requiredRequest)

      val result: Future[Result] = controller.generateDDIPP(randomUtr).apply(fakeAuthRequest.withJsonBody(requiredJson))
      val bodyText: String = contentAsString(result)
      status(result) mustBe BAD_REQUEST
      bodyText must include("Your submission contains one or more errors")
    }

    "return 400 for generate DDIPP with invalid knownFact" in {
      val requiredRequest = validNewDDI.copy(knownFact = List.empty)
      val requiredJson = Json.toJson(requiredRequest)

      val result: Future[Result] = controller.generateDDIPP(randomUtr).apply(fakeAuthRequest.withJsonBody(requiredJson))
      val bodyText: String = contentAsString(result)
      status(result) mustBe BAD_REQUEST
      bodyText must include("Your submission contains one or more errors")
    }

    "return 400 for generate DDIPP with invalid service" in {
      val knownFact = KnownFact("", None)
      val requiredRequest = validNewDDI.copy(knownFact = List(knownFact))
      val requiredJson = Json.toJson(requiredRequest)

      val result: Future[Result] = controller.generateDDIPP(randomUtr).apply(fakeAuthRequest.withJsonBody(requiredJson))
      val bodyText: String = contentAsString(result)
      status(result) mustBe BAD_REQUEST
      bodyText must include("Your submission contains one or more errors")
    }

    "return 400 for generate DDIPP with invalid value" in {
      val knownFact = KnownFact("CESA", Some(""))
      val requiredRequest = validNewDDI.copy(knownFact = List(knownFact))
      val requiredJson = Json.toJson(requiredRequest)

      val result: Future[Result] = controller.generateDDIPP(randomUtr).apply(fakeAuthRequest.withJsonBody(requiredJson))
      val bodyText: String = contentAsString(result)
      status(result) mustBe BAD_REQUEST
      bodyText must include("Your submission contains one or more errors")
    }

    "return 400 for generate DDIPP with invalid sortCode" in {
      val ddi = validNewDDI.directDebitInstruction.copy(sortCode = Some(""))
      val requiredRequest = validNewDDI.copy(directDebitInstruction = ddi)
      val requiredJson = Json.toJson(requiredRequest)

      val result: Future[Result] = controller.generateDDIPP(randomUtr).apply(fakeAuthRequest.withJsonBody(requiredJson))
      val bodyText: String = contentAsString(result)
      status(result) mustBe BAD_REQUEST
      bodyText must include("Your submission contains one or more errors")
    }

    "return 400 for generate DDIPP with invalid accountNumber" in {
      val ddi = validNewDDI.directDebitInstruction.copy(accountNumber = Some(""))
      val requiredRequest = validNewDDI.copy(directDebitInstruction = ddi)
      val requiredJson = Json.toJson(requiredRequest)

      val result: Future[Result] = controller.generateDDIPP(randomUtr).apply(fakeAuthRequest.withJsonBody(requiredJson))
      val bodyText: String = contentAsString(result)
      status(result) mustBe BAD_REQUEST
      bodyText must include("Your submission contains one or more errors")
    }

    "return 400 for generate DDIPP with invalid accountName" in {
      val ddi = validNewDDI.directDebitInstruction.copy(accountName = Some(""))
      val requiredRequest = validNewDDI.copy(directDebitInstruction = ddi)
      val requiredJson = Json.toJson(requiredRequest)

      val result: Future[Result] = controller.generateDDIPP(randomUtr).apply(fakeAuthRequest.withJsonBody(requiredJson))
      val bodyText: String = contentAsString(result)
      status(result) mustBe BAD_REQUEST
      bodyText must include("Your submission contains one or more errors")
    }

    "return 400 for generate DDIPP with invalid ddiRefNumber" in {
      val ddi = validNewDDI.directDebitInstruction.copy(ddiRefNumber = "")
      val requiredRequest = validNewDDI.copy(directDebitInstruction = ddi)
      val requiredJson = Json.toJson(requiredRequest)

      val result: Future[Result] = controller.generateDDIPP(randomUtr).apply(fakeAuthRequest.withJsonBody(requiredJson))
      val bodyText: String = contentAsString(result)
      status(result) mustBe BAD_REQUEST
      bodyText must include("Your submission contains one or more errors")
    }

    "return 400 for generate DDIPP with invalid ppType" in {
      val paymentPlan = validNewDDI.paymentPlan.copy(ppType = "")
      val requiredRequest = validNewDDI.copy(paymentPlan = paymentPlan)
      val requiredJson = Json.toJson(requiredRequest)

      val result: Future[Result] = controller.generateDDIPP(randomUtr).apply(fakeAuthRequest.withJsonBody(requiredJson))
      val bodyText: String = contentAsString(result)
      status(result) mustBe BAD_REQUEST
      bodyText must include("Your submission contains one or more errors")
    }

    "return 400 for generate DDIPP with invalid hodService" in {
      val paymentPlan = validNewDDI.paymentPlan.copy(hodService = "")
      val requiredRequest = validNewDDI.copy(paymentPlan = paymentPlan)
      val requiredJson = Json.toJson(requiredRequest)

      val result: Future[Result] = controller.generateDDIPP(randomUtr).apply(fakeAuthRequest.withJsonBody(requiredJson))
      val bodyText: String = contentAsString(result)
      status(result) mustBe BAD_REQUEST
      bodyText must include("Your submission contains one or more errors")
    }

    "return 400 for generate DDIPP with invalid paymentCurrency" in {
      val paymentPlan = validNewDDI.paymentPlan.copy(paymentCurrency = "")
      val requiredRequest = validNewDDI.copy(paymentPlan = paymentPlan)
      val requiredJson = Json.toJson(requiredRequest)

      val result: Future[Result] = controller.generateDDIPP(randomUtr).apply(fakeAuthRequest.withJsonBody(requiredJson))
      val bodyText: String = contentAsString(result)
      status(result) mustBe BAD_REQUEST
      bodyText must include("Your submission contains one or more errors")
    }

    "return 400 for generate DDIPP with invalid initialPaymentAmount" in {
      val paymentPlan = validNewDDI.paymentPlan.copy(initialPaymentAmount = Some(""))
      val requiredRequest = validNewDDI.copy(paymentPlan = paymentPlan)
      val requiredJson = Json.toJson(requiredRequest)

      val result: Future[Result] = controller.generateDDIPP(randomUtr).apply(fakeAuthRequest.withJsonBody(requiredJson))
      val bodyText: String = contentAsString(result)
      status(result) mustBe BAD_REQUEST
      bodyText must include("Your submission contains one or more errors")
    }

    "return 400 for generate DDIPP with invalid initialPaymentStartDate" in {
      val paymentPlan = validNewDDI.paymentPlan.copy(initialPaymentStartDate = Some(LocalDate.parse("2100-01-01")))
      val requiredRequest = validNewDDI.copy(paymentPlan = paymentPlan)
      val requiredJson = Json.toJson(requiredRequest)

      val result: Future[Result] = controller.generateDDIPP(randomUtr).apply(fakeAuthRequest.withJsonBody(requiredJson))
      val bodyText: String = contentAsString(result)
      status(result) mustBe BAD_REQUEST
      bodyText must include("Your submission contains one or more errors")
    }

    "return 400 for generate DDIPP with invalid scheduledPaymentAmount" in {
      val paymentPlan = validNewDDI.paymentPlan.copy(scheduledPaymentAmount = "")
      val requiredRequest = validNewDDI.copy(paymentPlan = paymentPlan)
      val requiredJson = Json.toJson(requiredRequest)

      val result: Future[Result] = controller.generateDDIPP(randomUtr).apply(fakeAuthRequest.withJsonBody(requiredJson))
      val bodyText: String = contentAsString(result)
      status(result) mustBe BAD_REQUEST
      bodyText must include("Your submission contains one or more errors")
    }

    "return 400 for generate DDIPP with invalid scheduledPaymentStartDate" in {
      val paymentPlan = validNewDDI.paymentPlan.copy(scheduledPaymentStartDate = LocalDate.parse("2100-01-01"))
      val requiredRequest = validNewDDI.copy(paymentPlan = paymentPlan)
      val requiredJson = Json.toJson(requiredRequest)

      val result: Future[Result] = controller.generateDDIPP(randomUtr).apply(fakeAuthRequest.withJsonBody(requiredJson))
      val bodyText: String = contentAsString(result)
      status(result) mustBe BAD_REQUEST
      bodyText must include("Your submission contains one or more errors")
    }

    "return 400 for generate DDIPP with invalid scheduledPaymentEndDate" in {
      val paymentPlan = validNewDDI.paymentPlan.copy(scheduledPaymentEndDate = LocalDate.parse("2100-01-01"))
      val requiredRequest = validNewDDI.copy(paymentPlan = paymentPlan)
      val requiredJson = Json.toJson(requiredRequest)

      val result: Future[Result] = controller.generateDDIPP(randomUtr).apply(fakeAuthRequest.withJsonBody(requiredJson))
      val bodyText: String = contentAsString(result)
      status(result) mustBe BAD_REQUEST
      bodyText must include("Your submission contains one or more errors")
    }

    "return 400 for generate DDIPP with invalid scheduledPaymentFrequency" in {
      val paymentPlan = validNewDDI.paymentPlan.copy(scheduledPaymentFrequency = "")
      val requiredRequest = validNewDDI.copy(paymentPlan = paymentPlan)
      val requiredJson = Json.toJson(requiredRequest)

      val result: Future[Result] = controller.generateDDIPP(randomUtr).apply(fakeAuthRequest.withJsonBody(requiredJson))
      val bodyText: String = contentAsString(result)
      status(result) mustBe BAD_REQUEST
      bodyText must include("Your submission contains one or more errors")
    }

    "return 400 for generate DDIPP with invalid balancingPaymentAmount" in {
      val paymentPlan = validNewDDI.paymentPlan.copy(balancingPaymentAmount = "")
      val requiredRequest = validNewDDI.copy(paymentPlan = paymentPlan)
      val requiredJson = Json.toJson(requiredRequest)

      val result: Future[Result] = controller.generateDDIPP(randomUtr).apply(fakeAuthRequest.withJsonBody(requiredJson))
      val bodyText: String = contentAsString(result)
      status(result) mustBe BAD_REQUEST
      bodyText must include("Your submission contains one or more errors")
    }

    "return 400 for generate DDIPP with invalid balancingPaymentDate" in {
      val paymentPlan = validNewDDI.paymentPlan.copy(balancingPaymentDate = LocalDate.parse("2100-01-01"))
      val requiredRequest = validNewDDI.copy(paymentPlan = paymentPlan)
      val requiredJson = Json.toJson(requiredRequest)

      val result: Future[Result] = controller.generateDDIPP(randomUtr).apply(fakeAuthRequest.withJsonBody(requiredJson))
      val bodyText: String = contentAsString(result)
      status(result) mustBe BAD_REQUEST
      bodyText must include("Your submission contains one or more errors")
    }

    "return 400 for generate DDIPP with invalid totalLiability" in {
      val paymentPlan = validNewDDI.paymentPlan.copy(totalLiability = "")
      val requiredRequest = validNewDDI.copy(paymentPlan = paymentPlan)
      val requiredJson = Json.toJson(requiredRequest)

      val result: Future[Result] = controller.generateDDIPP(randomUtr).apply(fakeAuthRequest.withJsonBody(requiredJson))
      val bodyText: String = contentAsString(result)
      status(result) mustBe BAD_REQUEST
      bodyText must include("Your submission contains one or more errors")
    }

    "return 400 for generate DDIPP with invalid suspensionStartDate" in {
      val paymentPlan = validNewDDI.paymentPlan.copy(suspensionStartDate = Some(LocalDate.parse("2100-01-01")))
      val requiredRequest = validNewDDI.copy(paymentPlan = paymentPlan)
      val requiredJson = Json.toJson(requiredRequest)

      val result: Future[Result] = controller.generateDDIPP(randomUtr).apply(fakeAuthRequest.withJsonBody(requiredJson))
      val bodyText: String = contentAsString(result)
      status(result) mustBe BAD_REQUEST
      bodyText must include("Your submission contains one or more errors")
    }

    "return 400 for generate DDIPP with invalid suspensionEndDate" in {
      val paymentPlan = validNewDDI.paymentPlan.copy(suspensionEndDate = Some(LocalDate.parse("2100-01-01")))
      val requiredRequest = validNewDDI.copy(paymentPlan = paymentPlan)
      val requiredJson = Json.toJson(requiredRequest)

      val result: Future[Result] = controller.generateDDIPP(randomUtr).apply(fakeAuthRequest.withJsonBody(requiredJson))
      val bodyText: String = contentAsString(result)
      status(result) mustBe BAD_REQUEST
      bodyText must include("Your submission contains one or more errors")
    }
  }

}
