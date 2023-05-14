package com.bj4.lottery2023.compose.possibility

import android.annotation.SuppressLint
import android.widget.Toast
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.bj4.lottery2023.R
import com.bj4.lottery2023.compose.general.Grid
import com.bj4.lottery2023.compose.general.GridFactory
import com.bj4.lottery2023.compose.possibility.vm.Chart
import com.bj4.lottery2023.compose.possibility.vm.PossibilityScreenViewModel
import com.bj4.lottery2023.compose.possibility.vm.PossibilityUiEvent
import org.koin.java.KoinJavaComponent

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun PossibilityScreen(navController: NavController = rememberNavController()) {
    val viewModel: PossibilityScreenViewModel by KoinJavaComponent.inject(PossibilityScreenViewModel::class.java)

    LaunchedEffect(key1 = Unit) {
        viewModel.handle(PossibilityUiEvent.Reload)
    }

    Scaffold(
        topBar = { PossibilityToolbar(navController) },
        content = { paddingValues -> PossibilityContent(Modifier.padding(paddingValues)) }
    )
}

@Composable
fun PossibilityContent(modifier: Modifier) {
    val viewModel: PossibilityScreenViewModel by KoinJavaComponent.inject(PossibilityScreenViewModel::class.java)
    val chartList = viewModel.viewModelState.collectAsState().value.chartList

    val horizontalScrollState = rememberScrollState(0)
    val lazyListState = rememberLazyListState(0)

    Column(modifier = modifier) {
        CountNumberComponent(modifier = Modifier.padding(all = 16.dp))

        LazyColumn(
            modifier = Modifier
                .padding(start = 16.dp)
                .horizontalScroll(horizontalScrollState),
            state = lazyListState
        ) {
            chartList.forEach { chart ->
                when (chart) {
                    is Chart.PossibilityList -> {
                        item {
                            Column(modifier = Modifier.padding(vertical = 16.dp)) {
                                Text(
                                    text = stringResource(id = R.string.possibility_order),
                                    modifier = Modifier.padding(vertical = 4.dp)
                                )
                                Row {
                                    GridFactory(
                                        grid = Grid(
                                            index = -1,
                                            text = stringResource(id = R.string.possibility_index),
                                            type = Grid.Type.Date
                                        ), fontSize = 20, extraSpacing = 2
                                    )
                                    chart.indexRow?.dataList?.forEach { grid ->
                                        GridFactory(grid = grid, fontSize = 20, extraSpacing = 2)
                                    }
                                }
                                Row {
                                    GridFactory(
                                        grid = Grid(
                                            index = -1,
                                            text = stringResource(id = R.string.possibility_times),
                                            type = Grid.Type.Date
                                        ), fontSize = 20, extraSpacing = 2
                                    )
                                    chart.countRow?.dataList?.forEach { grid ->
                                        GridFactory(grid = grid, fontSize = 20, extraSpacing = 2)
                                    }
                                }
                            }
                        }
                    }

                    is Chart.PossibilityListOrderByHighest -> {
                        item {
                            Column(modifier = Modifier.padding(vertical = 16.dp)) {
                                Text(
                                    text = stringResource(id = R.string.possibility_order_h_t_l),
                                    modifier = Modifier.padding(vertical = 4.dp)
                                )
                                Row {
                                    GridFactory(
                                        grid = Grid(
                                            index = -1,
                                            text = stringResource(id = R.string.possibility_index),
                                            type = Grid.Type.Date
                                        ), fontSize = 20, extraSpacing = 2
                                    )
                                    chart.indexRow?.dataList?.forEach { grid ->
                                        GridFactory(grid = grid, fontSize = 20, extraSpacing = 2)
                                    }
                                }
                                Row {
                                    GridFactory(
                                        grid = Grid(
                                            index = -1,
                                            text = stringResource(id = R.string.possibility_times),
                                            type = Grid.Type.Date
                                        ), fontSize = 20, extraSpacing = 2
                                    )
                                    chart.countRow?.dataList?.forEach { grid ->
                                        GridFactory(grid = grid, fontSize = 20, extraSpacing = 2)
                                    }
                                }
                            }
                        }
                    }

                    is Chart.PossibilityListOrderByLowest -> {
                        item {
                            Column(modifier = Modifier.padding(vertical = 16.dp)) {
                                Text(
                                    text = stringResource(id = R.string.possibility_order_l_t_h),
                                    modifier = Modifier.padding(vertical = 4.dp)
                                )
                                Row {
                                    GridFactory(
                                        grid = Grid(
                                            index = -1,
                                            text = stringResource(id = R.string.possibility_index),
                                            type = Grid.Type.Date
                                        ), fontSize = 20, extraSpacing = 2
                                    )
                                    chart.indexRow?.dataList?.forEach { grid ->
                                        GridFactory(grid = grid, fontSize = 20, extraSpacing = 2)
                                    }
                                }
                                Row {
                                    GridFactory(
                                        grid = Grid(
                                            index = -1,
                                            text = stringResource(id = R.string.possibility_times),
                                            type = Grid.Type.Date
                                        ), fontSize = 20, extraSpacing = 2
                                    )
                                    chart.countRow?.dataList?.forEach { grid ->
                                        GridFactory(grid = grid, fontSize = 20, extraSpacing = 2)
                                    }
                                }
                            }
                        }
                    }

                    is Chart.PossibilityListNoShowUntilToday -> {
                        item {
                            Column(modifier = Modifier.padding(vertical = 16.dp)) {
                                Text(
                                    text = stringResource(id = R.string.possibility_no_show_until_today),
                                    modifier = Modifier.padding(vertical = 4.dp)
                                )
                                Row {
                                    GridFactory(
                                        grid = Grid(
                                            index = -1,
                                            text = stringResource(id = R.string.possibility_index),
                                            type = Grid.Type.Date
                                        ), fontSize = 20, extraSpacing = 2
                                    )
                                    chart.indexRow?.dataList?.forEach { grid ->
                                        GridFactory(grid = grid, fontSize = 20, extraSpacing = 2)
                                    }
                                }
                                Row {
                                    GridFactory(
                                        grid = Grid(
                                            index = -1,
                                            text = stringResource(id = R.string.possibility_times),
                                            type = Grid.Type.Date
                                        ), fontSize = 20, extraSpacing = 2
                                    )
                                    chart.countRow?.dataList?.forEach { grid ->
                                        GridFactory(grid = grid, fontSize = 20, extraSpacing = 2)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CountNumberComponent(modifier: Modifier) {
    val context = LocalContext.current
    val viewModel: PossibilityScreenViewModel by KoinJavaComponent.inject(PossibilityScreenViewModel::class.java)

    val textFieldText = remember {
        mutableStateOf(viewModel.viewModelState.value.count.toString())
    }

    LaunchedEffect(key1 = Unit) {
        viewModel.eventStateSharedFlow.collect { event ->
            when (event) {
                is PossibilityUiEvent.WrongFormat -> {
                    Toast.makeText(
                        context,
                        context.resources.getString(R.string.wrong_count_format, event.text),
                        Toast.LENGTH_LONG
                    ).show()
                }

                else -> {}
            }
        }
    }

    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
        Text(text = stringResource(id = R.string.choose_number))
        TextField(
            value = textFieldText.value,
            onValueChange = { textFieldText.value = it },
            modifier = modifier.padding(horizontal = 8.dp),
            singleLine = true,
            maxLines = 1,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        )
        Button(onClick = { viewModel.handle(PossibilityUiEvent.ChangeNumberOfRows(textFieldText.value)) }) {
            Text(text = stringResource(id = android.R.string.ok))
        }
    }
}