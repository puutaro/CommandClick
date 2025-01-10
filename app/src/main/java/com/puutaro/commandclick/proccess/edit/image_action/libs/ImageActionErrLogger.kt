package com.puutaro.commandclick.proccess.edit.image_action.libs

import android.content.Context
import com.puutaro.commandclick.common.variable.CheckTool
import com.puutaro.commandclick.util.LogSystems
import com.puutaro.commandclick.util.datetime.LocalDatetimeTool
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup
import java.time.LocalDateTime
import java.util.concurrent.locks.ReentrantReadWriteLock
import kotlin.concurrent.withLock

object ImageActionErrLogger {
    private val durationSec = 5

    enum class ImageActionErrType {
        I_VAR,
        I_AC_VAR,
        I_RETURN,
        FUNC,
        I_IF,
        AWAIT,
    }

//            object AlreadyErr {
//                private var isAlreadyErr = false
//                private val mutex = Mutex()
//
//                suspend fun init(){
//                    mutex.withLock {
//                        isAlreadyErr = false
//                    }
//                }
//
//                suspend fun enable() {
//                    mutex.withLock {
//                        isAlreadyErr = true
//                    }
//                }
//
//                suspend fun get(): Boolean {
//                    mutex.withLock {
//                        return isAlreadyErr
//                    }
//                }
//            }

    object LogDatetime {
        private var beforeOutputTime = LocalDateTime.parse("2020-02-15T21:30:50")
        private val mutex = ReentrantReadWriteLock()

        suspend fun update(
            datetime: LocalDateTime
        ) {
            mutex.writeLock().withLock {
                beforeOutputTime = datetime
            }
        }

        suspend fun get(): LocalDateTime {
            mutex.readLock().withLock {
                return beforeOutputTime
            }
        }
    }

    suspend fun sendErrLog(
        context: Context?,
        errType: ImageActionErrType,
        errMessage: String,
        keyToSubKeyConWhere: String,
    ) {
        val currentDatetime =
            withContext(Dispatchers.IO) {
                LocalDateTime.now()
            }
        val diffSec = withContext(Dispatchers.IO) {
            val beforeOutputTime = LogDatetime.get()
            LocalDatetimeTool.getDurationSec(
                beforeOutputTime,
                currentDatetime
            )
        }
        if(diffSec < durationSec) return
//                if(AlreadyErr.get()) return
        withContext(Dispatchers.IO) {
            LogDatetime.update(
                currentDatetime
            )
//                    AlreadyErr.enable()
        }
        val spanKeyToSubKeyConWhere =
            CheckTool.LogVisualManager.execMakeSpanTagHolder(
                CheckTool.errBrown,
                keyToSubKeyConWhere
            )
        val errToastMessage =
            Jsoup.parse(errMessage).text()
        val logErrMessage =
            "[SETTING ACTION] (${errType.name}) $errMessage about ${spanKeyToSubKeyConWhere}"
        LogSystems.broadErrLog(
            context,
            errToastMessage,
            logErrMessage,
        )
    }
}