package com.vestel.remote

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.vestel.remote.ui.VestelViewModel
import com.vestel.remote.ui.components.RemoteScreen
import com.vestel.remote.ui.theme.RemoteBackground
import com.vestel.remote.ui.theme.VestelRemoteTheme

class MainActivity : ComponentActivity() {

    private val viewModel: VestelViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            VestelRemoteTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = RemoteBackground
                ) {
                    RemoteScreen(viewModel = viewModel)
                }
            }
        }
    }
}
