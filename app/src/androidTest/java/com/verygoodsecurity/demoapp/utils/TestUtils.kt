package com.verygoodsecurity.demoapp.utils

import android.view.View
import android.widget.TextView
import androidx.annotation.IdRes
import androidx.test.espresso.Espresso
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.ViewInteraction
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import org.hamcrest.CoreMatchers
import org.hamcrest.Matcher
import org.hamcrest.Matchers

object TestUtils {

    fun interactWithDisplayedView(@IdRes id: Int): ViewInteraction {
        return Espresso.onView(ViewMatchers.withId(id))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }

    fun interactWithNestedView(
        @IdRes nestedId: Int,
        @IdRes parentId: Int
    ): ViewInteraction {
        val nestedView = Espresso.onView(ViewMatchers.withId(nestedId))
            .check(ViewAssertions.matches(Matchers.not(ViewMatchers.isDisplayed())))
        Espresso.onView(ViewMatchers.withId(parentId))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        return nestedView
    }

    fun performClick(interaction: ViewInteraction) {
        pauseTestFor(200)
        interaction.perform(ViewActions.click())
    }

    fun pauseTestFor(milliseconds: Long) {
        try {
            Thread.sleep(milliseconds)
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
    }

    fun setTextInTextView(value: String?): ViewAction = object : ViewAction {
        override fun getConstraints(): Matcher<View> = CoreMatchers.allOf(
            ViewMatchers.isDisplayed(),
            ViewMatchers.isAssignableFrom(TextView::class.java)
        )

        override fun perform(uiController: UiController, view: View) {
            (view as TextView).text = value
        }

        override fun getDescription(): String = "Replace text"
    }
}