package com.codr.movieshazam

import android.app.Application
import androidx.lifecycle.ProcessLifecycleOwner
import com.codr.movieshazam.ui.presentation.recording.RSViewModel
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class MyApplication: Application() {

    override fun onCreate() {
        super.onCreate()
        // Register the lifecycle observer
        val lifecycleObserver = AppLifecycleObserver(
            context = this,
            viewModel = RSViewModel(
            rsDataSource = RsDataSourceImpl(this),
            context = this
        ))
        ProcessLifecycleOwner.get().lifecycle.addObserver(lifecycleObserver)
//        createAppNotificationChannel()
    }
}