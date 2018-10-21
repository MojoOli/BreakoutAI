package at.fhhgb.breakout.systems

import at.fhhgb.breakout.BricksComponent
import at.fhhgb.breakout.PhysicsComponent
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.physics.box2d.*
import com.google.inject.Inject

class CollisionSystem @Inject constructor(private val world: World) : IteratingSystem(Family.all(BricksComponent::class.java, PhysicsComponent::class.java).get()) {
    private var collisionBodyA: Body? = null
    private var collisionBodyB: Body? = null

    init {
        world.setContactListener(object : ContactListener {
            override fun beginContact(contact: Contact) {
                collisionBodyA = contact.fixtureA.body
                collisionBodyB = contact.fixtureB.body
            }

            override fun endContact(contact: Contact?) {}
            override fun preSolve(contact: Contact?, oldManifold: Manifold?) {}
            override fun postSolve(contact: Contact?, impulse: ContactImpulse?) {}
        })
    }

    override fun processEntity(entity: Entity, deltaTime: Float) {
        val body =  entity.getComponent(PhysicsComponent::class.java).body
        if (collisionBodyA == body || collisionBodyB == body) {
            world.destroyBody(body)
            engine.removeEntity(entity)
            collisionBodyA = null
            collisionBodyB = null
            return
        }
    }
}