package com.codr.movieshazam.ui.bottom_navbar

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.material.Text
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.codr.movieshazam.data.NavigationItem
import com.codr.movieshazam.ui.theme.AppBackGround
import com.codr.movieshazam.ui.theme.TextColor
import com.codr.movieshazam.ui.util.Constants.navigationItems
import com.codr.movieshazam.ui.util.font.Poppins
import kotlinx.coroutines.flow.MutableStateFlow

@Composable
fun BottomNavigationBar(
    modifier: Modifier,
    currentRoute: MutableStateFlow<Int>,
    onNavigate: (Int) -> Unit
) {

    val theCurrentRoute by currentRoute.collectAsState()


    NavigationBar(
        modifier = modifier,
        containerColor = AppBackGround
    ) {
        Row(
            modifier = Modifier.background(
                AppBackGround
            )
        ) {
            navigationItems.forEachIndexed { index, item ->
                val isSelected = theCurrentRoute == index

                NavigationBarItem(
                    selected = theCurrentRoute == index,
                    onClick = {
                        onNavigate(index)
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
                            color = TextColor
                        )
                    },
                )
            }
        }
    }
}


@Composable
fun HandleNavItemIcon(
    item: NavigationItem,
    isSelected: Boolean
) {
    Icon(
        imageVector = if (isSelected) item.filledIcon else item.outlinedIcon,
        contentDescription = null
    )
}