package com.verygoodsecurity.vgsshow.widget

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.PorterDuff
import android.graphics.drawable.BitmapDrawable
import android.os.Build
import android.os.Parcelable
import android.util.AttributeSet
import android.widget.ImageView.ScaleType
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.AppCompatImageView
import com.verygoodsecurity.vgsshow.R
import com.verygoodsecurity.vgsshow.core.exception.VGSException
import com.verygoodsecurity.vgsshow.util.extension.decodeBitmap
import com.verygoodsecurity.vgsshow.util.extension.logException
import com.verygoodsecurity.vgsshow.widget.core.VGSFieldType
import com.verygoodsecurity.vgsshow.widget.core.VGSView

/**
 * VGS basic view control that displays revealed images to the user.
 */
class VGSImageView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : VGSView<AppCompatImageView>(context, attrs, defStyleAttr) {

    init {

        clear()
    }

    /**
     * @see OnLoadStateChangeListener
     */
    var onLoadStateChangeListener: OnLoadStateChangeListener? = null

    override fun getFieldType() = VGSFieldType.IMAGE

    override fun createChildView(
        attrs: AttributeSet?,
        defStyleAttr: Int
    ) = AppCompatImageView(context, attrs, defStyleAttr)

    override fun saveState(state: Parcelable?): BaseSavedState? = null

    override fun restoreState(state: BaseSavedState) {}

    /**
     * Controls how the image should be resized or moved to match the size of this image view.
     *
     * @param scaleType The desired scaling mode.
     *
     * @see ScaleType
     *
     * @attr [R.styleable.VGSImageView_android_scaleType]
     */
    fun setScaleType(scaleType: ScaleType) {
        view.scaleType = scaleType
    }

    /**
     * Returns the current [ScaleType] that is used to scale the bounds of an image to the bounds of the image view.
     *
     * @return The [ScaleType] used to scale the image.
     *
     * @attr [R.styleable.VGSImageView_android_scaleType]
     */
    fun getScaleType(): ScaleType = view.scaleType

    /**
     * Set this to true if you want the image view to adjust its bounds
     * to preserve the aspect ratio of its drawable.
     *
     * @param adjustViewBounds Whether to adjust the bounds of this view
     * to preserve the original aspect ratio of the drawable.
     *
     * @attr [R.styleable.VGSImageView_android_adjustViewBounds]
     */
    fun setAdjustViewBounds(adjustViewBounds: Boolean) {
        view.adjustViewBounds = adjustViewBounds
    }

    /**
     * True when image view is adjusting its bounds to preserve the aspect ratio of its drawable.
     *
     * @return Whether to adjust the bounds of this view to preserve the original aspect ratio
     * of the drawable.
     *
     * @attr [R.styleable.VGSImageView_android_adjustViewBounds]
     */
    fun getAdjustViewBounds(): Boolean = view.adjustViewBounds

    /**
     * An optional argument to supply a maximum width for this view. Only valid if
     * [setAdjustViewBounds] has been set to true. To set an image to be a maximum
     * of 100 x 100 while preserving the original aspect ratio, do the following:
     * 1) set adjustViewBounds to true
     * 2) set maxWidth and maxHeight to 100
     * 3) set the height and width layout params to WRAP_CONTENT.
     *
     * <p>
     * Note that this view could be still smaller than 100 x 100 using this approach if the original
     * image is small. To set an image to a fixed size, specify that size in the layout params and
     * then use [setScaleType] to determine how to fit the image within the bounds.
     * </p>
     *
     * @param maxWidth Maximum width for this view.
     *
     * @attr [R.styleable.VGSImageView_android_maxWidth]
     */
    fun setMaxWidth(maxWidth: Int) {
        view.maxWidth = maxWidth
    }

    /**
     * The maximum width of this view.
     *
     * @return The maximum width of this view.
     *
     * @attr [R.styleable.VGSImageView_android_maxWidth]
     */
    fun getMaxWidth(): Int = view.maxWidth

    /**
     * An optional argument to supply a maximum height for this view. Only valid if
     * [setAdjustViewBounds] has been set to true. To set an image to be a
     * maximum of 100 x 100 while preserving the original aspect ratio, do the following:
     * 1) set adjustViewBounds to true
     * 2) set maxWidth and maxHeight to 100
     * 3) set the height and width layout params to WRAP_CONTENT.
     *
     * <p>
     * Note that this view could be still smaller than 100 x 100 using this approach if the original
     * image is small. To set an image to a fixed size, specify that size in the layout params and
     * then use [setScaleType] to determine how to fit the image within the bounds.
     * </p>
     *
     * @param maxHeight Maximum height for this view.
     *
     * @attr [R.styleable.VGSImageView_android_maxHeight]
     */
    fun setMaxHeight(maxHeight: Int) {
        view.maxHeight = maxHeight
    }

    /**
     * The maximum height of this view.
     *
     * @return The maximum height of this view.
     *
     * @attr [R.styleable.VGSImageView_android_maxHeight]
     */
    fun getMaxHeight(): Int = view.maxHeight

    /**
     * Sets whether this image view will crop to padding.
     *
     * @param cropToPadding Whether this image view will crop to padding.
     *
     * @attr [R.styleable.VGSImageView_android_cropToPadding]
     */
    fun setCropToPadding(cropToPadding: Boolean) {
        view.cropToPadding = cropToPadding
    }

    /**
     * Return whether this image view crops to padding.
     *
     * @return Whether this image view crops to padding.
     *
     * @attr [R.styleable.VGSImageView_android_cropToPadding]
     */
    fun getCropToPadding(): Boolean = view.cropToPadding

    /**
     * Set the offset of the widget's text baseline from the widget's top
     * boundary. This value is overridden by the [setBaselineAlignBottom] property.
     *
     * @param baseline The baseline to use, or -1 if none is to be provided.
     *
     * @attr [R.styleable.VGSImageView_android_baseline]
     */
    fun setImageViewBaseline(baseline: Int) {
        view.baseline = baseline
    }

    /**
     * Return the offset of the widget's text baseline from the widget's top boundary.
     *
     * @return The offset of the baseline within the widget's bounds or -1 if baseline alignment
     * is not supported.
     *
     * @attr [R.styleable.VGSImageView_android_baseline]
     */
    fun getImageViewBaseline(): Int = view.baseline

    /**
     * Sets whether the baseline of this view to the bottom of the view.
     * Setting this value overrides any calls to setBaseline.
     *
     * @param aligned If true, the image view will be baseline aligned by its bottom edge.
     *
     * @attr [R.styleable.VGSImageView_android_baselineAlignBottom]
     */
    fun setBaselineAlignBottom(aligned: Boolean) {
        view.baselineAlignBottom = aligned
    }

    /**
     * Checks whether this view's baseline is considered the bottom of the view.
     *
     * @return True if the image view's baseline is considered the bottom of the view, false if otherwise.
     *
     * @attr [R.styleable.VGSImageView_android_baselineAlignBottom]
     */
    fun getBaselineAlignBottom(): Boolean = view.baselineAlignBottom

    /**
     * Applies a tint to the image drawable. Does not modify the current tint mode,
     * which is [PorterDuff.Mode.SRC_IN] by default.
     *
     * @param tint The tint to apply, may be {@code null} to clear tint.
     *
     * @see ColorStateList
     *
     * @attr [R.styleable.VGSImageView_android_tint]
     */
    fun setImageTintList(tint: ColorStateList) {
        view.imageTintList = tint
    }

    /**
     * Get the current [ColorStateList] used to tint the image Drawable, or null if no tint is applied.
     *
     * @return The tint applied to the image drawable.
     *
     * @attr [R.styleable.VGSImageView_android_tint]
     */
    fun getImageTintList(): ColorStateList? = view.imageTintList

    /**
     * Specifies the blending mode used to apply the tint specified by [setImageTintList] to the
     * image drawable. The default mode is [PorterDuff.Mode.SRC_IN].
     *
     * @param tintMode The blending mode used to apply the tint, may be null to clear tint.
     *
     * @attr [R.styleable.VGSImageView_android_tintMode]
     */
    fun setImageTintMode(tintMode: PorterDuff.Mode) {
        view.imageTintMode = tintMode
    }

    /**
     * Gets the blending mode used to apply the tint to the image Drawable.
     *
     * @return The blending mode used to apply the tint to the image Drawable.
     *
     * @see PorterDuff.Mode
     *
     * @attr [R.styleable.VGSImageView_android_tintMode]
     */
    fun getImageTintMode(): PorterDuff.Mode? = view.imageTintMode

    /**
     * Check if image reveled.
     *
     * @return True if image reveled, false otherwise.
     */
    fun hasImage(): Boolean {
        return (view.drawable as? BitmapDrawable)?.bitmap != null
    }

    /**
     * Remove previously reveled image.
     */
    fun clear() {
        view.setImageBitmap(null)
    }

    internal fun setImageByteArray(array: ByteArray) {
        onLoadStateChangeListener?.onStart(this)
        try {
            view.setImageBitmap(array.decodeBitmap())
            onLoadStateChangeListener?.onComplete(this)
        } catch (e: VGSException.ImageNotValid) {
            logException(e)
            onLoadStateChangeListener?.onError(this, e)
        }
    }

    /**
     * Interface definition for a callback to be invoked when image loading state changed.
     */
    interface OnLoadStateChangeListener {

        /**
         * Called when new image loading started.
         *
         * @param view This view.
         */
        fun onStart(view: VGSImageView)

        /**
         * Called when image loading completed.
         *
         * @param view This view.
         */
        fun onComplete(view: VGSImageView)

        /**
         * Called if image is not loaded.
         *
         * @param view This view.
         * @param exception Reason why image is not loaded.
         */
        fun onError(view: VGSImageView, exception: VGSException)
    }
}