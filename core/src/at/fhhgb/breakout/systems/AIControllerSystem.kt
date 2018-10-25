package at.fhhgb.breakout.systems

import at.fhhgb.breakout.PhysicsComponent
import at.fhhgb.breakout.PlayerComponent
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IteratingSystem
import com.google.inject.Inject

class AIControllerSystem @Inject constructor() : IteratingSystem(Family.all(PlayerComponent::class.java).get()) {
    private var stateSystem: StateSystem? = null

    override fun processEntity(entity: Entity, deltaTime: Float) {
        if(stateSystem == null){
            stateSystem = engine.getSystem(StateSystem::class.java)
        }

        val paddle = entity.getComponent(PhysicsComponent::class.java).body

//        val velocityX = when {
//            moveLeft -> -250f
//            moveRight -> 250f
//            else -> body.linearVelocity.x * 0.9f
//        }
//        body.setLinearVelocity(velocityX, 0f)
    }
}