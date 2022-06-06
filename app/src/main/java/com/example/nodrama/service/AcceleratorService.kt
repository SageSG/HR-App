package com.example.nodrama.service

import android.app.Service
import android.content.Context
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Binder
import android.os.IBinder
import android.speech.tts.TextToSpeech
import android.util.Log
import android.widget.Toast
import java.util.*
import kotlin.math.abs
import kotlin.math.sqrt
import kotlin.random.Random

class AcceleratorService : Service(), SensorEventListener, TextToSpeech.OnInitListener {
    private val localBinder = LocalBinder()

    /**
     * Array of quotes to play
     */
    private var quoteList = arrayOf(
        "You’ve got to get up every morning with determination if you’re going to go to bed with satisfaction.",
        "Go as far as you can see; when you get there, you’ll be able to see further.",
        "Your talent determines what you can do. Your motivation determines how much you’re willing to do. Your attitude determines how well you do it.",
        "The individual who says it is not possible should move out of the way of those doing it.",
        "I learned this, at least, by my experiment; that if one advances confidently in the direction of his dreams, and endeavors to live the life which he has imagined, he will meet with a success unexpected in common hours.",
        "When someone tells me ‘no,’ it doesn’t mean I can’t do it, it simply means I can’t do it with them."
    )
    var sensorManager: SensorManager ?= null
    var sensor: Sensor? = null
    var accelerationCurrentValue: Double = 0.0
    var accelerationPreviousValue: Double = 0.0
    var changeInAcceleration: Double = 0.0
    var tts : TextToSpeech?= null

    /**
     * Provide bindng for the sensor Service
     */
    override fun onBind(intent: Intent): IBinder {
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        sensor = sensorManager!!.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        sensorManager?.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL)
        tts = TextToSpeech(this, this)
        Log.d(TAG, "SERVICE READY")
        return localBinder
    }

    /***
     * Randomize an integer for randomizing a random quote in the quote list.
     */
    private fun randomGenerator() : Int {
        return Random.nextInt(0,5)
    }

    /***
     * 1. Calculate the change in acceleration.
     * 2. If more than certain number, the quote will be played out.
     */
    override fun onSensorChanged(event: SensorEvent?) {
        val x = event!!.values[0].toString().toDouble()
        val y = event.values[1].toString().toDouble()
        val z = event.values[2].toString().toDouble()

        accelerationCurrentValue = sqrt(x * x + y * y + z * z)
        changeInAcceleration = abs(accelerationCurrentValue - accelerationPreviousValue)
        accelerationPreviousValue = accelerationCurrentValue

        if (changeInAcceleration > 14){
            tts!!.setPitch(1.4f)
            tts!!.speak(quoteList[randomGenerator()], TextToSpeech.QUEUE_FLUSH, null, "")
            Log.d("Accelerometer", "I LOVE YOU")
        }
    }

    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {
    }

    /**
     * To initialize the text to speech
     * @param status text to speech's status
     */
    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS){
            val result = tts!!.setLanguage(Locale.US)
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED){
                Toast.makeText(this, "The Language specified is not supported!", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(this, "Initialization Failed!", Toast.LENGTH_SHORT).show()
        }
    }

    inner class LocalBinder: Binder() {
        fun getService(): AcceleratorService = this@AcceleratorService
    }

    /**
     * Final call to destroy the activity when it is finished
     */
    override fun onDestroy() {
        if (tts != null){
            tts!!.stop()
            tts!!.shutdown()
        }
        super.onDestroy()
    }

    companion object{
        private val TAG = AcceleratorService::class.simpleName
    }
}