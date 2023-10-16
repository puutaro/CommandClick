package com.puutaro.commandclick.service

import java.io.*
import java.util.*


private fun mergeMp3(inputPathList: List<String>, outputPath: String) {
    val iterator = inputPathList.iterator()
//        val iterator = inputs.iterator()
    SequenceInputStream(object : Enumeration<InputStream> {
        override fun hasMoreElements(): Boolean = iterator.hasNext()
        override fun nextElement(): InputStream = FileInputStream(iterator.next())
    }).use { sequenceStream ->
        FileOutputStream(outputPath).use { outputStream ->
            generateSequence { sequenceStream.read() }
                .takeWhile { it != -1 }
                .forEach { outputStream.write(it) }
        }
    }
}
