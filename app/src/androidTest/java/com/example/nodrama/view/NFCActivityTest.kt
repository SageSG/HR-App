package com.example.nodrama.view

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.activityScenarioRule
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import com.example.nodrama.R
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4ClassRunner::class)
class NFCActivityTest{

    @get:Rule
    var mActivityRule = activityScenarioRule<NFCActivity>()

    @Test
    fun checkActivityVisibility() {
        onView(withId(R.id.activity_nfc))
            .check(matches(isDisplayed()))
     }

    @Test
    fun testView(){
        onView(withId(R.id.textViewNFCTitle))
            .check(matches(withText("Room Access")))
        onView(withId(R.id.imageView))
            .check(matches(isDisplayed()))
        onView(withId(R.id.textViewNFC))
            .check(matches(withText("Hold your phone against the reader to access room.")))
    }
}