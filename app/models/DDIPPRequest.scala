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

package uk.gov.hmrc.ssttp.desstub.models

import java.time.{LocalDate, LocalDateTime}

case class DDIPPRequest(requestingService: String, submissionDateTime: LocalDateTime,
                        knownFact: List[KnownFact], directDebitInstruction: DirectDebitInstruction,
                        paymentPlan: PaymentPlan, printFlag: Boolean) {

  def isValid: Boolean = {
    requestingService.matches("^[A-Z]{1,40}$") && knownFact.map(fact => fact.isValid).reduce(_ && _) && directDebitInstruction.isValid
  }

}

case class DirectDebitInstruction(sortCode: Option[String], accountNumber: Option[String], accountName: Option[String],
                                  paperAuddisFlag: Boolean, ddiRefNumber: String) {

  def isValid: Boolean = Seq(
    sortCode.matches("^[0-9]{6}$"),
    accountNumber.matches("^[0-9]{8}$"),
    accountName.matches("^[a-zA-Z][a-zA-Z '.& \\/]{1,39}$"),
    ddiRefNumber.matches("^[0-9]{1,12}$")
  ).reduce(_ && _)
}

case class PaymentPlan(ppType: String, paymentReference: String,
                       hodService: String, paymentCurrency: String,
                       initialPaymentAmount: String, initialPaymentStartDate: LocalDate,
                       scheduledPaymentAmount: String, scheduledPaymentStartDate: LocalDate,
                       scheduledPaymentEndDate: LocalDate, scheduledPaymentFrequency: String,
                       balancingPaymentAmount: String, balancingPaymentDate: LocalDate,
                       totalLiability: String, suspensionStartDate: Option[LocalDate],
                       suspensionEndDate: Option[LocalDate]) {

  def isValid: Boolean = Seq(
    ppType.matches("One-Off Payment|Budget Payment Plan|Time to Pay|Variable Payment Plan"),
    paymentReference.matches("^[A-Za-z0-9]{1,18}$"),
    hodService.matches("CESA|NTC|PAYE|COTA|SDLT|VAT|NIQB|NIDN|MISC"),
    paymentCurrency.matches("GBP"),
    initialPaymentAmount.matches(amountRegex),
    scheduledPaymentAmount.matches(amountRegex),
    scheduledPaymentFrequency.matches("Weekly|CalendarMonthly|Fortnightly|FourWeekly|Quarterly|SixMonthly|Annually"),
    balancingPaymentAmount.matches(amountRegex),
    totalLiability.matches(amountRegex)
  ).reduce(_ && _)

  private val amountRegex = "^([0-9]{1,16}|[0-9]{1,14}\\.[0-9]{1}|[0-9]{1,13}\\.[0-9]{2})$"
}