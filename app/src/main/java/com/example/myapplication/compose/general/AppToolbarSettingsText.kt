package com.example.myapplication.compose.general

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun AppToolbarSettingsText(text: String, modifier: Modifier = Modifier) {
    Text(
        text = text,
        modifier = modifier.padding(horizontal = 8.dp, vertical = 8.dp),
        textAlign = TextAlign.Center
    )
}

@Composable
fun AppToolbarSettingsDropDownText(
    text: String,
    modifier: Modifier = Modifier,
) {
    Text(
        text = text, modifier = modifier
            .padding(horizontal = 4.dp, vertical = 2.dp)
            .fillMaxSize()
    )
}