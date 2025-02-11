package com.puutaro.commandclick.proccess.edit.setting_action.libs

import com.puutaro.commandclick.proccess.edit.setting_action.SettingActionKeyManager
import kotlinx.coroutines.Deferred
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentMap
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.locks.ReentrantReadWriteLock
import kotlin.concurrent.withLock

object SettingActionData {

    class LoopKeyToVarNameValueStrMap {
        private val loopKeyToVarNameValueStrMap: ConcurrentHashMap<String, ConcurrentHashMap<String, String>> = ConcurrentHashMap()
//        private val loopKeyToVarNameValueStrMapMutex = ReentrantReadWriteLock()
        fun getAsyncVarNameToValueStr(
            loopKey: String
        ): ConcurrentHashMap<String, String>? {
            return loopKeyToVarNameValueStrMap.get(
                loopKey
            )
//    loopKeyToVarNameValueStrMapMutex.readLock().withLock {
//                loopKeyToVarNameValueStrMap.get(
//                    loopKey
//                )
//            }
        }

        suspend fun put(
            loopKey: String,
            varName: String,
            valueStr: String?,
        ){
//            loopKeyToVarNameValueStrMapMutex.writeLock().withLock {
            if(
                valueStr.isNullOrEmpty()
            ) return

            loopKeyToVarNameValueStrMap.get(
                loopKey
            ).let { curVarNameValueStrMap ->
                when (curVarNameValueStrMap.isNullOrEmpty()) {
                    false -> curVarNameValueStrMap.put(
                        varName,
                        valueStr
                    )

                    else -> {
                        val varNameValueStrMap: ConcurrentHashMap<String, String> =
                            ConcurrentHashMap()
                        varNameValueStrMap.put(
                            varName,
                            valueStr
                        )
                        loopKeyToVarNameValueStrMap.put(
                            loopKey,
                            varNameValueStrMap,
                        )
                    }
                }
            }
        }

        fun initPrivateLoopKeyVarNameValueStrMapMutex(
            loopKey: String
        ) {
            loopKeyToVarNameValueStrMap.get(
                    loopKey
                )?.clear()
//            loopKeyToVarNameValueStrMapMutex.writeLock().withLock {
//                loopKeyToVarNameValueStrMap.get(
//                    loopKey
//                )?.clear()
//            }
        }

        fun clearPrivateLoopKeyVarNameValueStrMapMutex(loopKey: String){
//            loopKeyToVarNameValueStrMapMutex.writeLock().withLock {
                loopKeyToVarNameValueStrMap.remove(
                    loopKey
                )
//            }
        }
    }

    class PrivateLoopKeyVarNameValueStrMap {
        private val privateLoopKeyVarNameValueStrMap =
            ConcurrentHashMap<String, ConcurrentHashMap<String, String>>()
//        private val privateLoopKeyVarNameValueStrMapMutex = ReentrantReadWriteLock()
        suspend fun getAsyncVarNameToValueStr(
            loopKey: String
        ): ConcurrentHashMap<String, String>? {
            return privateLoopKeyVarNameValueStrMap.get(
                loopKey
            )
//    privateLoopKeyVarNameValueStrMapMutex.readLock().withLock {
//                privateLoopKeyVarNameValueStrMap.get(
//                    loopKey
//                )
//            }
        }

        fun put(
            loopKey: String,
            varName: String,
            valueStr: String?,
        ){
//            privateLoopKeyVarNameValueStrMapMutex.writeLock().withLock {
            if(
                valueStr.isNullOrEmpty()

            ) return
            privateLoopKeyVarNameValueStrMap.get(
                loopKey
            ).let { curPrivateVarNameValueMap ->
                when (curPrivateVarNameValueMap.isNullOrEmpty()) {
                    false -> curPrivateVarNameValueMap.put(
                        varName,
                        valueStr
                    )

                    else -> {
                        val vaNameToValueStrMap: ConcurrentHashMap<String, String> =
                            ConcurrentHashMap()
                        vaNameToValueStrMap.put(
                            varName,
                            valueStr
                        )
                        privateLoopKeyVarNameValueStrMap.put(
                            loopKey,
                            vaNameToValueStrMap,
                        )
                    }
                }
            }
//            }
        }

        suspend fun initPrivateLoopKeyVarNameValueStrMapMutex(
            loopKey: String
        ) {
            privateLoopKeyVarNameValueStrMap.get(
                loopKey
            )?.clear()
//            privateLoopKeyVarNameValueStrMapMutex.writeLock().withLock {
//                privateLoopKeyVarNameValueStrMap.get(
//                    loopKey
//                )?.clear()
//            }
        }

        suspend fun clearPrivateLoopKeyVarNameValueStrMapMutex(loopKey: String){
            privateLoopKeyVarNameValueStrMap.remove(
                loopKey
            )
//            privateLoopKeyVarNameValueStrMapMutex.writeLock().withLock {
//                privateLoopKeyVarNameValueStrMap.remove(
//                    loopKey
//                )
//            }
        }
    }

    class LoopKeyToAsyncDeferredVarNameValueStrMap {
        private val loopKeyToAsyncDeferredVarNameValueStrMap = ConcurrentHashMap<
                String,
                ConcurrentHashMap <
                        String,
                        Deferred<
                                Pair<
                                        Pair<String, String?>,
                                        SettingActionKeyManager.BreakSignal?
                                        >?
                                >
                        >
                >()
//        private val asyncLoopKeyToVarNameValueStrMapMutex = ReentrantReadWriteLock()
        fun getAsyncVarNameToValueStrAndExitSignal(loopKey: String):  MutableMap <
                String,
                Deferred<
                        Pair<
                                Pair<String, String?>,
                                SettingActionKeyManager.BreakSignal?
                                >?
                        >
                >? {
            return  loopKeyToAsyncDeferredVarNameValueStrMap.get(
                loopKey
            )

//        asyncLoopKeyToVarNameValueStrMapMutex.readLock().withLock {
//                loopKeyToAsyncDeferredVarNameValueStrMap.get(
//                    loopKey
//                )
//            }
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
//            asyncLoopKeyToVarNameValueStrMapMutex.writeLock().withLock {
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

                        else -> {
                            val asyncDeferredVarNameValueStrMap: ConcurrentHashMap <
                                    String,
                                    Deferred<
                                            Pair<
                                                    Pair<String, String?>,
                                                    SettingActionKeyManager.BreakSignal?
                                                    >?
                                            >
                                    > = ConcurrentHashMap()
                            asyncDeferredVarNameValueStrMap.put(
                                varName,
                                deferredVarNameValueStrMapAndBreakSignal
                            )
                            loopKeyToAsyncDeferredVarNameValueStrMap.put(
                                loopKey,
                                asyncDeferredVarNameValueStrMap
//                                mutableMapOf(varName to deferredVarNameValueStrMapAndBreakSignal)
                            )
                        }
                    }
                }
//            }
        }

        fun clearVarName(
            loopKey: String,
            varName: String,
        ) {
//            asyncLoopKeyToVarNameValueStrMapMutex.writeLock().withLock {
                loopKeyToAsyncDeferredVarNameValueStrMap.get(
                    loopKey
                )?.remove(varName)
//            }
        }

        fun clearAsyncVarNameToValueStrAndExitSignal(loopKey: String) {
//            asyncLoopKeyToVarNameValueStrMapMutex.writeLock().withLock {
                loopKeyToAsyncDeferredVarNameValueStrMap.remove(
                    loopKey
                )
//            }
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