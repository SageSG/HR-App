package com.example.nodrama.model.Entities

import com.google.firebase.database.IgnoreExtraProperties

/**
 * Assign null default values to create a no-argument default constructor
 * which is needed for deserialisation from DataSnapshot
 */
@IgnoreExtraProperties
data class RoomAuthentication(var RoomId: String? = null, val UserId: String? = null) {
}
