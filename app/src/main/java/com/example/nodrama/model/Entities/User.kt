package com.example.nodrama.model.Entities

import com.google.firebase.database.IgnoreExtraProperties

/**
 * Assign null default values to create a no-argument default constructor
 * which is needed for deserialisation from DataSnapshot
 */
@IgnoreExtraProperties
data class User(var userId: String? = null, var fullname: String? = null, var email : String? = null, var age: String? = null, var phoneNum : String? = null, var fingerprint: Boolean = false) {
}
