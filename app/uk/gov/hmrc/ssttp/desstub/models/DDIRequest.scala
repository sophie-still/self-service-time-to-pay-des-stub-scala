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

case class DDIRequest(requestingService: String, knownFact: Option[List[KnownFact]]) {

  def isValid: Boolean =
    requestingService.matches("^[A-Z]{1,40}$") && safeKnownFactCheck(knownFact)

  def safeKnownFactCheck(knownFact: Option[List[KnownFact]]): Boolean =
    knownFact match {
      case Some(kF) if kF.isEmpty => true
      case Some(kF) => kF.map(fact => fact.isValid).reduce(_ && _)
      case None => true
    }
}

case class KnownFact(service: String, value: Option[String]) {

  def isValid: Boolean = Seq(
    service.matches("CESA|NTC|PAYE|COTA|TPSS|CIS"),
    value.matches("^[A-Za-z0-9]{1,25}$")
  ).reduce(_ && _)
}
