package com.puutaro.commandclick.proccess.edit.setting_action

import kotlinx.coroutines.Job
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.util.concurrent.ConcurrentHashMap

class SettingActionAsyncCoroutine {

    private val settingActionAsyncCoroutineList = ConcurrentHashMap.newKeySet<Job>()
//    private val settingActionAsyncCoroutineListMutex = Mutex()

    suspend fun put(job: Job?){
        if(job == null) return
//        settingActionAsyncCoroutineListMutex.withLock {
            settingActionAsyncCoroutineList.add(job)
//        }
    }

    suspend fun clean(){
//        settingActionAsyncCoroutineListMutex.withLock {
            settingActionAsyncCoroutineList.forEach {
                it?.cancel()
            }
            settingActionAsyncCoroutineList.clear()
        }
//    }
}