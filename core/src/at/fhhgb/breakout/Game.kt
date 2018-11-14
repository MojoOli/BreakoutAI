package at.fhhgb.breakout

import at.fhhgb.breakout.systems.AIControllerSystem
import at.fhhgb.breakout.systems.StateSystem
import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
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
    private val train = false

    override fun create() {
        batch = SpriteBatch()
        injector = Guice.createInjector(GameModule(this))
        injector.getInstance(Systems::class.java).list.map { injector.getInstance(it) }.forEach { engine.addSystem(it) }

//        createBricks()
        createBall()
        createPaddle()
        createWall()

        if (train) {
            simulate()
        }
    }

    private fun createBall() {
        val world = injector.getInstance(World::class.java)
        engine.addEntity(Entity().apply {
            add(ColorComponent(Color.BLACK))
            add(SizeComponent(20f, 20f))
            add(RenderComponent(RenderType.Ball))
            add(TransformComponent(Vector2(Gdx.graphics.width / 2f, 100f)))

            val body = world.createBody(BodyDef().also {
                it.type = BodyDef.BodyType.DynamicBody
                it.bullet = true
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

            body.setLinearVelocity(Random().nextFloat() * 500f - 250f, 500f)
            add(BallComponent())
        })
    }

    private fun createPaddle() {
        val world = injector.getInstance(World::class.java)

        engine.addEntity(Entity().apply {
            add(ColorComponent(Color.ORANGE))
            add(SizeComponent(100f, 20f))
            add(RenderComponent(RenderType.Paddle))
            add(TransformComponent(Vector2(Gdx.graphics.width / 2f, 50f)))

            val body = world.createBody(BodyDef().also {
                it.type = BodyDef.BodyType.KinematicBody
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
                    add(RenderComponent(RenderType.Brick))
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

        body.setTransform(Vector2(0f, Gdx.graphics.height.toFloat() + 100f), 0f)
        body.createFixture(PolygonShape().apply {
            setAsBox(Gdx.graphics.width.toFloat(), 100f)
        }, 1.0f)

        // Left
        val bodyLeft = world.createBody(BodyDef().also {
            it.type = BodyDef.BodyType.StaticBody
        })

        bodyLeft.setTransform(Vector2(-100f, Gdx.graphics.height.toFloat()), 0f)
        bodyLeft.createFixture(PolygonShape().apply {
            setAsBox(100f, Gdx.graphics.height.toFloat())
        }, 1.0f)

        // Right
        val bodyRight = world.createBody(BodyDef().also {
            it.type = BodyDef.BodyType.StaticBody
        })

        bodyRight.setTransform(Vector2(Gdx.graphics.width.toFloat() + 100f, 0f), 0f)
        bodyRight.createFixture(PolygonShape().apply {
            setAsBox(100f, Gdx.graphics.height.toFloat())
        }, 1.0f)
    }

    private fun simulate() {
        for (i in (0..100000)) {
            if (engine.getSystem(StateSystem::class.java).state != State.Running) {
                println("" + (i / 100000f) * 100f + "%")
                engine.getSystem(StateSystem::class.java).state = State.Running
                restart()
            }

            engine.update(5f)
        }
    }

    override fun render() {
        if (!train) {
            if (engine.getSystem(StateSystem::class.java).state != State.Running) {
                engine.getSystem(StateSystem::class.java).state = State.Running
                restart()
            }

            Gdx.gl.glClearColor(1f, 1f, 1f, 1f)
            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)
            engine.update(0.08f)
        }
    }

    private fun restart() {
        val paddle = engine.getEntitiesFor(Family.all(PlayerComponent::class.java, PhysicsComponent::class.java).get())[0]
        paddle.getComponent(PhysicsComponent::class.java).body.setTransform(Vector2(Gdx.graphics.width / 2f, 50f), 0f)
        paddle.getComponent(PhysicsComponent::class.java).body.setLinearVelocity(0f, 0f)

        val ball = engine.getEntitiesFor(Family.all(BallComponent::class.java, PhysicsComponent::class.java).get())[0]
        ball.getComponent(PhysicsComponent::class.java).body.setTransform(Vector2(Gdx.graphics.width / 2f, 100f), 0f)
        ball.getComponent(PhysicsComponent::class.java).body.setLinearVelocity(Random().nextFloat() * 600f - 300f, 500f)

        val bricks = engine.getEntitiesFor(Family.all(BricksComponent::class.java, PhysicsComponent::class.java).get())
        for (brick in bricks) {
            brick.getComponent(PhysicsComponent::class.java).body.isActive = true
            brick.getComponent(BricksComponent::class.java).alive = true
        }

        engine.getSystem(AIControllerSystem::class.java).environment.restart()
    }

    override fun dispose() {
        engine.getSystem(AIControllerSystem::class.java).environment.saveQTable()
        batch.dispose()
    }
}