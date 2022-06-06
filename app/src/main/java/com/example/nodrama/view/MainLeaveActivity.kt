package com.example.nodrama.view

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.nodrama.R
import com.example.nodrama.databinding.ActivityLeaveMainBinding
import com.example.nodrama.model.Entities.Leave
import com.example.nodrama.viewmodel.LeaveViewModel
import com.example.nodrama.viewmodel.LoginRegisterViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationBarView

class MainLeaveActivity : AppCompatActivity(), PendingLeaveAdapter.OnClickListener {
    private lateinit var binding: ActivityLeaveMainBinding
    private var loginRegisterViewModel: LoginRegisterViewModel? = null
    private var leaveViewModel: LeaveViewModel? = null
    private val pendingAdapter = PendingLeaveAdapter(this)
    private val pastAdapter = PastLeaveAdapter()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_leave_main)
        binding = ActivityLeaveMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        /***
         * Initialize instance of a view model
         */
        loginRegisterViewModel = ViewModelProvider(this).get(
            LoginRegisterViewModel::class.java
        )

        /***
         * Initialize instance of a view model
         */
        leaveViewModel = ViewModelProvider(this).get(
            LeaveViewModel::class.java
        )

        binding.btnApplyLeave.setOnClickListener{
            val intent = Intent(this, ApplyLeaveActivity::class.java)
            startActivity(intent)
        }

        /**
         * Call functions to populate current leave, pending leave and past leave balances
         */
        populateLeaveBalances()
        populatePendingLeaves()
        populatePastLeaves()


        /***
         * Bottom navigation logic
         */
        var fab = findViewById<FloatingActionButton>(R.id.navFab)
        fab.setOnClickListener {
            val navIntent = Intent(this, NFCActivity::class.java)
            startActivity(navIntent)
            // mini rotating animation -- fun stuff :>
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

    /***
     * Populate the text view of all the different types of leave types' balances
     */
    private fun populateLeaveBalances() {
        loginRegisterViewModel?.getUserDetailsLiveData()?.observe(this) {
            leaveViewModel?.getUserLeaveBalances(it?.userId.toString())
                ?.observe(this) { leaveBalances ->
                    if (leaveBalances != null) {
                        binding.textViewAnnualBalance.text = leaveBalances.annual.toString()
                        binding.textViewMedicalBalance.text = leaveBalances.medical.toString()
                        binding.textViewFamilyBalances.text = leaveBalances.family.toString()
                        binding.textViewBirthdayBalance.text = leaveBalances.birthday.toString()
                    }
                }
        }
    }

    /**
     * Populate user's pending leaves
     */
    private fun populatePendingLeaves() {
        val decoration = DividerItemDecoration(this, DividerItemDecoration.VERTICAL)
        binding.recyclerViewPendingLeave.addItemDecoration(decoration)
        binding.recyclerViewPendingLeave.adapter = pendingAdapter
        binding.recyclerViewPendingLeave.layoutManager = LinearLayoutManager(this)
        loginRegisterViewModel?.getUserDetailsLiveData()?.observe(this) {
            if (it != null) {
                leaveViewModel?.getPendingLeaves(it.userId.toString())?.observe(this) { leaveList ->
                    leaveList?.let { pendingAdapter.submitList(leaveList) }
                }
            }
        }
    }

    override fun onItemClick(leave: Leave?) {
        Log.d("OnItemClicked", leave.toString())
        createNewDialog(leave)

    }

    /***
     * create dialog
     * @param isSuccess checks whether the room is authenticated or not
     * @param roomId returns the room name to be displayed on the dialog
     */
    fun createNewDialog(leave: Leave?) {
        var view = View.inflate(this@MainLeaveActivity, R.layout.popup_confirmation_leave, null)
        val builder = AlertDialog.Builder(this@MainLeaveActivity)
        builder.setView(view)
        val dialog = builder.create()
        dialog.show()
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        var btnConfirm = dialog.findViewById<Button>(R.id.btnConfirm)
        var btnCancel = dialog.findViewById<Button>(R.id.btnCancelLeave)
        dialog.setCancelable(false)
        loginRegisterViewModel?.getUserDetailsLiveData()?.observe(this) { user ->
            btnConfirm.setOnClickListener {
                if (it != null) {
                    if (leave != null) {
                        leaveViewModel?.deleteLeave(user?.userId.toString(), leave)
                        val intent = intent
                        finish()
                        startActivity(intent)
                        dialog.dismiss()
                    }
                }
            }
        }
        btnCancel.setOnClickListener{
            dialog.dismiss()
        }
    }

    /**
     * Populate user's past leaves
     */
    private fun populatePastLeaves() {
        val decoration = DividerItemDecoration(this, DividerItemDecoration.VERTICAL)
        binding.recyclerViewPastLeave.addItemDecoration(decoration)
        binding.recyclerViewPastLeave.adapter = pastAdapter
        binding.recyclerViewPastLeave.layoutManager = LinearLayoutManager(this)
        loginRegisterViewModel?.getUserDetailsLiveData()?.observe(this) {
            if (it != null) {
                leaveViewModel?.getPastLeaves(it.userId.toString())?.observe(this) { leaveList ->
                    leaveList?.let { pastAdapter.submitList(leaveList) }
                }
            }
        }
    }

}

