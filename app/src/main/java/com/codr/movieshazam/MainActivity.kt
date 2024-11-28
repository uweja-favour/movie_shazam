package com.codr.movieshazam

import android.Manifest
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.core.app.ActivityCompat
import androidx.hilt.navigation.compose.hiltViewModel
import com.codr.movieshazam.ui.bottom_navbar.BottomNavigationBar
import com.codr.movieshazam.ui.presentation.recording.RSViewModel
import com.codr.movieshazam.ui.presentation.recording.MainScreen
import com.codr.movieshazam.ui.theme.MovieShazamTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.MutableStateFlow

private const val REQUEST_CODE_POST_NOTIFICATIONS = 1001
private var initialPage = 0
private const val PREFS_NAME = "app_prefs"
private const val KEY_POST_NOTIFICATIONS_GRANTED = "post_notifications_granted"

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

        }

    }





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
////      The method canScheduleExactAlarms() is only available starting from API 31 (Android 12).
////      The check for Build.VERSION.SDK_INT >= Build.VERSION_CODES.S ensures the method is only called on compatible devices.
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
//            val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
//            if (!alarmManager.canScheduleExactAlarms()) {
//                val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)
//                startActivity(intent)
//            }
//        }
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


@Composable
private fun MovieShazam() {
    val currentRoute = remember {
        MutableStateFlow(initialPage)
    }

    Box(
        modifier = Modifier
        .fillMaxSize()
    ) {

        when(currentRoute.collectAsState().value) {
            0 -> {
                MainScreen(
                    viewModel = hiltViewModel<RSViewModel>()
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