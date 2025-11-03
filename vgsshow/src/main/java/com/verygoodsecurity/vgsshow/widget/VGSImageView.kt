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
 * A `VGSImageView` is used to display revealed images.
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
     * Controls how the image should be resized or moved to match the size of this `ImageView`.
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
     * Returns the current `ScaleType` used to scale the bounds of an image to the bounds of this `ImageView`.
     *
     * @return The `ScaleType` used to scale the image.
     */
    fun getScaleType(): ScaleType = view.scaleType

    /**
     * If set to `true`, this `ImageView` will adjust its bounds to preserve the aspect ratio of its drawable.
     *
     * @param adjustViewBounds `true` to adjust the bounds of this view to preserve the original aspect ratio
     * of the drawable.
     */
    fun setAdjustViewBounds(adjustViewBounds: Boolean) {
        view.adjustViewBounds = adjustViewBounds
    }

    /**
     * Returns `true` if this `ImageView` is adjusting its bounds to preserve the aspect ratio of its drawable.
     *
     * @return `true` if this `ImageView` is adjusting its bounds, `false` otherwise.
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
     * @param maxWidth The maximum width for this view.
     */
    fun setMaxWidth(maxWidth: Int) {
        view.maxWidth = maxWidth
    }

    /**
     * Returns the maximum width of this view.
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
     * Returns the maximum height of this view.
     *
     * @return The maximum height of this view.
     */
    fun getMaxHeight(): Int = view.maxHeight

    /**
     * Sets whether this `ImageView` will crop to its padding.
     *
     * @param cropToPadding `true` to crop to padding, `false` otherwise.
     */
    fun setCropToPadding(cropToPadding: Boolean) {
        view.cropToPadding = cropToPadding
    }

    /**
     * Returns whether this `ImageView` is cropping to its padding.
     *
     * @return `true` if this `ImageView` is cropping to its padding, `false` otherwise.
     */
    fun getCropToPadding(): Boolean = view.cropToPadding

    /**
     * Sets the baseline of this view to the specified value.
     *
     * @param baseline The baseline to use.
     */
    fun setImageViewBaseline(baseline: Int) {
        view.baseline = baseline
    }

    /**
     * Returns the baseline of this view.
     *
     * @return The baseline of this view.
     */
    fun getImageViewBaseline(): Int = view.baseline

    /**
     * Sets whether the baseline of this view is aligned with the bottom of the view.
     *
     * @param aligned If true, the image view will be baseline aligned by its bottom edge.
     *
     * @attr [R.styleable.VGSImageView_android_baselineAlignBottom]
     */
    fun setBaselineAlignBottom(aligned: Boolean) {
        view.baselineAlignBottom = aligned
    }

    /**
     * Returns `true` if this view's baseline is aligned with the bottom of the view, `false` otherwise.
     *
     * @return `true` if this view's baseline is aligned with the bottom, `false` otherwise.
     */
    fun getBaselineAlignBottom(): Boolean = view.baselineAlignBottom

    /**
     * Applies a tint to the image drawable.
     *
     * @see ColorStateList
     *
     * @attr [R.styleable.VGSImageView_android_tint]
     */
    fun setImageTintList(tint: ColorStateList) {
        view.imageTintList = tint
    }

    /**
     * Returns the `ColorStateList` used to tint the image drawable.
     *
     * @return The tint applied to the image drawable.
     *
     * @attr [R.styleable.VGSImageView_android_tint]
     */
    fun getImageTintList(): ColorStateList? = view.imageTintList

    /**
     * Specifies the blending mode used to apply the tint to the image drawable.
     *
     * @param tintMode The blending mode used to apply the tint, may be null to clear tint.
     *
     * @attr [R.styleable.VGSImageView_android_tintMode]
     */
    fun setImageTintMode(tintMode: PorterDuff.Mode) {
        view.imageTintMode = tintMode
    }

    /**
     * Returns the blending mode used to apply the tint to the image drawable.
     *
     * @see PorterDuff.Mode
     *
     * @attr [R.styleable.VGSImageView_android_tintMode]
     */
    fun getImageTintMode(): PorterDuff.Mode? = view.imageTintMode

    /**
     * Returns `true` if an image has been revealed and is ready to be displayed.
     *
     * @return `true` if an image is available, `false` otherwise.
     */
    fun hasImage(): Boolean {
        return (view.drawable as? BitmapDrawable)?.bitmap != null
    }

    /**
     * Removes the previously revealed image.
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