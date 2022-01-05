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

package views

import views.behaviours.ViewBehaviours
import views.html.InfoView

class InfoViewSpec extends ViewBehaviours {

  "InterruptPage view" must {

    val view = viewFor[InfoView](Some(emptyUserAnswers))

    val applyView = view.apply()(fakeRequest, messages)

    behave like normalPageTitleWithSectionSubheading(
      view = applyView,
      messageKeyPrefix = "protector.info",
      messageKeyParam = "",
      expectedGuidanceKeys = "section1.subheading", "section1.p1", "section1.bullet1", "section1.bullet2", "section1.bullet3",
      "section2.subheading", "section2.p1", "section2.bullet1", "section2.bullet2",
      "details.heading", "details.subheading1", "details.p1", "details.subheading2", "details.p2",
      "section3.subheading", "section3.p1", "section3.p2", "section3.bullet1", "section3.bullet2", "section3.bullet3", "section3.bullet4"
    )

    behave like pageWithBackLink(applyView)

    behave like pageWithASubmitButton(applyView)
  }
}
