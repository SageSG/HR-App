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
class AccountActivityTest{
    @get:Rule
    var mActivityRule = activityScenarioRule<AccountActivity>()

    @Test
    fun checkActivityVisibility() {
        onView(withId(R.id.activity_account))
            .check(matches(isDisplayed()))
    }

    @Test
    fun testTextViews(){
        onView(withId(R.id.textViewAccountName))
            .check(matches(isDisplayed()))
        onView(withId(R.id.textViewAccountEmail))
            .check(matches(isDisplayed()))
        onView(withId(R.id.tvPersonalInfo))
            .check(matches(isDisplayed()))
        onView(withId(R.id.textEmployeeNum))
            .check(matches(isDisplayed()))
        onView(withId(R.id.textContactNum))
            .check(matches(isDisplayed()))
        onView(withId(R.id.textPosition))
            .check(matches(isDisplayed()))
        onView(withId(R.id.textJoinDate))
            .check(matches(isDisplayed()))
        onView(withId(R.id.tvAccountActions))
            .check(matches(isDisplayed()))
        onView(withId(R.id.buttonBiometricLayout))
            .check(matches(isDisplayed()))
        onView(withId(R.id.buttonBiometric))
            .check(matches(isDisplayed()))
    }
}