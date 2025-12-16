package com.mj8lnir.audiotageditor

import java.io.File
import java.util.concurrent.ConcurrentHashMap

object FileBuffer {

    private val filesBuffer = ConcurrentHashMap.newKeySet<File>()

    fun getFiles(): Set<File> {
        return mutableSetOf<File>().apply {
            addAll(filesBuffer)
        }
    }

    fun getSize(): Int = filesBuffer.size

    fun clearBuffer() {
        filesBuffer.clear()
    }

    fun addFiles(files: Collection<File>) {
        filesBuffer.addAll(files)
    }

    fun isNotEmpty(): Boolean {
        return filesBuffer.isNotEmpty()
    }

    fun isEmpty(): Boolean {
        return filesBuffer.isEmpty()
    }
}