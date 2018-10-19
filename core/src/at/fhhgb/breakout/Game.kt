package at.fhhgb.breakout

import com.badlogic.ashley.core.Engine
import com.badlogic.gdx.ApplicationAdapter
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.google.inject.Guice
import com.google.inject.Injector

open class Game : ApplicationAdapter() {
    lateinit var batch: SpriteBatch
    private lateinit var img: Texture
    private lateinit var injector: Injector
    private var engine = Engine()

    override fun create() {
        batch = SpriteBatch()
        img = Texture("badlogic.jpg")
        injector = Guice.createInjector()
        injector.getInstance(Systems::class.java).list.map { injector.getInstance(it) }.forEach { engine.addSystem(it) }

        createEntities()
    }

    private fun createEntities() {

    }

    override fun render() {
        Gdx.gl.glClearColor(1f, 0f, 0f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)
        engine.update(Gdx.graphics.deltaTime)
    }

    override fun dispose() {
        batch.dispose()
        img.dispose()
    }
}