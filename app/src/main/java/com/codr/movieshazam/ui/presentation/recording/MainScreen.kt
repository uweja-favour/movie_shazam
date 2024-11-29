package com.codr.movieshazam.ui.presentation.recording

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.FiberManualRecord
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.codr.movieshazam.EventController
import com.codr.movieshazam.ObserveAsEvent
import com.codr.movieshazam.ui.theme.AppBackGround
import com.codr.movieshazam.ui.theme.LARGE_PADDING
import com.codr.movieshazam.ui.theme.MEDIUM_PADDING
import com.codr.movieshazam.ui.util.Constants.PLAYBACK_COMPLETE
import com.codr.movieshazam.ui.util.Constants.RECORDING
import com.codr.movieshazam.ui.util.Constants.SNACK_BAR_EVENT
import com.codr.movieshazam.ui.util.getCurrentMillis
import kotlinx.coroutines.launch


@Composable
fun MainScreen(
    viewModel: RSViewModel,
    paddingValues: PaddingValues
) {

    val noOfCheckedItems by viewModel.noOfCheckedItems.collectAsState()
    val context = LocalContext.current

    val isRecording by viewModel.isRecording.collectAsState()

    BackHandler(enabled = isRecording) {
        if (isRecording) {
            viewModel.onBackPressFromRecordingScreen()
        }
    }

    BackHandler(enabled = noOfCheckedItems >= 1) {
        viewModel.toggleAllItemsAsChecked(false)
    }

    Surface(
        modifier = Modifier
            .fillMaxSize(),
        color = AppBackGround
    ) {

        AnimatedVisibility(visible = !isRecording) {
            MainContent(
                viewModel = viewModel,
                paddingValues = paddingValues,
                context = context
            )
        }

        AnimatedVisibility(visible = isRecording) {
            RecordingScreen() {
                if (isRecording) {
                    viewModel.stopRecording()
                    Toast.makeText(context, "Recording stopped", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}




@Composable
fun MainContent(
    viewModel: RSViewModel,
    paddingValues: PaddingValues,
    context: Context
) {
    val listOfRecordings by viewModel.listOfRecordings.collectAsState()
    val isPlaying by viewModel.isPlaying.collectAsState()
    val isRecording by viewModel.isRecording.collectAsState()
    val canPerformAction = !isPlaying && !isRecording
    val screenHeight = LocalConfiguration.current.screenHeightDp

    var isSelectable by remember {
        mutableStateOf(false)
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            items(listOfRecordings, key = { it.id }) { recording -> // Use a unique `id` for the key
                Spacer(modifier = Modifier.height(8.dp))
                CustomListItem(
                    viewModel = viewModel,
                    item = recording,
                    title = recording.fileName,
                    subtitle = recording.dateAdded,
                    isChecked = recording.isChecked,
                    onCheckedChange = { isChecked, item ->
                        viewModel.toggleItemChecked(item, isChecked)
                    }
                )
                Spacer(modifier = Modifier.height(8.dp))
                HorizontalDivider()
            }

            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height((screenHeight / 3.2).dp)
                )
            }
        }

        ControlButtons(
            modifier = Modifier.align(Alignment.BottomCenter),
            paddingValues = paddingValues,
            viewModel = viewModel
        )
    }
}



@Composable
fun ControlButtons(
    modifier: Modifier = Modifier,
    paddingValues: PaddingValues,
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

    val noOfCheckedItems by viewModel.noOfCheckedItems.collectAsState()

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = (screenHeight * .08).dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {

        // Record Button
        if (noOfCheckedItems < 1) {
            CircularButton(
                icon = Icons.Default.FiberManualRecord,
                contentDescription = "Start Recording",
                color = Color.Red,
                onClick = {
                    if (canPerformAction) {
                        viewModel.startRecording(cacheDir,"$RECORDING ${getCurrentMillis()}.mp3") // Provide filename explicitly
                        Toast.makeText(context, "Recording started", Toast.LENGTH_SHORT).show()
                    }
                }
            )
        } else {
            CircularButton(
                icon = Icons.Default.Delete,
                contentDescription = "Start Recording",
                color = Color.Red,
                increaseIconSize = true,
                onClick = {
                    viewModel.deleteCheckedItems()
                }
            )
        }
    }
}



@Composable
fun CircularButton(
    icon: ImageVector,
    contentDescription: String?,
    color: Color,
    increaseIconSize: Boolean = false,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .size(64.dp) // Size of the button
            .clip(CircleShape),
        shape = CircleShape,
        colors = ButtonDefaults.buttonColors(color),
        contentPadding = PaddingValues(
            if (increaseIconSize) {
                16.dp
            } else 0.dp
        ) // Remove default padding
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = Color.White,
            modifier = if (increaseIconSize) Modifier.fillMaxSize() else Modifier.size(24.dp) // Adjust icon size
        )
    }
}


//
//// Stop Recording Button
//CircularButton(
//icon = Icons.Default.Stop,
//contentDescription = "Stop Recording",
//color = Color.Yellow,
//onClick = {
//    if (isRecording) {
//        viewModel.stopRecording()
//        Toast.makeText(context, "Recording stopped", Toast.LENGTH_SHORT).show()
//    }
//}
//)
//
//// Play Button
//CircularButton(
//icon = Icons.Default.PlayArrow,
//contentDescription = "Play Recording",
//color = Color.Green,
//onClick = {
//    if (canPerformAction) {
//        viewModel.play()
//        Toast.makeText(context, "Playing recording", Toast.LENGTH_SHORT).show()
//    }
//}
//)
//
//// Pause Playback Button
//CircularButton(
//icon = Icons.Default.Pause,
//contentDescription = "Pause Playback",
//color = Color.Blue,
//onClick = {
//    if (isPlaying) {
//        viewModel.pausePlaying() // Assuming pause functionality exists in ViewModel
//        Toast.makeText(context, "Playback paused", Toast.LENGTH_SHORT).show()
//    }
//}
//)









//                ListItem(
//                    modifier = Modifier
//                        .combinedClickable(
//                            onClick = {
//                                if (isSelectable) {
//                                    viewModel.toggleItemAsChecked(item = recording, isChecked = !recording.isChecked)
//                                }
//                            },
//                            onLongClick = {
//                                isSelectable = true
//                                viewModel.toggleItemAsChecked(item = recording, isChecked = !recording.isChecked)
//                            }
//                        ),
//                    colors = ListItemDefaults.colors(MaterialTheme.colorScheme.onTertiary),
//                    leadingContent = {
//                        Icon(
//                            imageVector = Icons.Default.Mic,
//                            contentDescription = "microphone"
//                        )
//                    },
//                    trailingContent = {
//                        when {
//                            isSelectable -> {
//                                Checkbox(
//                                    onCheckedChange = { isChecked ->
//                                        viewModel.toggleItemAsChecked(item = recording, isChecked = isChecked)
//                                    },
//                                    checked = recording.isChecked,
//                                    // add a modifier
//                                )
//                            }
//                            else -> {
//                                Icon(
//                                    imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
//                                    contentDescription = null,
//                                    modifier = Modifier.pointerInput(Unit){
//                                        detectTapGestures(
//                                            onTap = {
//                                                when {
//                                                    !viewModel.isPlaying.value -> {
//                                                        viewModel.play()
//                                                    }
//                                                    else -> {
//                                                        viewModel.pausePlaying()
//                                                    }
//                                                }
//                                            }
//                                        )
//                                    }
//                                )
//                            }
//                        }
//                    },
//                    headlineContent = {
//                        Text(
//                            text = recording.fileName,
//                            fontFamily = Poppins,
//                            overflow = TextOverflow.Ellipsis
//                        )
//                    },
//                    supportingContent = {
//                        Text(
//                            text = recording.dateAdded,
//                            fontFamily = Poppins,
//                            overflow = TextOverflow.Ellipsis
//                        )
//                    },
//                )