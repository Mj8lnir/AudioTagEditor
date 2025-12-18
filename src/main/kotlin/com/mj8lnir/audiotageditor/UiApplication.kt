package com.mj8lnir.audiotageditor

import javafx.application.Application
import javafx.stage.Stage
import org.springframework.boot.builder.SpringApplicationBuilder
import org.springframework.context.ApplicationEvent
import org.springframework.context.ConfigurableApplicationContext

internal class UiApplication : Application() {

    private val context: ConfigurableApplicationContext =
        SpringApplicationBuilder(AudioTagEditorApplication::class.java).run()

    override fun start(stage: Stage) {
        context.publishEvent(StageReadyEvent(stage))
    }
}

class StageReadyEvent(stage: Stage) : ApplicationEvent(stage) {
    fun getStage(): Stage {
        return super.source as Stage
    }
}
