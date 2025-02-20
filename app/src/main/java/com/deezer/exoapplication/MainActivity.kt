package com.deezer.exoapplication

import android.Manifest.permission.POST_NOTIFICATIONS
import android.content.Intent
import android.os.Build
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
import androidx.core.app.ActivityCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.media3.common.Player
import com.deezer.exoapplication.player.presentation.PlayerScreen
import com.deezer.exoapplication.player.presentation.PlayerViewModel
import com.deezer.exoapplication.ui.theme.ExoAppTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject lateinit var player: Player

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ActivityCompat.requestPermissions(this, arrayOf(POST_NOTIFICATIONS), 0)
        }
        startServiceIfNeeded()

        enableEdgeToEdge()
        setContent {
            ExoAppTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->

                    val viewModel = hiltViewModel<PlayerViewModel>()
                    val uiModel by viewModel.uiState.collectAsStateWithLifecycle()

                    val singleAudioFilePickerLauncher = rememberLauncherForActivityResult(
                        contract = ActivityResultContracts.GetContent(),
                    ) { uri ->
                        uri?.let { viewModel.onTrackAdded(uri) }
                    }

                    PlayerScreen(
                        player = player,
                        uiModel = uiModel,
                        onPause = viewModel::onPause,
                        onResume = viewModel::onResume,
                        onTrackSelected = viewModel::onTrackSelected,
                        onTrackRemoved = viewModel::onTrackRemoved,
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

    private fun startServiceIfNeeded() {
        if(MyMediaPlaybackService.isServiceRunning) return
        
        val intent = Intent(applicationContext, MyMediaPlaybackService::class.java)
        intent.action = MyMediaPlaybackService.Action.START.toString()
        startService(intent)
    }
}