package com.verygoodsecurity.demoapp

import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.assertThat
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.platform.app.InstrumentationRegistry.getInstrumentation
import androidx.test.uiautomator.UiDevice
import com.verygoodsecurity.demoapp.TestUtils.interactWithDisplayedView
import com.verygoodsecurity.demoapp.TestUtils.interactWithNestedView
import com.verygoodsecurity.demoapp.TestUtils.pauseTestFor
import com.verygoodsecurity.demoapp.TestUtils.performClick
import com.verygoodsecurity.demoapp.actions.SetTextAction
import com.verygoodsecurity.demoapp.check.SecureTextCheck
import com.verygoodsecurity.demoshow.R
import com.verygoodsecurity.demoshow.ui.MainActivity
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
        const val CARD_NUMBER_RESULT = "4111 - 1111 - 1111 - 1111"
        const val CARD_EXP_DATE = "02/2023"
    }

    @get:Rule
    val rule = ActivityScenarioRule(MainActivity::class.java)

    private lateinit var device: UiDevice

    @Before
    fun prepareDevice_redactData() {
        device = UiDevice.getInstance(getInstrumentation())

        val startWithActivityBtn = interactWithDisplayedView(R.id.btnStartActivityMain)
        performClick(startWithActivityBtn)

        val cardInputField = interactWithNestedView(R.id.etCardNumberVGSActivity, R.id.tilCardNumberVGSActivity)
        val cardExpDateInputField = interactWithNestedView(R.id.etExpDateVGSActivity, R.id.tilExpDateVGSActivity)
        val numberResponseToke = interactWithDisplayedView(R.id.tvCardNumberAliasVGSActivity)
        val expirationResponseToke = interactWithDisplayedView(R.id.tvExpDateAliasVGSActivity)
        val submitButton = interactWithDisplayedView(R.id.mbSubmitVGSActivity)

        cardInputField.perform(SetTextAction(CARD_NUMBER))
        cardExpDateInputField.perform(SetTextAction(CARD_EXP_DATE))

        performClick(submitButton)
        pauseTestFor(5000)

        numberResponseToke.check(matches(not(withText(""))))
        expirationResponseToke.check(matches(not(withText(""))))
    }

    @Test
    fun checkRevealedData_dataRevealedSuccessfully() {
        assertThat(device, notNullValue())

        val revealedCardNumber = interactWithDisplayedView(R.id.tvCardNumberVGSActivity)
        val revealedCardExpDate = interactWithDisplayedView(R.id.tvCardExpirationVGSActivity)
        val revealButton = interactWithDisplayedView(R.id.mbRequestVGSActivity)

        performClick(revealButton)
        pauseTestFor(5000)

        revealedCardNumber.check(SecureTextCheck(CARD_NUMBER_RESULT))
        revealedCardExpDate.check(SecureTextCheck(CARD_EXP_DATE))
    }

    @Test
    fun checkRevealedData_testDeviceRotation() {
        assertThat(device, notNullValue())

        val revealedCardNumber = interactWithDisplayedView(R.id.tvCardNumberVGSActivity)
        val revealedCardExpDate = interactWithDisplayedView(R.id.tvCardExpirationVGSActivity)
        val revealButton = interactWithDisplayedView(R.id.mbRequestVGSActivity)

        performClick(revealButton)
        pauseTestFor(5000)

        device.setOrientationRight()

        revealedCardNumber.check(SecureTextCheck(CARD_NUMBER_RESULT))
        revealedCardExpDate.check(SecureTextCheck(CARD_EXP_DATE))

        device.setOrientationNatural()

        revealedCardNumber.check(SecureTextCheck(CARD_NUMBER_RESULT))
        revealedCardExpDate.check(SecureTextCheck(CARD_EXP_DATE))
    }
}