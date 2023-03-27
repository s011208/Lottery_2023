package com.example.myapplication.compose.general

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.example.myapplication.R
import com.example.myapplication.compose.AppToolbarSettings

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