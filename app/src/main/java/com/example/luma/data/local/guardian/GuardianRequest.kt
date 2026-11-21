package com.example.luma.data.local.guardian

data class GuardianRequest(
    val linkId: String,
    val walkerName: String,
    val walkerLastName: String,
    val createdAtMillis: Long
)
