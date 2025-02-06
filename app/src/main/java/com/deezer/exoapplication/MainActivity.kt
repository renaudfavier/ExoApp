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
import com.deezer.exoapplication.player.PlayerScreen
import com.deezer.exoapplication.player.PlayerViewModel
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
                    ) {
                        viewModel.onTrackAdd(it)
                    }

                    PlayerScreen(
                        tracks = tracks,
                        onTrackSelected = { id -> viewModel.onTrackSelected(id) },
                        onTrackRemove = { id -> viewModel.onTrackRemove(id) },
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