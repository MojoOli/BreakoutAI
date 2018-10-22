package at.fhhgb.breakout.systems

import at.fhhgb.breakout.BallComponent
import at.fhhgb.breakout.PhysicsComponent
import at.fhhgb.breakout.State
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IteratingSystem
import com.google.inject.Inject

class StateSystem @Inject constructor() : IteratingSystem(Family.all(BallComponent::class.java, PhysicsComponent::class.java).get()) {
    var state = State.Running

    override fun processEntity(entity: Entity, deltaTime: Float) {
        val body = entity.getComponent(PhysicsComponent::class.java).body
        if (body.position.y < 0f) {
            println("Game Over")
            state = State.GameOver
        }
    }
}