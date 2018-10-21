package at.fhhgb.breakout.systems

import com.badlogic.ashley.core.EntitySystem
import com.badlogic.gdx.physics.box2d.World
import com.google.inject.Inject

class PhysicsSystem @Inject constructor(private val world: World) : EntitySystem() {
    private var accumulator = 0f
    override fun update(deltaTime: Float) {
        // https://github.com/libgdx/libgdx/wiki/Box2d#stepping-the-simulation
        // fixed time step
        // max frame time to avoid spiral of death (on slow devices)
        val frameTime = Math.min(deltaTime, 0.25f)
        accumulator += frameTime
        while (accumulator >= TIME_STEP) {
            world.step(TIME_STEP, VELOCITY_ITERATIONS, POSITION_ITERATIONS)
            accumulator -= TIME_STEP
        }
    }

    companion object {
        private const val TIME_STEP = 1f / 300f
        private const val VELOCITY_ITERATIONS = 6
        private const val POSITION_ITERATIONS = 2
    }
}