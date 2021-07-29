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

package controllers

import config.FrontendAppConfig
import connectors.TrustsStoreConnector
import controllers.actions.StandardActionSets
import forms.{AddAProtectorFormProvider, YesNoFormProvider}
import models.AddAProtector
import models.protectors.Protectors
import navigation.ProtectorNavigator
import play.api.Logging
import play.api.data.Form
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.PlaybackRepository
import services.TrustService
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import utils.{AddAProtectorViewHelper, Session}
import views.html.{AddAProtectorView, AddAProtectorYesNoView, MaxedOutProtectorsView}

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class AddAProtectorController @Inject()(
                                         override val messagesApi: MessagesApi,
                                         standardActionSets: StandardActionSets,
                                         val controllerComponents: MessagesControllerComponents,
                                         val appConfig: FrontendAppConfig,
                                         trustStoreConnector: TrustsStoreConnector,
                                         trustService: TrustService,
                                         addAnotherFormProvider: AddAProtectorFormProvider,
                                         yesNoFormProvider: YesNoFormProvider,
                                         repository: PlaybackRepository,
                                         addAnotherView: AddAProtectorView,
                                         yesNoView: AddAProtectorYesNoView,
                                         completeView: MaxedOutProtectorsView,
                                         navigator: ProtectorNavigator
                                       )(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport with Logging {

  private val addAnotherForm: Form[AddAProtector] = addAnotherFormProvider()

  private val yesNoForm: Form[Boolean] = yesNoFormProvider.withPrefix("addAProtectorYesNo")

  def onPageLoad(): Action[AnyContent] = standardActionSets.verifiedForIdentifier.async {
    implicit request =>

      for {
        protectors <- trustService.getProtectors(request.userAnswers.identifier)
        updatedAnswers <- Future.fromTry(request.userAnswers.cleanup)
        _ <- repository.set(updatedAnswers)
      } yield {

        val protectorRows = new AddAProtectorViewHelper(protectors).rows

        protectors match {
          case Protectors(Nil, Nil) =>
            Ok(yesNoView(yesNoForm))
          case _ =>
            if (protectors.nonMaxedOutOptions.isEmpty) {
              Ok(completeView(
                inProgressProtectors = protectorRows.inProgress,
                completeProtectors = protectorRows.complete,
                heading = protectors.addToHeading
              ))
            } else {
              Ok(addAnotherView(
                form = addAnotherForm,
                inProgressProtectors = protectorRows.inProgress,
                completeProtectors = protectorRows.complete,
                heading = protectors.addToHeading,
                maxedOut = protectors.maxedOutOptions.map(x => x.messageKey)
              ))
            }
        }
      }
  }

  def submitOne(): Action[AnyContent] = standardActionSets.verifiedForIdentifier.async {
    implicit request =>

      yesNoForm.bindFromRequest().fold(
        (formWithErrors: Form[_]) => {
          Future.successful(BadRequest(yesNoView(formWithErrors)))
        },
        addNow => {
          if (addNow) {
            for {
              updatedAnswers <- Future.fromTry(request.userAnswers.cleanup)
              _ <- repository.set(updatedAnswers)
            } yield Redirect(controllers.routes.InfoController.onPageLoad())
          } else {
            for {
              _ <- trustStoreConnector.setTaskComplete(request.userAnswers.identifier)
            } yield {
              Redirect(appConfig.maintainATrustOverview)
            }
          }
        }
      )
  }

  def submitAnother(): Action[AnyContent] = standardActionSets.verifiedForIdentifier.async {
    implicit request =>

      trustService.getProtectors(request.userAnswers.identifier).flatMap { protectors =>
        addAnotherForm.bindFromRequest().fold(
          (formWithErrors: Form[_]) => {

            val rows = new AddAProtectorViewHelper(protectors).rows

            Future.successful(BadRequest(
              addAnotherView(
                formWithErrors,
                rows.inProgress,
                rows.complete,
                protectors.addToHeading,
                maxedOut = protectors.maxedOutOptions.map(x => x.messageKey)
              )
            ))
          },
          {
            case AddAProtector.YesNow =>
              for {
                updatedAnswers <- Future.fromTry(request.userAnswers.cleanup)
                _ <- repository.set(updatedAnswers)
              } yield Redirect(navigator.addProtectorRoute(protectors))

            case AddAProtector.YesLater =>
              Future.successful(Redirect(appConfig.maintainATrustOverview))

            case AddAProtector.NoComplete =>
              for {
                _ <- trustStoreConnector.setTaskComplete(request.userAnswers.identifier)
              } yield {
                Redirect(appConfig.maintainATrustOverview)
              }
          }
        )
      }
  }

  def submitComplete(): Action[AnyContent] = standardActionSets.verifiedForIdentifier.async {
    implicit request =>

      for {
        _ <- trustStoreConnector.setTaskComplete(request.userAnswers.identifier)
      } yield {
        logger.info(s"[Session ID: ${Session.id(hc)}]" +
          s" user has finished maintaining protectors and is returning to the task list")
        Redirect(appConfig.maintainATrustOverview)
      }
  }
}
