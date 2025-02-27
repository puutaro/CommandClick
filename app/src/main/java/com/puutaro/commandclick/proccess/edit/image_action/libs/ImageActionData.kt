package com.puutaro.commandclick.proccess.edit.image_action.libs

import android.graphics.Bitmap
import com.puutaro.commandclick.proccess.edit.image_action.ImageActionKeyManager
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.util.concurrent.locks.ReentrantReadWriteLock
import kotlin.concurrent.withLock

object ImageActionData {
    class LoopKeyToAsyncDeferredVarNameBitmapMap {
        private val loopKeyToAsyncDeferredVarNameBitmapMap = hashMapOf<
                String,
                HashMap <
                        String,
                        Deferred<
                                Pair<
                                        Pair<String, Bitmap?>,
                                        ImageActionKeyManager.BreakSignal?
                                        >?
                                >
                        >
                >()
        private val asyncLoopKeyToVarNameBitmapMapMutex = ReentrantReadWriteLock()
//        suspend fun getAsyncVarNameToBitmapAndExitSignalMap(loopKey: String):  MutableMap <
//                String,
//                Deferred<
//                        Pair<
//                                Pair<String, Bitmap?>,
//                                ImageActionKeyManager.BreakSignal?
//                                >?
//                        >
//                >? {
//            return asyncLoopKeyToVarNameBitmapMapMutex.readLock().withLock {
//                loopKeyToAsyncDeferredVarNameBitmapMap.get(
//                    loopKey
//                )
//            }
//        }

        suspend fun getAsyncVarNameToBitmapAndExitSignalFromMap(
            loopKey: String,
            varName: String,
        ): Deferred<
                        Pair<
                                Pair<String, Bitmap?>,
                                ImageActionKeyManager.BreakSignal?
                                >?
                        >? {
            return asyncLoopKeyToVarNameBitmapMapMutex.readLock().withLock {
                loopKeyToAsyncDeferredVarNameBitmapMap.get(
                    loopKey
                )?.get(
                    varName
                )
            }
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
            asyncLoopKeyToVarNameBitmapMapMutex.writeLock().withLock {
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

                        else -> loopKeyToAsyncDeferredVarNameBitmapMap.put(
                            loopKey,
                            hashMapOf(varName to deferredVarNameBitmapMapAndBreakSignal)
                        )
                    }
                }
            }
        }

        suspend fun clearVarName(
            loopKey: String,
            varName: String,
        ) {
            asyncLoopKeyToVarNameBitmapMapMutex.writeLock().withLock {
                loopKeyToAsyncDeferredVarNameBitmapMap.get(
                    loopKey
                )?.remove(varName)
            }
        }

        suspend fun clearAsyncVarNameToBitmapAndExitSignal(loopKey: String) {
            asyncLoopKeyToVarNameBitmapMapMutex.writeLock().withLock {
                loopKeyToAsyncDeferredVarNameBitmapMap.remove(
                    loopKey
                )
            }
        }
    }

    class PrivateLoopKeyVarNameBitmapMap {
        private val privateLoopKeyVarNameBitmapMap =
            hashMapOf<String, HashMap<String, Bitmap?>>()
        private val privateLoopKeyVarNameBitmapMapMutex = ReentrantReadWriteLock()
//        suspend fun getAsyncVarNameToBitmapMap(
//            loopKey: String
//        ): MutableMap<String, Bitmap?>? {
//            return privateLoopKeyVarNameBitmapMapMutex.readLock().withLock {
//                privateLoopKeyVarNameBitmapMap.get(
//                    loopKey
//                )
//            }
//        }

        suspend fun getAsyncVarNameToBitmapFromMap(
            loopKey: String,
            varName: String,
        ): Bitmap? {
            return privateLoopKeyVarNameBitmapMapMutex.readLock().withLock {
                privateLoopKeyVarNameBitmapMap.get(
                    loopKey
                )?.get(varName)
            }
        }

        suspend fun convertAsyncVarNameToBitmapToMap(
            loopKey: String,
        ): Map<String, Bitmap?>? {
            return privateLoopKeyVarNameBitmapMapMutex.readLock().withLock {
                privateLoopKeyVarNameBitmapMap.get(
                    loopKey
                )?.toMap()
            }
        }

        suspend fun put(
            loopKey: String,
            varName: String,
            bitmap: Bitmap?,
        ){
            privateLoopKeyVarNameBitmapMapMutex.writeLock().withLock {
                privateLoopKeyVarNameBitmapMap.get(
                    loopKey
                ).let { curPrivateVarNameValueMap ->
                    when (curPrivateVarNameValueMap.isNullOrEmpty()) {
                        false -> curPrivateVarNameValueMap.put(
                            varName,
                            bitmap
                        )

                        else -> privateLoopKeyVarNameBitmapMap.put(
                            loopKey,
                            hashMapOf(varName to bitmap)
                        )
                    }
                }
            }
        }

        suspend fun initPrivateLoopKeyVarNameBitmapMapMutex(
            loopKey: String
        ) {
            return privateLoopKeyVarNameBitmapMapMutex.writeLock().withLock {
                privateLoopKeyVarNameBitmapMap.get(
                    loopKey
                )?.clear()
            }
        }

        suspend fun clearPrivateLoopKeyVarNameBitmapMapMutex(loopKey: String){
            privateLoopKeyVarNameBitmapMapMutex.writeLock().withLock {
                privateLoopKeyVarNameBitmapMap.remove(
                    loopKey
                )
            }
        }
    }

    class LoopKeyToVarNameBitmapMap {
        private val loopKeyToVarNameBitmapMap =
            hashMapOf<String, HashMap<String, Bitmap?>>()
        private val loopKeyToVarNameBitmapMapMutex = ReentrantReadWriteLock()
//        suspend fun getAsyncVarNameToBitmapMap(
//            loopKey: String
//        ): MutableMap<String, Bitmap?>? {
//            return loopKeyToVarNameBitmapMapMutex.readLock().withLock {
//                loopKeyToVarNameBitmapMap.get(
//                    loopKey
//                )
//            }
//        }

        suspend fun getAsyncVarNameToBitmapFromMap(
            loopKey: String,
            varName: String,
        ): Bitmap? {
            return loopKeyToVarNameBitmapMapMutex.readLock().withLock {
                loopKeyToVarNameBitmapMap.get(
                    loopKey
                )?.get(
                    varName
                )
            }
        }

        suspend fun convertAsyncVarNameToBitmapToMap(
            loopKey: String,
        ): Map<String, Bitmap?>? {
            return loopKeyToVarNameBitmapMapMutex.readLock().withLock {
                loopKeyToVarNameBitmapMap.get(
                    loopKey
                )?.toMap()
            }
        }

        suspend fun put(
            loopKey: String,
            varName: String,
            bitmap: Bitmap?,
        ){
            loopKeyToVarNameBitmapMapMutex.writeLock().withLock {
                loopKeyToVarNameBitmapMap.get(
                    loopKey
                ).let { curVarNameBitmapMap ->
                    when (curVarNameBitmapMap.isNullOrEmpty()) {
                        false -> curVarNameBitmapMap.put(
                            varName,
                            bitmap
                        )

                        else -> loopKeyToVarNameBitmapMap.put(
                            loopKey,
                            hashMapOf(varName to bitmap)
                        )
                    }
                }
            }
        }

        suspend fun initPrivateLoopKeyVarNameBitmapMapMutex(
            loopKey: String
        ) {
            return loopKeyToVarNameBitmapMapMutex.writeLock().withLock{
                loopKeyToVarNameBitmapMap.get(
                    loopKey
                )?.clear()
            }
        }

        suspend fun clearPrivateLoopKeyVarNameBitmapMapMutex(loopKey: String){
            loopKeyToVarNameBitmapMapMutex.writeLock().withLock {
                loopKeyToVarNameBitmapMap.remove(
                    loopKey
                )
            }
        }
    }

    class ForVarNameBitmapMap {
        private val forVarNameBitmapMap =
            hashMapOf<String, Bitmap?>()
        private val forVarNameBitmapMapMutex = ReentrantReadWriteLock()
//        suspend fun getAsyncVarNameToBitmapMap(
//            loopKey: String
//        ): MutableMap<String, Bitmap?>? {
//            return loopKeyToVarNameBitmapMapMutex.readLock().withLock {
//                loopKeyToVarNameBitmapMap.get(
//                    loopKey
//                )
//            }
//        }

        suspend fun get(
            varName: String,
        ): Bitmap? {
            return forVarNameBitmapMapMutex.readLock().withLock {
                forVarNameBitmapMap.get(
                    varName
                )
            }
        }

//        suspend fun convertAsyncVarNameToBitmapToMap(
//            loopKey: String,
//        ): Map<String, Bitmap?>? {
//            return forVarNameBitmapMapMutex.readLock().withLock {
//                forVarNameBitmapMap.get(
//                    loopKey
//                )?.toMap()
//            }
//        }

        suspend fun put(
//            loopKey: String,
            varName: String,
            bitmap: Bitmap?,
        ){
            forVarNameBitmapMapMutex.writeLock().withLock {
                forVarNameBitmapMap.put(
                            varName,
                            bitmap
                        )
            }
        }


        suspend fun initPrivateLoopKeyVarNameBitmapMapMutex() {
            forVarNameBitmapMapMutex.writeLock().withLock{
                forVarNameBitmapMap.clear()
            }
        }

//        suspend fun clearPrivateLoopKeyVarNameBitmapMapMutex(loopKey: String){
//            forVarNameBitmapMapMutex.writeLock().withLock {
//                forVarNameBitmapMap.remove(
//                    loopKey
//                )
//            }
//        }
    }

    class ImageActionExitManager {
        private var exitSignal: ImageActionKeyManager.BreakSignal? = null
        //AtomicBoolean(false)
                private val exitSignalMutex = Mutex()
        suspend fun setExitSignal(
            signal: ImageActionKeyManager.BreakSignal?
        ){
            val notUpdateSignalSeq = sequenceOf(
                null,
                ImageActionKeyManager.BreakSignal.RETURN_SIGNAL,
            )
            if(
                notUpdateSignalSeq.contains(signal)
            ) return
            val curSignal = get()
            if(
                curSignal != null
            ) return
            exitSignalMutex.withLock {
                exitSignal = signal
                    //AtomicBoolean(true)
            }
        }

        suspend fun get(): ImageActionKeyManager.BreakSignal? {
            return exitSignalMutex.withLock {
                exitSignal
            }
        }

        suspend fun isExit(): Boolean {
            val signal = get()
            return isExitBySignal(
                signal
            )
        }

        fun isExitBySignal(
            signal: ImageActionKeyManager.BreakSignal?
        ): Boolean {
            val okSignalSequence = sequenceOf(
                null,
                ImageActionKeyManager.BreakSignal.RETURN_SIGNAL,
            )
            if(
                okSignalSequence.contains(signal)
            ) return false
            return true
        }

        companion object {
            fun isStopAfter(
                signal: ImageActionKeyManager.BreakSignal?
            ): Boolean {
                val okSignalSeq = sequenceOf(
                    null,
                    ImageActionKeyManager.BreakSignal.RETURN_SIGNAL,
                )
                if(
                    okSignalSeq.contains(signal)
                ) return false
                val stopSignalSeq = sequenceOf(
                    ImageActionKeyManager.BreakSignal.ERR_EXIT_SIGNAL,
                )
                return stopSignalSeq.contains(signal)
            }
        }
    }
}