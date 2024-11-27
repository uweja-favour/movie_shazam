package com.codr.movieshazam.ui.presentation.recording

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.DropdownMenu
import androidx.compose.material.Surface
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.FiberManualRecord
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.codr.movieshazam.playback.AndroidAudioPlayer
import com.codr.movieshazam.playback.AudioPlayer
import com.codr.movieshazam.record.AudioRecorder
import com.codr.movieshazam.ui.theme.AppBackGround
import com.codr.movieshazam.ui.theme.MEDIUM_PADDING
import com.codr.movieshazam.ui.theme.SMALL_PADDING
import com.codr.movieshazam.ui.theme.TextColor
import com.codr.movieshazam.ui.util.font.Poppins

private val searchAndFind = DropDownItem(
    title = "Search and find",
    icon = Icons.Default.ArrowUpward
)

private val recordAndSave = DropDownItem(
    title = "Record and save",
    icon = Icons.Default.Save,
)


@Composable
fun RecordingScreen(
    viewModel: RSViewModel
) {

    var actionSelected: DropDownItem by remember {
        mutableStateOf(recordAndSave)
    }
    val context = LocalContext.current

    fun handleActionSelected(it: DropDownItem) {
        actionSelected = it
        Toast.makeText(context, "${it.title} mode selected", Toast.LENGTH_SHORT).show()
    }

    Scaffold(
        topBar = {
            AppBar(
                selectedAction = actionSelected,
                onActionSelected = { handleActionSelected(it) }
            )
        }
    ) { paddingValues ->
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            color = AppBackGround
        ) {
            // Add your screen content here
            MainContent(
                viewModel = viewModel
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AppBar(
    selectedAction: DropDownItem,
    onActionSelected: (DropDownItem) -> Unit
) {
    var expandDropDownMenu by rememberSaveable { mutableStateOf(false) }

    TopAppBar(
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "Record",
                    fontFamily = Poppins
                )
            }
        },
        actions = {
           IconButton(
               onClick = {
                   expandDropDownMenu = !expandDropDownMenu
               }
           ) {
               Icon(
                   imageVector = Icons.Default.MoreVert,
                   contentDescription = null
               )
               AppBarDropDownMenu(
                   expandDropDownMenu = expandDropDownMenu,
                   selectedAction = selectedAction,
                   onToggleDropDownMenu = { expandDropDownMenu = it},
                   onActionSelected = onActionSelected
               )
           }
        },
    )
}

data class DropDownItem(
    val title: String,
    val icon: ImageVector,
)


@Composable
private fun AppBarDropDownMenu(
    expandDropDownMenu: Boolean,
    selectedAction: DropDownItem,
    onToggleDropDownMenu: (Boolean) -> Unit,
    onActionSelected: (DropDownItem) -> Unit
) {

    val actionsList = listOf(
        recordAndSave,
        searchAndFind,
    )

    DropdownMenu(
        expanded = expandDropDownMenu,
        modifier = Modifier.background(Color.DarkGray),
        onDismissRequest = { onToggleDropDownMenu(false) },
    ) {
        actionsList.forEach {
            DropdownMenuItem(
                text = {
                    Text(
                        text = it.title,
                        fontFamily = Poppins,
                        color = TextColor
                    )
                },
                onClick = {
                    onActionSelected(it)
                },
                leadingIcon = {
                    Icon(
                        imageVector = it.icon,
                        contentDescription = null
                    )
                },
                trailingIcon = {
                    if (selectedAction == it) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                        )
                    }
                }
            )
        }
    }
}


@Composable
fun MainContent(
    viewModel: RSViewModel
) {
    val listOfRecordings by viewModel.listOfRecordings.collectAsState()

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize()
        ) {
            items(listOfRecordings) { recording ->
                ListItem(
                    trailingContent = {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = null
                        )
                    },
                    headlineContent = {
                        Text(
                            text = recording.fileName,
                            fontFamily = Poppins
                        )
                    },
                    supportingContent = {
                        Text(
                            text = recording.dateAdded,
                            fontFamily = Poppins
                        )
                    },
                )
            }
        }

        AudioControlButtons(
            modifier = Modifier.align(Alignment.BottomCenter),
            viewModel = viewModel
        )
    }

}



@Composable
fun AudioControlButtons(
    modifier: Modifier = Modifier,
    viewModel: RSViewModel
) {
    // UI state tracking
    val isRecording by viewModel.isRecording.collectAsState()
    val isPlaying by viewModel.isPlaying.collectAsState()

    // Derived state to manage button availability
    val canPerformAction = !isRecording && !isPlaying

    // Screen and context utilities
    val screenHeight = LocalConfiguration.current.screenHeightDp
    val context = LocalContext.current
    val cacheDir = context.cacheDir

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height((screenHeight * 0.24).dp)
            .background(Color.Black)
            .padding(MEDIUM_PADDING.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        // Record Button
        CircularButton(
            icon = Icons.Default.FiberManualRecord,
            contentDescription = "Start Recording",
            color = Color.Red,
            onClick = {
                if (canPerformAction) {
                    viewModel.startRecording(cacheDir,"audio.mp3") // Provide filename explicitly
                    Toast.makeText(context, "Recording started", Toast.LENGTH_SHORT).show()
                }
            }
        )

        // Stop Recording Button
        CircularButton(
            icon = Icons.Default.Stop,
            contentDescription = "Stop Recording",
            color = Color.Yellow,
            onClick = {
                if (isRecording) {
                    viewModel.stopRecording()
                    Toast.makeText(context, "Recording stopped", Toast.LENGTH_SHORT).show()
                }
            }
        )

        // Play Button
        CircularButton(
            icon = Icons.Default.PlayArrow,
            contentDescription = "Play Recording",
            color = Color.Green,
            onClick = {
                if (canPerformAction) {
                    viewModel.play()
                    Toast.makeText(context, "Playing recording", Toast.LENGTH_SHORT).show()
                }
            }
        )

        // Pause Playback Button
        CircularButton(
            icon = Icons.Default.Pause,
            contentDescription = "Pause Playback",
            color = Color.Blue,
            onClick = {
                if (isPlaying) {
                    viewModel.pausePlaying() // Assuming pause functionality exists in ViewModel
                    Toast.makeText(context, "Playback paused", Toast.LENGTH_SHORT).show()
                }
            }
        )
    }
}




@Composable
fun CircularButton(
    icon: ImageVector,
    contentDescription: String?,
    color: Color,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .size(64.dp)
            .clip(CircleShape),
        shape = CircleShape,
        colors = ButtonDefaults.buttonColors(color)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = Color.White,
            modifier = Modifier.size(32.dp)
        )
    }
}