package com.example.taskflowmobile2

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.taskflowmobile2.navigation.TaskFlowNavGraph
import com.example.taskflowmobile2.ui.theme.TaskFlowMobile2Theme
import com.example.taskflowmobile2.ui.viewmodel.TaskViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val viewModel: TaskViewModel = hiltViewModel()
            val uiState by viewModel.uiState.collectAsState()

            TaskFlowMobile2Theme(darkTheme = uiState.isDarkMode) {
                Surface(modifier = Modifier.fillMaxSize()) {
                    TaskFlowNavGraph(viewModel = viewModel)
                }
            }
        }
    }
}
