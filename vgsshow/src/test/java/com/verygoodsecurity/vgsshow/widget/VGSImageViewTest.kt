package com.verygoodsecurity.vgsshow.widget

import android.app.Activity
import android.content.res.ColorStateList
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.PorterDuff
import android.widget.ImageView.ScaleType
import com.verygoodsecurity.vgsshow.util.extension.decodeBitmap
import io.mockk.every
import io.mockk.mockkStatic
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.*
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class VGSImageViewTest {

    private lateinit var view: VGSImageView

    private companion object {

        private const val CONTENT_PATH = "<CONTENT_PATH>"
    }

    @Before
    fun setup() {
        val controller = Robolectric.buildActivity(Activity::class.java)
        view = VGSImageView(controller.get())
        view.onAttachedToWindow()
    }

    @Test
    fun getChildCount_initialState_oneReturned() {
        // Assert
        assertEquals(1, view.childCount)
    }

    @Test
    fun getContentPath_default_emptyReturned() {
        // Assert
        assertEquals("", view.getContentPath())
    }

    @Test
    fun getContentPath_valueSet() {
        // Act
        view.setContentPath(CONTENT_PATH)
        // Assert
        assertEquals(CONTENT_PATH, view.getContentPath())
    }

    @Test
    fun setScaleType_valueSet() {
        // Act
        view.setScaleType(ScaleType.CENTER)
        // Assert
        assertEquals(ScaleType.CENTER, view.getScaleType())
    }

    @Test
    fun setAdjustViewBounds_valueSet() {
        // Act
        view.setAdjustViewBounds(true)
        // Assert
        assertTrue(view.getAdjustViewBounds())
    }

    @Test
    fun setMaxWidth_valueSet() {
        // Act
        view.setMaxWidth(1)
        // Assert
        assertEquals(1, view.getMaxWidth())
    }

    @Test
    fun setMaxHeight_valueSet() {
        // Act
        view.setMaxHeight(1)
        // Assert
        assertEquals(1, view.getMaxHeight())
    }

    @Test
    fun setCropToPadding_valueSet() {
        // Act
        view.setCropToPadding(true)
        // Assert
        assertTrue(view.getCropToPadding())
    }

    @Test
    fun setImageViewBaseline_valueSet() {
        // Act
        view.setImageViewBaseline(1)
        // Assert
        assertEquals(1, view.getImageViewBaseline())
    }

    @Test
    fun setBaselineAlignBottom_valueSet() {
        // Act
        view.setBaselineAlignBottom(true)
        // Assert
        assertTrue(view.getBaselineAlignBottom())
    }

    @Test
    fun setImageTintList_valueSet() {
        // Arrange
        val tint = ColorStateList.valueOf(Color.BLACK)
        // Act
        view.setImageTintList(tint)
        // Assert
        assertEquals(tint, view.getImageTintList())
    }

    @Test
    fun setImageTintMode_valueSet() {
        // Arrange
        val tintMode = PorterDuff.Mode.DST_IN
        // Act
        view.setImageTintMode(tintMode)
        // Assert
        assertEquals(tintMode, view.getImageTintMode())
    }

    @Test
    fun hasImage_noImage_falseReturned() {
        // Assert
        assertFalse(view.hasImage())
    }

    @Test
    fun hasImage_defaultImage_trueReturned() {
        // Arrange
        val byteArray = mockByteArray()
        // Act
        view.setImageByteArray(byteArray)
        // Assert
        assertTrue(view.hasImage())
    }

    @Test
    fun clearImage_HasImageFalseAfterClear() {
        // Arrange
        val byteArray = mockByteArray()
        // Act
        view.setImageByteArray(byteArray)
        // Assert
        assertTrue(view.hasImage())
        view.clear()
        assertFalse(view.hasImage())
    }

    private fun mockByteArray(): ByteArray {
        val result = ByteArray(0)
        mockkStatic(ByteArray::decodeBitmap)
        every { result.decodeBitmap() } returns Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888)
        return result
    }
}