package com.puutaro.commandclick.proccess.edit.image_action.libs

import android.graphics.Bitmap
import com.puutaro.commandclick.proccess.edit.image_action.ImageActionKeyManager
import kotlinx.coroutines.Deferred
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.locks.ReentrantReadWriteLock
import kotlin.concurrent.withLock

object ImageActionData {
    class LoopKeyToAsyncDeferredVarNameBitmapMap {
        private val loopKeyToAsyncDeferredVarNameBitmapMap: ConcurrentHashMap<
                String,
                ConcurrentHashMap <
                        String,
                        Deferred<
                                Pair<
                                        Pair<String, Bitmap?>,
                                        ImageActionKeyManager.BreakSignal?
                                        >?
                                >
                        >
                > = ConcurrentHashMap()
//        mutableMapOf<
//                String,
//                MutableMap <
//                        String,
//                        Deferred<
//                                Pair<
//                                        Pair<String, Bitmap?>,
//                                        ImageActionKeyManager.BreakSignal?
//                                        >?
//                                >
//                        >
//                >()
//        private val asyncLoopKeyToVarNameBitmapMapMutex = ReentrantReadWriteLock()
        suspend fun getAsyncVarNameToBitmapAndExitSignal(loopKey: String):  ConcurrentHashMap <
                String,
                Deferred<
                        Pair<
                                Pair<String, Bitmap?>,
                                ImageActionKeyManager.BreakSignal?
                                >?
                        >
                >? {
            return loopKeyToAsyncDeferredVarNameBitmapMap.get(
                loopKey
            )
//    asyncLoopKeyToVarNameBitmapMapMutex.readLock().withLock {
//                loopKeyToAsyncDeferredVarNameBitmapMap.get(
//                    loopKey
//                )
//            }
        }

        suspend fun put(
            loopKey: String,
            varName: String,
            deferredVarNameBitmapMapAndBreakSignal: Deferred<
                    Pair<
                            Pair<String, Bitmap?>,
                            ImageActionKeyManager.BreakSignal?
                            >?
                    >
        ){
//            asyncLoopKeyToVarNameBitmapMapMutex.writeLock().withLock {
                loopKeyToAsyncDeferredVarNameBitmapMap.get(
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
                                deferredVarNameBitmapMapAndBreakSignal
                            )
                        }

                        else -> {
                            val asyncDeferredVarNameBitmapMap: ConcurrentHashMap <
                                    String,
                                    Deferred<
                                            Pair<
                                                    Pair<String, Bitmap?>,
                                                    ImageActionKeyManager.BreakSignal?
                                                    >?
                                            >
                                    > = ConcurrentHashMap()
                            asyncDeferredVarNameBitmapMap.put(
                                varName,
                                deferredVarNameBitmapMapAndBreakSignal
                            )
                            loopKeyToAsyncDeferredVarNameBitmapMap.put(
                                loopKey,
                                asyncDeferredVarNameBitmapMap
//                                mutableMapOf(varName to deferredVarNameBitmapMapAndBreakSignal)
                            )
                        }
                    }
                }
//            }
        }

        suspend fun clearVarName(
            loopKey: String,
            varName: String,
        ) {
//            asyncLoopKeyToVarNameBitmapMapMutex.writeLock().withLock {
                loopKeyToAsyncDeferredVarNameBitmapMap.get(
                    loopKey
                )?.remove(varName)
//            }
        }

        suspend fun clearAsyncVarNameToBitmapAndExitSignal(loopKey: String) {
//            asyncLoopKeyToVarNameBitmapMapMutex.writeLock().withLock {
                loopKeyToAsyncDeferredVarNameBitmapMap.remove(
                    loopKey
                )
//            }
        }
    }

    class PrivateLoopKeyVarNameBitmapMap {
        private val privateLoopKeyVarNameBitmapMap =
            ConcurrentHashMap<String, ConcurrentHashMap<String, Bitmap>>()
//        private val privateLoopKeyVarNameBitmapMapMutex = ReentrantReadWriteLock()
        suspend fun getAsyncVarNameToBitmap(
            loopKey: String
        ): MutableMap<String, Bitmap>? {
            return privateLoopKeyVarNameBitmapMap.get(
                loopKey
            )
//    privateLoopKeyVarNameBitmapMapMutex.readLock().withLock {
//                privateLoopKeyVarNameBitmapMap.get(
//                    loopKey
//                )
//            }
        }

        suspend fun put(
            loopKey: String,
            varName: String,
            bitmap: Bitmap?,
        ){
//            privateLoopKeyVarNameBitmapMapMutex.writeLock().withLock {
            if(bitmap == null) return
            privateLoopKeyVarNameBitmapMap.get(
                loopKey
            ).let { curPrivateVarNameValueMap ->
                when (curPrivateVarNameValueMap.isNullOrEmpty()) {
                    false -> curPrivateVarNameValueMap.put(
                        varName,
                        bitmap
                    )

                    else -> {
                        val loopKeyVarNameBitmapMap: ConcurrentHashMap<String, Bitmap> = ConcurrentHashMap()
                        loopKeyVarNameBitmapMap.put(
                            varName,
                            bitmap
                        )
                        privateLoopKeyVarNameBitmapMap.put(
                            loopKey,
                            loopKeyVarNameBitmapMap,
//                            mutableMapOf(varName to bitmap)
                        )
                    }
                }
            }
//            }
        }

        suspend fun initPrivateLoopKeyVarNameBitmapMapMutex(
            loopKey: String
        ) {
            privateLoopKeyVarNameBitmapMap.get(
                loopKey
            )?.clear()
//            privateLoopKeyVarNameBitmapMapMutex.writeLock().withLock {
//                privateLoopKeyVarNameBitmapMap.get(
//                    loopKey
//                )?.clear()
//            }
        }

        suspend fun clearPrivateLoopKeyVarNameBitmapMapMutex(loopKey: String){
//            privateLoopKeyVarNameBitmapMapMutex.writeLock().withLock {
                privateLoopKeyVarNameBitmapMap.remove(
                    loopKey
                )
//            }
        }
    }

    class LoopKeyToVarNameBitmapMap {
        private val loopKeyToVarNameBitmapMap =
            ConcurrentHashMap<String, ConcurrentHashMap<String, Bitmap>>()
//        private val loopKeyToVarNameBitmapMapMutex = ReentrantReadWriteLock()
        suspend fun getAsyncVarNameToBitmap(
            loopKey: String
        ): MutableMap<String, Bitmap>? {
            return loopKeyToVarNameBitmapMap.get(
                loopKey
            )
//    loopKeyToVarNameBitmapMapMutex.readLock().withLock {
//                loopKeyToVarNameBitmapMap.get(
//                    loopKey
//                )
//            }
        }

        suspend fun put(
            loopKey: String,
            varName: String,
            bitmap: Bitmap?,
        ){
//            loopKeyToVarNameBitmapMapMutex.writeLock().withLock {
            if(bitmap == null) return
            loopKeyToVarNameBitmapMap.get(
                loopKey
            ).let { curVarNameBitmapMap ->
                when (curVarNameBitmapMap.isNullOrEmpty()) {
                    false -> curVarNameBitmapMap.put(
                        varName,
                        bitmap
                    )

                    else -> {
                        val varNameBitmapMap = ConcurrentHashMap<String, Bitmap>()
                        varNameBitmapMap.put(
                            varName,
                            bitmap
                        )
                        loopKeyToVarNameBitmapMap.put(
                            loopKey,
                            varNameBitmapMap
//                            mutableMapOf(varName to bitmap)
                        )
                    }
                }
            }
//            }
        }

        suspend fun initPrivateLoopKeyVarNameBitmapMapMutex(
            loopKey: String
        ) {
//            loopKeyToVarNameBitmapMapMutex.writeLock().withLock{
                loopKeyToVarNameBitmapMap.get(
                    loopKey
                )?.clear()
//            }
        }

        suspend fun clearPrivateLoopKeyVarNameBitmapMapMutex(loopKey: String){
//            loopKeyToVarNameBitmapMapMutex.writeLock().withLock {
                loopKeyToVarNameBitmapMap.remove(
                    loopKey
                )
//            }
        }
    }

    class ImageActionExitManager {
        private var exitSignal = AtomicBoolean(false)
        //        private val exitSignalMutex = Mutex()
        suspend fun setExit(){
//            exitSignalMutex.withLock {
            exitSignal = AtomicBoolean(true)
//            }
        }

        suspend fun get(): Boolean {
            return exitSignal.get()
//            exitSignalMutex.withLock {
//                exitSignal
//            }
        }
    }
}