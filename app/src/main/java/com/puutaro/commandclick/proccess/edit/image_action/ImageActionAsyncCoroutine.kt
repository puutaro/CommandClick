package com.puutaro.commandclick.proccess.edit.image_action

import kotlinx.coroutines.Job
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class ImageActionAsyncCoroutine {

    private val imageActionAsyncCoroutineList = arrayListOf<Job?>()
    private val imageActionAsyncCoroutineListMutex = Mutex()

    suspend fun put(job: Job?){
        imageActionAsyncCoroutineListMutex.withLock {
            imageActionAsyncCoroutineList.add(job)
        }
    }

    suspend fun clean(){
        imageActionAsyncCoroutineListMutex.withLock {
            imageActionAsyncCoroutineList.forEach {
                it?.cancel()
            }
            imageActionAsyncCoroutineList.clear()
        }
    }
}