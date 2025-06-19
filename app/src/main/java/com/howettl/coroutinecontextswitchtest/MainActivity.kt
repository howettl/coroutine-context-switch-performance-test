package com.howettl.coroutinecontextswitchtest

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.howettl.coroutinecontextswitchtest.MainViewModel.RunState.Complete
import com.howettl.coroutinecontextswitchtest.MainViewModel.RunState.NotStarted
import com.howettl.coroutinecontextswitchtest.MainViewModel.RunState.Running
import com.howettl.coroutinecontextswitchtest.ui.theme.CoroutineContextSwitchTestTheme

class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels<MainViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val viewState = viewModel.viewState.collectAsState()
            Content(
                runState = viewState.value.runState,
                useCoroutineContextSwitching = viewState.value.useCoroutineContextSwitching,
                toggleUseContextSwitching = viewModel::toggleUseContextSwitching,
                startOrCancelRun = viewModel::startOrCancelRun,
                totalIterations = viewState.value.totalIterations,
                onTotalIterationsUpdated = viewModel::updateTotalIterations,
            )
        }
    }

    @Composable
    private fun Content(
        modifier: Modifier = Modifier,
        runState: MainViewModel.RunState,
        useCoroutineContextSwitching: Boolean,
        startOrCancelRun: () -> Unit = {},
        toggleUseContextSwitching: () -> Unit = {},
        totalIterations: Int,
        onTotalIterationsUpdated: (Int) -> Unit = {},
    ) {
        CoroutineContextSwitchTestTheme {
            Scaffold(modifier = modifier.fillMaxSize()) { innerPadding ->
                Column(
                    Modifier
                        .padding(innerPadding)
                        .padding(24.dp)
                ) {
                    val settingsChangeEnabled = runState == NotStarted || runState is Complete
                    Row(
                        Modifier.clickable(
                            enabled = settingsChangeEnabled,
                            onClick = toggleUseContextSwitching,
                        ),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text("Enable coroutine context switching")
                        Spacer(Modifier.width(8.dp))
                        Switch(
                            checked = useCoroutineContextSwitching,
                            onCheckedChange = { toggleUseContextSwitching() },
                            enabled = settingsChangeEnabled,
                        )
                    }
                    Spacer(Modifier.height(12.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("Total iterations")
                        Spacer(Modifier.width(8.dp))
                        TextField(
                            value = totalIterations.toString(),
                            onValueChange = { onTotalIterationsUpdated(it.toInt()) },
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Number
                            )
                        )
                    }
                    Spacer(Modifier.height(24.dp))
                    Button(onClick = startOrCancelRun) {
                        Text(
                            when (runState) {
                                is Complete, NotStarted -> "Start!"
                                Running -> "Cancel"
                            }
                        )
                    }
                    Spacer(Modifier.height(48.dp))
                    Text(
                        text = when (runState) {
                            is Complete -> "Status: Complete! Total duration: ${runState.duration}ms."
                            NotStarted -> "Status: Not started yet."
                            Running -> "Status: Running..."
                        }
                    )
                }
            }
        }
    }

    @Composable
    @Preview
    private fun ContentPreview() {
        Content(
            runState = NotStarted,
            useCoroutineContextSwitching = true,
            totalIterations = 2000,
        )
    }
}