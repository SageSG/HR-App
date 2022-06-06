package com.example.nodrama.model.Entities

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class LeaveBalances(
    var annual: Int = 0,
    var birthday: Int = 0,
    var family: Int = 0,
    var medical: Int = 0
)