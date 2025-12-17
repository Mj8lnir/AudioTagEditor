package com.mj8lnir.audiotageditor

import javafx.application.Application
import org.springframework.boot.autoconfigure.SpringBootApplication

@SpringBootApplication
class AudioTagEditorApplication

fun main(args: Array<String>) {
    Application.launch(UiApplication::class.java, *args)
}
