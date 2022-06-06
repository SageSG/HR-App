package com.example.nodrama.view

import android.app.Activity
import android.app.Instrumentation
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.provider.MediaStore
import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.intent.Intents.intending
import androidx.test.espresso.intent.matcher.IntentMatchers.hasAction
import androidx.test.espresso.intent.rule.IntentsTestRule
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.activityScenarioRule
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import com.example.nodrama.R
import org.hamcrest.Matcher
import org.hamcrest.Matchers
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4ClassRunner::class)
class ARTActivityTest {

    @get:Rule
    var mActivityRule = activityScenarioRule<ARTActivity>()

    @get:Rule
    val intentsTestRule: IntentsTestRule<ARTActivity> = IntentsTestRule(ARTActivity::class.java)

    @Test
    fun checkActivityVisibility() {
        onView(withId(R.id.activity_apply_art))
            .check(matches(isDisplayed()))
    }

    @Test
    fun testTextView() {
        onView(withId(R.id.textView4))
            .check(matches(withText(R.string.art_question3)))
    }

    @Test
    fun testSpinner() {
        onView(withId(R.id.spinnerHRW)).perform(ViewActions.click())
        Espresso.onData(Matchers.anything()).atPosition(0).perform(ViewActions.click())
        onView(withId(R.id.spinnerHRW)).check(
            matches(
                withSpinnerText(
                    Matchers.containsString(
                        "No"
                    )
                )
            )
        )
    }

    @Test
    fun testCamera() {
        val activityResult = createImageCaptureActivityResultStub()
        val expectedIntent: Matcher<Intent> = hasAction(MediaStore.ACTION_IMAGE_CAPTURE)
        intending(expectedIntent).respondWith(activityResult)

        onView(withId(R.id.cameraButton)).perform(click())
        intending(expectedIntent)
    }


    private fun createImageCaptureActivityResultStub(): Instrumentation.ActivityResult? {
        val bundle = Bundle()
        bundle.putParcelable(
            "data",
            BitmapFactory.decodeResource(
                intentsTestRule.activity.resources,
                R.drawable.ic_main_art
            )
        )
        val resultData = Intent()
        resultData.putExtras(bundle)
        return Instrumentation.ActivityResult(Activity.RESULT_OK, resultData)
    }
}