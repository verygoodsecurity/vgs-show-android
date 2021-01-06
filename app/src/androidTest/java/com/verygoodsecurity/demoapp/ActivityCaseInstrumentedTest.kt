package com.verygoodsecurity.demoapp

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.ViewInteraction
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.platform.app.InstrumentationRegistry.getInstrumentation
import androidx.test.uiautomator.UiDevice
import com.verygoodsecurity.demoapp.actions.SetTextAction
import com.verygoodsecurity.demoshow.MainActivity
import com.verygoodsecurity.demoshow.R
import org.hamcrest.Matchers.not
import org.hamcrest.Matchers.notNullValue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@LargeTest
class ActivityCaseInstrumentedTest {

    companion object {
        const val CARD_NUMBER = "4111111111111111"
        const val CARD_EXP_DATE = "02/2023"
    }

    @get:Rule
    val rule = ActivityScenarioRule(MainActivity::class.java)

    private lateinit var device: UiDevice

    @Before
    fun prepareDevice() {
        device = UiDevice.getInstance(getInstrumentation())
    }

    @Test
    fun test_submit() {
        assertThat(device, notNullValue())

        val submitButton = interactWithSubmitButton()

        val cardInputField = interactWithCardNumber()
        val cardExpDateInputField = interactWithCardExp()

        val numberResponseToke = interactWithCardNumberResponse()
        val expirationResponseToke = interactWithExpDateResponse()

        cardInputField.perform(SetTextAction(CARD_NUMBER))
        cardExpDateInputField.perform(SetTextAction(CARD_EXP_DATE))

        performClick(submitButton)
        pauseTestFor(5000)

        numberResponseToke.check(matches(not(withText(""))))
        expirationResponseToke.check(matches(not(withText(""))))
    }

    private fun interactWithCardNumber(): ViewInteraction {
        val cardHolderField = onView(withId(R.id.cardNumber))
            .check(matches(not(isDisplayed())))
        onView(withId(R.id.cardNumberLayout))
            .check(matches(isDisplayed()))

        return cardHolderField
    }

    private fun interactWithCardExp(): ViewInteraction {
        val cardInputField = onView(withId(R.id.expDate))
            .check(matches(not(isDisplayed())))
        onView(withId(R.id.expDateLayout))
            .check(matches(isDisplayed()))

        return cardInputField
    }

    private fun interactWithCardNumberResponse() =
        onView(withId(R.id.tokenView1)).check(matches(isDisplayed()))

    private fun interactWithExpDateResponse() =
        onView(withId(R.id.tokenView2)).check(matches(isDisplayed()))

    private fun interactWithSubmitButton(): ViewInteraction {
        return onView(withId(R.id.submitButton))
            .check(matches(isDisplayed()))
    }

    private fun performClick(interaction: ViewInteraction) {
        pauseTestFor(200)
        interaction.perform(click())
    }

    private fun pauseTestFor(milliseconds: Long) {
        try {
            Thread.sleep(milliseconds)
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
    }
}