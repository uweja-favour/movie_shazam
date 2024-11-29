package com.codr.movieshazam.ui.presentation.recording

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.CheckCircleOutline
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.codr.movieshazam.ui.theme.TextColor
import com.codr.movieshazam.ui.util.Constants.recordAndSave
import com.codr.movieshazam.ui.util.Constants.searchAndFind
import com.codr.movieshazam.ui.util.font.Poppins



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppBar(
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
                            imageVector = Icons.Default.CheckCircleOutline,
                            contentDescription = null,
                            tint = Color.Green
                        )
                    }
                }
            )
        }
    }
}

