package com.example.nodrama.view

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
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
class MainActivityTest{

    @get:Rule
    var mActivityRule = activityScenarioRule<MainActivity>()

    @Test
    fun checkActivityVisibility() {
        onView(withId(R.id.activity_main))
            .check(matches(isDisplayed()))
     }

    @Test
    fun testNavigationToTimesheet(){
        onView(withId(R.id.cardViewTimesheet))
            .perform(click())

        onView(withId(R.id.activity_timesheet))
            .check(matches(isDisplayed()))
    }

    @Test
    fun testNavigationToLeaves(){
        onView(withId(R.id.cardViewLeaves))
            .perform(click())

        onView(withId(R.id.activity_leave))
            .check(matches(isDisplayed()))
    }

    @Test
    fun testNavigationToPayslips(){
        onView(withId(R.id.cardViewPayslips))
            .perform(click())

        onView(withId(R.id.activity_payslips))
            .check(matches(isDisplayed()))
    }

    @Test
    fun testNavigationToART(){
        onView(withId(R.id.cardViewART))
            .perform(click())

        onView(withId(R.id.activity_art))
            .check(matches(isDisplayed()))
    }
}