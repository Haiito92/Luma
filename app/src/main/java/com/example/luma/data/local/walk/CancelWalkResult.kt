package com.example.luma.data.local.walk

sealed interface CancelWalkResult {
    data object Success : CancelWalkResult
    data object NoActiveWalk : CancelWalkResult
    data object Failed : CancelWalkResult
}