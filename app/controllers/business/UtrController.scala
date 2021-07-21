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

package controllers.business

import config.annotations.BusinessProtector
import controllers.actions.business.NameRequiredAction
import controllers.actions.{ProtectorNameRequest, StandardActionSets}
import forms.UtrFormProvider
import models.Mode
import navigation.Navigator
import pages.business.{IndexPage, UtrPage}
import play.api.data.Form
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.PlaybackRepository
import services.TrustService
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.business.UtrView

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class UtrController @Inject()(
                               val controllerComponents: MessagesControllerComponents,
                               standardActionSets: StandardActionSets,
                               nameAction: NameRequiredAction,
                               formProvider: UtrFormProvider,
                               playbackRepository: PlaybackRepository,
                               view: UtrView,
                               @BusinessProtector navigator: Navigator,
                               trustsService: TrustService
                              )(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport {

  private def form(utrs: List[String])(implicit request: ProtectorNameRequest[AnyContent]): Form[String] =
    formProvider.apply("businessProtector.utr", request.userAnswers.identifier, utrs)

  private def index(implicit request: ProtectorNameRequest[AnyContent]): Option[Int] = request.userAnswers.get(IndexPage)

  def onPageLoad(mode: Mode): Action[AnyContent] = standardActionSets.verifiedForIdentifier.andThen(nameAction).async {
    implicit request =>

      trustsService.getBusinessUtrs(request.userAnswers.identifier, index) map { utrs =>
        val preparedForm = request.userAnswers.get(UtrPage) match {
          case None => form(utrs)
          case Some(value) => form(utrs).fill(value)
        }

        Ok(view(preparedForm, request.protectorName, mode))
      }
  }

  def onSubmit(mode: Mode): Action[AnyContent] = standardActionSets.verifiedForIdentifier.andThen(nameAction).async {
    implicit request =>

      trustsService.getBusinessUtrs(request.userAnswers.identifier, index) flatMap { utrs =>
        form(utrs).bindFromRequest().fold(
          (formWithErrors: Form[_]) =>
            Future.successful(BadRequest(view(formWithErrors, request.protectorName, mode))),
          value => {
            for {
              updatedAnswers <- Future.fromTry(request.userAnswers.set(UtrPage, value))
              _ <- playbackRepository.set(updatedAnswers)
            } yield Redirect(navigator.nextPage(UtrPage, mode, updatedAnswers))
          }
        )
      }
  }
}
