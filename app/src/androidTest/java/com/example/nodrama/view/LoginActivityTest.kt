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
class LoginActivityTest{
    @get:Rule
    var mActivityRule = activityScenarioRule<LoginActivity>()

    @Test
    fun checkActivityVisibility() {
        onView(withId(R.id.login_activity_view))
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
            .check(matches(withText("Login to NoDramaHR")))
        onView(withId(R.id.editTextLoginEmail))
            .check(matches(isDisplayed()))
        onView(withId(R.id.editTextLoginPassword))
            .check(matches(isDisplayed()))
    }

    @Test
    fun testLoginButtonValidation(){
        onView(withId(R.id.buttonLogin))
            .perform(click())
        onView(withId(R.id.login_activity_view))
            .check(matches(isDisplayed()))
    }

    @Test
    fun testRegisterButton(){
        onView(withId(R.id.buttonRegister))
            .perform(click())
        onView(withId(R.id.activity_register))
            .check(matches(isDisplayed()))
    }

    @Test
    fun testForgetPasswordButton(){
        onView(withId(R.id.textViewForgetPassword))
            .perform(click())
        onView(withId(R.id.activity_reset_password))
            .check(matches(isDisplayed()))
    }
}