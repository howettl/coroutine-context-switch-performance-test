package com.howettl.coroutinecontextswitchtest

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.howettl.coroutinecontextswitchtest.MainViewModel.RunState.Complete
import com.howettl.coroutinecontextswitchtest.MainViewModel.RunState.NotStarted
import com.howettl.coroutinecontextswitchtest.MainViewModel.RunState.Running
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {

    private val _viewState: MutableStateFlow<ViewState> = MutableStateFlow(ViewState())
    val viewState: StateFlow<ViewState>
        get() = _viewState.asStateFlow()

    fun startRun() {
        viewModelScope.launch {
            _viewState.update { it.copy(runState = Running) }
            val startTime = System.currentTimeMillis()
            delay(10000)
            val delta = System.currentTimeMillis() - startTime
            _viewState.update { it.copy(runState = Complete(delta)) }
        }
    }

    fun toggleUseContextSwitching() {
        _viewState.update {
            it.copy(
                useCoroutineContextSwitching = !it.useCoroutineContextSwitching
            )
        }
    }

    data class ViewState(
        val runState: RunState = NotStarted,
        val useCoroutineContextSwitching: Boolean = true,
    )

    sealed interface RunState {
        data object NotStarted : RunState
        data object Running : RunState
        data class Complete(val duration: Long) : RunState
    }
}