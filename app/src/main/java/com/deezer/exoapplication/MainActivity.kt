package com.deezer.exoapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.deezer.exoapplication.player.presentation.PlayerScreen
import com.deezer.exoapplication.player.presentation.PlayerViewModel
import com.deezer.exoapplication.ui.theme.ExoAppTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ExoAppTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->

                    val viewModel = hiltViewModel<PlayerViewModel>()
                    val tracks by viewModel.uiState.collectAsStateWithLifecycle()

                    val singleAudioFilePickerLauncher = rememberLauncherForActivityResult(
                        contract = ActivityResultContracts.GetContent(),
                    ) { uri ->
                        uri?.let { viewModel.onTrackAdded(uri) }
                    }

                    PlayerScreen(
                        player = viewModel.player,
                        tracks = tracks,
                        onTrackSelected = { id -> viewModel.onTrackSelected(id) },
                        onTrackRemoved = { id -> viewModel.onTrackRemoved(id) },
                        onAddTrack = {
                            singleAudioFilePickerLauncher.launch(input = "audio/*")
                        },
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    )
                }
            }
        }
    }
}