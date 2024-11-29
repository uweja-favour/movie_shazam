package com.codr.movieshazam.data

import androidx.compose.ui.graphics.vector.ImageVector

data class NavigationItem(
    val title: String,
    val route: String,
    val filledIcon: ImageVector,
    val outlinedIcon: ImageVector
)