package com.mj8lnir.audiotageditor

import javafx.fxml.FXML
import javafx.fxml.Initializable
import javafx.scene.Node
import javafx.scene.Scene
import javafx.scene.control.CheckBox
import javafx.scene.control.Label
import javafx.scene.control.TextField
import javafx.scene.input.InputEvent
import javafx.scene.input.MouseEvent
import javafx.stage.FileChooser
import javafx.stage.Stage
import org.springframework.stereotype.Controller
import java.net.URL
import java.util.*

@Controller
internal class TagEditorController(
    private val service: AudioTagService,
    private val playlistService: GarminPlaylistService,
    private val fileBuffer: FileBuffer,
) : Initializable {

    @FXML
    private lateinit var playlistCheckbox: CheckBox

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
        label.text = LABEL_PROMPT_TEXT
        playlistNameTextField.isDisable = true
        playlistCheckbox.selectedProperty().addListener { _, _, isSelected ->
            playlistNameTextField.isDisable = !isSelected
        }
        fileChooser.title = "Choose files"
        if (fileChooser.extensionFilters.isEmpty()) {
            fileChooser.extensionFilters.addAll(
                FileChooser.ExtensionFilter("Music (*.mp3)", "*.mp3"),
            )
        }
    }

    fun start() {
        if (fileBuffer.isEmpty()) {
            label.text = FILES_NOT_CHOSEN_TEXT
            return
        }
        if (playlistCheckbox.isSelected) {
            createPlaylist()
        } else {
            editTags()
        }
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

    private fun editTags() {
        val albumName = albumNameTextField.text.trim()
        val successCount = service.editTags(fileBuffer.getFiles(), albumName)
        label.text = "completed: $successCount/${fileBuffer.getSize()}"
    }

    private fun createPlaylist() {
        val playlistName = playlistNameTextField.text.trim().ifBlank {
            label.text = "specify playlist name!"
            return
        }
        val playlistCreated = playlistService.createPlaylist(fileBuffer.getFiles(), playlistName)
        label.text = "playlist '$playlistName' ${if (playlistCreated) "created" else "not created"}"
    }

    private fun getScene(inputEvent: InputEvent): Scene {
        val source: Node = inputEvent.source as Node
        return source.scene
    }
}