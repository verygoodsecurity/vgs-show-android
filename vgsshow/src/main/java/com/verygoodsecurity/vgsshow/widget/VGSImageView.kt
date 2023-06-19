package com.verygoodsecurity.vgsshow.widget

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.PorterDuff
import android.os.Build
import android.os.Parcelable
import android.util.AttributeSet
import android.widget.ImageView.ScaleType
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.AppCompatImageView
import com.verygoodsecurity.vgsshow.util.extension.decodeBitmap
import com.verygoodsecurity.vgsshow.util.extension.logWaring
import com.verygoodsecurity.vgsshow.widget.core.VGSFieldType
import com.verygoodsecurity.vgsshow.widget.core.VGSView

@Suppress("unused")
class VGSImageView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : VGSView<AppCompatImageView>(context, attrs, defStyleAttr) {

    init {

        view.setImageBitmap(null)
    }

    override fun getFieldType() = VGSFieldType.IMAGE

    override fun createChildView(
        attrs: AttributeSet?,
        defStyleAttr: Int
    ) = AppCompatImageView(context, attrs, defStyleAttr)

    override fun saveState(state: Parcelable?): BaseSavedState? = null

    override fun restoreState(state: BaseSavedState) {}

    fun setScaleType(scaleType: ScaleType) {
        view.scaleType = scaleType
    }

    fun getScaleType(): ScaleType = view.scaleType

    fun setAdjustViewBounds(adjustViewBounds: Boolean) {
        view.adjustViewBounds = adjustViewBounds
    }

    fun getAdjustViewBounds(): Boolean = view.adjustViewBounds

    fun setMaxWidth(maxWidth: Int) {
        view.maxWidth = maxWidth
    }

    fun getMaxWidth(): Int = view.maxWidth

    fun setMaxHeight(maxHeight: Int) {
        view.maxHeight = maxHeight
    }

    fun getMaxHeight(): Int = view.maxHeight

    fun setCropToPadding(cropToPadding: Boolean) {
        view.cropToPadding = cropToPadding
    }

    fun getCropToPadding(): Boolean = view.cropToPadding

    fun setImageViewBaseline(baseline: Int) {
        view.baseline = baseline
    }

    fun getImageViewBaseline(): Int = view.baseline

    fun setBaselineAlignBottom(aligned: Boolean) {
        view.baselineAlignBottom = aligned
    }

    fun getBaselineAlignBottom(): Boolean = view.baselineAlignBottom

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    fun setImageTintList(tint: ColorStateList) {
        view.imageTintList = tint
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    fun getImageTintList(): ColorStateList? = view.imageTintList

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    fun setImageTintMode(tintMode: PorterDuff.Mode) {
        view.imageTintMode = tintMode
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    fun getImageTinMode(): PorterDuff.Mode? = view.imageTintMode

    internal fun setImageByteArray(image: ByteArray) {
        image.decodeBitmap()?.let { view.setImageBitmap(it) }
            ?: logWaring("Revealed image not valid!")
    }
}