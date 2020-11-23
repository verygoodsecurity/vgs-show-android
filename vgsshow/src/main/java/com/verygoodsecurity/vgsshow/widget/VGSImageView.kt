package com.verygoodsecurity.vgsshow.widget

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.os.Parcel
import android.os.Parcelable
import android.util.AttributeSet
import android.util.Base64
import androidx.annotation.WorkerThread
import androidx.appcompat.widget.AppCompatImageView
import com.verygoodsecurity.vgsshow.util.ThreadHelper.runOnBackgroundThread
import com.verygoodsecurity.vgsshow.util.ThreadHelper.runOnUiThread
import com.verygoodsecurity.vgsshow.util.extension.isValidUrl
import com.verygoodsecurity.vgsshow.util.extension.logDebug
import com.verygoodsecurity.vgsshow.widget.core.VGSFieldType
import com.verygoodsecurity.vgsshow.widget.core.VGSView
import java.io.InputStream
import java.net.URL
import java.util.concurrent.Future

class VGSImageView @JvmOverloads internal constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : VGSView<AppCompatImageView>(context, attrs, defStyleAttr) {

    private var imageLoadingTask: Future<*>? = null

    init {

        // TODO: Read&Apply attributes
    }

    override fun getFieldType() = VGSFieldType.IMAGE

    override fun createChildView() = AppCompatImageView(context)

    override fun saveState(state: Parcelable?): BaseSavedState? = SavedState(state).apply {
//        bitmap = (view.drawable as? BitmapDrawable)?.bitmap
    }

    override fun restoreState(state: BaseSavedState) {
        (state as? SavedState)?.let { view.setImageBitmap(it.bitmap) }
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        imageLoadingTask?.cancel(true)
    }

    internal fun setImage(image: String?) {
        imageLoadingTask?.cancel(true)
        imageLoadingTask = runOnBackgroundThread {
            val bitmap = image.takeIf { it != null }?.let {
                try {
                    when {
                        it.isValidUrl() -> loadImage(URL(it))
                        else -> decodeImage(it)
                    }
                } catch (e: Exception) {
                    // TODO: Call state listener with error
                    logDebug(e.message.toString())
                    null
                }
            }
            runOnUiThread { view.setImageBitmap(bitmap) }
        }
    }

    @Throws(Exception::class)
    @WorkerThread
    private fun loadImage(url: URL): Bitmap {
        var stream: InputStream? = null
        return try {
            BitmapFactory.decodeStream(url.openStream().also { stream = it })
        } finally {
            stream?.close()
        }
    }

    @Throws(Exception::class)
    @WorkerThread
    private fun decodeImage(encodedImage: String): Bitmap {
        val decodedArray: ByteArray = Base64.decode(encodedImage, Base64.DEFAULT)
        return BitmapFactory.decodeByteArray(decodedArray, 0, decodedArray.size)
    }

    // TODO: Think about access restriction
    class SavedState : BaseSavedState {

        var bitmap: Bitmap? = null

        constructor(superState: Parcelable?) : super(superState)

        constructor(`in`: Parcel) : super(`in`) {
            bitmap = Bitmap.CREATOR.createFromParcel(`in`)
        }

        override fun writeToParcel(out: Parcel?, flags: Int) {
            super.writeToParcel(out, flags)
            out?.writeParcelable(bitmap, flags)
        }

        companion object {

            @JvmField
            val CREATOR = object : Parcelable.Creator<SavedState> {
                override fun createFromParcel(source: Parcel): SavedState {
                    return SavedState(source)
                }

                override fun newArray(size: Int): Array<SavedState?> {
                    return arrayOfNulls(size)
                }
            }
        }
    }

    // TODO: Add some kind of listener to subscribe to VGSImageView states changes
}