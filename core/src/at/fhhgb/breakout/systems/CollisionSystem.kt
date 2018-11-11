package at.fhhgb.breakout.systems

import at.fhhgb.breakout.PhysicsComponent
import at.fhhgb.breakout.PlayerComponent
import com.badlogic.ashley.core.EntitySystem
import com.badlogic.ashley.core.Family
import com.badlogic.gdx.physics.box2d.*
import com.google.inject.Inject

class CollisionSystem @Inject constructor(world: World) : EntitySystem() {
    private var paddleBody: Body? = null

    init {
        world.setContactListener(object : ContactListener {
            override fun beginContact(contact: Contact) {
                if (contact.fixtureA.body == paddleBody || contact.fixtureB.body == paddleBody) {
                    engine.getSystem(AIControllerSystem::class.java).environment.hasHitBall()
                }
            }

            override fun endContact(contact: Contact?) {}
            override fun preSolve(contact: Contact?, oldManifold: Manifold?) {}
            override fun postSolve(contact: Contact?, impulse: ContactImpulse?) {}
        })
    }

    override fun update(deltaTime: Float) {
        if (paddleBody == null) {
            paddleBody = engine.getEntitiesFor(Family.all(PlayerComponent::class.java).get()).get(0).getComponent(PhysicsComponent::class.java).body
        }

        super.update(deltaTime)
    }
}