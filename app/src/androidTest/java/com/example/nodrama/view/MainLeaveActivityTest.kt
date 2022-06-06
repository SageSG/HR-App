package com.example.nodrama.view

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.scrollTo
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
class MainLeaveActivityTest{

    @get:Rule
    var mActivityRule = activityScenarioRule<MainLeaveActivity>()

    @Test
    fun checkActivityVisibility() {
        onView(withId(R.id.activity_leave))
            .check(matches(isDisplayed()))
     }

    @Test
    fun testNavigationToApplyLeave(){
        onView(withId(R.id.btnApplyLeave))
            .perform(click())

        onView(withId(R.id.activity_apply_leave))
            .check(matches(isDisplayed()))
    }

    @Test
    fun testCardViews(){
        onView(withId(R.id.imageViewAnnual))
            .check(matches(isDisplayed()))
        onView(withId(R.id.textViewAnnual))
            .check(matches(isDisplayed()))
        onView(withId(R.id.textViewAnnualBalance))
            .check(matches(isDisplayed()))
        onView(withId(R.id.textViewDays))
            .check(matches(isDisplayed()))

        onView(withId(R.id.imageViewMedical))
            .check(matches(isDisplayed()))
        onView(withId(R.id.textViewMedical))
            .check(matches(isDisplayed()))
        onView(withId(R.id.textViewMedicalBalance))
            .check(matches(isDisplayed()))
        onView(withId(R.id.textViewMedicalDays))
            .check(matches(isDisplayed()))

        onView(withId(R.id.imageViewFamily))
            .check(matches(isDisplayed()))
        onView(withId(R.id.textViewFamily))
            .check(matches(isDisplayed()))
        onView(withId(R.id.textViewFamilyBalances))
            .check(matches(isDisplayed()))
        onView(withId(R.id.textViewFamilyDays))
            .check(matches(isDisplayed()))

        onView(withId(R.id.imageViewBirthday))
            .perform(scrollTo())
            .check(matches(isDisplayed()))
        onView(withId(R.id.textViewBirthday))
            .perform(scrollTo())
            .check(matches(isDisplayed()))
        onView(withId(R.id.textViewBirthdayBalance))
            .perform(scrollTo())
            .check(matches(isDisplayed()))
        onView(withId(R.id.textViewBirthdayDays))
            .perform(scrollTo())
            .check(matches(isDisplayed()))
    }

    @Test
    fun testPendingRequestRecyclerView(){
        onView(withId(R.id.tableLayout1))
            .check(matches(isDisplayed()))
    }

    @Test
    fun testPastRequestRecyclerView(){
        onView(withId(R.id.tableLayout2))
            .check(matches(isDisplayed()))
    }
}