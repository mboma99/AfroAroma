package com.example.afroaroma.controller


import androidx.test.filters.LargeTest
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import android.view.View
import android.view.ViewGroup

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.*
import androidx.test.espresso.matcher.ViewMatchers.*

import com.example.afroaroma.R

import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.TypeSafeMatcher
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

import org.hamcrest.Matchers.allOf
import org.hamcrest.Matchers.`is`

@LargeTest
@RunWith(AndroidJUnit4::class)
class NavigateToLoginPageTest {

    @Rule
    @JvmField
    var mActivityScenarioRule = ActivityScenarioRule(SplashActivity::class.java)

    @Test
    fun loginTest() {
        Thread.sleep(7000)
        val appCompatButton = onView(
allOf(withId(R.id.btnLoginPage), withText("login"),
childAtPosition(
childAtPosition(
withClassName(`is`("android.widget.FrameLayout")),
1),
3),
isDisplayed()))
        appCompatButton.perform(click())

        val appCompatEditText = onView(
allOf(withId(R.id.email),
childAtPosition(
childAtPosition(
withClassName(`is`("android.widget.ScrollView")),
0),
2)))
        appCompatEditText.perform(scrollTo(), replaceText("admin@admin.com"), closeSoftKeyboard())

        val appCompatEditText2 = onView(
allOf(withId(R.id.password),
childAtPosition(
childAtPosition(
withClassName(`is`("android.widget.ScrollView")),
0),
3)))
        appCompatEditText2.perform(scrollTo(), replaceText("123456@"), closeSoftKeyboard())

        val appCompatButton2 = onView(
allOf(withId(R.id.btnLoginPage), withText("login"),
childAtPosition(
childAtPosition(
withClassName(`is`("android.widget.ScrollView")),
0),
5)))
        appCompatButton2.perform(scrollTo(), click())
        }

    private fun childAtPosition(
            parentMatcher: Matcher<View>, position: Int): Matcher<View> {

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
