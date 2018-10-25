package at.fhhgb.breakout.systems

import at.fhhgb.breakout.BallComponent
import at.fhhgb.breakout.PhysicsComponent
import at.fhhgb.breakout.State
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.math.Vector2
import com.google.inject.Inject

class StateSystem @Inject constructor() : IteratingSystem(Family.all(BallComponent::class.java, PhysicsComponent::class.java).get()) {
    var state = State.Running

    // quantize ball position
    val QUANTIZE_STEP = 8f
    private val xQuantize = Gdx.graphics.width / QUANTIZE_STEP
    private val yQUantize = Gdx.graphics.height / QUANTIZE_STEP
    var ballPositionQuantized = Vector2(0f,0f)

    override fun processEntity(entity: Entity, deltaTime: Float) {
        val body = entity.getComponent(PhysicsComponent::class.java).body
//        if (body.position.y < 0f) {
//            state = State.GameOver
//        }

        // calculate quantized ball position
        ballPositionQuantized = Vector2((body.position.x / xQuantize).toInt().toFloat(), (body.position.y / yQUantize).toInt().toFloat())
        println(ballPositionQuantized)
    }
}