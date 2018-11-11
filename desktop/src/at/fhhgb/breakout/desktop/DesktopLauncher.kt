package at.fhhgb.breakout.desktop

import at.fhhgb.breakout.Game
import com.badlogic.gdx.backends.lwjgl.LwjglApplication
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration

object DesktopLauncher {
    @JvmStatic
    fun main(arg: Array<String>) {
        val config = LwjglApplicationConfiguration()
        config.width = 640
        config.height = 480
        LwjglApplication(Game(), config)
    }
}