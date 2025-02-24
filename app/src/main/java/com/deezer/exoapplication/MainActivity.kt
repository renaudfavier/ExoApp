package com.deezer.exoapplication

import android.Manifest.permission.POST_NOTIFICATIONS
import android.content.Intent
import android.net.Uri
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContent {
            ExoAppTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->

                    val viewModel = hiltViewModel<PlayerViewModel>()
                    val uiModel by viewModel.uiState.collectAsStateWithLifecycle()

                    val audioFilePickerLauncher = audioFilePickerLauncher { viewModel.onTrackAdded(it) }
                    val permission = notificationPermissionPickerLauncher()

                    LaunchedEffect(Unit) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                            permission.launch(POST_NOTIFICATIONS)
                        }
                    }

                    PlayerScreen(
                        uiModel = uiModel,
                        onPause = viewModel::onPause,
                        onResume = viewModel::onResume,
                        onTrackSelected = viewModel::onTrackSelected,
                        onTrackRemoved = viewModel::onTrackRemoved,
                        onAddTrack = {
                            audioFilePickerLauncher.launch(input = "audio/*")
                        },
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    )
                }
            }
        }
    }

    @Composable
    private fun audioFilePickerLauncher(onResult: (Uri) -> Unit) =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.GetContent(),
        ) { uri ->
            uri?.let { onResult(uri) }
        }


    @Composable
    private fun notificationPermissionPickerLauncher() = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            if (!isGranted) {
                finish()
            } else {
                startServiceIfNeeded()
            }
        }
    )

    private fun startServiceIfNeeded() {
        if(MyMediaPlaybackService.isServiceRunning) return
        
        val intent = Intent(applicationContext, MyMediaPlaybackService::class.java)
        intent.action = MyMediaPlaybackService.Action.START.toString()
        startService(intent)
    }
}