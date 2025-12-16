package com.mj8lnir.audiotageditor

import javafx.fxml.FXMLLoader
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.scene.paint.Color
import javafx.util.Callback
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.ApplicationContext
import org.springframework.context.ApplicationListener
import org.springframework.core.io.Resource
import org.springframework.stereotype.Component
import kotlin.io.path.name

@Component
internal class StageInitializer(
    @Value("classpath:parent.fxml") private val fxmlFile: Resource,
    @Value("classpath:parent.css") private val styleFile: Resource,
    private val context: ApplicationContext,
) : ApplicationListener<StageReadyEvent> {

    override fun onApplicationEvent(event: StageReadyEvent) {
        val fxmlLoader = FXMLLoader(fxmlFile.url).apply {
            this.controllerFactory = Callback { aClass -> context.getBean(aClass) }
        }
        val parent = fxmlLoader.load<Parent>()
        val stage = event.getStage()
        val scene = Scene(parent, 160.0, 235.0).apply {
            this.stylesheets.add(styleFile.filePath.name)
        }
        scene.fill = Color.TRANSPARENT
        stage.isResizable = false
        stage.scene = scene
        stage.show()
    }
}