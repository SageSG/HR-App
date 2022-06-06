package com.example.nodrama.model.Entities

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class Leave(
    var leaveId: String? = null,
    var leaveType: String? = null,
    var startDate: String? = null,
    var endDate: String? = null,
    var daysTaken: Int = 0,
    var status: String? = null
)