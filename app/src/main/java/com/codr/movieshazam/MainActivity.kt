package com.codr.movieshazam

import android.annotation.SuppressLint
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.lifecycleScope
import com.codr.movieshazam.permission_package.PermissionHelper
import com.codr.movieshazam.permission_package.PermissionTypes
import com.codr.movieshazam.ui.bottom_navbar.BottomNavigationBar
import com.codr.movieshazam.ui.presentation.recording.AppBar
import com.codr.movieshazam.ui.presentation.recording.DropDownItem
import com.codr.movieshazam.ui.presentation.recording.MSHelperObject
import com.codr.movieshazam.ui.presentation.recording.MainScreen
import com.codr.movieshazam.ui.presentation.recording.RSViewModel
import com.codr.movieshazam.ui.theme.MovieShazamTheme
import com.codr.movieshazam.ui.util.Constants
import com.codr.movieshazam.ui.util.Constants.PLAYBACK_COMPLETE
import com.codr.movieshazam.ui.util.Constants.SNACK_BAR_EVENT
import com.codr.movieshazam.ui.util.Constants.navigationItems
import com.codr.movieshazam.ui.util.Constants.recordAndSave
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch

private var initialPage = 0

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private lateinit var permissionHelper: PermissionHelper

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    @SuppressLint("BatteryLife")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

//        val permissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) {}

        permissionHelper = PermissionHelper(this)
        val permissionsToRequest = listOf(
            PermissionTypes.POST_NOTIFICATIONS,
            PermissionTypes.RECORD_AUDIO,
        )

        lifecycleScope.launch {
            permissionHelper.requestMultiplePermissions(permissionsToRequest)
        }

        setContent {
            MovieShazamTheme {
                MovieShazam()
            }
        }

        // Ensure the app is not battery-optimized
        promptIgnoreBatteryOptimizations()
    }

    @SuppressLint("BatteryLife")
    private fun promptIgnoreBatteryOptimizations() {
        val intent = Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS).apply {
            data = Uri.parse("package:$packageName")
        }
        startActivity(intent)
    }

    override fun onPause() {
        super.onPause()
       cancelForeGroundActivity()
    }

    override fun onDestroy() {
        super.onDestroy()
        cancelForeGroundActivity()
    }

    private fun cancelForeGroundActivity() {
        if (!MSHelperObject.isRecording.value && !MSHelperObject.isPlaying.value) {
            // Stop the recording service if it's not running
            Log.d("THE LOG", "Removing service, destroying notification channel")
            val stopServiceIntent = Intent(this, AudioRecordingService::class.java)
            stopService(stopServiceIntent)

            // Cancel all notifications
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.cancelAll()
            notificationManager.deleteNotificationChannel(Constants.CHANNEL_ID)
        }
    }
}



@RequiresApi(Build.VERSION_CODES.TIRAMISU)
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
                        MainScreen(mainScreenVM)
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
