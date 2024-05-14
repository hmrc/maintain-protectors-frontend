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

package controllers.individual

import base.SpecBase
import models.UserAnswers
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito._
import org.scalatest.BeforeAndAfterEach
import org.scalatestplus.mockito.MockitoSugar
import pages.individual.IndexPage
import play.api.test.FakeRequest
import play.api.test.Helpers._

import scala.concurrent.Future

class PassportOrIdCardDetailsControllerSpec extends SpecBase with MockitoSugar with BeforeAndAfterEach {

  val index = 0

  val baseAnswers: UserAnswers = emptyUserAnswers.set(IndexPage, index).success.value

  private lazy val passportOrIdCardDetailsRoute: String = routes.PassportOrIdCardDetailsController.onPageLoad().url

  override def beforeEach(): Unit = {
    reset(playbackRepository)
    when(playbackRepository.set(any())).thenReturn(Future.successful(true))
  }

  "PassportOrIdCardDetails Controller" must {

    "redirect to Check Details for a GET" in {

      val application = applicationBuilder(userAnswers = Some(baseAnswers)).build()

      val request = FakeRequest(GET, passportOrIdCardDetailsRoute)

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.individual.amend.routes.CheckDetailsController.renderFromUserAnswers(index).url

      application.stop()
    }

    "redirect to Check Details for a POST" in {

      val application = applicationBuilder(userAnswers = Some(baseAnswers)).build()

      val request =
        FakeRequest(POST, passportOrIdCardDetailsRoute)
          .withFormUrlEncodedBody(("value", "true"))

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.individual.amend.routes.CheckDetailsController.renderFromUserAnswers(index).url

      application.stop()
    }

    "redirect to Session Expired for a GET if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      val request = FakeRequest(GET, passportOrIdCardDetailsRoute)

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad().url

      application.stop()
    }

    "redirect to Session Expired for a POST if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      val request =
        FakeRequest(POST, passportOrIdCardDetailsRoute)
          .withFormUrlEncodedBody(("value", "true"))

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad().url

      application.stop()
    }
  }
}
