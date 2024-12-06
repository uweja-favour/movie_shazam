package com.codr.movieshazam.ui.presentation.recording

import android.os.Build
import androidx.activity.compose.BackHandler
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.FiberManualRecord
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import com.codr.movieshazam.data.Recording
import com.codr.movieshazam.ui.theme.AppBackGround
import com.codr.movieshazam.ui.theme.SMALL_PADDING
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
fun MainScreen(
    viewModel: RSViewModel
) {

    val noOfCheckedItems by viewModel.noOfCheckedItems.collectAsState()
    val listOfRecordings by viewModel.listOfRecordings.collectAsState()

    val isRecording by viewModel.isRecording.collectAsState()
    val isPlaying by viewModel.isPlaying.collectAsState()

    BackHandler(enabled = isRecording, onBack = viewModel::stopRecording)
    BackHandler(enabled = noOfCheckedItems >= 1, onBack = viewModel::toggleAllItemsAsChecked)

    Surface(
        modifier = Modifier
            .fillMaxSize(),
        color = AppBackGround
    ) {

        AnimatedVisibility(visible = !isRecording) {
            MainContent(
                listOfRecordings = listOfRecordings,
                noOfCheckedItems = noOfCheckedItems,
                isRecording = isRecording,
                isPlaying = isPlaying,
                onCheckedChanged = { isChecked, item -> viewModel.toggleItemChecked(isChecked, item) },
                onRecord = viewModel::startRecording,
                onDelete = viewModel::deleteCheckedItems
            )
        }

        AnimatedVisibility(visible = isRecording) {
            RecordingScreen(onStopRecording = viewModel::stopRecording)
        }
    }
}


@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
private fun MainContent(
    listOfRecordings: List<Recording>,
    noOfCheckedItems: Int,
    isRecording: Boolean,
    isPlaying: Boolean,
    onCheckedChanged: (Boolean, Recording) -> Unit,
    onRecord: () -> Unit,
    onDelete: () -> Unit,
) {
    val canPerformAction = !isPlaying && !isRecording
    val screenHeight = LocalConfiguration.current.screenHeightDp

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
                    item = recording,
                    noOfCheckedItems = noOfCheckedItems,
                    title = recording.fileName,
                    subtitle = recording.dateAdded,
                    isChecked = recording.isChecked,
                    onCheckedChange = { isChecked, item ->
                        onCheckedChanged(isChecked, item)
                    }
                )
                Spacer(modifier = Modifier.height(SMALL_PADDING.dp))
            }

            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height((screenHeight / 4.2).dp)
                )
            }
        }

        ControlButtons(
            modifier = Modifier.align(Alignment.BottomCenter),
            noOfCheckedItems = noOfCheckedItems,
            isRecording = isRecording,
            isPlaying = isPlaying,
            onRecord = onRecord,
            onDelete = onDelete,
        )
    }
}



@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@OptIn(ExperimentalPermissionsApi::class)
@Composable
private fun ControlButtons(
    modifier: Modifier = Modifier,
    noOfCheckedItems: Int,
    isRecording: Boolean,
    isPlaying: Boolean,
    onRecord: () -> Unit,
    onDelete: () -> Unit,
) {
    // Derived state to manage button availability
    val canPerformAction = !isRecording && !isPlaying

    // Screen and context utilities
    val screenHeight = LocalConfiguration.current.screenHeightDp
    val postNotificationPermissionState = rememberPermissionState(
        permission = android.Manifest.permission.POST_NOTIFICATIONS
    )

    val audioRecordingPermissionState = rememberPermissionState(
        permission = android.Manifest.permission.RECORD_AUDIO
    )

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
                    when {
                        !canPerformAction -> return@CircularButton
                        postNotificationPermissionState.status.isGranted.not() -> postNotificationPermissionState.launchPermissionRequest()
                        audioRecordingPermissionState.status.isGranted -> onRecord()
                        else -> audioRecordingPermissionState.launchPermissionRequest()
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
                    onDelete()
                }
            )
        }
    }
}



@Composable
private fun CircularButton(
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