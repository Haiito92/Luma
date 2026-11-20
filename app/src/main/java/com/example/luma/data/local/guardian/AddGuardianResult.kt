package com.example.luma.data.local.guardian

sealed interface AddGuardianResult {
    object Sent : AddGuardianResult
    object UserNotFound : AddGuardianResult
    object CannotAddSelf : AddGuardianResult
    object AlreadyAdded : AddGuardianResult
    object NotSignedIn : AddGuardianResult
    object NoConnection : AddGuardianResult
    object Failed : AddGuardianResult
}