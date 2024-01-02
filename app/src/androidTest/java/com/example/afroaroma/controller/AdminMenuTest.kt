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
class AdminMenuTest {

    @Rule
    @JvmField
    var mActivityScenarioRule = ActivityScenarioRule(SplashActivity::class.java)

    @Test
    fun adminMenuTest() {
        val textView = onView(
            allOf(
                withId(R.id.txtWelcome), withText("Welcome back, admin"),
                withParent(withParent(withId(R.id.fragmentContainerView2))),
                isDisplayed()
            )
        )
        textView.check(matches(isDisplayed()))

        val appCompatButton = onView(
            allOf(
                withId(R.id.viewMenu), withText("view menu"),
                childAtPosition(
                    childAtPosition(
                        withId(R.id.fragmentContainerView2),
                        0
                    ),
                    4
                ),
                isDisplayed()
            )
        )
        appCompatButton.perform(click())

        val constraintLayout = onData(anything())
            .inAdapterView(
                allOf(
                    withId(R.id.menuListView),
                    childAtPosition(
                        withClassName(`is`("android.widget.LinearLayout")),
                        1
                    )
                )
            )
            .atPosition(3)
        constraintLayout.perform(scrollTo(), click())

        val appCompatButton2 = onView(
            allOf(
                withId(R.id.btnEdit), withText("edit"),
                childAtPosition(
                    childAtPosition(
                        withClassName(`is`("android.widget.LinearLayout")),
                        2
                    ),
                    0
                )
            )
        )
        appCompatButton2.perform(scrollTo(), click())

        val editText = onView(
            allOf(
                withId(R.id.editNameEditText), withText("Savannah Spice Latte"),
                withParent(withParent(IsInstanceOf.instanceOf(android.widget.LinearLayout::class.java))),
                isDisplayed()
            )
        )
        editText.check(matches(isDisplayed()))

        val textView2 = onView(
            allOf(
                withId(R.id.editTitleTextView), withText("Edit Savannah Spice Latte"),
                withParent(
                    allOf(
                        withId(R.id.relativeLayout),
                        withParent(IsInstanceOf.instanceOf(android.widget.ScrollView::class.java))
                    )
                ),
                isDisplayed()
            )
        )
        textView2.check(matches(isDisplayed()))

        val appCompatImageView = onView(
            allOf(
                withId(R.id.btnBack),
                childAtPosition(
                    allOf(
                        withId(R.id.relativeLayout),
                        childAtPosition(
                            withClassName(`is`("android.widget.ScrollView")),
                            0
                        )
                    ),
                    0
                )
            )
        )
        appCompatImageView.perform(scrollTo(), click())

        val appCompatButton3 = onView(
            allOf(
                withId(R.id.btnAdd), withText("add"),
                childAtPosition(
                    childAtPosition(
                        withClassName(`is`("android.widget.LinearLayout")),
                        2
                    ),
                    1
                )
            )
        )
        appCompatButton3.perform(scrollTo(), click())

        val appCompatImageView2 = onView(
            allOf(
                withId(R.id.btnBack),
                childAtPosition(
                    allOf(
                        withId(R.id.relativeLayout),
                        childAtPosition(
                            withClassName(`is`("android.widget.ScrollView")),
                            0
                        )
                    ),
                    0
                )
            )
        )
        appCompatImageView2.perform(scrollTo(), click())
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
