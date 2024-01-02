package com.example.afroaroma.controller


import android.view.View
import android.view.ViewGroup
import androidx.test.espresso.Espresso.onData
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.*
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.example.afroaroma.R
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.Matchers.allOf
import org.hamcrest.Matchers.anything
import org.hamcrest.Matchers.`is`
import org.hamcrest.TypeSafeMatcher
import org.hamcrest.core.IsInstanceOf
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@LargeTest
@RunWith(AndroidJUnit4::class)
class AdminViewFeedbackTest {

    @Rule
    @JvmField
    var mActivityScenarioRule = ActivityScenarioRule(SplashActivity::class.java)

    @Test
    fun adminViewFeedbackTest() {
        val appCompatButton = onView(
            allOf(
                withId(R.id.viewFeedback), withText("feedback"),
                childAtPosition(
                    childAtPosition(
                        withId(R.id.fragmentContainerView2),
                        0
                    ),
                    7
                ),
                isDisplayed()
            )
        )
        appCompatButton.perform(click())

        val constraintLayout = onData(anything())
            .inAdapterView(
                allOf(
                    withId(R.id.feedbackListView),
                    childAtPosition(
                        withClassName(`is`("android.widget.LinearLayout")),
                        2
                    )
                )
            )
            .atPosition(0)
        constraintLayout.perform(click())

        val linearLayout = onView(
            allOf(
                withParent(withParent(withId(R.id.feedbackListView))),
                isDisplayed()
            )
        )
        linearLayout.check(matches(isDisplayed()))

        val constraintLayout2 = onData(anything())
            .inAdapterView(
                allOf(
                    withId(R.id.feedbackListView),
                    childAtPosition(
                        withClassName(`is`("android.widget.LinearLayout")),
                        2
                    )
                )
            )
            .atPosition(0)
        constraintLayout2.perform(click())

        val constraintLayout3 = onData(anything())
            .inAdapterView(
                allOf(
                    withId(R.id.feedbackListView),
                    childAtPosition(
                        withClassName(`is`("android.widget.LinearLayout")),
                        2
                    )
                )
            )
            .atPosition(0)
        constraintLayout3.perform(click())

        val appCompatButton2 = onView(
            allOf(
                withId(R.id.btnViewFeedback), withText("View"),
                childAtPosition(
                    childAtPosition(
                        withClassName(`is`("android.widget.RelativeLayout")),
                        1
                    ),
                    2
                )
            )
        )
        appCompatButton2.perform(scrollTo(), click())

        val textView = onView(
            allOf(
                withId(R.id.customerNameInput), withText("Customer S."),
                withParent(withParent(IsInstanceOf.instanceOf(android.widget.LinearLayout::class.java))),
                isDisplayed()
            )
        )
        textView.check(matches(isDisplayed()))

        val textView2 = onView(
            allOf(
                withId(R.id.feedbackHeadlineInput), withText("Amzing service!!!"),
                withParent(withParent(IsInstanceOf.instanceOf(android.widget.LinearLayout::class.java))),
                isDisplayed()
            )
        )
        textView2.check(matches(isDisplayed()))

        val textView3 = onView(
            allOf(
                withId(R.id.feedbackInput), withText("great customer service"),
                withParent(withParent(IsInstanceOf.instanceOf(android.widget.LinearLayout::class.java))),
                isDisplayed()
            )
        )
        textView3.check(matches(isDisplayed()))

        val textView4 = onView(
            allOf(
                withId(R.id.feedbackScoreInput), withText("5 / 5"),
                withParent(withParent(IsInstanceOf.instanceOf(android.widget.LinearLayout::class.java))),
                isDisplayed()
            )
        )
        textView4.check(matches(isDisplayed()))

        val textView5 = onView(
            allOf(
                withId(R.id.orderDateInput), withText("12 / December / 2023"),
                withParent(withParent(IsInstanceOf.instanceOf(android.widget.LinearLayout::class.java))),
                isDisplayed()
            )
        )
        textView5.check(matches(isDisplayed()))
    }

    private fun childAtPosition(
        parentMatcher: Matcher<View>, position: Int
    ): Matcher<View> {

        return object : TypeSafeMatcher<View>() {
            override fun describeTo(description: Description) {
                description.appendText("Child at position $position in parent ")
                parentMatcher.describeTo(description)
            }

            public override fun matchesSafely(view: View): Boolean {
                val parent = view.parent
                return parent is ViewGroup && parentMatcher.matches(parent)
                        && view == parent.getChildAt(position)
            }
        }
    }
}
