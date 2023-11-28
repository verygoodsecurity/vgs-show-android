package com.verygoodsecurity.vgsshow.widget.view.textview.extension

import android.text.method.TransformationMethod
import android.widget.TextView
import io.mockk.every
import io.mockk.mockk
import io.mockk.verifySequence
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class TextViewKtTest {

    private val view: TextView = mockk()
    private val transformationMethod: TransformationMethod = mockk()

    @Before
    fun before() {
        every { view.transformationMethod = any() } returns Unit
    }

    @Test
    fun `updateTransformationMethod - correct functions called`() {
        // Act
        view.updateTransformationMethod(transformationMethod)

        // Assert
        verifySequence {
            view.transformationMethod = null
            view.transformationMethod = transformationMethod
        }
    }
}