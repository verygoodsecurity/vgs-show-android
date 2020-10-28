package com.verygoodsecurity.vgsshow.widget

import android.app.Activity
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.android.controller.ActivityController

@RunWith(RobolectricTestRunner::class)
class VGSTextViewTest {

    private lateinit var activityController: ActivityController<Activity>
    private lateinit var activity: Activity

    private lateinit var view: VGSTextView

    @Before
    fun setUp() {
        activityController = Robolectric.buildActivity(Activity::class.java)
        activity = activityController.get()

        view = VGSTextView(activity)
    }

    @Test
    fun test_internal_state() {
        view.onAttachedToWindow()
        val internal = view.getState()
        assertNotNull(internal)
        assertTrue(internal!!.isViewReady)
    }

    @Test
    fun test_attach_view() {
        view.onAttachedToWindow()

        assertEquals(1, view.childCount)
    }

    @Test
    fun test_field_name() {
        val fn = "test_field_name"

        view.setFieldName(fn)
        assertEquals(view.getFieldName(), fn)

        view.onAttachedToWindow()
        assertEquals(view.getFieldName(), fn)

        val state = view.getState()
        assertEquals(state?.fieldName, fn)
    }
}