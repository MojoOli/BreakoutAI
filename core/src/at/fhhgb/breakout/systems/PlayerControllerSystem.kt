package at.fhhgb.breakout.systems

import at.fhhgb.breakout.PhysicsComponent
import at.fhhgb.breakout.PlayerComponent
import at.fhhgb.breakout.SizeComponent
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.InputAdapter
import com.google.inject.Inject

class PlayerControllerSystem @Inject constructor() : IteratingSystem(Family.all(PlayerComponent::class.java, SizeComponent::class.java).get()) {
    private var moveLeft = false
    private var moveRight = false

    init {
        Gdx.input.inputProcessor = object : InputAdapter() {
            override fun keyDown(keycode: Int): Boolean {
                if (keycode == Input.Keys.LEFT) {
                    moveLeft = true
                } else if (keycode == Input.Keys.RIGHT) {
                    moveRight = true
                }
                return true
            }

            override fun keyUp(keycode: Int): Boolean {
                if (keycode == Input.Keys.LEFT) {
                    moveLeft = false
                } else if (keycode == Input.Keys.RIGHT) {
                    moveRight = false
                }
                return true
            }
        }
    }

    override fun processEntity(entity: Entity, deltaTime: Float) {
        val body = entity.getComponent(PhysicsComponent::class.java).body
        val width = entity.getComponent(SizeComponent::class.java).width

        if (moveLeft && body.position.x - width /2f > 0) {
            body.setTransform(body.position.x - 10f, body.position.y, 0f)
        } else if (moveRight && body.position.x + width /2f < Gdx.graphics.width) {
            body.setTransform(body.position.x + 10f, body.position.y, 0f)
        }
    }
}