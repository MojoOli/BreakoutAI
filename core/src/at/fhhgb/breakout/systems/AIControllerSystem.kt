package at.fhhgb.breakout.systems

import at.fhhgb.breakout.*
import com.badlogic.ashley.core.EntitySystem
import com.badlogic.ashley.core.Family
import com.google.inject.Inject

class AIControllerSystem @Inject constructor() : EntitySystem() {
    private var ballComponent: PhysicsComponent? = null
    private var paddleComponent: PhysicsComponent? = null
    private var paddleSizeComponent: SizeComponent? = null
    private var ballSizeComponent: SizeComponent? = null

    private val qTable = QTable(16, 0.9f, 0.5f)

    override fun update(deltaTime: Float) {
        if (ballComponent == null || paddleComponent != null) {
            setupComponents()
        }

        val action = qTable.update(ballComponent!!.body.position, paddleComponent!!.body.position, ballSizeComponent!!, paddleSizeComponent!!)

        val velocityX = when (action) {
            Action.Left -> -300f
            Action.Right -> 300f
            else -> paddleComponent!!.body.linearVelocity.x * 0.9f
        }
        paddleComponent!!.body.setLinearVelocity(velocityX, 0f)
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