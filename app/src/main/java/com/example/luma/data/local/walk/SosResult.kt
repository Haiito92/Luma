package com.example.luma.data.local.walk

interface SosResult {
    data object Success : SosResult
    data object AlreadySent : SosResult
    data object NoActiveWalk : SosResult
    data object Failed : SosResult
}