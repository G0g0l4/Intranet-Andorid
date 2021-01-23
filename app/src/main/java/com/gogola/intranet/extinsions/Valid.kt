package com.gogola.intranet.extinsions

import java.io.Serializable

data class Valid(
    val success: Boolean,
    val message: String
) : Serializable