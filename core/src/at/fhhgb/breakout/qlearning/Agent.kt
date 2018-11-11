package at.fhhgb.breakout.qlearning

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import java.io.FileWriter
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Paths
import java.util.*


class Agent(private val quantize: Int, private val learningRate: Float, private val discountFactor: Float) {
    private var qTable: Array<Array<Array<Array<FloatArray>>>> = Array(quantize) { Array(quantize) { Array(5) { Array(quantize) { FloatArray(3) } } } }
    private var exploration = 0.1f
    private var oldState: State? = null
    private var oldAction: Action = Action.Stay

    private var score = 0f

    init {
        loadQTable()
    }

    fun getAction(state: State): Action {
        oldState = state

        val leftQValue = qTable[state.ballPosX][state.ballPosY][state.ballDirection][state.paddlePosX][Action.Left.ordinal]
        val rightQValue = qTable[state.ballPosX][state.ballPosY][state.ballDirection][state.paddlePosX][Action.Right.ordinal]
        val stayQValue = qTable[state.ballPosX][state.ballPosY][state.ballDirection][state.paddlePosX][Action.Stay.ordinal]

        val random = Random()
        val randomAction = (random.nextFloat() * 3).toInt()

        val explore = exploration > random.nextFloat()

        oldAction = if (explore || (leftQValue == rightQValue && leftQValue == stayQValue)) {
            Action.getAction(randomAction)
        } else if (leftQValue > rightQValue && leftQValue > stayQValue) {
            Action.Left
        } else if (rightQValue > leftQValue && rightQValue > stayQValue) {
            Action.Right
        } else {
            Action.Stay
        }

        return oldAction
    }

    fun stateChanged(newState: State, reward: Int) {
//        println("OldState: $oldState, NewState: $newState")
        val qValue = getQValue(newState, oldAction, reward)
        score += qValue
        qTable[oldState!!.ballPosX][oldState!!.ballPosY][oldState!!.ballDirection][oldState!!.paddlePosX][oldAction.ordinal] = qValue
    }

    private fun getQValue(newState: State, action: Action, reward: Int): Float {
        val oldValue = qTable[oldState!!.ballPosX][oldState!!.ballPosY][oldState!!.ballDirection][oldState!!.paddlePosX][action.ordinal]
        val estimatedFutureValue = getEstimatedFutureValue(action, newState)

        return (1 - learningRate) * oldValue + learningRate * (reward + discountFactor * estimatedFutureValue)
    }

    private fun getEstimatedFutureValue(action: Action, newState: State): Float {
        var leftQValue = Float.MIN_VALUE
        if (newState.paddlePosX > 0) {
            leftQValue = qTable[newState.ballPosX][newState.ballPosY][newState.ballDirection][newState.paddlePosX][Action.Left.ordinal]
        }
        val stayQValue = qTable[newState.ballPosX][newState.ballPosY][newState.ballDirection][newState.paddlePosX][Action.Stay.ordinal]

        var rightQValue = Float.MIN_VALUE
        if (newState.paddlePosX < quantize - 1) {
            rightQValue = qTable[newState.ballPosX][newState.ballPosY][newState.ballDirection][newState.paddlePosX][Action.Right.ordinal]
        }

        return if (leftQValue > rightQValue && leftQValue > stayQValue) {
            leftQValue
        } else if (rightQValue > leftQValue && rightQValue > stayQValue) {
            rightQValue
        } else {
            stayQValue
        }
    }

    fun restart() {
        println("Score: $score, Exploration: $exploration")
        score = 0f
        if (exploration > 0.01f)
            exploration -= 0.001f
    }

    fun saveQTable() {
        FileWriter("qtable.json").use {
            val gson = GsonBuilder().create()
            gson.toJson(qTable, it)
        }
    }

    fun loadQTable() {
        val fileName = "./qtable.json"
        val path = Paths.get(fileName)

        try {
            Files.newBufferedReader(path, StandardCharsets.UTF_8).use {
                val qtable = it.readLine()
                val gson = Gson()
                qTable = gson.fromJson(qtable, Array<Array<Array<Array<FloatArray>>>>::class.java)
            }
        } catch (e: Exception) {
            println("file nor available")
        }
    }
}