package com.example.nodrama.view

import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.activityScenarioRule
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import com.example.nodrama.R
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4ClassRunner::class)
class PayslipActivityTest{

    @get:Rule
    var mActivityRule = activityScenarioRule<PayslipActivity>()

    @Test
    fun checkActivityVisibility() {
        onView(withId(R.id.activity_payslips))
            .check(matches(isDisplayed()))
     }

    @Test
    fun testViewTable(){
        onView(withId(R.id.tableLayout1))
            .check(matches(isDisplayed()))

        onView(withId(R.id.recyclerViewPayslip))
            .check(matches(isDisplayed()))
    }

    @Test
    fun testClickableRow(){
        onView(withId(R.id.recyclerViewPayslip))
            .perform(RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(0, click()))
        onView(withText("Secured Document"))
            .check(matches(isDisplayed()))
    }
}