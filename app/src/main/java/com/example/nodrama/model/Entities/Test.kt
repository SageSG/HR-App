package com.example.nodrama.model.Entities

import com.google.firebase.database.IgnoreExtraProperties

/**
 * Assign null default values to create a no-argument default constructor
 * which is needed for deserialisation from DataSnapshot
 */
@IgnoreExtraProperties
data class Test(val TestId: String? = null, val UserId: String? = null, val fullname: String? = null, val phone: String? = null, val hrw: String? = null, val result: String? = null, val time: String? = null) {
}

