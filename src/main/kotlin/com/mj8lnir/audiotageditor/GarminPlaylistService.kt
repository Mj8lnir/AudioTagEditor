package com.mj8lnir.audiotageditor

import org.springframework.stereotype.Service
import java.io.File
import java.io.FileWriter
import java.nio.charset.StandardCharsets
import kotlin.io.path.Path

@Service
internal class GarminPlaylistService {

    companion object {
        private const val GARMIN_MUSIC_PREFIX = "0:/MUSIC"
    }

    fun createPlaylist(files: Set<File>, playlistName: String): Boolean {
        val parentFolder = files.first().parentFile?.parent ?: return false
        val playlistFile = Path(parentFolder).resolve("$playlistName.m3u8").toFile()
        try {
            if (playlistFile.exists()) {
                playlistFile.delete()
            } else {
                playlistFile.createNewFile()
            }
            FileWriter(playlistFile, StandardCharsets.UTF_8).use { writer ->
                files.forEach { file ->
                    writer.write("$GARMIN_MUSIC_PREFIX/${file.parentFile.name}/${file.name}".uppercase())
                    writer.write(System.lineSeparator())
                }
                writer.flush()
            }
        } catch (_: Exception) {
            return false
        }
        return true
    }
}