package com.verygoodsecurity.demoapp.utils

import androidx.annotation.IdRes
import androidx.test.espresso.Espresso
import androidx.test.espresso.ViewInteraction
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
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
}