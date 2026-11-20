package com.example.luma.data.local

sealed interface CancelWalkResult {
    object Success : CancelWalkResult
}