package at.fhhgb.breakout

import at.fhhgb.breakout.systems.*
import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.EntitySystem
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.Box2D
import com.badlogic.gdx.physics.box2d.World
import com.google.inject.Binder
import com.google.inject.Module
import com.google.inject.Provides
import com.google.inject.Singleton

class GameModule(private val game: Game) : Module {
    override fun configure(binder: Binder) {
        binder.requireAtInjectOnConstructors()
        binder.requireExactBindingAnnotations()
    }

    @Provides
    @Singleton
    fun batch(): SpriteBatch {
        return game.batch
    }

    @Provides
    @Singleton
    fun systems(): Systems {
        return Systems(listOf(
                StateSystem::class.java,
                AIControllerSystem::class.java,
                PlayerControllerSystem::class.java,
                PhysicsSystem::class.java,
                CollisionSystem::class.java,
                PhysicsSynchronizationSystem::class.java,
                RenderingSystem::class.java,
                PhysicsDebugSystem::class.java
        ))
    }


    @Provides
    @Singleton
    fun camera(): OrthographicCamera {
        return OrthographicCamera(Gdx.graphics.width.toFloat(), Gdx.graphics.height.toFloat()).apply {
            position.set(Gdx.graphics.width.toFloat() / 2f, Gdx.graphics.height.toFloat() / 2f, 0f)
            update()
        }
    }

    @Provides
    @Singleton
    fun world(): World {
        Box2D.init()
        return World(Vector2(0f, 0f), true)
    }

    @Provides
    @Singleton
    fun engine(): Engine {
        return game.engine
    }
}

data class Systems(val list: List<Class<out EntitySystem>>)