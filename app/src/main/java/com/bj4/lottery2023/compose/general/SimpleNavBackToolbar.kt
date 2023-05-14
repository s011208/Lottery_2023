package com.bj4.lottery2023.compose.general

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SimpleNavBackToolbar(navController: NavController = rememberNavController(), title: String) {

    TopAppBar(
        title = {
            Text(
                text = title
            )

        },
        navigationIcon = {
            IconButton(onClick = { navController.navigateUp() }) {
                Icon(
                    imageVector = Icons.Filled.ArrowBack,
                    contentDescription = stringResource(id = com.bj4.lottery2023.R.string.back)
                )
            }
        }, modifier = Modifier.padding(vertical = 16.dp)
    )
}