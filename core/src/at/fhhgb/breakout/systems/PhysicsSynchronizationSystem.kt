package at.fhhgb.breakout.systems

import at.fhhgb.breakout.PhysicsComponent
import at.fhhgb.breakout.TransformComponent
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IteratingSystem
import com.google.inject.Inject

class PhysicsSynchronizationSystem @Inject constructor() : IteratingSystem(Family.all(TransformComponent::class.java, PhysicsComponent::class.java).get()) {
    override fun processEntity(entity: Entity, deltaTime: Float) {
        entity.getComponent(TransformComponent::class.java).position.set(entity.getComponent(PhysicsComponent::class.java).body.position)
    }
}