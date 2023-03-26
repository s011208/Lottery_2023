package com.example.myapplication.compose.general

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun DialogText(text: String, modifier: Modifier) {
    Text(
        text = text, modifier = modifier
            .padding(vertical = 8.dp)
            .fillMaxWidth()
    )
}