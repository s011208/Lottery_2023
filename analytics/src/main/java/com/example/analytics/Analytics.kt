package com.example.analytics

import android.os.Bundle
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.crashlytics.ktx.crashlytics
import com.google.firebase.ktx.Firebase

class Analytics {
    companion object {
        private const val SYNC_SOURCE = "sync_source"
        private const val KEY_LOTTERY_TYPE_CLICK = "lottery_type"
        private const val KEY_SORTING = "sorting"
    }

    private val firebaseAnalytics: FirebaseAnalytics = Firebase.analytics

    fun trackSyncSource(source: String) {
        val bundle = Bundle()
        bundle.putString("source_type", source)
        firebaseAnalytics.logEvent(SYNC_SOURCE, bundle)
    }

    fun trackLotteryTypeClick(type: String) {
        val bundle = Bundle()
        bundle.putString("type", type)
        firebaseAnalytics.logEvent(KEY_LOTTERY_TYPE_CLICK, bundle)
    }

    fun trackSortingType(type: String) {
        val bundle = Bundle()
        bundle.putString("type", type)
        firebaseAnalytics.logEvent(KEY_SORTING, bundle)
    }

    fun recordException(throwable: Throwable) {
        Firebase.crashlytics.recordException(throwable)
    }

    fun log(log: String) {
        Firebase.crashlytics.log(log)
    }
}