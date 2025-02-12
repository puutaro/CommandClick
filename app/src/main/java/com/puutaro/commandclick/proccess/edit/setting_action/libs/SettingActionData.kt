package com.puutaro.commandclick.proccess.edit.setting_action.libs

import com.puutaro.commandclick.proccess.edit.setting_action.SettingActionKeyManager
import kotlinx.coroutines.Deferred
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.locks.ReentrantReadWriteLock
import kotlin.concurrent.withLock

object SettingActionData {

    class LoopKeyToVarNameValueStrMap {
        private val loopKeyToVarNameValueStrMap =
            mutableMapOf<String, MutableMap<String, String?>>()
        private val loopKeyToVarNameValueStrMapMutex = ReentrantReadWriteLock()
//        fun getAsyncVarNameToValueStrMap(
//            loopKey: String
//        ): MutableMap<String, String?>? {
//            return loopKeyToVarNameValueStrMapMutex.readLock().withLock {
//                loopKeyToVarNameValueStrMap.get(
//                    loopKey
//                )
//            }
//        }

        fun convertAsyncVarNameToValueStrMapToMap(
            loopKey: String
        ): Map<String, String?>? {
            return loopKeyToVarNameValueStrMapMutex.readLock().withLock {
                loopKeyToVarNameValueStrMap.get(
                    loopKey
                )?.toMap()
            }
        }

        fun getAsyncVarNameToValueStrFromMap(
            loopKey: String,
            varName: String,
        ): String? {
            return loopKeyToVarNameValueStrMapMutex.readLock().withLock {
                loopKeyToVarNameValueStrMap.get(
                    loopKey
                )?.get(
                    varName
                )
            }
        }

        suspend fun put(
            loopKey: String,
            varName: String,
            valueStr: String?,
        ){
            loopKeyToVarNameValueStrMapMutex.writeLock().withLock {
                loopKeyToVarNameValueStrMap.get(
                    loopKey
                ).let { curVarNameValueStrMap ->
                    when (curVarNameValueStrMap.isNullOrEmpty()) {
                        false -> curVarNameValueStrMap.put(
                            varName,
                            valueStr
                        )

                        else -> loopKeyToVarNameValueStrMap.put(
                            loopKey,
                            mutableMapOf(varName to valueStr)
                        )
                    }
                }
            }
        }

        fun initPrivateLoopKeyVarNameValueStrMapMutex(
            loopKey: String
        ) {
            return loopKeyToVarNameValueStrMapMutex.writeLock().withLock {
                loopKeyToVarNameValueStrMap.get(
                    loopKey
                )?.clear()
            }
        }

        fun clearPrivateLoopKeyVarNameValueStrMapMutex(loopKey: String){
            loopKeyToVarNameValueStrMapMutex.writeLock().withLock {
                loopKeyToVarNameValueStrMap.remove(
                    loopKey
                )
            }
        }
    }

    class PrivateLoopKeyVarNameValueStrMap {
        private val privateLoopKeyVarNameValueStrMap =
            mutableMapOf<String, MutableMap<String, String?>>()
        private val privateLoopKeyVarNameValueStrMapMutex = ReentrantReadWriteLock()
//        suspend fun getAsyncVarNameToValueStrMap(
//            loopKey: String
//        ): MutableMap<String, String?>? {
//            return privateLoopKeyVarNameValueStrMapMutex.readLock().withLock {
//                privateLoopKeyVarNameValueStrMap.get(
//                    loopKey
//                )
//            }
//        }

        suspend fun convertAsyncVarNameToValueStrToMap(
            loopKey: String,
        ): Map<String, String?>? {
            return privateLoopKeyVarNameValueStrMapMutex.readLock().withLock {
                privateLoopKeyVarNameValueStrMap.get(
                    loopKey
                )?.toMap()
            }
        }

        suspend fun getAsyncVarNameToValueStrFromMap(
            loopKey: String,
            varName: String,
        ): String? {
            return privateLoopKeyVarNameValueStrMapMutex.readLock().withLock {
                privateLoopKeyVarNameValueStrMap.get(
                    loopKey
                )?.get(varName)
            }
        }

        fun put(
            loopKey: String,
            varName: String,
            valueStr: String?,
        ){
            privateLoopKeyVarNameValueStrMapMutex.writeLock().withLock {
                privateLoopKeyVarNameValueStrMap.get(
                    loopKey
                ).let { curPrivateVarNameValueMap ->
                    when (curPrivateVarNameValueMap.isNullOrEmpty()) {
                        false -> curPrivateVarNameValueMap.put(
                            varName,
                            valueStr
                        )

                        else -> privateLoopKeyVarNameValueStrMap.put(
                            loopKey,
                            mutableMapOf(varName to valueStr)
                        )
                    }
                }
            }
        }

        suspend fun initPrivateLoopKeyVarNameValueStrMapMutex(
            loopKey: String
        ) {
            return privateLoopKeyVarNameValueStrMapMutex.writeLock().withLock {
                privateLoopKeyVarNameValueStrMap.get(
                    loopKey
                )?.clear()
            }
        }

        suspend fun clearPrivateLoopKeyVarNameValueStrMapMutex(loopKey: String){
            privateLoopKeyVarNameValueStrMapMutex.writeLock().withLock {
                privateLoopKeyVarNameValueStrMap.remove(
                    loopKey
                )
            }
        }
    }

    class LoopKeyToAsyncDeferredVarNameValueStrMap {
        private val loopKeyToAsyncDeferredVarNameValueStrMap = mutableMapOf<
                String,
                MutableMap <
                        String,
                        Deferred<
                                Pair<
                                        Pair<String, String?>,
                                        SettingActionKeyManager.BreakSignal?
                                        >?
                                >
                        >
                >()
        private val asyncLoopKeyToVarNameValueStrMapMutex = ReentrantReadWriteLock()
//        fun getAsyncVarNameToValueStrAndExitSignalMap(loopKey: String):  MutableMap <
//                String,
//                Deferred<
//                        Pair<
//                                Pair<String, String?>,
//                                SettingActionKeyManager.BreakSignal?
//                                >?
//                        >
//                >? {
//            return asyncLoopKeyToVarNameValueStrMapMutex.readLock().withLock {
//                loopKeyToAsyncDeferredVarNameValueStrMap.get(
//                    loopKey
//                )
//            }
//        }

        fun getAsyncVarNameToValueStrAndExitSignalFromMap(
            loopKey: String,
            varName: String,
        ): Deferred<
                        Pair<
                                Pair<String, String?>,
                                SettingActionKeyManager.BreakSignal?
                                >?
                        >? {
            return asyncLoopKeyToVarNameValueStrMapMutex.readLock().withLock {
                loopKeyToAsyncDeferredVarNameValueStrMap.get(
                    loopKey
                )?.get(varName)
            }
        }

        fun put(
            loopKey: String,
            varName: String,
            deferredVarNameValueStrMapAndBreakSignal: Deferred<
                    Pair<
                            Pair<String, String?>,
                            SettingActionKeyManager.BreakSignal?
                            >?
                    >
        ){
            asyncLoopKeyToVarNameValueStrMapMutex.writeLock().withLock {
                loopKeyToAsyncDeferredVarNameValueStrMap.get(
                    loopKey
                ).let { curPrivateMapLoopKeyVarNameValueMap ->
                    when (curPrivateMapLoopKeyVarNameValueMap.isNullOrEmpty()) {
                        false -> {
                            val isAlreadyExist =
                                curPrivateMapLoopKeyVarNameValueMap
                                    .get(varName) != null
                            if(isAlreadyExist) return@let
                            curPrivateMapLoopKeyVarNameValueMap.put(
                                varName,
                                deferredVarNameValueStrMapAndBreakSignal
                            )
                        }

                        else -> loopKeyToAsyncDeferredVarNameValueStrMap.put(
                            loopKey,
                            mutableMapOf(varName to deferredVarNameValueStrMapAndBreakSignal)
                        )
                    }
                }
            }
        }

        fun clearVarName(
            loopKey: String,
            varName: String,
        ) {
            asyncLoopKeyToVarNameValueStrMapMutex.writeLock().withLock {
                loopKeyToAsyncDeferredVarNameValueStrMap.get(
                    loopKey
                )?.remove(varName)
            }
        }

        fun clearAsyncVarNameToValueStrAndExitSignal(loopKey: String) {
            asyncLoopKeyToVarNameValueStrMapMutex.writeLock().withLock {
                loopKeyToAsyncDeferredVarNameValueStrMap.remove(
                    loopKey
                )
            }
        }
    }

    class SettingActionExitManager {
        private var exitSignal = AtomicBoolean(false)
        //        private val exitSignalMutex = ReentrantReadWriteLock()
        fun setExit(){

//            exitSignalMutex.writeLock().withLock{
            exitSignal = AtomicBoolean(true)
//            }
        }

        fun get(): Boolean {
            return exitSignal.get()
        }
    }

}