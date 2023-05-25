package com.bj4.lottery2023.compose.possibility

import android.annotation.SuppressLint
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
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
import com.bj4.lottery2023.compose.general.GridFactory
import com.bj4.lottery2023.compose.possibility.vm.Chart
import com.bj4.lottery2023.compose.possibility.vm.PossibilityScreenViewModel
import com.bj4.lottery2023.compose.possibility.vm.PossibilityUiEvent
import com.example.analytics.Analytics
import org.koin.java.KoinJavaComponent

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun PossibilityScreen(navController: NavController = rememberNavController()) {
    val viewModel: PossibilityScreenViewModel by KoinJavaComponent.inject(PossibilityScreenViewModel::class.java)

    LaunchedEffect(key1 = Unit) {
        viewModel.handle(PossibilityUiEvent.Reload)
    }

    LaunchedEffect(key1 = Unit) {
        val analytics: Analytics by KoinJavaComponent.inject(Analytics::class.java)
        analytics.trackScreen("PossibilityScreen")
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
        Row(
            modifier = Modifier
                .padding(vertical = 8.dp, horizontal = 16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(32.dp)
        ) {
            CountNumberComponent()
            CheckBoxGroup()
        }

        ChartContent(horizontalScrollState, lazyListState, chartList)
    }
}

@Composable
private fun ChartContent(
    horizontalScrollState: ScrollState,
    lazyListState: LazyListState,
    chartList: List<Chart>
) {
    LazyColumn(
        modifier = Modifier
            .padding(start = 16.dp)
            .horizontalScroll(horizontalScrollState),
        state = lazyListState
    ) {
        chartList.forEach { chart ->
            when (chart) {
                is Chart.TableChart -> {
                    item {
                        TableChartColumn(chart)
                    }
                }
            }
        }
    }
}

@Composable
private fun TableChartColumn(chart: Chart.TableChart) {
    Column(modifier = Modifier.padding(vertical = 16.dp)) {
        val title = when (chart) {
            is Chart.TableChart.PossibilityList -> stringResource(id = R.string.possibility_order)
            is Chart.TableChart.NoShowUntilToday -> stringResource(
                id = R.string.possibility_no_show_until_today
            )

            is Chart.TableChart.PossibilityListOrderByDescent -> stringResource(
                id = R.string.possibility_order_h_t_l
            )

            is Chart.TableChart.PossibilityListOrderByAscent -> stringResource(
                id = R.string.possibility_order_l_t_h
            )

            is Chart.TableChart.NoShowUntilTodayOrderByAscent -> stringResource(id = R.string.possibility_no_show_until_today_l_t_h)
            is Chart.TableChart.NoShowUntilTodayOrderByDescent -> stringResource(id = R.string.possibility_no_show_until_today_h_t_l)
        }
        TableChart(title, chart)
    }
}

@Composable
private fun CheckBoxGroup() {
    val viewModel: PossibilityScreenViewModel by KoinJavaComponent.inject(PossibilityScreenViewModel::class.java)
    val showByIndex = remember {
        mutableStateOf(viewModel.viewModelState.value.showByIndex)
    }

    val showByAscent = remember {
        mutableStateOf(viewModel.viewModelState.value.showByAscent)
    }

    val showByDescent = remember {
        mutableStateOf(viewModel.viewModelState.value.showByDescent)
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        OrderCheckBox(
            R.string.order_by_index,
            showByIndex
        ) { viewModel.handle(PossibilityUiEvent.ShowOrderByIndex(showByIndex.value)) }

        OrderCheckBox(R.string.order_by_asc, showByAscent) {
            viewModel.handle(PossibilityUiEvent.ShowOrderByAsc(showByAscent.value))
        }

        OrderCheckBox(R.string.order_by_desc, showByDescent) {
            viewModel.handle(PossibilityUiEvent.ShowOrderByDesc(showByDescent.value))
        }
    }
}

@Composable
private fun OrderCheckBox(@StringRes title: Int, value: MutableState<Boolean>, click: () -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Checkbox(
            checked = value.value,
            onCheckedChange = {
                value.value = !value.value
                click()
            }
        )

        Text(text = stringResource(id = title))
    }
}

private const val MIN_GRID_HEIGHT = 25

@Composable
private fun TableChart(
    title: String,
    chart: Chart.TableChart
) {
    val viewModel: PossibilityScreenViewModel by KoinJavaComponent.inject(PossibilityScreenViewModel::class.java)
    Text(
        text = title,
        modifier = Modifier.padding(vertical = 4.dp)
    )
    Row(verticalAlignment = Alignment.CenterVertically) {
        chart.indexRow?.dataList?.forEach { grid ->
            GridFactory(
                grid = grid,
                fontSize = viewModel.viewModelState.value.fontSize,
                extraSpacing = viewModel.viewModelState.value.extraSpacing,
                minHeight = MIN_GRID_HEIGHT
            )
        }
    }
    Row(verticalAlignment = Alignment.CenterVertically) {
        chart.countRow?.dataList?.forEach { grid ->
            GridFactory(
                grid = grid,
                fontSize = viewModel.viewModelState.value.fontSize,
                extraSpacing = viewModel.viewModelState.value.extraSpacing,
                minHeight = MIN_GRID_HEIGHT
            )
        }
    }
}

@Composable
fun CountNumberComponent(modifier: Modifier = Modifier) {
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