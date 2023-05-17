package com.example.analytics

import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.analytics.ktx.logEvent
import com.google.firebase.crashlytics.ktx.crashlytics
import com.google.firebase.ktx.Firebase

class Analytics {
    companion object {
        private const val SYNC_SOURCE = "sync_source"
        private const val KEY_LOTTERY_TYPE_CLICK = "lottery_type"
        private const val KEY_SORTING = "sorting"
    }

    private val firebaseAnalytics: FirebaseAnalytics = Firebase.analytics

    init {
        firebaseAnalytics.setAnalyticsCollectionEnabled(true)
    }

    fun trackSyncSource(source: String) {
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_ITEM) {
            param(FirebaseAnalytics.Param.ITEM_ID, SYNC_SOURCE)
            param(FirebaseAnalytics.Param.ITEM_NAME, source)
        }
    }

    fun trackLotteryTypeClick(type: String) {
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_ITEM) {
            param(FirebaseAnalytics.Param.ITEM_ID, KEY_LOTTERY_TYPE_CLICK)
            param(FirebaseAnalytics.Param.ITEM_NAME, type)
        }
    }

    fun trackSortingType(type: String) {
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_ITEM) {
            param(FirebaseAnalytics.Param.ITEM_ID, KEY_SORTING)
            param(FirebaseAnalytics.Param.ITEM_NAME, type)
        }
    }

    fun recordException(throwable: Throwable) {
        Firebase.crashlytics.recordException(throwable)
    }

    fun log(log: String) {
        Firebase.crashlytics.log(log)
    }

    fun trackScreen(screenName: String) {
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW) {
            param(FirebaseAnalytics.Param.SCREEN_NAME, screenName)
        }
    }
}