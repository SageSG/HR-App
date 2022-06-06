package com.example.nodrama.view

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.animation.AnimationUtils
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.nodrama.R
import com.example.nodrama.viewmodel.ARTViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationBarView

class ArtHomeActivity : AppCompatActivity() {

    private lateinit var artRecyclerview: RecyclerView
    private lateinit var artAdapter: ArtAdapter
    lateinit var artViewModal: ARTViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_art_home)
        Log.d("ArtHomeActivity", "onCreate()")

        val sharedPreferences = getSharedPreferences("userData", Context.MODE_PRIVATE)
        val userId = sharedPreferences.getString("userid", null)!!

        artViewModal = ViewModelProvider(
            this,
            ViewModelProvider.AndroidViewModelFactory.getInstance(application)
        ).get(ARTViewModel::class.java)

        artRecyclerview = findViewById(R.id.recyclerViewArtResult)
        artRecyclerview.layoutManager = LinearLayoutManager(this)

        retrieveArtData(userId.toString())

        val submitArtButton = findViewById<Button>(R.id.submitArtButton)

        /**
         * On click listener to navigate to the new ART submission form
         */
        submitArtButton.setOnClickListener {
            val intent = Intent(this, ARTActivity::class.java)
            startActivity(intent)
        }

        /***
         * Bottom navigation logic
         */
        var fab = findViewById<FloatingActionButton>(R.id.navFab)
        fab.setOnClickListener {
            val navIntent = Intent(this, NFCActivity::class.java)
            startActivity(navIntent)
            /**
             * Rotating animation
             */
            val rotate_left = AnimationUtils.loadAnimation(
                this,
                R.anim.nfc_rotateleft
            )
            fab.startAnimation(rotate_left)
        }

        /**
         * Implicitly declare type of bottomNavigationView as NavigationBarView
         */
        var bottomNavigationView = findViewById<NavigationBarView>(R.id.bottomNavigationView)
        bottomNavigationView.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.miHome -> {
                    val navIntent = Intent(this, MainActivity::class.java)
                    startActivity(navIntent)
                    true
                }
                R.id.miAccount -> {
                    val navIntent = Intent(this, AccountActivity::class.java)
                    startActivity(navIntent)
                    true
                }
                else -> false
            }
        }
    }

    /**
     * Retrieve past ART submissions
     */
    fun retrieveArtData(userId : String){
        artViewModal.callARTFromView(userId)?.observe(this){
            it.let {
                artRecyclerview.adapter = ArtAdapter(it!!)
            }
        }
    }


}