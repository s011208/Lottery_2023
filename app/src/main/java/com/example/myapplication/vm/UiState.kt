package com.example.myapplication.vm

sealed class UiState {
    object Empty: UiState()

    data class Show(val size: Int): UiState()
}