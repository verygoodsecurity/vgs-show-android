package com.verygoodsecurity.demoapp.actions

import android.view.View
import androidx.test.espresso.UiController
import androidx.test.espresso.matcher.ViewMatchers.*
import com.verygoodsecurity.vgscollect.view.InputFieldView
import org.hamcrest.Matcher

class FieldClickAction() : BaseAction() {

    override fun getDescription(): String = "VGS input field click"

    override fun getConstraints(): Matcher<View> = isAssignableFrom(InputFieldView::class.java)

    override fun perform(uiController: UiController?, view: View?) {
        uiController?.runAction {
            val inputField = view as InputFieldView
            inputField.performClick()
        }
    }
}