package com.verygoodsecurity.vgsshow.widget.view.pdf.state

import android.os.Parcel
import android.os.Parcelable
import android.view.View

class VGSPDFViewState : View.BaseSavedState {

    var data: ByteArray? = null

    var defaultPage: Int = 0

    var isSwipeEnabled: Boolean = true

    var isSwipeHorizontalEnabled: Boolean = false

    var isDoubleTapEnabled: Boolean = false

    var isAntialiasingEnabled: Boolean = true

    var spacing: Int = 0

    constructor(superState: Parcelable?) : super(superState)

    constructor(`in`: Parcel) : super(`in`) {
        data = `in`.createByteArray()
        defaultPage = `in`.readInt()
        isSwipeEnabled = `in`.readInt() == 1
        isSwipeHorizontalEnabled = `in`.readInt() == 1
        isDoubleTapEnabled = `in`.readInt() == 1
        isAntialiasingEnabled = `in`.readInt() == 1
        spacing = `in`.readInt()
    }

    override fun writeToParcel(out: Parcel, flags: Int) {
        super.writeToParcel(out, flags)
        out.writeByteArray(data)
        out.writeInt(defaultPage)
        out.writeInt(if (isSwipeEnabled) 1 else 0)
        out.writeInt(if (isSwipeHorizontalEnabled) 1 else 0)
        out.writeInt(if (isDoubleTapEnabled) 1 else 0)
        out.writeInt(if (isAntialiasingEnabled) 1 else 0)
        out.writeInt(spacing)
    }

    companion object {

        @JvmField
        val CREATOR = object : Parcelable.Creator<VGSPDFViewState> {

            override fun createFromParcel(source: Parcel): VGSPDFViewState {
                return VGSPDFViewState(source)
            }

            override fun newArray(size: Int): Array<VGSPDFViewState?> {
                return arrayOfNulls(size)
            }
        }
    }
}