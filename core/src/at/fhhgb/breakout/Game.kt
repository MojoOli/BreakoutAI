package at.fhhgb.breakout

import at.fhhgb.breakout.systems.StateSystem
import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.ApplicationAdapter
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.*
import com.google.inject.Guice
import com.google.inject.Injector
import java.util.*

open class Game : ApplicationAdapter() {
    lateinit var batch: SpriteBatch
    var engine = Engine()
    private lateinit var injector: Injector

    override fun create() {
        batch = SpriteBatch()
        injector = Guice.createInjector(GameModule(this))
        injector.getInstance(Systems::class.java).list.map { injector.getInstance(it) }.forEach { engine.addSystem(it) }

        createBricks()
        createBall()
        createPaddle()
        createWall()
    }

    private fun createBall() {
        val world = injector.getInstance(World::class.java)
        engine.addEntity(Entity().apply {
            add(ColorComponent(Color.BLACK))
            add(SizeComponent(20f, 20f))
            add(ShapeComponent(Shape.Type.Circle))
            add(TransformComponent(Vector2(Gdx.graphics.width / 2f, 100f)))

            val body = world.createBody(BodyDef().also {
                it.type = BodyDef.BodyType.DynamicBody

            })

            val circleShape = CircleShape().apply {
                radius = 10f

            }
            body.createFixture(FixtureDef().apply {
                shape = circleShape
                density = 1f
                friction = 0f
                restitution = 1f
            })
            body.setTransform(this.getComponent(TransformComponent::class.java).position, 0f)
            add(PhysicsComponent(body))
            val r  = Random()
            val xImpulse = r.nextFloat() * 100000f - 50000f
            println(xImpulse)
            body.applyLinearImpulse(Vector2(xImpulse, 90000f), body.position, true)
            add(BallComponent())
        })
    }

    private fun createPaddle() {
        val world = injector.getInstance(World::class.java)
        engine.addEntity(Entity().apply {
            add(ColorComponent(Color.ORANGE))
            add(SizeComponent(100f, 20f))
            add(ShapeComponent(Shape.Type.Polygon))
            add(TransformComponent(Vector2(Gdx.graphics.width / 2f, 50f)))

            val body = world.createBody(BodyDef().also {
                it.type = BodyDef.BodyType.StaticBody
            })

            val polyShape = PolygonShape().apply {
                setAsBox(100f / 2f, 20f / 2f)
            }

            body.createFixture(FixtureDef().apply {
                shape = polyShape
                density = 10f
                friction = .4f
                restitution = .1f
            })
            body.setTransform(this.getComponent(TransformComponent::class.java).position, 0f)
            add(PhysicsComponent(body))
            add(PlayerComponent())
        })
    }

    private fun createBricks() {
        val world = injector.getInstance(World::class.java)
        for (i in 0..2) {
            for (j in 0..9) {
                engine.addEntity(Entity().apply {
                    add(ColorComponent(Color.BLACK))
                    add(SizeComponent(50f, 20f))
                    add(ShapeComponent(Shape.Type.Polygon))
                    add(TransformComponent(Vector2(50f + 60f * j, 400f - i * 30f)))

                    val body = world.createBody(BodyDef().also {
                        it.type = BodyDef.BodyType.StaticBody
                    })
                    body.createFixture(PolygonShape().apply {
                        setAsBox(50f / 2f, 20f / 2f)
                    }, 1.0f)
                    body.setTransform(this.getComponent(TransformComponent::class.java).position, 0f)
                    add(PhysicsComponent(body))
                    add(BricksComponent())
                })
            }
        }
    }

    private fun createWall() {
        val world = injector.getInstance(World::class.java)
        val body = world.createBody(BodyDef().also {
            it.type = BodyDef.BodyType.StaticBody
        })

        body.setTransform(Vector2(0f, Gdx.graphics.height.toFloat()), 0f)
        body.createFixture(PolygonShape().apply {
            setAsBox(Gdx.graphics.width.toFloat(), 0f)
        }, 1.0f)

        // Left
        val bodyLeft = world.createBody(BodyDef().also {
            it.type = BodyDef.BodyType.StaticBody
        })

        bodyLeft.setTransform(Vector2(0f, Gdx.graphics.height.toFloat()), 0f)
        bodyLeft.createFixture(PolygonShape().apply {
            setAsBox(0f, Gdx.graphics.height.toFloat())
        }, 1.0f)

        // Right
        val bodyRight = world.createBody(BodyDef().also {
            it.type = BodyDef.BodyType.StaticBody
        })

        bodyRight.setTransform(Vector2(Gdx.graphics.width.toFloat(), 0f), 0f)
        bodyRight.createFixture(PolygonShape().apply {
            setAsBox(0f, Gdx.graphics.height.toFloat())
        }, 1.0f)
    }

    override fun render() {
        if(engine.getSystem(StateSystem::class.java).state == State.Running) {
            Gdx.gl.glClearColor(1f, 1f, 1f, 1f)
            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)
            engine.update(Gdx.graphics.deltaTime)
        }
    }

    override fun dispose() {
        batch.dispose()
    }
}