/*
 * Copyright 2021 HM Revenue & Customs
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

package navigation

import models.Constant.MAX
import models.ProtectorType.{BusinessProtector, IndividualProtector}
import models.protectors.{Protector, Protectors}
import models.{NormalMode, ProtectorType}
import play.api.mvc.Call

class ProtectorNavigator {

  def addProtectorRoute(protectors: Protectors): Call = {
    val routes: List[(List[Protector], Call)] = List(
      (protectors.protector, addProtectorNowRoute(IndividualProtector)),
      (protectors.protectorCompany, addProtectorNowRoute(BusinessProtector))
    )

    routes.filter(_._1.size < MAX) match {
      case (_, x) :: Nil => x
      case _ => controllers.routes.AddNowController.onPageLoad()
    }
  }

  def addProtectorNowRoute(`type`: ProtectorType): Call = {
    `type` match {
      case IndividualProtector => controllers.individual.routes.NameController.onPageLoad(NormalMode)
      case BusinessProtector => controllers.business.routes.NameController.onPageLoad(NormalMode)
    }
  }
}
