package com.example.nodrama.view

import android.widget.DatePicker
import androidx.test.espresso.Espresso.onData
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.PickerActions
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.activityScenarioRule
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import com.example.nodrama.R
import org.hamcrest.Matchers
import org.hamcrest.Matchers.anything
import org.hamcrest.Matchers.containsString
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4ClassRunner::class)
class ApplyLeaveActivityTest{

    @get:Rule
    var mActivityRule = activityScenarioRule<ApplyLeaveActivity>()

    @Test
    fun checkActivityVisibility() {
        onView(withId(R.id.activity_apply_leave))
            .check(matches(isDisplayed()))
     }

    @Test
    fun testLeaveTypeSpinner(){
        onView(withId(R.id.spinnerLeaveType)).perform(click())
        onData(anything()).atPosition(1).perform(click())
        onView(withId(R.id.spinnerLeaveType)).check(matches(withSpinnerText(containsString("Medical"))))
    }

    @Test
    fun testDatePickers(){
        //Start Date Picker
        onView(withId(R.id.startDatePickerButton))
            .perform(click())

        onView(withClassName(Matchers.equalTo(DatePicker::class.java.name)))
            .perform(PickerActions.setDate(2022, 4, 4))

        onView(withText("OK")).perform(click())

        onView(withId(R.id.startDatePickerButton))
            .check(matches(isDisplayed()))

        //End Date Picker
        onView(withId(R.id.endDatePickerButton))
            .perform(click())

        onView(withClassName(Matchers.equalTo(DatePicker::class.java.name)))
            .perform(PickerActions.setDate(2022, 4, 6))

        onView(withText("OK")).perform(click())

        onView(withId(R.id.endDatePickerButton))
            .check(matches(isDisplayed()))
    }

    @Test
    fun testRemarksText(){
        onView(withId(R.id.editTextRemarks))
            .check(matches(isDisplayed()))
    }
}