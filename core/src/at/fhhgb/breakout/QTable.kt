package at.fhhgb.breakout

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.math.Vector2
import java.util.*

class QTable constructor(private val quantize: Int, private val learningRate: Float, private val discountFactor: Float) {
    private val quantizeStepX = Gdx.graphics.width / quantize
    private val quantizeStepY = Gdx.graphics.height / quantize
    private var qTable: Array<FloatArray> = Array(quantize * quantize * quantize) { FloatArray(3) }

    fun update(ballPos: Vector2, paddlePosX: Float): Action {
        val ballPosQuantized = getBallPosQuantized(ballPos)
        val paddlePosQuantized = getPaddlePosQuantized(paddlePosX)


        val leftQValue = qTable[getQTablePosition(ballPosQuantized, paddlePosQuantized)][Action.Left.ordinal]
        val rightQValue = qTable[getQTablePosition(ballPosQuantized, paddlePosQuantized)][Action.Right.ordinal]
        val stayQValue = qTable[getQTablePosition(ballPosQuantized, paddlePosQuantized)][Action.Stay.ordinal]

        val random = Random()
        val randomAction = (random.nextFloat() * 3).toInt()

        val action = if (leftQValue == rightQValue && leftQValue == stayQValue) {
            Action.getAction(randomAction)
        } else if (leftQValue > rightQValue && leftQValue > stayQValue) {
            Action.Left
        } else if (rightQValue > leftQValue && rightQValue > stayQValue) {
            Action.Right
        } else {
            Action.Stay
        }


        qTable[getQTablePosition(ballPosQuantized, paddlePosQuantized)][action.ordinal] = getQValue(action, ballPosQuantized, paddlePosQuantized)

        println(qTable[getQTablePosition(ballPosQuantized, paddlePosQuantized)][action.ordinal])

        return action
    }

    private fun getQValue(action: Action, ballPosQuantized: Vector2, paddlePosQuantized: Int): Float {
        val oldValue = qTable[getQTablePosition(ballPosQuantized, paddlePosQuantized)][action.ordinal]
        val reward = getReward(ballPosQuantized, paddlePosQuantized + (action.ordinal - 1))
        val estimatedFutureValue = getEstimatedFutureValue(action, ballPosQuantized, paddlePosQuantized)

        return (1 - learningRate) * oldValue + learningRate * (reward + discountFactor * estimatedFutureValue)
    }

    private fun getBallPosQuantized(ballPos: Vector2): Vector2 {
        return Vector2((ballPos.x / quantizeStepX).toInt().toFloat(), (ballPos.y / quantizeStepY).toInt().toFloat())
    }

    private fun getPaddlePosQuantized(paddlePosX: Float): Int {
        return (paddlePosX / quantizeStepX).toInt()
    }

    private fun getReward(ballPosQuantized: Vector2, paddlePosQuantized: Int): Float {
        if (ballPosQuantized.y <= 0) {
            return -1000f
        }

        if(paddlePosQuantized == ballPosQuantized.x.toInt() && ballPosQuantized.y.toInt() == 1){
            return 1000f
        }

        return 0f
    }

    private fun getQTablePosition(ballPosQuantized: Vector2, paddlePosQuantized: Int): Int {
        return (ballPosQuantized.x * (quantize * quantize) + ballPosQuantized.y * quantize + paddlePosQuantized).toInt()
    }

    private fun getEstimatedFutureValue(action: Action, ballPosQuantized: Vector2, paddlePosQuantized: Int): Float {
        val leftQValue = qTable[getQTablePosition(ballPosQuantized, paddlePosQuantized + (action.ordinal - 1))][Action.Left.ordinal]
        val stayQValue = qTable[getQTablePosition(ballPosQuantized, paddlePosQuantized + (action.ordinal - 1))][Action.Stay.ordinal]
        val rightQValue = qTable[getQTablePosition(ballPosQuantized, paddlePosQuantized + (action.ordinal - 1))][Action.Right.ordinal]

        return if (leftQValue > rightQValue && leftQValue > stayQValue) {
            leftQValue
        } else if (rightQValue > leftQValue && rightQValue > stayQValue) {
            rightQValue
        } else {
            stayQValue
        }
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