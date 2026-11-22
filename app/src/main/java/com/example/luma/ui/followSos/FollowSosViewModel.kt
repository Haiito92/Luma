package com.example.luma.ui.followSos

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.luma.data.local.guardian.FollowedPerson
import com.example.luma.data.local.walk.ObserveWalkResult
import com.example.luma.data.local.walk.WalkRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

typealias FollowedPerson = FollowedPerson

class FollowSosViewModel(
    private val walkRepository: WalkRepository,
) : ViewModel() {

    private val _state = MutableStateFlow<FollowSosUiState>(FollowSosUiState.Loading)
    val state: StateFlow<FollowSosUiState> = _state.asStateFlow()

    private var observeJob: Job? = null

    fun observeWalk(walkId: String){

        observeJob?.cancel()

        observeJob = viewModelScope.launch {
            walkRepository.observeWalk(walkId).collect { result ->
                _state.value = when (result) {
                    is ObserveWalkResult.Success -> FollowSosUiState.Success(result.walk)
                    is ObserveWalkResult.WalkNotFound -> FollowSosUiState.WalkNotFound
                    is ObserveWalkResult.Failed -> FollowSosUiState.Error
                }
            }
        }
    }
}
