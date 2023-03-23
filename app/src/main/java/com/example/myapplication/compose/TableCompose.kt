package com.example.myapplication.compose

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.example.myapplication.vm.Grid
import com.example.myapplication.vm.Row

@Composable
fun Table(rowList: List<Row>) {
    val scroll = rememberScrollState(0)

    LazyColumn(modifier = Modifier.horizontalScroll(scroll)) {
        rowList.forEach { row ->
            item {
                Row {
                    row.dataList.forEach { grid ->
                        Text(
                            text = grid.text, color = if (grid.visible) {
                                if (grid.type == Grid.Type.Special || grid.type == Grid.Type.SpecialColumnTitle) {
                                    Color.Red
                                } else {
                                    Color.Unspecified
                                }
                            } else {
                                Color.Transparent
                            }
                        )
                    }
                }
            }
        }
    }

}