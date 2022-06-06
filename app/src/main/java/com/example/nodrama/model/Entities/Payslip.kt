package com.example.nodrama.model.Entities

import com.google.firebase.database.IgnoreExtraProperties
import java.io.Serializable

/**
 * Serialize data to convert to a format that can be transferred and stored in the database
 */
@IgnoreExtraProperties
data class Payslip(
    var payslipId: String? = null,
    var period: String? = null,
    var description: String? = null,
    var date: String? = null,
    var fileName: String? = null
) : Serializable