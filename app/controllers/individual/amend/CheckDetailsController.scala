/*
 * Copyright 2024 HM Revenue & Customs
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

package controllers.individual.amend

import config.FrontendAppConfig
import connectors.TrustsConnector
import controllers.actions._
import controllers.actions.individual.NameRequiredAction
import extractors.IndividualProtectorExtractor
import handlers.ErrorHandler
import javax.inject.Inject
import models.UserAnswers
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc._
import repositories.PlaybackRepository
import services.TrustService
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import utils.mappers.IndividualProtectorMapper
import utils.print.IndividualProtectorPrintHelper
import viewmodels.AnswerSection
import views.html.individual.amend.CheckDetailsView

import scala.concurrent.{ExecutionContext, Future}

class CheckDetailsController @Inject()(
                                        override val messagesApi: MessagesApi,
                                        standardActionSets: StandardActionSets,
                                        val controllerComponents: MessagesControllerComponents,
                                        view: CheckDetailsView,
                                        service: TrustService,
                                        connector: TrustsConnector,
                                        val appConfig: FrontendAppConfig,
                                        playbackRepository: PlaybackRepository,
                                        printHelper: IndividualProtectorPrintHelper,
                                        mapper: IndividualProtectorMapper,
                                        nameAction: NameRequiredAction,
                                        extractor: IndividualProtectorExtractor,
                                        errorHandler: ErrorHandler
                                      )(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport {

  private def render(userAnswers: UserAnswers,
                     index: Int,
                     name: String)
                    (implicit request: Request[AnyContent]): Result=
  {
    val section: AnswerSection = printHelper(userAnswers, adding = false, name)
    Ok(view(Seq(section), index))
  }

  def extractAndRender(index: Int): Action[AnyContent] = standardActionSets.verifiedForIdentifier.async {
    implicit request =>

      service.getIndividualProtector(request.userAnswers.identifier, index) flatMap {
        trust =>
          for {
            extractedF <- Future.fromTry(extractor(request.userAnswers, trust, index))
            _ <- playbackRepository.set(extractedF)
          } yield {
              render(extractedF, index, trust.name.displayName)
          }
      }
  }

  def renderFromUserAnswers(index: Int) : Action[AnyContent] = standardActionSets.verifiedForIdentifier.andThen(nameAction) {
    implicit request =>
      render(request.userAnswers, index, request.protectorName)
  }

  def onSubmit(index: Int): Action[AnyContent] = standardActionSets.verifiedForIdentifier.async {
    implicit request =>

      mapper(request.userAnswers).map {
        individual =>
          connector.amendIndividualProtector(request.userAnswers.identifier, index, individual).map(_ =>
            Redirect(controllers.routes.AddAProtectorController.onPageLoad())
          )
      }.getOrElse(errorHandler.internalServerErrorTemplate.map(html => InternalServerError(html)))
  }
}
