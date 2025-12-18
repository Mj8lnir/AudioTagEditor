package com.mj8lnir.audiotageditor

import org.springframework.stereotype.Component
import java.io.File
import java.util.concurrent.ConcurrentHashMap

@Component
internal class FileBuffer {

    companion object {
        private val filesBuffer = ConcurrentHashMap.newKeySet<File>()
    }

    fun getFiles(): Set<File> = mutableSetOf<File>().apply {
        addAll(filesBuffer)
    }

    fun getSize(): Int = filesBuffer.size

    fun clearBuffer() {
        filesBuffer.clear()
    }

    fun addFiles(files: Collection<File>) {
        filesBuffer.addAll(files)
    }

    fun isEmpty(): Boolean = filesBuffer.isEmpty()

}