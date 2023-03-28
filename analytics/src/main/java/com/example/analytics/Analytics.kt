package com.example.analytics

import android.os.Bundle
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase

class Analytics {
    companion object {
        private const val SYNC_SOURCE = "sync_source"
    }

    private val firebaseAnalytics: FirebaseAnalytics = Firebase.analytics

    fun trackSyncSource(source: String) {
        android.util.Log.v("QQQQ", "trackSyncSource: $source")
        firebaseAnalytics.logEvent(SYNC_SOURCE, Bundle().also {
            it.putString(FirebaseAnalytics.Param.ITEM_NAME, source)
        })
    }
}