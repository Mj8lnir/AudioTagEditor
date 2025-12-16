package com.mj8lnir.audiotageditor

import org.jaudiotagger.audio.AudioFile
import org.jaudiotagger.audio.AudioFileIO
import org.jaudiotagger.tag.FieldKey
import org.springframework.stereotype.Service
import java.io.File

@Service
internal class AudioTagService {

    private companion object {
        const val DELIMITER = " - "
        const val DEFAULT_ALBUM_NAME = "Music"
        const val DEFAULT_PLAYLIST_NAME = "Music"
    }

    fun editTags(
        files: Set<File>,
        albumName: String,
        createPlaylist: Boolean,
        playlistName: String,
    ): Int {
        val successCount = files.count { file ->
            processAudioFile(AudioFileIO.read(file), albumName)
        }
        return successCount
    }

    private fun processAudioFile(audioFile: AudioFile, albumName: String): Boolean {
        val artistName = extractArtistName(audioFile.file)
        val title = extractTitle(audioFile.file)
        if (title.isNotBlank() && artistName.isNotBlank()) {
            val tag = audioFile.tagOrCreateAndSetDefault
            tag.setField(FieldKey.ALBUM, albumName.ifBlank { DEFAULT_ALBUM_NAME })
            tag.setField(FieldKey.ARTIST, artistName)
            tag.setField(FieldKey.TITLE, title)
            AudioFileIO.write(audioFile)
            return true
        }
        return false
    }

    fun extractArtistName(file: File): String {
        val fileName = file.nameWithoutExtension
        return if (fileName.contains(DELIMITER)) {
            fileName
                .substringBefore(DELIMITER)
                .trim()
                .replaceFirstChar { it.uppercaseChar() }
        } else {
            ""
        }
    }

    fun extractTitle(file: File): String {
        val fileName = file.nameWithoutExtension
        return if (fileName.contains(DELIMITER)) {
            fileName.substringAfter(DELIMITER)
                .trim()
                .replaceFirstChar { it.uppercaseChar() }
        } else {
            ""
        }
    }
}