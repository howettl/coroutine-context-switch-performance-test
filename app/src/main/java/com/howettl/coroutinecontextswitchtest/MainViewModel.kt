package com.howettl.coroutinecontextswitchtest

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.howettl.coroutinecontextswitchtest.MainViewModel.RunState.Complete
import com.howettl.coroutinecontextswitchtest.MainViewModel.RunState.NotStarted
import com.howettl.coroutinecontextswitchtest.MainViewModel.RunState.Running
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainViewModel : ViewModel() {

    private val _viewState: MutableStateFlow<ViewState> = MutableStateFlow(ViewState())
    val viewState: StateFlow<ViewState>
        get() = _viewState.asStateFlow()

    private lateinit var testRunJob: Job

    fun startOrCancelRun() {
        when (viewState.value.runState) {
            is Complete, NotStarted -> testRunJob = performTestRun(viewState.value.useCoroutineContextSwitching)
            Running -> {
                testRunJob.cancel()
                _viewState.update { it.copy(runState = NotStarted) }
            }
        }
    }

    fun toggleUseContextSwitching() {
        _viewState.update {
            it.copy(
                useCoroutineContextSwitching = !it.useCoroutineContextSwitching
            )
        }
    }

    fun updateTotalIterations(newCount: Int) {
        _viewState.update { it.copy(totalIterations = newCount) }
    }

    private fun performTestRun(useContextSwitching: Boolean) = viewModelScope.launch {
        _viewState.update { it.copy(runState = Running) }
        val startValue = viewState.value.totalIterations
        val startTime = System.currentTimeMillis()
        if (useContextSwitching) {
            decrementWithSwitching(startValue)
        } else {
            decrementWithoutSwitching(startValue)
        }
        val delta = System.currentTimeMillis() - startTime
        _viewState.update { it.copy(runState = Complete(delta)) }
    }

    private fun decrementWithoutSwitching(@Suppress("SameParameterValue") startValue: Int) {
        var count = startValue
        while (count > 0) {
            count--
        }
    }

    private suspend fun decrementWithSwitching(startValue: Int) {
        var count = startValue
        while (count > 0) {
            withContext(Dispatchers.Default) {
                count--
            }
        }
    }

    data class ViewState(
        val runState: RunState = NotStarted,
        val useCoroutineContextSwitching: Boolean = true,
        val totalIterations: Int = 2000,
    )

    sealed interface RunState {
        data object NotStarted : RunState
        data object Running : RunState
        data class Complete(val duration: Long) : RunState
    }
}