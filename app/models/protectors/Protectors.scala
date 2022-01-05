/*
 * Copyright 2022 HM Revenue & Customs
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

package models.protectors

import models.Constant.MAX
import models.ProtectorType
import play.api.i18n.{Messages, MessagesProvider}
import play.api.libs.functional.syntax._
import play.api.libs.json.{Reads, __}
import viewmodels.RadioOption

import java.time.LocalDate

trait Protector {
  val entityStart: LocalDate
}

case class Protectors(protector: List[IndividualProtector],
                      protectorCompany: List[BusinessProtector]) {

  val size: Int = (protector ++ protectorCompany).size

  def addToHeading()(implicit mp: MessagesProvider): String = {

    size match {
      case c if c > 1 => Messages("addAProtector.count.heading", c)
      case _ => Messages("addAProtector.heading")
    }
  }

  private val options: List[(Int, ProtectorType)] = {
    (protector.size, ProtectorType.IndividualProtector) ::
      (protectorCompany.size, ProtectorType.BusinessProtector) ::
      Nil
  }

  val nonMaxedOutOptions: List[RadioOption] = {
    options.filter(x => x._1 < MAX).map {
      x => RadioOption(ProtectorType.prefix, x._2.toString)
    }
  }

  val maxedOutOptions: List[RadioOption] = {
    options.filter(x => x._1 >= MAX).map {
      x => RadioOption(ProtectorType.prefix, x._2.toString)
    }
  }

}

object Protectors {
  implicit val reads: Reads[Protectors] = (
    (__ \ "protectors" \ "protector").readWithDefault[List[IndividualProtector]](Nil)
      and (__ \ "protectors" \ "protectorCompany").readWithDefault[List[BusinessProtector]](Nil)
    ).apply(Protectors.apply _)
}
