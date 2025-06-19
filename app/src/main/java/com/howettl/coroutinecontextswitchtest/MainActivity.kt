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
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
            CoroutineContextSwitchTestTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Column(
                        Modifier
                            .padding(innerPadding)
                            .padding(24.dp)) {
                        val viewState = viewModel.viewState.collectAsState()
                        Row(
                            Modifier.clickable { viewModel.toggleUseContextSwitching() },
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Switch(
                                checked = viewState.value.useCoroutineContextSwitching,
                                onCheckedChange = { viewModel.toggleUseContextSwitching() }
                            )
                            Spacer(Modifier.width(8.dp))
                            Text("Enable coroutine context switching")
                        }
                        Spacer(Modifier.height(24.dp))
                        Button(onClick = viewModel::startRun, Modifier.align(Alignment.CenterHorizontally)) {
                            Text("Start!")
                        }
                        Spacer(Modifier.height(48.dp))
                        Row {
                            Text("Status:")
                            Spacer(Modifier.width(8.dp))
                            Text(
                                text = when (val result = viewState.value.runState) {
                                    is Complete -> "Complete! Total duration: ${result.duration}ms."
                                    NotStarted -> "Not started yet."
                                    Running -> "Running..."
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}