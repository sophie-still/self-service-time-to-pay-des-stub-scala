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

import play.api.libs.json._
import play.api.test.FakeRequest
import play.api.test.Helpers.AUTHORIZATION
import uk.gov.hmrc.ssttp.desstub.models.Arrangement

import scala.io.Source

package object testData {

  def loadFile(file: String): String = Json.parse(getClass.getResourceAsStream(s"/$file")).toString

  val saReturn = loadFile("SAReturn.json")
  val saDebit = loadFile("SADebit.json")
  val commPreferences = loadFile("CommPreferences.json")

  val validArrangementSubmission = Json.parse(getClass.getResourceAsStream("/validTTPArrangement.json"))
  val arrangement = Json.fromJson[Arrangement](validArrangementSubmission)

  val validExistingDDI = Json.parse(getClass.getResourceAsStream("/existingDDI.json"))
  val validNewDDI = Json.parse(getClass.getResourceAsStream("/newDDI.json"))

  val validDDIPPRequest = Json.parse(getClass.getResourceAsStream("/DDIPPRequest.json"))

  val yourSubmissionError = "Your submission contains one or more errors"

  val fakeAuthRequest = FakeRequest().withHeaders(AUTHORIZATION -> "Authorised")
  val fakeAuthEnvironmentRequest = FakeRequest().withHeaders(
    AUTHORIZATION -> "Authorised",
    "Environment" -> "Environment")

}
