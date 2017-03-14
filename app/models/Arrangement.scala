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

import java.time.{LocalDate => Date}

case class Arrangement(
  ttpArrangement: TTPArrangement,
  letterAndControl: Option[LetterAndControl] = None
) {
  def isValid: Boolean =
    ttpArrangement.isValid && safeLetterAndControlCheck(letterAndControl)
  
  def safeLetterAndControlCheck(letterAndControl: Option[LetterAndControl]): Boolean =
    letterAndControl match {
      case Some(lAndC) => lAndC.isValid
      case None => true
  }
}

case class DebitDetails (
  debitType: String,
  dueDate: Date
) {
  def isValid: Boolean = Seq(
      debitType.matches("^[A-Z0-9]{3,4}$"),
      dueDate.matches(dateRegex)).reduce(_ && _)
}

case class TTPArrangement(
  startDate: Date,
  endDate: Date,
  firstPaymentDate: Date,
  firstPaymentAmount: String,
  regularPaymentAmount: String,
  regularPaymentFrequency: String,
  reviewDate: Date,
  initials: String,
  enforcementAction: String,
  directDebit: Boolean,
  debitDetails: List[DebitDetails],
  saNote: String
) {
  def isValid: Boolean = {

    val amountValidation = "^[0-9]{1,11}\\.[0-9]{2}$"

    Seq(
      startDate.matches(dateRegex),
      endDate.matches(dateRegex),
      firstPaymentDate.matches(dateRegex),
      firstPaymentAmount.matches(amountValidation),
      regularPaymentAmount.matches(amountValidation),
      regularPaymentFrequency.matches("Weekly|Fortnightly|Monthly|6-Monthly|12-Monthly"),
      reviewDate.matches(dateRegex),
      initials.matches("^[A-Za-z]{1,3}$"),
      enforcementAction.matches("Distraint|CCP|SP|Summary Warrant|Other"),
      debitDetails.nonEmpty && debitDetails.map(_.isValid).reduce(_ && _),
      saNote.matches("^[a-zA-Z0-9\\u00a3 ,.\\/]{1,250}$")
    ).reduce(_ && _)
  }
}

case class LetterAndControl (
  customerName: Option[String],
  salutation: Option[String],
  addressLine1: Option[String],
  addressLine2: Option[String],
  addressLine3: Option[String],
  addressLine4: Option[String],
  addressLine5: Option[String],
  postcode: Option[String],
  totalAll: Option[String],
  clmIndicateInt: Option[String],
  clmPymtString: Option[String],
  officeName1: Option[String],
  officeName2: Option[String],
  officePostCode: Option[String],
  officePhone: Option[String],
  officeFax: Option[String],
  officeOpeningHours: Option[String],
  template: Option[String],
  exceptionType: Option[String],
  exceptionReason: Option[String]
) {
  def isValid: Boolean = {
    val customerNameSalutationValidation = "^.{1,250}$"
    val addressLine12Validation = "^[A-Za-z0-9 ,.&']{1,100}$"
    val addressLine34Validation = "^[A-Za-z0-9 \\-&']{1,35}$"
    val postCodeValidation = "^[A-Z]{1,2}[0-9][0-9A-Z]?\\s?[0-9][A-Z]{2}|BFPO\\s?[0-9]{1,10}$"
    val officeNameValidation = "^[a-zA-Z0-9 '&]{1,255}$"
    val officePhoneFaxValidation = "^[0-9-+()#x ]{1,40}$"

    Seq(
      customerName.matches(customerNameSalutationValidation),
      salutation.matches(customerNameSalutationValidation),
      addressLine1.matches(addressLine12Validation),
      addressLine2.matches(addressLine12Validation),
      addressLine3.matches(addressLine34Validation),
      addressLine4.matches(addressLine34Validation),
      addressLine5.matches("^[A-Za-z0-9 \\-&']{1,30}$"),
      postcode.matches(postCodeValidation),
      totalAll.matches("^\\u00a3?[0-9]{1,3}(?:,?[0-9]{3})*\\.[0-9]{2}$"),
      clmIndicateInt.matches("^[a-zA-Z ,.;\\-\\(\\)]{1,255}$"),
      clmPymtString.matches("^[a-zA-Z0-9 \\u00a3.,]{1,255}$"),
      officeName1.matches(officeNameValidation),
      officeName2.matches(officeNameValidation),
      officePostCode.matches(postCodeValidation),
      officePhone.matches(officePhoneFaxValidation),
      officeFax.matches(officePhoneFaxValidation),
      officeOpeningHours.matches("^[A-Za-z0-9 .\\-]{1,40}$"),
      template.matches("^[A-Z0-9]{1,24}$"),
      exceptionType.matches("^[A-Za-z0-9\\-]{1,24}$"),
      exceptionReason.matches("^[a-zA-Z ]{1,100}$")
    ).reduce(_ && _)
  }
}
