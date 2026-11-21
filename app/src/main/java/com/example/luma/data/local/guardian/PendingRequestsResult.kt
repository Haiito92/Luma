package com.example.luma.data.local.guardian

sealed interface PendingRequestsResult {
    data class Success(val requests: List<GuardianRequest>) : PendingRequestsResult
    object Failed : PendingRequestsResult
    object NotSignIn : PendingRequestsResult
}