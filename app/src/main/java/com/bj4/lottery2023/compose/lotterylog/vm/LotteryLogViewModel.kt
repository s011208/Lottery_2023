package com.bj4.lottery2023.compose.lotterylog.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bj4.lottery2023.compose.lotterytable.vm.Source
import com.example.data.LoadingState
import com.example.data.LotteryLog
import com.example.data.LotteryType
import com.example.service.usecase.LotteryLogUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Calendar

class LotteryLogViewModel(private val lotteryLogUseCase: LotteryLogUseCase) : ViewModel() {

    data class TaskGroup(
        val timeStamp: String,
        val itemList: List<DisplayItem>,
        val source: String,
    )

    data class DisplayItem(
        val type: LotteryType,
        val timeStamp: Long,
        val result: LoadingState,
        val message: String,
    )

    data class ViewModelState(val taskGroupLList: List<TaskGroup> = listOf())

    private val _viewModelState: MutableStateFlow<ViewModelState> = MutableStateFlow(
        ViewModelState()
    )
    val viewModelState: StateFlow<ViewModelState> = _viewModelState.asStateFlow()

    fun handleUiEvent(event: LotteryLogUiEvent) {
        when (event) {
            LotteryLogUiEvent.RequestData -> {
                viewModelScope.launch(Dispatchers.IO) {
                    val rtn = mutableListOf<TaskGroup>()
                    val mappingMap = mutableMapOf<String, MutableList<LotteryLog>>()
                    val allData = lotteryLogUseCase.getAll()
                    allData.forEach { log ->
                        mappingMap[log.taskId] =
                            mappingMap.getOrDefault(log.taskId, mutableListOf())
                                .also { it.add(log) }
                    }
                    mappingMap.forEach { (key, value) ->
                        val calendar = Calendar.getInstance().also { it.timeInMillis = key.toLong() }
                        val taskGroup = TaskGroup(calendar.time.toString(), value.map {
                            DisplayItem(
                                it.type,
                                it.timeStamp,
                                it.state,
                                it.errorMessage,
                            )
                        }, value.firstOrNull()?.source ?: Source.UNKNOWN.toString())
                        taskGroup.itemList.sortedBy { it.type }
                        rtn.add(taskGroup)
                    }
                    rtn.sortByDescending { it.timeStamp }

                    _viewModelState.emit(
                        ViewModelState(rtn)
                    )
                }
            }

            LotteryLogUiEvent.ClearCache -> {
                viewModelScope.launch(Dispatchers.IO) {
                    lotteryLogUseCase.clear()
                    _viewModelState.emit(
                        ViewModelState()
                    )
                }
            }
        }
    }
}

sealed class LotteryLogUiEvent {
    object RequestData : LotteryLogUiEvent()

    object ClearCache : LotteryLogUiEvent()
}