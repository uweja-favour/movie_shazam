package com.codr.movieshazam.ui.presentation.recording

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.DropdownMenu
import androidx.compose.material.Surface
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircleOutline
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import com.codr.movieshazam.ui.theme.AppBackGround
import com.codr.movieshazam.ui.theme.TextColor
import com.codr.movieshazam.ui.util.font.Poppins

private val searchAndFind =  DropDownItem(
    title = "Search and find",
    icon = Icons.Default.ArrowUpward
)

private val recordAndSave = DropDownItem(
    title = "Record and save",
    icon = Icons.Default.Save,
)


@Composable
fun RecordingScreen(viewModel: RSViewModel) {

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
            modifier = Modifier.padding(paddingValues),
            color = AppBackGround
        ) {
            // Add your screen content here
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
