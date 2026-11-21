package com.example.luma.data.local.walk

sealed interface FinishWalkResult {
    object Success : FinishWalkResult
    object NoActiveWalk : FinishWalkResult
    object Failed : FinishWalkResult
}