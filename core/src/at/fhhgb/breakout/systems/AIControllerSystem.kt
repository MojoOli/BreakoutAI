package at.fhhgb.breakout.systems

import at.fhhgb.breakout.BallComponent
import at.fhhgb.breakout.PhysicsComponent
import at.fhhgb.breakout.PlayerComponent
import at.fhhgb.breakout.SizeComponent
import at.fhhgb.breakout.qlearning.Action
import at.fhhgb.breakout.qlearning.Environment
import com.badlogic.ashley.core.EntitySystem
import com.badlogic.ashley.core.Family
import com.badlogic.gdx.Gdx
import com.google.inject.Inject

class AIControllerSystem @Inject constructor() : EntitySystem() {
    private var ballComponent: PhysicsComponent? = null
    private var paddleComponent: PhysicsComponent? = null
    private var paddleSizeComponent: SizeComponent? = null
    private var ballSizeComponent: SizeComponent? = null

    val environment = Environment(8)

    override fun update(deltaTime: Float) {
        if (ballComponent == null || paddleComponent != null) {
            setupComponents()
        }

        val action = environment.update(ballComponent!!.body.position, ballComponent!!.body.linearVelocity, paddleComponent!!.body.position)

        when {
            paddleComponent!!.body.position.x - paddleSizeComponent!!.width / 2f < 0 -> {
                paddleComponent!!.body.setLinearVelocity(0f, 0f)
                paddleComponent!!.body.setTransform(paddleSizeComponent!!.width / 2f, paddleComponent!!.body.position.y, 0f)
            }
            paddleComponent!!.body.position.x + paddleSizeComponent!!.width / 2f > Gdx.graphics.width -> {
                paddleComponent!!.body.setLinearVelocity(0f, 0f)
                paddleComponent!!.body.setTransform(Gdx.graphics.width - paddleSizeComponent!!.width / 2f, paddleComponent!!.body.position.y, 0f)
            }
            else -> {
                val velocityX = when (action) {
                    Action.Left -> -400f
                    Action.Right -> 400f
                    else -> paddleComponent!!.body.linearVelocity.x * 0.9f
                }
                paddleComponent!!.body.setLinearVelocity(velocityX, 0f)
            }
        }
    }

    private fun setupComponents() {
        val entities = engine.getEntitiesFor(Family.one(PlayerComponent::class.java, BallComponent::class.java).get())

        for (entity in entities) {
            if (entity.getComponent(PlayerComponent::class.java) != null) {
                paddleComponent = entity.getComponent(PhysicsComponent::class.java)
                paddleSizeComponent = entity.getComponent(SizeComponent::class.java)
            } else {
                ballComponent = entity.getComponent(PhysicsComponent::class.java)
                ballSizeComponent = entity.getComponent(SizeComponent::class.java)
            }
        }
    }
}