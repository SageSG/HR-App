package com.example.nodrama.view

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.rules.activityScenarioRule
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import com.example.nodrama.R
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4ClassRunner::class)
class TimesheetActivityTest{
    @get:Rule
    var mActivityRule = activityScenarioRule<TimesheetActivity>()

    @Test
    fun checkActivityVisibility() {
        onView(withId(R.id.activity_timesheet))
            .check(matches(isDisplayed()))
    }

//    /***
//     * Recycler view comes into view
//     */
//    @Test
//    fun testDisplayRecyclerView(){
//        onView(withId(R.id.recyclerViewTimesheet))
//            .check(matches(isDisplayed()))
//    }

    /***
     * Test textViews for todays timesheet
     */
    @Test
    fun testTextViews(){
        onView(withId(R.id.textViewToday))
            .check(matches(isDisplayed()))
        onView(withId(R.id.textViewCurrentDate))
            .check(matches(isDisplayed()))
        onView(withId(R.id.lblClockin))
            .check(matches(isDisplayed()))
        onView(withId(R.id.textViewClockInDate))
            .check(matches(isDisplayed()))
        onView(withId(R.id.lblClockout))
            .check(matches(isDisplayed()))
        onView(withId(R.id.textViewClockOutDate))
            .check(matches(isDisplayed()))
        onView(withId(R.id.textViewPR))
            .check(matches(isDisplayed()))
    }

    /***
     * test table layout for past timesheet
     */
    @Test
    fun testTableLayout(){
        onView(withId(R.id.tableLayoutPastHistory))
            .check(matches(isDisplayed()))
    }
}