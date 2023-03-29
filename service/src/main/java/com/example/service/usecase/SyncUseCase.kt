package com.example.service.usecase

import com.example.debugger.MyLog
import com.example.service.service.ParseService

class SyncUseCase(val service: ParseService) {

    fun parseLto() {
        val lotteryData = service.parseLto()
        MyLog.log("parseLto lotteryData size: $lotteryData")
    }

    fun parseLtoBig() {
        val lotteryData = service.parseLtoBig()
        MyLog.log("parseLtoBig lotteryData size: $lotteryData")
    }

    fun parseLtoHk() {
        val lotteryData = service.parseLtoHk()
        MyLog.log("parseLtoHk lotteryData size: $lotteryData")
    }

    fun clearDatabase() {
        service.clearDatabase()
    }
}