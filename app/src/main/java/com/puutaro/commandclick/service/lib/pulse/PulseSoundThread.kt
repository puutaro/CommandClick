package com.puutaro.commandclick.service.lib.pulse

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioManager
import android.media.AudioTrack
import com.puutaro.commandclick.common.variable.network.UsePort
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.BufferedInputStream
import java.io.IOException
import java.io.UnsupportedEncodingException
import java.net.Socket
import java.net.UnknownHostException


class PulseSoundThread(
    private val pcIpv4Address: String,
) {
    private var mTerminate = false
    private val mPort = UsePort.pluseRecieverPort.num
    private var pulseReceiverSock: Socket? = null
    private var audioData: BufferedInputStream? = null


    fun terminate() {
        mTerminate = true
    }

    fun exit(){
        try {
            if(
                pulseReceiverSock?.isClosed != true
            ) pulseReceiverSock?.close()
        } catch (e: Exception){
            print("pass")
        }
    }

    fun enableSock():Boolean{
        try {
            val socket = Socket(pcIpv4Address, mPort)
            socket.close()
            return true
        } catch(e: Exception){
            return false
        }
    }

    suspend fun run(
        context: Context?,
    ) {
        try {
            withContext(Dispatchers.IO) {
                pulseReceiverSock = Socket(pcIpv4Address, mPort)
            }
        } catch (e: UnknownHostException) {
            // TODO if the host name could not be resolved into an IP address.
            terminate()
            e.printStackTrace()
        } catch (e: IOException) {
            // TODO if an error occurs while creating the socket
            terminate()
            e.printStackTrace()
        } catch (e: SecurityException) {
            // TODO if a security manager exists and it denies the permission to
            // connect to the given address and port.
            terminate()
            e.printStackTrace()
        }
        try {
            audioData = withContext(Dispatchers.IO) {
                BufferedInputStream(pulseReceiverSock?.getInputStream())
            }
        } catch (e: UnsupportedEncodingException) {
            // TODO Auto-generated catch block
            terminate()
            e.printStackTrace()
        } catch (e: IOException) {
            // TODO Auto-generated catch block
            terminate()
            e.printStackTrace()
        }
        if (mTerminate) return

        // Create AudioPlayer
        /*
		 * final int sampleRate = AudioTrack
		 * .getNativeOutputSampleRate(AudioManager.STREAM_MUSIC);
		 */
        // TODO native audio?
        val audioManager = context?.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        val sampleRate = audioManager.getProperty(AudioManager.PROPERTY_OUTPUT_SAMPLE_RATE).toInt()

        val musicLength = AudioTrack.getMinBufferSize(
            sampleRate,
            AudioFormat.CHANNEL_OUT_MONO,
            AudioFormat.ENCODING_PCM_16BIT
        )

        val audioAttributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_MEDIA)
            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
            .build()

        val audioFormat = AudioFormat.Builder()
            .setSampleRate(sampleRate)
            .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
            .setChannelMask(AudioFormat.CHANNEL_OUT_STEREO)
            .build()


        // then, initialize with new constructor
        val audioTrack = AudioTrack(
            audioAttributes,
            audioFormat,
            musicLength * 2,
            AudioTrack.MODE_STREAM,
            0
        )
        audioTrack.play()

        // TODO buffer size computation
        val audioBuffer = ByteArray(musicLength * 8)
        while (!mTerminate) {
            try {
                val sizeRead = withContext(Dispatchers.IO) {
                    audioData?.read(
                        audioBuffer,
                        0,
                        musicLength * 8
                    ) ?: 0
                }
                var sizeWrite = audioTrack.write(audioBuffer, 0, sizeRead)
                if (sizeWrite == AudioTrack.ERROR_INVALID_OPERATION) {
                    sizeWrite = 0
                }
                if (sizeWrite == AudioTrack.ERROR_BAD_VALUE) {
                    sizeWrite = 0
                }
            } catch (e: IOException) {
                // TODO Auto-generated catch block
                e.printStackTrace()
            }
        }
        audioTrack.stop()
        pulseReceiverSock?.isClosed
        audioData = null
    }
}