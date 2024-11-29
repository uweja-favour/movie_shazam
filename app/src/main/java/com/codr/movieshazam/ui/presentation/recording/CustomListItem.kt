package com.codr.movieshazam.ui.presentation.recording

import android.util.Log
import androidx.compose.animation.animateColor
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.codr.movieshazam.data.Recording
import com.codr.movieshazam.ui.theme.MEDIUM_PADDING
import com.codr.movieshazam.ui.theme.MEDIUM_SMALL_PADDING
import com.codr.movieshazam.ui.theme.SMALL_PADDING

@Composable
fun CustomListItem(
    viewModel: RSViewModel,
    item: Recording,
    title: String,
    subtitle: String,
    isChecked: Boolean,
    onCheckedChange: (Boolean, Recording) -> Unit
) {
    val screenWidth = LocalConfiguration.current.screenWidthDp
    val noOfCheckedItems by viewModel.noOfCheckedItems.collectAsState()
    var scale by remember { mutableFloatStateOf(1f) }

    Surface(
        modifier = Modifier
            .scale(scale)
            .padding(horizontal = MEDIUM_SMALL_PADDING.dp),
        color = MaterialTheme.colorScheme.inverseOnSurface, // color
        shape = RoundedCornerShape(20)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .padding(15.dp)
                .pointerInput(Unit) {
                    detectTapGestures(
                        onPress = {
                            if (noOfCheckedItems >= 1) {
                                onCheckedChange(!item.isChecked, item)
                            } else {
                                scale = 0.95f
                                tryAwaitRelease()
                                scale = 1f
                            }
                        },
                        onLongPress = {
                            onCheckedChange(!item.isChecked, item)
                        }
                    )
                },
        ) {

            Row(
                modifier = Modifier
                    .padding(horizontal = SMALL_PADDING.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {

                Icon(
                    imageVector = Icons.Default.Mic,
                    contentDescription = "mic"
                )

                Spacer(modifier = Modifier.width(16.dp))


                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodyMedium.copy(color = Color.Gray)
                    )
                }

                if (noOfCheckedItems >= 1) {
                    CustomCheckbox(
                        checked = isChecked,
                        screenWidth = screenWidth,
                        onCheckedChange = { newCheckedState ->
                            Log.d("THE LOG", "boolean to pass is $newCheckedState")
                            onCheckedChange(newCheckedState, item)
                        }
                    )
                } else {
                    Surface(
                        modifier = Modifier.size((screenWidth * .079).dp),
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.onTertiary
                    ) {
                        Box(
                            contentAlignment = Alignment.Center, // Center the icon inside the surface
                            modifier = Modifier.fillMaxSize()
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.Send,
                                contentDescription = "search for song",
                                tint = Color.White,
                                modifier = Modifier.size((screenWidth * .05).dp) // Adjust the icon size here
                            )
                        }
                    }
                }
            }
        }
    }
}




//
//
//
//@Composable
//fun CustomListItem(
//    viewModel: RSViewModel,
//    item: Recording,
//    title: String,
//    subtitle: String,
//    isChecked: Boolean,
//    onCheckedChange: (Boolean, Recording) -> Unit
//) {
//    var scale by remember { mutableFloatStateOf(1f) }
//    val noOfCheckedItems by viewModel.noOfCheckedItems.collectAsState()
//
//    Box(
//        modifier = Modifier
//            .fillMaxWidth()
//            .padding(8.dp)
//            .clip(RoundedCornerShape(12.dp))
//            .background(MaterialTheme.colorScheme.surface)
//            .scale(scale)
//            .padding(16.dp)
//            .pointerInput(Unit) {
//                detectTapGestures(
//                    onPress = {
//                        scale = 0.95f
//                        tryAwaitRelease()
//                        scale = 1f
//                    },
//                    onLongPress = {
//                        // Toggle using ViewModel
//                        val newCheckedState = !isChecked
//                        viewModel.toggleItemChecked(item, newCheckedState)
//                        onCheckedChange(newCheckedState, item)
//                    }
//                )
//            }
//    ) {
//        Row(
//            modifier = Modifier,
//            verticalAlignment = Alignment.CenterVertically,
//            horizontalArrangement = Arrangement.SpaceBetween
//        ) {
//            Icon(
//                imageVector = Icons.Default.Mic,
//                contentDescription = "mic"
//            )
//
//            Spacer(modifier = Modifier.width(16.dp))
//
//            Column(
//                modifier = Modifier.weight(1f)
//            ) {
//                Text(
//                    text = title,
//                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold)
//                )
//                Spacer(modifier = Modifier.height(4.dp))
//                Text(
//                    text = subtitle,
//                    style = MaterialTheme.typography.bodyMedium.copy(color = Color.Gray)
//                )
//            }
//
//            if (noOfCheckedItems >= 1) {
//                // Updated Checkbox handling
//                CustomCheckbox(
//                    modifier = Modifier,
//                    checked = isChecked,
//                    onCheckedChange = { newCheckedState ->
//                        viewModel.toggleItemChecked(item, newCheckedState)
//                        onCheckedChange(newCheckedState, item)
//                    }
//                )
//            }
//        }
//    }
//}

@Composable
private fun CustomCheckbox(
    modifier: Modifier = Modifier,
    screenWidth: Int,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {

    val transition = updateTransition(targetState = checked, label = "CheckboxTransition")

    // Animating the checkmark color
    val checkmarkColor by transition.animateColor(
        transitionSpec = { tween(durationMillis = 300) },
        label = "CheckmarkColor"
    ) { state -> if (state) Color.Green else Color.LightGray }

    // Animating the scale for the checkbox
    val scale by transition.animateFloat(
        transitionSpec = { spring(dampingRatio = Spring.DampingRatioLowBouncy) },
        label = "CheckboxScale"
    ) { state -> if (state) 1.2f else 1f }


    Box(
        modifier = modifier
            .size((screenWidth * .056).dp)
            .scale(scale)
            .clip(RoundedCornerShape(50))
            .background(checkmarkColor, RoundedCornerShape(20))
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = { onCheckedChange(!checked) }
                )
            },
        contentAlignment = Alignment.Center
    ) {
        if (checked) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = "Checked",
                tint = Color.White,
                modifier = Modifier.fillMaxSize(.8f)
            )
        }
    }
}


//@Composable
//fun CustomCheckbox(
//    modifier: Modifier,
//    checked: Boolean,
//    onCheckedChange: (Boolean) -> Unit
//) {
//    val transition = updateTransition(targetState = checked, label = "CheckboxTransition")
//
//    val screenWidth = LocalConfiguration.current.screenWidthDp
//    // Animating the checkmark color
//    val checkmarkColor by transition.animateColor(
//        transitionSpec = { tween(durationMillis = 300) },
//        label = "CheckmarkColor"
//    ) { state -> if (state) Color.Green else Color.LightGray }
//
//    // Animating the scale for the checkbox
//    val scale by transition.animateFloat(
//        transitionSpec = { spring(dampingRatio = Spring.DampingRatioLowBouncy) },
//        label = "CheckboxScale"
//    ) { state -> if (state) 1.2f else 1f }
//
//    Box(
//        modifier = modifier
//            .size((screenWidth * .056).dp)
//            .scale(scale)
//            .background(checkmarkColor, RoundedCornerShape(20))
//            .padding(2.dp)
//            .pointerInput(Unit) {
//                detectTapGestures(
//                    onTap = { onCheckedChange(!checked) }
//                )
//            }
//    ) {
//
//        Canvas(modifier = Modifier.fillMaxSize()) {
//            if (checked) {
//                drawLine(
//                    color = Color.White,
//                    start = center - center / 2f,
//                    end = center,
//                    strokeWidth = 4.dp.toPx(),
//                    cap = StrokeCap.Round
//                )
//                drawLine(
//                    color = Color.White,
//                    start = center,
//                    end = center + center / 2f,
//                    strokeWidth = 4.dp.toPx(),
//                    cap = StrokeCap.Round
//                )
//            }
//        }
//    }
//}