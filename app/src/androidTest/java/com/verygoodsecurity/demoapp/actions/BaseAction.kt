package com.verygoodsecurity.demoapp.actions

import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction

abstract class BaseAction : ViewAction {

    protected fun UiController.runAction(func:() -> Unit ) {
        loopMainThreadUntilIdle()
        func()
        loopMainThreadUntilIdle()
    }
}