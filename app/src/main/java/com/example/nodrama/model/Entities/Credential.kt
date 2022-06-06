package com.example.nodrama.model.Entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Specify the name of the table
 */
@Entity(tableName = "credTable")

data class Credential (@ColumnInfo(name = "allow_biometric") val access: Boolean = false,
                       @ColumnInfo(name = "user_email") val email: String = "null",
                       @ColumnInfo(name = "user_password") val password: String = "null") {
    /**
     * 1. Specify the key and auto-generate as true
     * 2. Set initial value as 0
     */
    @PrimaryKey(autoGenerate = false) var id : Int = 0
}
