package at.fhhgb.breakout.qlearning

class State(var ballPosX:Int, var ballPosY:Int, var ballDirection: Int, var paddlePosX: Int){
    fun compareState(ballPosX:Int, ballPoxY:Int, ballDirection:Int, paddlePosX: Int): Boolean{
        return this.ballPosX == ballPosX && this.ballPosY == ballPoxY && this.ballDirection == ballDirection && this.paddlePosX == paddlePosX
    }

    override fun toString(): String {
        return "BallX: $ballPosX; BallY: $ballPosY; BallDirection: $ballDirection, PaddleX: $paddlePosX"
    }
}