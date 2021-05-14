package com.verygoodsecurity.demoapp

import android.app.Activity.RESULT_OK
import android.app.Instrumentation
import android.content.Intent
import android.provider.MediaStore
import androidx.test.core.app.launchActivity
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers.hasAction
import androidx.test.espresso.intent.matcher.IntentMatchers.isInternal
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.UiDevice
import com.verygoodsecurity.demoapp.utils.TestUtils
import com.verygoodsecurity.demoshow.R
import com.verygoodsecurity.demoshow.ui.MainActivity
import org.hamcrest.CoreMatchers.not
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@LargeTest
class PDFActivityInstrumentedTest {

    val rule = launchActivity<MainActivity>()

    private lateinit var device: UiDevice

    @Before
    fun setup() {
        device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
        Intents.init()
        mockFileSelect()
        openTestedActivity()
    }

    @After
    fun teardown() {
        Intents.release()
    }

    @Test
    fun test() {
        // Arrange
        val attachFileButton = TestUtils.interactWithDisplayedView(R.id.mbAttachFile)

        // Act
        TestUtils.performClick(attachFileButton)

        // Assert
    }

    private fun mockFileSelect() {
        val resultData = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
        Intents.intending(not(isInternal()))
            .respondWith(
                Instrumentation.ActivityResult(
                    RESULT_OK,
                    resultData
                )
            )
    }

    private fun openTestedActivity() {
        val pdfActivityBtn = TestUtils.interactWithDisplayedView(R.id.btnStartRevelPDF)
        TestUtils.performClick(pdfActivityBtn)
    }
}