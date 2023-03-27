package com.example.myapplication.compose.appsettings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
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
fun ResetDatabase(dialogOpen: MutableState<Boolean>) {
    val viewModel: MyViewModel by KoinJavaComponent.inject(MyViewModel::class.java)

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
                Column(
                    modifier = Modifier.padding(all = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(text = stringResource(id = R.string.reset_title))
                    Text(text = stringResource(id = R.string.reset_message))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Button(onClick = { dialogOpen.value = false }) {
                            Text(text = stringResource(id = android.R.string.cancel))
                        }
                        Button(onClick = {
                            dialogOpen.value = false
                            viewModel.handleEvent(MyEvents.ResetData)
                        }) {
                            Text(text = stringResource(id = android.R.string.ok))
                        }
                    }
                }
            }
        }
    }
}