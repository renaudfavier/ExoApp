package com.deezer.exoapplication.player

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.deezer.exoapplication.ui.theme.ExoAppTheme

data class TrackUiModel(val id: Int, val title: String, val isSelected: Boolean)

@Composable
fun PlayerScreen(
    tracks: List<TrackUiModel>,
    onTrackSelected: (Int) -> Unit,
    onTrackRemove: (Int) -> Unit,
    onAddTrack: () -> Unit,
    modifier: Modifier = Modifier
) = Column(
    modifier = modifier.background(Color.Blue)
) {
    Box(modifier = Modifier
        .fillMaxWidth()
        .weight(1f)) {
        Playlist(
            tracks = tracks,
            onTrackSelected = onTrackSelected,
            onTrackRemove = onTrackRemove,
            modifier = Modifier.fillMaxSize()
        )

        FloatingActionButton(
            onClick = onAddTrack,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Add",
            )
        }
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(72.dp)
    )
}


@Composable
fun Playlist(
    tracks: List<TrackUiModel>,
    onTrackSelected: (Int) -> Unit,
    onTrackRemove: (Int) -> Unit,
    modifier: Modifier = Modifier
) = LazyColumn(modifier.background(Color.Red)) {
    items(tracks) { track ->
        Track(
            title = track.title,
            isSelected = track.isSelected,
            onSelected = { onTrackSelected(track.id) },
            onRemove = { onTrackRemove(track.id) },
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
fun Track(
    title: String,
    isSelected: Boolean,
    onSelected: () -> Unit,
    onRemove: () -> Unit,
    modifier: Modifier = Modifier
) = Row(
    modifier = modifier
        .background(
            if (isSelected) Color.DarkGray
            else Color.Transparent
        ),
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.SpaceBetween
) {
    Button(
        onClick = onSelected,
        modifier = Modifier.weight(1f),
        colors = ButtonDefaults.textButtonColors()
    ) {
        Text(
            text = title,
            color = if (isSelected) Color.White
            else Color.Black,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Start
        )
    }

    IconButton(onRemove) {
        Icon(
            imageVector = Icons.Default.Clear,
            contentDescription = "Clear",
        )
    }

}

@Preview
@Composable
private fun PlayerScreenPreview() {

    val fakeTracks = listOf(
        TrackUiModel(1, "Track 1", false),
        TrackUiModel(2, "Track 2", false),
        TrackUiModel(3, "Track 3", true),
        TrackUiModel(4, "Track 4", false),
    )

    ExoAppTheme {
        PlayerScreen(
            tracks = fakeTracks,
            onTrackRemove = {},
            onTrackSelected = {},
            onAddTrack = {},
            modifier = Modifier.fillMaxSize()
        )
    }
}
