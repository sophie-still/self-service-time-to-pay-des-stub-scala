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
import play.api.http.Status
import play.api.mvc._
import play.api.test.Helpers._
import play.api.test._
import scala.concurrent.Future

class EligibilityStubControllerSpec extends PlaySpec with Results {

  val fakeRequest = FakeRequest("GET", "/sa/1234/returns")

  "Eligibility Controller" should {
    "reject requests with no authorization header" in {
      val controller = new EligibilityStubController()
      val result: Future[Result] = controller.generateSAReturns("1234").apply(FakeRequest())
      val bodyText: String = contentAsString(result)
      bodyText mustBe "No authorization header present"
    }
  }


}
