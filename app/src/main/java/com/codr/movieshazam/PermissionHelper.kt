package com.codr.movieshazam

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.codr.movieshazam.ui.util.Constants.KEY_POST_NOTIFICATIONS_GRANTED
import com.codr.movieshazam.ui.util.Constants.PREFS_NAME
import kotlinx.coroutines.coroutineScope

class PermissionHelper(
    private val activity: ComponentActivity,
    private val notificationPermissionLauncher: ActivityResultLauncher<String>
) {
    private val prefs by lazy {
        activity.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    private fun hasPermissionBeenGranted(): Boolean {
        return prefs.getBoolean(KEY_POST_NOTIFICATIONS_GRANTED, false)
    }

    fun isPermissionGranted(permission: String): Boolean {
        return ContextCompat.checkSelfPermission(activity, permission) == PackageManager.PERMISSION_GRANTED
    }

    suspend fun requestNotificationPermission() = coroutineScope {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            !hasPermissionBeenGranted() &&
            !isPermissionGranted(Manifest.permission.POST_NOTIFICATIONS)
        ) {
            notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }
}
