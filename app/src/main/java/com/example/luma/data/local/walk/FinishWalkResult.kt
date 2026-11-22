package com.example.luma.data.local.walk

sealed interface FinishWalkResult {
    data object Success : FinishWalkResult
    data object NoActiveWalk : FinishWalkResult
    data object Failed : FinishWalkResult
}