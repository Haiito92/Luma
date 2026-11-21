package com.example.luma.data.local.guardian

sealed interface GuardiansResult {
    data class Success(val guardians: List<Guardian>) : GuardiansResult
    object Failed : GuardiansResult
    object NotSignIn : GuardiansResult
}