package com.verygoodsecurity.demoapp

import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.UiDevice
import com.verygoodsecurity.demoapp.utils.TestUtils
import com.verygoodsecurity.demoshow.R
import com.verygoodsecurity.demoshow.ui.MainActivity
import com.verygoodsecurity.vgsshow.widget.VGSPDFView
import junit.framework.AssertionFailedError
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@LargeTest
class PDFActivityCaseInstrumentedTest {

    @get:Rule
    val rule = ActivityScenarioRule(MainActivity::class.java)

    private lateinit var device: UiDevice

    @Before
    fun setup() {
        device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
        openTargetActivity()
    }

    @Test
    fun revealPDF_validAlias_hasDocument() {
        // Arrange
        mockAlias(MOCK_VALID_PDF_ALIAS)
        val revealBtn = TestUtils.interactWithDisplayedView(R.id.mbReveal)
        val pdfView = TestUtils.interactWithDisplayedView(R.id.vgsPDFView)
        // Act
        TestUtils.performClick(revealBtn)
        TestUtils.pauseTestFor(5000)
        // Assert
        pdfView.check { view, _ ->
            if (!(view as VGSPDFView).hasDocument) {
                throw AssertionFailedError("VGSPDFView has no document!")
            }
        }
    }

    @Test
    fun revealPDF_invalidAlias_noDocument() {
        // Arrange
        mockAlias(null)
        val revealBtn = TestUtils.interactWithDisplayedView(R.id.mbReveal)
        val pdfView = TestUtils.interactWithDisplayedView(R.id.vgsPDFView)
        // Act
        TestUtils.performClick(revealBtn)
        TestUtils.pauseTestFor(5000)
        // Assert
        pdfView.check { view, _ ->
            if ((view as VGSPDFView).hasDocument) {
                throw AssertionFailedError("VGSPDFView should not have document!")
            }
        }
    }

    private fun openTargetActivity() {
        val startWithPDFActivityBtn = TestUtils.interactWithDisplayedView(R.id.btnStartRevelPDF)
        TestUtils.performClick(startWithPDFActivityBtn)
    }

    private fun mockAlias(alias: String?) {
        val tvFileAlias = TestUtils.interactWithDisplayedView(R.id.tvFileAlias)
        tvFileAlias.perform(TestUtils.setTextInTextView(alias))
    }

    companion object {

        private const val MOCK_VALID_PDF_ALIAS = "tok_sandbox_pUsowRoCKkTHS8KinS55Bb"
    }
}