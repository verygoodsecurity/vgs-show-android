package com.verygoodsecurity.demoapp

import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.assertThat
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.UiDevice
import com.verygoodsecurity.demoapp.TestUtils.interactWithDisplayedView
import com.verygoodsecurity.demoapp.TestUtils.interactWithNestedView
import com.verygoodsecurity.demoapp.TestUtils.pauseTestFor
import com.verygoodsecurity.demoapp.TestUtils.performClick
import com.verygoodsecurity.demoapp.actions.SetTextAction
import com.verygoodsecurity.demoapp.check.SecureTextCheck
import com.verygoodsecurity.demoshow.R
import com.verygoodsecurity.demoshow.ui.MainActivity
import org.hamcrest.CoreMatchers.notNullValue
import org.hamcrest.Matchers
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@LargeTest
class FragmentCaseInstrumentedTest {

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
        device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())

        val startWithActivityBtn = interactWithDisplayedView(R.id.btnStartFragmentMain)
        performClick(startWithActivityBtn)

        val cardInputField =
            interactWithNestedView(R.id.etCardNumberVGSFragment, R.id.tilCardNumberVGSFragment)
        val cardExpDateInputField =
            interactWithNestedView(R.id.etExpDateVGSFragment, R.id.tilExpDateVGSFragment)
        val numberResponseToke = interactWithDisplayedView(R.id.tvCardNumberTokenVGSFragment)
        val expirationResponseToke = interactWithDisplayedView(R.id.tvExpDateTokenVGSFragment)
        val submitButton = interactWithDisplayedView(R.id.btnSubmitVGSFragment)

        cardInputField.perform(SetTextAction(CARD_NUMBER))
        cardExpDateInputField.perform(SetTextAction(CARD_EXP_DATE))

        performClick(submitButton)
        pauseTestFor(5000)

        numberResponseToke.check(ViewAssertions.matches(Matchers.not(ViewMatchers.withText(""))))
        expirationResponseToke.check(ViewAssertions.matches(Matchers.not(ViewMatchers.withText(""))))
    }

    @Test
    fun checkRevealedData_dataRevealedSuccessfully() {
        assertThat(device, notNullValue())

        val revealedCardNumber = interactWithDisplayedView(R.id.vtvCardNumberVGSFragment)
        val revealedCardExpDate = interactWithDisplayedView(R.id.vtvExpirationVGSFragment)
        val revealButton = interactWithDisplayedView(R.id.btnRequestVGSFragment)

        performClick(revealButton)
        pauseTestFor(5000)

        revealedCardNumber.check(SecureTextCheck(CARD_NUMBER_RESULT))
        revealedCardExpDate.check(SecureTextCheck(CARD_EXP_DATE))
    }

    @Test
    fun checkRevealedData_testDeviceRotation() {
        assertThat(device, notNullValue())

        val revealedCardNumber = interactWithDisplayedView(R.id.vtvCardNumberVGSFragment)
        val revealedCardExpDate = interactWithDisplayedView(R.id.vtvExpirationVGSFragment)
        val revealButton = interactWithDisplayedView(R.id.btnRequestVGSFragment)

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