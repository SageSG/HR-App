package com.example.nodrama.view

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.activityScenarioRule
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import com.example.nodrama.R
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4ClassRunner::class)
class ResetPasswordActivityTest{
    @get:Rule
    var mActivityRule = activityScenarioRule<ResetPasswordActivity>()

    @Test
    fun checkActivityVisibility() {
        onView(withId(R.id.activity_reset_password))
            .check(matches(isDisplayed()))
    }

    @Test
    fun testImageView(){
        onView(withId(R.id.imageViewLogin))
            .check(matches(isDisplayed()))
    }

    @Test
    fun testTextViews(){
        onView(withId(R.id.textViewAppName))
            .check(matches(withText(R.string.reset_password)))
        onView(withId(R.id.rpLabel))
            .check(matches(withText(R.string.rpHelper)))
        onView(withId(R.id.editTextFPEmail))
            .check(matches(isDisplayed()))
    }

    @Test
    fun testForgetPasswordErrorButton(){
        onView(withId(R.id.buttonResetPassword))
            .perform(click())
        onView(withId(R.id.activity_reset_password))
            .check(matches(isDisplayed()))
    }
}