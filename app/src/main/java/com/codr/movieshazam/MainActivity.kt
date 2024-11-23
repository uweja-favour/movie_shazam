package com.codr.movieshazam

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.codr.movieshazam.ui.bottom_navbar.BottomNavigationBar
import com.codr.movieshazam.ui.presentation.recording.RSViewModel
import com.codr.movieshazam.ui.presentation.recording.RecordingScreen
import com.codr.movieshazam.ui.theme.MovieShazamTheme
import kotlinx.coroutines.flow.MutableStateFlow

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MovieShazamTheme {
                MovieShazam()
            }
        }
    }
}

@Composable
fun MovieShazam() {

    val currentRoute = remember {
        MutableStateFlow(0)
    }

    Box(modifier = Modifier.fillMaxSize()) {

        when(currentRoute.collectAsState().value) {
            0 -> {
                RecordingScreen(
                    viewModel = viewModel<RSViewModel>()
                )
            }
            1 -> {
                Text("HELLO WORLD")
            }
        }

        BottomNavigationBar(
            modifier = Modifier.align(Alignment.BottomCenter),
            currentRoute = currentRoute,
            onNavigate = {
                currentRoute.value = it
            }
        )
    }
}