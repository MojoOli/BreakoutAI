package at.fhhgb.breakout.systems

import at.fhhgb.breakout.BallComponent
import at.fhhgb.breakout.PhysicsComponent
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IteratingSystem
import com.google.inject.Inject

class GameOverSystem @Inject constructor() : IteratingSystem(Family.all(BallComponent::class.java, PhysicsComponent::class.java).get()) {
    var isGameOver = false

    override fun processEntity(entity: Entity, deltaTime: Float) {
        val body = entity.getComponent(PhysicsComponent::class.java).body
        if (body.position.y < 0f) {
            println("Game Over")
            isGameOver = true
        }
    }
}