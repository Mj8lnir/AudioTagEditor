package com.mj8lnir.audiotageditor

import javafx.fxml.FXML
import javafx.fxml.Initializable
import javafx.scene.Node
import javafx.scene.Scene
import javafx.scene.control.Label
import javafx.scene.control.TextField
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.input.InputEvent
import javafx.scene.input.MouseEvent
import javafx.stage.FileChooser
import javafx.stage.Stage
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.io.Resource
import org.springframework.stereotype.Controller
import java.net.URL
import java.util.*

@Controller
internal class TagEditorController(
    private val service: AudioTagService,
    private val playlistService: GarminPlaylistService,
    private val fileBuffer: FileBuffer,
    @Value("classpath:garmin.png")
    private val garminIcon: Resource,
) : Initializable {

    @FXML
    private lateinit var appLogo: ImageView

    @FXML
    private lateinit var albumNameTextField: TextField

    @FXML
    private lateinit var playlistNameTextField: TextField

    @FXML
    private lateinit var label: Label

    companion object {
        private const val LABEL_PROMPT_TEXT = "Select .mp3 to start"
        private const val FILES_NOT_CHOSEN_TEXT = "Files not chosen"
        private val fileChooser: FileChooser = FileChooser()
    }

    override fun initialize(p0: URL?, p1: ResourceBundle?) {
        appLogo.image = Image(garminIcon.url.toExternalForm())
        label.text = LABEL_PROMPT_TEXT
        fileChooser.title = "Choose files"
        if (fileChooser.extensionFilters.isEmpty()) {
            fileChooser.extensionFilters.addAll(
                FileChooser.ExtensionFilter("Music (*.mp3)", "*.mp3"),
            )
        }
    }

    fun startTags() {
        if (fileBuffer.isEmpty()) {
            label.text = FILES_NOT_CHOSEN_TEXT
            fileBuffer.clearBuffer()
            return
        }
        val albumName = albumNameTextField.text.trim()
        val successCount = service.editTags(fileBuffer.getFiles(), albumName)
        label.text = "completed: $successCount/${fileBuffer.getSize()}"
        fileBuffer.clearBuffer()
    }

    fun startPlaylists() {
        if (fileBuffer.isEmpty()) {
            label.text = FILES_NOT_CHOSEN_TEXT
            fileBuffer.clearBuffer()
            return
        }
        val playlistName = playlistNameTextField.text.trim().ifBlank {
            label.text = "specify playlist name!"
            fileBuffer.clearBuffer()
            return
        }
        val playlistCreated = playlistService.createPlaylist(fileBuffer.getFiles(), playlistName)
        label.text = "playlist '$playlistName' ${if (playlistCreated) "created" else "not created"}"
        fileBuffer.clearBuffer()
    }

    fun chooseFiles(mouseEvent: MouseEvent) {
        val scene = getScene(mouseEvent)
        val stage = scene.window as Stage
        label.text = LABEL_PROMPT_TEXT
        fileBuffer.clearBuffer()
        try {
            fileBuffer.addFiles(fileChooser.showOpenMultipleDialog(stage))
            if (fileBuffer.isEmpty()) {
                label.text = FILES_NOT_CHOSEN_TEXT
            } else {
                label.text = "Selected ${fileBuffer.getSize()} songs"
            }
        } catch (_: Exception) {
            label.text = FILES_NOT_CHOSEN_TEXT
        }
    }

    fun stop(mouseEvent: MouseEvent) {
        val scene = getScene(mouseEvent)
        val stage = scene.window as Stage
        stage.close()
    }

    private fun getScene(inputEvent: InputEvent): Scene {
        val source: Node = inputEvent.source as Node
        return source.scene
    }
}