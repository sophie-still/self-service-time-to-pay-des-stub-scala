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

package uk.gov.hmrc.ssttp.desstub.controllers

import javax.inject.Inject

import play.api.mvc._
import uk.gov.hmrc.play.microservice.controller.BaseController
import uk.gov.hmrc.ssttp.desstub.controllers.ResponseHandling.{verifyUtr, _}

class ArrangementStubController @Inject()() extends BaseController {

  /**
    * Represents the ttp arrangement DES endpoint which is called by the Time To Pay Arrangement service
    * Carries out a number of validation checks on the data provided and returns any errors if any or
    * Returns an accepted response if successful
    */
  def submitArrangement(utr: String): Action[AnyContent] =  arrangementAction(utr) { request =>
    Accepted("")
  }

  private def arrangementAction(utr: String)(block: Request[AnyContent] => Result): Action[AnyContent] =
    (requireEnvironment compose requireAuthorisation compose verifyUtr(utr) compose parseArrangement) (Action { request => block(request) })

}
