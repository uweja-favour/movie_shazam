package com.codr.movieshazam.permission_package

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import kotlinx.coroutines.coroutineScope

class PermissionHelper(
    private val context: Context,
    private val requestCode: Int = 1
) {

    /**
     * Checks if the given permission has been granted.
     */
    private fun isPermissionGranted(permission: String): Boolean {
        return ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED
    }

    /**
     * Requests permission(s) at once.
     */
    suspend fun requestMultiplePermissions(permissionTypes: List<PermissionTypes>) = coroutineScope {
        val listOfPermissions = permissionTypes
            .filter { !isPermissionGranted(permission = permissionMap[it] ?: "") } // get permissions that has not been granted
            .mapNotNull { permissionMap[it] } // map non null items to permissionMap string values
            .toTypedArray()

        if (listOfPermissions.isNotEmpty()) {
            ActivityCompat.requestPermissions(
                context as Activity,
                listOfPermissions,
                requestCode
            )
        }
    }

    /**
     * Requests permission(s) at once.
     */
    suspend fun requestMultiplePermissions(permissionTypes: List<String>) = coroutineScope {
        val listOfPermissions = permissionTypes
            .filter { !isPermissionGranted(permission = it) } // get permissions that has not been granted
            .toTypedArray()

        if (listOfPermissions.isNotEmpty()) {
            ActivityCompat.requestPermissions(
                context as Activity,
                listOfPermissions,
                requestCode
            )
        }
    }

    private val permissionMap = mapOf(
        PermissionTypes.RECORD_AUDIO to Manifest.permission.RECORD_AUDIO,
        PermissionTypes.SET_ALARM to Manifest.permission.SET_ALARM,
        PermissionTypes.POST_NOTIFICATIONS to Manifest.permission.POST_NOTIFICATIONS
    )
}
