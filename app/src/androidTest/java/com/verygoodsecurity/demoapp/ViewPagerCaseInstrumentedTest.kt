package com.verygoodsecurity.demoapp

import androidx.test.espresso.action.ViewActions.swipeLeft
import androidx.test.espresso.action.ViewActions.swipeRight
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.UiDevice
import com.verygoodsecurity.demoapp.actions.SetTextAction
import com.verygoodsecurity.demoapp.check.SecureTextCheck
import com.verygoodsecurity.demoapp.utils.TestUtils.interactWithDisplayedView
import com.verygoodsecurity.demoapp.utils.TestUtils.interactWithNestedView
import com.verygoodsecurity.demoapp.utils.TestUtils.pauseTestFor
import com.verygoodsecurity.demoapp.utils.TestUtils.performClick
import com.verygoodsecurity.demoshow.R
import com.verygoodsecurity.demoshow.ui.MainActivity
import org.hamcrest.CoreMatchers
import org.hamcrest.Matchers
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@LargeTest
class ViewPagerCaseInstrumentedTest {

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

        val startWithActivityBtn = interactWithDisplayedView(R.id.btnStartViewPagerMain)
        performClick(startWithActivityBtn)

        val cardInputField =
            interactWithNestedView(R.id.etCardNumber, R.id.tilCardNumber)
        val cardExpDateInputField =
            interactWithNestedView(R.id.etExpDate, R.id.tilExpDate)
        val numberResponseToke = interactWithDisplayedView(R.id.tvCardNumberAlias)
        val expirationResponseToke = interactWithDisplayedView(R.id.tvExpDateAlias)
        val submitButton = interactWithDisplayedView(R.id.mbSubmit)

        cardInputField.perform(SetTextAction(CARD_NUMBER))
        cardExpDateInputField.perform(SetTextAction(CARD_EXP_DATE))

        performClick(submitButton)
        pauseTestFor(5000)

        numberResponseToke.check(ViewAssertions.matches(Matchers.not(ViewMatchers.withText(""))))
        expirationResponseToke.check(ViewAssertions.matches(Matchers.not(ViewMatchers.withText(""))))
    }

    @Test
    fun checkRevealedData_dataRevealedSuccessfully() {
        ViewMatchers.assertThat(device, CoreMatchers.notNullValue())

        val viewPager = interactWithDisplayedView(R.id.vpVGSViewPagerActivity)

        viewPager.perform(swipeLeft())

        val revealedCardNumber = interactWithDisplayedView(R.id.tvCardNumber)
        val revealedCardExpDate = interactWithDisplayedView(R.id.tvCardExpiration)
        val revealButton = interactWithDisplayedView(R.id.mbRequest)

        performClick(revealButton)
        pauseTestFor(5000)

        revealedCardNumber.check(SecureTextCheck(CARD_NUMBER_RESULT))
        revealedCardExpDate.check(SecureTextCheck(CARD_EXP_DATE))
    }

    @Test
    fun checkRevealedData_testDeviceRotation() {
        ViewMatchers.assertThat(device, CoreMatchers.notNullValue())

        val viewPager = interactWithDisplayedView(R.id.vpVGSViewPagerActivity)

        viewPager.perform(swipeLeft())

        val revealedCardNumber = interactWithDisplayedView(R.id.tvCardNumber)
        val revealedCardExpDate = interactWithDisplayedView(R.id.tvCardExpiration)
        val revealButton = interactWithDisplayedView(R.id.mbRequest)

        performClick(revealButton)
        pauseTestFor(5000)

        device.setOrientationRight()

        revealedCardNumber.check(SecureTextCheck(CARD_NUMBER_RESULT))
        revealedCardExpDate.check(SecureTextCheck(CARD_EXP_DATE))

        device.setOrientationNatural()

        revealedCardNumber.check(SecureTextCheck(CARD_NUMBER_RESULT))
        revealedCardExpDate.check(SecureTextCheck(CARD_EXP_DATE))
    }

    @Test
    fun checkRevealedData_testPageChange() {
        ViewMatchers.assertThat(device, CoreMatchers.notNullValue())

        val viewPager = interactWithDisplayedView(R.id.vpVGSViewPagerActivity)

        viewPager.perform(swipeLeft())

        val revealedCardNumber = interactWithDisplayedView(R.id.tvCardNumber)
        val revealedCardExpDate = interactWithDisplayedView(R.id.tvCardExpiration)
        val revealButton = interactWithDisplayedView(R.id.mbRequest)

        performClick(revealButton)
        pauseTestFor(5000)

        revealedCardNumber.check(SecureTextCheck(CARD_NUMBER_RESULT))
        revealedCardExpDate.check(SecureTextCheck(CARD_EXP_DATE))

        viewPager.perform(swipeLeft())
        viewPager.perform(swipeRight())

        revealedCardNumber.check(SecureTextCheck(CARD_NUMBER_RESULT))
        revealedCardExpDate.check(SecureTextCheck(CARD_EXP_DATE))
    }
}