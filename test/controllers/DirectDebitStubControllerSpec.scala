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

import org.scalatestplus.play._
import play.api.libs.json.JsValue
import play.api.mvc._
import play.api.test.Helpers._
import play.api.test._

import scala.concurrent.Future
import testData._


class DirectDebitStubControllerSpec extends PlaySpec with Results {

  val controller = new DirectDebitStubController()

  "Direct Debit Controller" should {
    "reject requests with no authorization header for generate DDI with existing details" in {
      val result: Future[Result] = controller.generateDDI("1234567890").apply(FakeRequest().withJsonBody(validExistingDDI))
      val bodyText: String = contentAsString(result)
      status(result) mustBe UNAUTHORIZED
      bodyText mustBe "No authorization header present"
    }

    "reject requests with no authorization header for generate DDI with new details" in {
      val result: Future[Result] = controller.generateDDI("1234567890").apply(FakeRequest().withJsonBody(validNewDDI))
      val bodyText: String = contentAsString(result)
      status(result) mustBe UNAUTHORIZED
      bodyText mustBe "No authorization header present"
    }

    "reject requests with no authorization header for generate DDIPP" in {
      val result: Future[Result] = controller.generateDDIPP("1234567890").apply(FakeRequest().withJsonBody(validDDIPPRequest))
      val bodyText: String = contentAsString(result)
      status(result) mustBe UNAUTHORIZED
      bodyText mustBe "No authorization header present"
    }

    "return 200 with populated ddi for generate DDI with existing details" in {
      val result: Future[Result] = controller.generateDDI("1234567890").apply(fakeAuthRequest.withJsonBody(validExistingDDI))
      val bodyText: String = contentAsString(result)
      status(result) mustBe OK
      bodyText mustBe ddiResponse
    }

    "return 200 with populated ddi for generate DDI with new details" in {
      val result: Future[Result] = controller.generateDDI("1234567890").apply(fakeAuthRequest.withJsonBody(validNewDDI))
      val bodyText: String = contentAsString(result)
      status(result) mustBe OK
      bodyText mustBe ddiResponse
    }

    "return 201 with ddipp for generate DDIPP" in {
      val result: Future[Result] = controller.generateDDIPP("1234567890").apply(fakeAuthRequest.withJsonBody(validDDIPPRequest))
      val bodyText: String = contentAsString(result)
      status(result) mustBe CREATED
      bodyText mustBe ddiPPResponse
    }

  }
}
