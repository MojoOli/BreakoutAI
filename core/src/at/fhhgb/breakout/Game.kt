package at.fhhgb.breakout

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.ApplicationAdapter
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.math.Vector2
import com.google.inject.Guice
import com.google.inject.Injector

open class Game : ApplicationAdapter() {
    lateinit var batch: SpriteBatch
    private lateinit var injector: Injector
    private var engine = Engine()

    private val height = 480f
    private val width = 640f

    override fun create() {
        batch = SpriteBatch()
        injector = Guice.createInjector(GameModule(this))
        injector.getInstance(Systems::class.java).list.map { injector.getInstance(it) }.forEach { engine.addSystem(it) }

        createEntities()
    }

    private fun createEntities() {
        // Bricks
        for (i in 0..2) {
            for (j in 0..9) {
                engine.addEntity(Entity().apply {
                    add(ColorComponent(Color.BLACK))
                    add(SizeComponent(50f, 20f))
                    add(TransformComponent(Vector2(25f + 60f * j, 400f - i * 30f)))
                })
            }
        }

        // Paddle
        engine.addEntity(Entity().apply {
            add(ColorComponent(Color.ORANGE))
            add(SizeComponent(100f, 20f))
            add(TransformComponent(Vector2(width / 2f - 100f / 2f, 50f)))
        })
    }

    override fun render() {
        Gdx.gl.glClearColor(1f, 1f, 1f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)
        engine.update(Gdx.graphics.deltaTime)
    }

    override fun dispose() {
        batch.dispose()
    }
}