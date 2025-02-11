package com.puutaro.commandclick.proccess.edit.image_action

import kotlinx.coroutines.Job
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.util.concurrent.ConcurrentHashMap

class ImageActionAsyncCoroutine {

    private val imageActionAsyncCoroutineList = ConcurrentHashMap.newKeySet<Job?>()
//    private val imageActionAsyncCoroutineListMutex = Mutex()

    suspend fun put(job: Job?){
        if(job == null) return
//        imageActionAsyncCoroutineListMutex.withLock {
            imageActionAsyncCoroutineList.add(job)
//        }
    }

    suspend fun clean(){
//        imageActionAsyncCoroutineListMutex.withLock {
            imageActionAsyncCoroutineList.forEach {
                it?.cancel()
            }
            imageActionAsyncCoroutineList.clear()
//        }
    }
}