package at.fhhgb.breakout.qlearning

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.math.Vector2

class Environment(private val quantize: Int) {
    private var oldState: State? = null
    private var oldAction: Action = Action.Stay
    private val agent = Agent(quantize, 0.2f, 0.9f)
    private var hasHitBall = false

    fun update(ballPos: Vector2, ballLinearVelocity: Vector2, paddlePos: Vector2): Action {
        val ballPosQuantized = getBallPosQuantized(ballPos)
        val ballDirectionQuantized = getBallDirectionQuantized(ballLinearVelocity)
        val paddlePosQuantizedX = getPaddlePosQuantized(paddlePos.x)

        if (oldState == null || !oldState!!.compareState(ballPosQuantized.x.toInt(), ballPosQuantized.y.toInt(), ballDirectionQuantized, paddlePosQuantizedX)) {
            val newState = State(ballPosQuantized.x.toInt(), ballPosQuantized.y.toInt(), ballDirectionQuantized, paddlePosQuantizedX)

            if (oldState != null) {
                agent.stateChanged(newState, getReward(oldAction, newState, (paddlePos.y / (Gdx.graphics.height / quantize)).toInt()))
            }

            oldAction = agent.getAction(newState)
            oldState = newState
        }

        return oldAction
    }

    fun hasHitBall() {
        hasHitBall = true
    }

    private fun getBallPosQuantized(ballPos: Vector2): Vector2 {
        val ballPosQuant = Vector2((ballPos.x / (Gdx.graphics.width / quantize)).toInt().toFloat(), (ballPos.y / (Gdx.graphics.height / quantize)).toInt().toFloat())
        if (ballPosQuant.x < 0) {
            ballPosQuant.x = 0f
        } else if (ballPosQuant.x > quantize - 1) {
            ballPosQuant.x = quantize - 1f
        }

        if (ballPosQuant.y < 0) {
            ballPosQuant.y = 0f
        } else if (ballPosQuant.y > quantize - 1) {
            ballPosQuant.y = quantize - 1f
        }

        return ballPosQuant
    }

    private fun getBallDirectionQuantized(ballLinearVelocity: Vector2): Int {
        if(ballLinearVelocity.y >= 0){
            return 0
        }

        if(ballLinearVelocity.x < -150){
            return 1
        }

        if(ballLinearVelocity.x < 0) {
            return 2
        }

        if(ballLinearVelocity.x > 150){
            return 3
        }

        return 4
    }

    private fun getPaddlePosQuantized(paddlePosX: Float): Int {
        var paddlePos = (paddlePosX / (Gdx.graphics.width / quantize)).toInt()
        if (paddlePos < 0) {
            paddlePos = 0
        } else if (paddlePos > quantize - 1) {
            paddlePos = quantize - 1
        }
        return paddlePos
    }

    private fun getReward(action: Action, newState: State, paddlePosY: Int): Int {
        var reward = if (action == Action.Stay) {
            0
        } else {
            -1
        }

        if (newState.ballPosY <= 0) {
            reward = -1000
        }

        if (hasHitBall) {
            hasHitBall = false
            reward = 1000
        }

        return reward
    }

    fun restart() {
        agent.restart()
    }

    fun saveQTable(){
        agent.saveQTable()
    }
}

enum class Action {
    Left,
    Stay,
    Right;

    companion object {
        fun getAction(action: Int): Action {
            return when (action) {
                0 -> Left
                1 -> Stay
                else -> Right
            }
        }
    }
}