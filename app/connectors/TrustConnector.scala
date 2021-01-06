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

package connectors

import config.FrontendAppConfig
import javax.inject.Inject
import models.protectors.{BusinessProtector, IndividualProtector, Protectors}
import models.{RemoveProtector, TrustDetails}
import play.api.libs.json.{JsValue, Json}
import uk.gov.hmrc.http.{HeaderCarrier, HttpResponse}
import uk.gov.hmrc.http.HttpClient
import uk.gov.hmrc.http.HttpReads.Implicits._

import scala.concurrent.{ExecutionContext, Future}

class TrustConnector @Inject()(http: HttpClient, config : FrontendAppConfig) {


  private def getTrustDetailsUrl(utr: String) = s"${config.trustsUrl}/trusts/$utr/trust-details"

  def getTrustDetails(utr: String)(implicit hc: HeaderCarrier, ex: ExecutionContext):  Future[TrustDetails] = {
    http.GET[TrustDetails](getTrustDetailsUrl(utr))
  }

  private def getProtectorsUrl(utr: String) = s"${config.trustsUrl}/trusts/$utr/transformed/protectors"

  def getProtectors(utr: String)(implicit hc: HeaderCarrier, ec : ExecutionContext): Future[Protectors] = {
    http.GET[Protectors](getProtectorsUrl(utr))
  }

  private def addIndividualProtectorUrl(utr: String) = s"${config.trustsUrl}/trusts/protectors/add-individual/$utr"

  def addIndividualProtector(utr: String, protector: IndividualProtector)(implicit hc: HeaderCarrier, ec: ExecutionContext): Future[HttpResponse] = {
    http.POST[JsValue, HttpResponse](addIndividualProtectorUrl(utr), Json.toJson(protector))
  }

  private def amendIndividualProtectorUrl(utr: String, index: Int) = s"${config.trustsUrl}/trusts/protectors/amend-individual/$utr/$index"

  def amendIndividualProtector(utr: String, index: Int, individual: IndividualProtector)(implicit hc: HeaderCarrier, ec: ExecutionContext): Future[HttpResponse] = {
    http.POST[JsValue, HttpResponse](amendIndividualProtectorUrl(utr, index), Json.toJson(individual))
  }
  
  private def addBusinessProtectorUrl(utr: String) = s"${config.trustsUrl}/trusts/protectors/add-business/$utr"

  def addBusinessProtector(utr: String, protector: BusinessProtector)(implicit hc: HeaderCarrier, ec: ExecutionContext): Future[HttpResponse] = {
    http.POST[JsValue, HttpResponse](addBusinessProtectorUrl(utr), Json.toJson(protector))
  }

  private def amendBusinessProtectorUrl(utr: String, index: Int) = s"${config.trustsUrl}/trusts/protectors/amend-business/$utr/$index"

  def amendBusinessProtector(utr: String, index: Int, protector: BusinessProtector)(implicit hc: HeaderCarrier, ec: ExecutionContext): Future[HttpResponse] = {
    http.POST[JsValue, HttpResponse](amendBusinessProtectorUrl(utr, index), Json.toJson(protector))
  }

  private def removeProtectorUrl(utr: String) = s"${config.trustsUrl}/trusts/protectors/$utr/remove"

  def removeProtector(utr: String, protector: RemoveProtector)(implicit hc: HeaderCarrier, ec: ExecutionContext): Future[HttpResponse] = {
    http.PUT[JsValue, HttpResponse](removeProtectorUrl(utr), Json.toJson(protector))
  }

}
