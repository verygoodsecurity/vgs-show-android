package com.verygoodsecurity.demoapp.actions

import android.view.View
import androidx.test.espresso.UiController
import androidx.test.espresso.matcher.ViewMatchers.isAssignableFrom
import androidx.test.espresso.remote.annotation.RemoteMsgField
import com.verygoodsecurity.vgscollect.view.InputFieldView
import org.hamcrest.Matcher
import java.util.*


class SetTextAction(
    @RemoteMsgField(order = 0)
    val stringToBeSet: String? = null
) : BaseAction() {

    override fun getDescription(): String = String.format(Locale.ROOT, "Sets the text(%s) for input field", stringToBeSet)

    override fun getConstraints(): Matcher<View> = isAssignableFrom(InputFieldView::class.java)

    override fun perform(uiController: UiController?, view: View?) {
        uiController?.runAction {
            val inputField = view as InputFieldView
            inputField.setText(stringToBeSet)
        }
    }
}