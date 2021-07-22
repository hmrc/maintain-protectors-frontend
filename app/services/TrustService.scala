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

package services

import com.google.inject.ImplementedBy
import connectors.TrustsConnector
import models.protectors.{BusinessProtector, IndividualProtector, Protectors}
import models.{NationalInsuranceNumber, RemoveProtector}
import uk.gov.hmrc.http.{HeaderCarrier, HttpResponse}

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class TrustServiceImpl @Inject()(connector: TrustsConnector) extends TrustService {

  override def getProtectors(identifier: String)(implicit hc:HeaderCarrier, ec:ExecutionContext): Future[Protectors] =
    connector.getProtectors(identifier)

  override def getIndividualProtector(identifier: String, index: Int)(implicit hc: HeaderCarrier, ex: ExecutionContext): Future[IndividualProtector] =
    getProtectors(identifier).map(_.protector(index))

  override def getBusinessProtector(identifier: String, index: Int)(implicit hc: HeaderCarrier, ex: ExecutionContext): Future[BusinessProtector] =
    getProtectors(identifier).map(_.protectorCompany(index))

  override def removeProtector(identifier: String, protector: RemoveProtector)(implicit hc: HeaderCarrier, ec: ExecutionContext): Future[HttpResponse] =
    connector.removeProtector(identifier, protector)

  override def getBusinessUtrs(identifier: String, index: Option[Int])(implicit hc: HeaderCarrier, ec: ExecutionContext): Future[List[String]] =
    getProtectors(identifier).map(_.protectorCompany
      .zipWithIndex
      .filterNot(x => index.contains(x._2))
      .flatMap(_._1.utr)
    )

  override def getIndividualNinos(identifier: String, index: Option[Int])
                                 (implicit hc: HeaderCarrier, ec: ExecutionContext): Future[List[String]] = {
    getProtectors(identifier).map(_.protector
      .zipWithIndex
      .filterNot(x => index.contains(x._2))
      .flatMap(_._1.identification)
      .collect {
        case NationalInsuranceNumber(nino) => nino
      }
    )
  }

}

@ImplementedBy(classOf[TrustServiceImpl])
trait TrustService {

  def getProtectors(identifier: String)(implicit hc: HeaderCarrier, ec: ExecutionContext): Future[Protectors]

  def getIndividualProtector(identifier: String, index: Int)(implicit hc: HeaderCarrier, ex: ExecutionContext): Future[IndividualProtector]

  def getBusinessProtector(identifier: String, index: Int)(implicit hc: HeaderCarrier, ex: ExecutionContext): Future[BusinessProtector]

  def removeProtector(identifier: String, protector: RemoveProtector)(implicit hc:HeaderCarrier, ec:ExecutionContext): Future[HttpResponse]

  def getBusinessUtrs(identifier: String, index: Option[Int])(implicit hc: HeaderCarrier, ec: ExecutionContext): Future[List[String]]

  def getIndividualNinos(identifier: String, index: Option[Int])(implicit hc: HeaderCarrier, ec: ExecutionContext): Future[List[String]]

}
