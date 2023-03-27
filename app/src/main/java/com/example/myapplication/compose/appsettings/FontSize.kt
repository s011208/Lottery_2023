package com.example.myapplication.compose.appsettings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.myapplication.R
import com.example.myapplication.compose.general.DialogText
import com.example.myapplication.vm.MyEvents
import com.example.myapplication.vm.MyViewModel
import com.example.service.cache.FontSize
import org.koin.java.KoinJavaComponent

@Composable
fun FontSettingsDialog(dialogOpen: MutableState<Boolean>) {
    val viewModel: MyViewModel by KoinJavaComponent.inject(MyViewModel::class.java)

    val type = viewModel.viewModelState.collectAsState().value.fontType

    if (dialogOpen.value) {
        Dialog(onDismissRequest = {
            dialogOpen.value = false
        }) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight(),
                shape = RoundedCornerShape(size = 10.dp)
            ) {
                LazyColumn(modifier = Modifier.padding(all = 16.dp)) {
                    item {
                        FontSize.values().forEach {
                            DialogText(
                                text = when (it) {
                                    FontSize.EXTRA_SMALL -> stringResource(id = R.string.x_small)
                                    FontSize.SMALL -> stringResource(id = R.string.small)
                                    FontSize.NORMAL -> stringResource(id = R.string.normal)
                                    FontSize.LARGE -> stringResource(id = R.string.large)
                                    FontSize.EXTRA_LARGE -> stringResource(id = R.string.x_large)
                                },
                                modifier = Modifier.clickable {
                                    viewModel.handleEvent(MyEvents.ChangeFontSize(it))
                                    dialogOpen.value = false
                                },
                                selected = type == it
                            )
                        }
                    }
                }
            }
        }
    }
}