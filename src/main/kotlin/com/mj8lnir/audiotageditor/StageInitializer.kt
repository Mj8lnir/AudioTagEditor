package com.mj8lnir.audiotageditor

import javafx.fxml.FXMLLoader
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.scene.image.Image
import javafx.scene.input.MouseEvent
import javafx.scene.paint.Color
import javafx.util.Callback
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.ApplicationContext
import org.springframework.context.ApplicationListener
import org.springframework.core.io.Resource
import org.springframework.stereotype.Component

@Component
internal class StageInitializer(
    @Value("classpath:parent.fxml") private val fxmlFile: Resource,
    @Value("classpath:parent.css") private val styleFile: Resource,
    @Value("classpath:icon.png") private val iconFile: Resource,
    private val context: ApplicationContext,
) : ApplicationListener<StageReadyEvent> {

    private companion object {
        var xOffset = 0.0
        var yOffset = 0.0
    }

    override fun onApplicationEvent(event: StageReadyEvent) {
        val fxmlLoader = FXMLLoader(fxmlFile.url).apply {
            this.controllerFactory = Callback { aClass -> context.getBean(aClass) }
        }
        val parent = fxmlLoader.load<Parent>()
        val stage = event.getStage()
        parent.setOnMousePressed { event: MouseEvent ->
            xOffset = event.sceneX
            yOffset = event.sceneY
        }

        parent.setOnMouseDragged { event: MouseEvent ->
            stage.x = event.screenX - xOffset
            stage.y = event.screenY - yOffset
        }
        stage.icons.add(Image(iconFile.url.toExternalForm()))
        val scene = Scene(parent, 180.0, 225.0).apply {
            this.stylesheets.add(styleFile.url.toExternalForm())
        }
        scene.fill = Color.TRANSPARENT
        stage.isResizable = false
        stage.scene = scene
        stage.show()
    }
}