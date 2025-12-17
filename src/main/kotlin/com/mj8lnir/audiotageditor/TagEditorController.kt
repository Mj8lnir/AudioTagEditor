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
) : Initializable {

    @FXML
    private lateinit var playlistCheckbox: CheckBox

    @FXML
    private lateinit var albumNameTextField: TextField

    @FXML
    private lateinit var playlistNameTextField: TextField

    @FXML
    private lateinit var label: Label

    private companion object {
        const val LABEL_PROMPT_TEXT = "Select .mp3 to start"
        const val FILES_NOT_CHOSEN_TEXT = "Files not chosen"
        val fileChooser: FileChooser = FileChooser()
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
                FileChooser.ExtensionFilter("All files", "*")
            )
        }
    }

    fun start() {
        if (FileBuffer.isEmpty()) {
            label.text = "Choose .mp3 first"
        } else {
            val albumName = albumNameTextField.text.trim()
            val playlistName = playlistNameTextField.text.trim()
            val createPlaylist = playlistCheckbox.isSelected
            val successCount = service.editTags(FileBuffer.getFiles(), albumName, createPlaylist, playlistName)
            label.text = "completed: $successCount/${FileBuffer.getFiles().size}"
        }
    }

    fun chooseFiles(mouseEvent: MouseEvent) {
        val scene = getScene(mouseEvent)
        val stage = scene.window as Stage
        label.text = LABEL_PROMPT_TEXT
        FileBuffer.clearBuffer()
        try {
            FileBuffer.addFiles(fileChooser.showOpenMultipleDialog(stage))
            if (FileBuffer.isEmpty()) {
                label.text = FILES_NOT_CHOSEN_TEXT
            } else {
                label.text = "Selected ${FileBuffer.getSize()} songs"
            }
        } catch (_: Exception) {
            label.text = FILES_NOT_CHOSEN_TEXT
        }
    }

    private fun getScene(inputEvent: InputEvent): Scene {
        val source: Node = inputEvent.source as Node
        return source.scene
    }
}