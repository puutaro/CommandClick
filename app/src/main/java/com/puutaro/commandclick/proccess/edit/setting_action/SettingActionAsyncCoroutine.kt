package com.puutaro.commandclick.proccess.edit.setting_action

import kotlinx.coroutines.Job
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class SettingActionAsyncCoroutine {

    private val settingActionAsyncCoroutineList = arrayListOf<Job?>()
    private val settingActionAsyncCoroutineListMutex = Mutex()

    suspend fun put(job: Job?){
        settingActionAsyncCoroutineListMutex.withLock {
            settingActionAsyncCoroutineList.add(job)
        }
    }

    suspend fun clean(){
        settingActionAsyncCoroutineListMutex.withLock {
            settingActionAsyncCoroutineList.forEach {
                it?.cancel()
            }
            settingActionAsyncCoroutineList.clear()
        }
    }
}