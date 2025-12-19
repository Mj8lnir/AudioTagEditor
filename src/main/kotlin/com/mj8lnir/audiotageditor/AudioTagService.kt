package com.mj8lnir.audiotageditor

import org.jaudiotagger.audio.AudioFile
import org.jaudiotagger.audio.AudioFileIO
import org.jaudiotagger.tag.FieldDataInvalidException
import org.jaudiotagger.tag.FieldKey
import org.springframework.stereotype.Service
import java.io.File

@Service
internal class AudioTagService {

    companion object {
        private const val DELIMITER = " - "
        private const val DEFAULT_ALBUM_NAME = "Music"
    }

    fun editTags(files: Set<File>, albumName: String): Int = files
        .count { file -> processAudioFile(AudioFileIO.read(file), albumName) }

    private fun processAudioFile(audioFile: AudioFile, albumName: String): Boolean {
        val artistName = extractArtistName(audioFile.file)
        val title = extractTitle(audioFile.file)
        if (title.isNotBlank() && artistName.isNotBlank()) {
            try {
                val tag = audioFile.tagOrCreateAndSetDefault
                if (albumName.isNotBlank()) {
                    tag.setField(FieldKey.ALBUM, albumName)
                } else if (tag.getFirst(FieldKey.ALBUM)?.isBlank() ?: true) {
                    tag.setField(FieldKey.ALBUM, DEFAULT_ALBUM_NAME)
                }
                tag.setField(FieldKey.ARTIST, artistName)
                tag.setField(FieldKey.TITLE, title)
                AudioFileIO.write(audioFile)
                return true
            } catch (_: FieldDataInvalidException) {
                return false
            }
        }
        return false
    }

    private fun extractArtistName(file: File): String {
        val fileName = prepareFilename(file)
        return if (fileName.contains(DELIMITER)) {
            fileName
                .substringBefore(DELIMITER)
                .replaceFirstChar { it.uppercaseChar() }
        } else {
            ""
        }
    }

    private fun extractTitle(file: File): String {
        val fileName = prepareFilename(file)
        return if (fileName.contains(DELIMITER)) {
            fileName.substringAfter(DELIMITER)
                .replaceFirstChar { it.uppercaseChar() }
        } else {
            ""
        }
    }

    private fun prepareFilename(file: File): String {
        return file.nameWithoutExtension
            .replace("Ё", "е", ignoreCase = true)
            .replace(Regex("[;?/:*<>|\\\\]"), "")
            .replace(Regex("-{2,}"), "-")
            .replace(Regex("\\s{2,}"), " ")
            .replace(Regex("\\(\\d+\\)"), "")
            .replace("-mp3", "")
            .replace("_", " ")
            .trim()
    }
}