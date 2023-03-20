package com.example.myapplication.vm

sealed class MyEvents {

    object StartSync: MyEvents()

    data class EndSync(val error: Throwable? = null): MyEvents()
}