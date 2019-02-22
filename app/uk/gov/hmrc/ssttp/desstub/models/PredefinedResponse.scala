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

package uk.gov.hmrc.ssttp.desstub.models

import java.util.function.UnaryOperator

import play.api.http.Status
import play.api.libs.json.{JsString, JsValue, Json, OFormat}

case class PredefinedResponse(body: JsValue, status: Int)

object PredefinedResponse {

  def transform[A](f: A =>A): UnaryOperator[A] = new UnaryOperator[A] {
    def apply(t: A): A = f(t)
  }

  implicit val format: OFormat[PredefinedResponse] = Json.format[PredefinedResponse]

  def unanthorised(utr: String) = PredefinedResponse(JsString(s"Unauthorized looking up $utr"), Status.UNAUTHORIZED)
  def notFound(utr: String) = PredefinedResponse(JsString(s"User not found with UTR $utr"), Status.NOT_FOUND)
  def internalServerError(utr: String) = PredefinedResponse(JsString(s"Server error looking up $utr"), Status.INTERNAL_SERVER_ERROR)



}
