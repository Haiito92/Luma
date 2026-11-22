package com.example.luma.ui.guardians

data class GuardedUiItemState(
    val walkerId: String,
    val fullName: String,
    val linkId: String,
    val sosWalkId: String? = null
){
    val hasActiveSos: Boolean
        get() = sosWalkId != null
}
