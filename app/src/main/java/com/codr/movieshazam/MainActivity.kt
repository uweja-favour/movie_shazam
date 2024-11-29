package com.codr.movieshazam

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import com.codr.movieshazam.ui.bottom_navbar.BottomNavigationBar
import com.codr.movieshazam.ui.presentation.recording.AppBar
import com.codr.movieshazam.ui.presentation.recording.DropDownItem
import com.codr.movieshazam.ui.presentation.recording.MainScreen
import com.codr.movieshazam.ui.presentation.recording.RSViewModel
import com.codr.movieshazam.ui.theme.AppBackGround
import com.codr.movieshazam.ui.theme.MovieShazamTheme
import com.codr.movieshazam.ui.util.Constants.PLAYBACK_COMPLETE
import com.codr.movieshazam.ui.util.Constants.SNACK_BAR_EVENT
import com.codr.movieshazam.ui.util.Constants.navigationItems
import com.codr.movieshazam.ui.util.Constants.recordAndSave
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch

private const val REQUEST_CODE_POST_NOTIFICATIONS = 1001
private var initialPage = 0
private const val PREFS_NAME = "app_prefs"
private const val KEY_POST_NOTIFICATIONS_GRANTED = "post_notifications_granted"
private const val REQUEST_RECORD_AUDIO_PERMISSION = 200

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.RECORD_AUDIO),
            0
        )
        setContent {
            MovieShazamTheme {
                MovieShazam()

            }
//          requestPermissions()
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.RECORD_AUDIO), REQUEST_RECORD_AUDIO_PERMISSION)
            }
        }
    }



    // PERMISSIONS HANDLING
//    private fun isPermissionGranted(permission: String): Boolean {
//        return ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED
//    }
//
//
//    private fun setPermissionGranted(granted: Boolean) {
//        val prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
//        prefs.edit().putBoolean(KEY_POST_NOTIFICATIONS_GRANTED, granted).apply()
//    }
//
//
//    private fun hasPermissionBeenGranted(): Boolean {
//        val prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
//        return prefs.getBoolean(KEY_POST_NOTIFICATIONS_GRANTED, false)
//    }
//
//
//    private fun requestPermissions() {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
//            if (!hasPermissionBeenGranted()) {
//                if (!isPermissionGranted(Manifest.permission.POST_NOTIFICATIONS)) {
//                    ActivityCompat.requestPermissions(
//                        this,
//                        arrayOf(Manifest.permission.POST_NOTIFICATIONS),
//                        REQUEST_CODE_POST_NOTIFICATIONS
//                    )
//                } else {
//                    setPermissionGranted(true)
//                }
//            }
//        }
//
//
//    }
//
//    @Deprecated("Deprecated in Java")
//    override fun onRequestPermissionsResult(
//        requestCode: Int,
//        permissions: Array<String>,
//        grantResults: IntArray
//    ) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
//        if (requestCode == REQUEST_CODE_POST_NOTIFICATIONS) {
//            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                Log.d("MY LOG", "Permission granted to post notifications.")
//                setPermissionGranted(true)
//            } else {
//                Log.d("MY LOG", "Permission denied to post notifications.")
//            }
//        }
//    }
}


@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun MovieShazam() {

    // Main Screen View Model
    val mainScreenVM = hiltViewModel<RSViewModel>()

    // Horizontal Pager
    val pagerState = rememberPagerState(initialPage = initialPage, pageCount = { 2 })
    val currentRoute = remember {
        MutableStateFlow(navigationItems[initialPage].route)
    }

    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.currentPage }
            .flowOn(Dispatchers.Main.immediate) // Explicitly on UI thread
            .collect { pageIndex ->
                currentRoute.value = navigationItems[pageIndex].route
            }
    }

    // Snack Bar
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val snackBarHostState  = remember {
        SnackbarHostState()
    }

    ObserveAsEvent(
        flow = EventController.emit,
    ) { event ->
        when (event.type) {
            PLAYBACK_COMPLETE -> {
                Log.d("THE LOG", "playback completed event received")
                mainScreenVM.onTogglePlayBackCompleted()
            }
            SNACK_BAR_EVENT -> {
                coroutineScope.launch {
                    snackBarHostState.currentSnackbarData?.dismiss()
                    val result = snackBarHostState.showSnackbar(
                        message = event.message,
                        actionLabel = event.snackBarEvent?.actionTitle,
                        duration = SnackbarDuration.Short
                    )

                    if (result == SnackbarResult.ActionPerformed) {
                        event.snackBarEvent?.action?.invoke()
                    }
                }
            }
        }
    }

    // Main Screen AppBar functions
    var actionSelected: DropDownItem by remember {
        mutableStateOf(recordAndSave)
    }

    fun handleActionSelected(it: DropDownItem) {
        actionSelected = it
        Toast.makeText(context, "${it.title} mode selected", Toast.LENGTH_SHORT).show()
    }

    Scaffold(
        snackbarHost = {
            SnackbarHost(
                hostState = snackBarHostState
            )
        },
        topBar = {
            AppBar(
                selectedAction = actionSelected,
                onActionSelected = { handleActionSelected(it) }
            )
        },
        bottomBar = {
            BottomNavigationBar(
                currentRoute = currentRoute,
                onNavigate = { targetPageRoute ->
                    coroutineScope.launch {
                        val targetPageIndex = navigationItems.indexOfFirst { it.route == targetPageRoute }
                        if (targetPageIndex >= 0) {
                            coroutineScope.launch(Dispatchers.Main.immediate) {
                                pagerState.scrollToPage(targetPageIndex)
                            }
                        }
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
        ) {
            HorizontalPager(state = pagerState) { pageIndex ->
                when(pageIndex) {
                    0 -> {
                        MainScreen(
                            viewModel = mainScreenVM,
                            paddingValues = paddingValues
                        )
                    }
                    1 -> {
                        Box(modifier = Modifier.fillMaxSize()) {
                            Text("Page undergoing work")
                        }
                    }
                }
            }
        }
    }
}
