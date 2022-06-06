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
class NavBarActivityTest{

    @get:Rule
    var mActivityRule = activityScenarioRule<MainActivity>()

    @Test
    fun checkNavBarVisibility() {
        onView(withId(R.id.bottomAppBar))
            .check(matches(isDisplayed()))
    }

    @Test
    fun testNavigationToNFC(){
        onView(withId(R.id.navFab))
            .perform(click())

        onView(withId(R.id.activity_nfc))
            .check(matches(isDisplayed()))
    }
}