package at.fhhgb.breakout

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.math.Vector2

class QTable constructor(private val quantize: Int, private val learningRate: Float, private val discountFactor: Float) {
    private val quantizeStepX = Gdx.graphics.width / quantize
    private val quantizeStepY = Gdx.graphics.height / quantize
    private var qTable: Array<FloatArray> = Array(quantize * quantize * quantize) { FloatArray(3) }

    fun update(ballPos: Vector2, paddlePosX: Float):Action {
        val ballPosQuantized = getBallPosQuantized(ballPos)
        val paddlePosQuantized = getPaddlePosQuantized(paddlePosX)

        val leftQValue = getQValue(Action.Left, ballPosQuantized, paddlePosQuantized)
        val rightQValue = getQValue(Action.Right, ballPosQuantized, paddlePosQuantized)
        val stayQValue = getQValue(Action.Stay, ballPosQuantized, paddlePosQuantized)

        qTable[getQTablePosition(ballPosQuantized, paddlePosQuantized)][Action.Left.ordinal] = leftQValue
        qTable[getQTablePosition(ballPosQuantized, paddlePosQuantized)][Action.Stay.ordinal] = stayQValue
        qTable[getQTablePosition(ballPosQuantized, paddlePosQuantized)][Action.Right.ordinal] = rightQValue

        return if (leftQValue > rightQValue && leftQValue > stayQValue) {
            Action.Left
        } else if (rightQValue > leftQValue && rightQValue > stayQValue) {
            Action.Right
        } else {
            Action.Stay
        }

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
        if(ballPosQuantized.y <= 0){
            return -1000f
        }

        var distance = ballPosQuantized.x - paddlePosQuantized
        if (distance > 0f) {
            distance *= -1f
        }

        return distance
    }

    private fun getQTablePosition(ballPosQuantized: Vector2, paddlePosQuantized: Int): Int {
        return (ballPosQuantized.x * (quantize * quantize) + ballPosQuantized.y * quantize + paddlePosQuantized).toInt()
    }

    private fun getEstimatedFutureValue(action: Action, ballPosQuantized: Vector2, paddlePosQuantized: Int): Float {
        val leftQValue = qTable[getQTablePosition(ballPosQuantized, paddlePosQuantized  + (action.ordinal - 1))][Action.Left.ordinal]
        val stayQValue = qTable[getQTablePosition(ballPosQuantized, paddlePosQuantized  + (action.ordinal - 1))][Action.Stay.ordinal]
        val rightQValue = qTable[getQTablePosition(ballPosQuantized, paddlePosQuantized  + (action.ordinal - 1))][Action.Right.ordinal]

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
    Right
}