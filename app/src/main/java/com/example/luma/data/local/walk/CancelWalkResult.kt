package com.example.luma.data.local.walk

sealed interface CancelWalkResult {
    object Success : CancelWalkResult
}