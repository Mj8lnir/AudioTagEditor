package com.mj8lnir.audiotageditor

import javafx.application.Application
import javafx.application.Platform
import javafx.stage.Stage
import org.springframework.boot.builder.SpringApplicationBuilder
import org.springframework.context.ApplicationEvent
import org.springframework.context.ConfigurableApplicationContext

internal class UiApplication : Application() {

    private lateinit var context: ConfigurableApplicationContext

    override fun init() {
        context = SpringApplicationBuilder(AudioTagEditorApplication::class.java).run()
    }

    override fun start(stage: Stage) {
        context.publishEvent(StageReadyEvent(stage))
    }

    override fun stop() {
        context.close()
        Platform.exit()
    }
}

class StageReadyEvent(stage: Stage) : ApplicationEvent(stage) {
    fun getStage(): Stage {
        return super.source as Stage
    }
}
