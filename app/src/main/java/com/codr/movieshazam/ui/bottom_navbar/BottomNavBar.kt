package com.codr.movieshazam.ui.bottom_navbar

import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.codr.movieshazam.data.NavigationItem
import com.codr.movieshazam.ui.util.Constants.navigationItems
import com.codr.movieshazam.ui.util.font.Poppins
import kotlinx.coroutines.flow.MutableStateFlow

@Composable
fun BottomNavigationBar(
    modifier: Modifier = Modifier,
    currentRoute: MutableStateFlow<String>,
    onNavigate: (String) -> Unit
) {

    val theCurrentRoute by currentRoute.collectAsState()

    NavigationBar(
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.onSecondary,
    ) {
        Row {
            navigationItems.forEach { item ->
                val isSelected = theCurrentRoute == item.route

                NavigationBarItem(
                    selected = theCurrentRoute == item.route,
                    onClick = {
                        onNavigate(item.route)
                    },
                    icon = {
                        HandleNavItemIcon(
                            item = item,
                            isSelected = isSelected
                        )
                    },
                    label = {
                        Text(
                            text = item.title,
                            fontFamily = Poppins,
                            color = Color.White
                        )
                    },
                )
            }
        }
    }
}


@Composable
private fun HandleNavItemIcon(
    item: NavigationItem,
    isSelected: Boolean
) {
    Icon(
        imageVector = if (isSelected) item.filledIcon else item.outlinedIcon,
        contentDescription = null
    )
}