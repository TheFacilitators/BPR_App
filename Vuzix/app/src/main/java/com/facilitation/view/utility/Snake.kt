package com.facilitation.view.utility

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.view.KeyEvent
import android.view.View
import com.facilitation.view.utility.enums.DirectionEnum

/** Class for handling the game logic of Snake.
 * @constructor
 * @param view the View in which the game is displayed.
 * @property segmentSize the default size of the UI elements.
 * @property snakeSegments a mutable list of SnakeSegment objects.
 * @property foodX an integer of the grid X-coordinates of the spawned food.
 * @property foodY an integer of the grid Y-coordinates of the spawned food.
 * @property score an integer of the users score.
 * @property snakeGrowth a boolean of whether the snake should grow longer.
 * @property gameActive a boolean of whether tha game is active or not.
 * @property currentDirection the direction in which the snake is headed.
 * @property snakePaint the color of the snake.
 * @property foodPaint the color of the food.
 * @property isFirstMove a boolean of whether a move is the first one of the current game.
 * @property gameOverListener a listener for when the game is over.
 * @see SnakeSegment
 * @see Direction
 * @see GameOverListener*/
class Snake(private val view: View) {
    private val segmentSize = 20
    private var snakeSegments = mutableListOf(SnakeSegment(0, 0))
    private var foodX: Int = 0
    private var foodY: Int = 0
    var score: Int = 0
    private var snakeGrowth: Boolean = false
    var gameActive = true
    private var currentDirection: DirectionEnum = DirectionEnum.RIGHT
    private val snakePaint = Paint()
    private val foodPaint = Paint()
    private var isFirstMove: Boolean = true
    private var gameOverListener: GameOverListener? = null

    /** A setter for gameOverListener.
     * @param listener the GameOverListener to set the variable to.*/
    fun setGameOverListener(listener: GameOverListener) {
        gameOverListener = listener
    }

    /** Initializing snakePaint to green, foodPaint to red and calling generateFood().*/
    init {
        snakePaint.color = Color.GREEN
        foodPaint.color = Color.RED
        generateFood()
    }

    /** Setting the color of the Canvas to reflect the location of game objects.
     * @param canvas the UI Canvas that the game is shown on.*/
    fun draw(canvas: Canvas) {

        canvas.drawColor(Color.BLACK)

        for (segment in snakeSegments) {
            canvas.drawRect(
                segment.x.toFloat(),
                segment.y.toFloat(),
                (segment.x + segmentSize).toFloat(),
                (segment.y + segmentSize).toFloat(),
                snakePaint
            )
        }

        canvas.drawRect(
            foodX.toFloat(),
            foodY.toFloat(),
            (foodX + segmentSize).toFloat(),
            (foodY + segmentSize).toFloat(),
            foodPaint
        )
    }

    /** A setter for the state of the game, setting the gameActive boolean to 'true'.*/
    fun startGame() {
        gameActive = true
    }

    /** A setter for the state of the game, setting the gameActive boolean to 'false'.*/
    fun stopGame() {
        gameActive = false
    }

    /** Setting the gameActive boolean to 'false' and calling onGameOver() on gameOverListener.*/
    private fun gameOver() {
        gameActive = false
        gameOverListener?.onGameOver(score)
        println("Game Over! Your Score: $score")
    }

    /** Resetting the state of the game.
     * Clears snakeSegments and adds a SnakeSegment at (0,0).
     * Sets foodX, foodY and score to 0.
     * Sets snakeGrowth to 'false' and gameActive & isFirstMove to 'true'.
     * Sets currentDirection to RIGHT.
     * @see SnakeSegment
     * @see DirectionEnum*/
    fun resetGame() {
        snakeSegments.clear()
        snakeSegments.add(SnakeSegment(0, 0))
        foodX = 0
        foodY = 0
        score = 0
        snakeGrowth = false
        currentDirection = DirectionEnum.RIGHT
        gameActive = true
        isFirstMove = true
    }

    /** Sets isFirstMove to 'false' if currently 'true' and calls moveSnake().
     * Calls checkWallCollision() and checkSelfCollision(), if either returns 'true' gameOver() is
     * called and the method is exited.
     * Calls checkFoodCollision() and calls increaseScore() and generateFood() if 'true'.*/
    fun update() {
        if(isFirstMove) {
            generateFood()
            isFirstMove = false
        }

        moveSnake()

        if (checkWallCollision() || checkSelfCollision()) {
            gameOver()
            return
        }

        if (checkFoodCollision()) {
            increaseScore()
            generateFood()
        }
    }

    /** Adds a new SnakeSegment coordinates for the next location based on currentDirection and if
     * snakeGrowth is 'false' then the last segment in snakeSegments.*/
    private fun moveSnake() {

        val newHeadX: Int
        val newHeadY: Int

        when (currentDirection) {
            DirectionEnum.UP -> {
                newHeadX = snakeSegments.first().x
                newHeadY = snakeSegments.first().y - segmentSize
            }
            DirectionEnum.DOWN -> {
                newHeadX = snakeSegments.first().x
                newHeadY = snakeSegments.first().y + segmentSize
            }
            DirectionEnum.LEFT -> {
                newHeadX = snakeSegments.first().x - segmentSize
                newHeadY = snakeSegments.first().y
            }
            DirectionEnum.RIGHT -> {
                newHeadX = snakeSegments.first().x + segmentSize
                newHeadY = snakeSegments.first().y
            }
        }

        val newHead = SnakeSegment(newHeadX, newHeadY)

        snakeSegments.add(0, newHead)

        if (snakeGrowth) {
            snakeGrowth = false
        } else {
            snakeSegments.removeAt(snakeSegments.size - 1)
        }
    }

    /** Checks whether the snake has collided with the boundaries of 'view'.
     * A 'true' result means that there has been a collision.
     * @return 'true': The coordinates of the first item in snakeSegments are less than 0, the X-coordinates are equal to or greater than the width of 'view' or the Y-coordinates are equal to or greater than the height of 'view'. 'false': Any of the aforementioned variables are false.*/
    private fun checkWallCollision(): Boolean {
        val headX = snakeSegments.first().x
        val headY = snakeSegments.first().y

        val canvasWidth = view.width
        val canvasHeight = view.height

        return headX < 0 || headY < 0 || headX >= canvasWidth || headY >= canvasHeight
    }

    /** Checks whether the snake has collided with itself.
     * A 'true' result means that there has been a collision.
     * Iterates through snakeSegments and compares the coordinates of the first item to every other
     * element in the list.
     * @return 'true': The coordinates of the first item in snakeSegments are the same as another items. 'false': The aforementioned items coordinates are not the same.*/
    private fun checkSelfCollision(): Boolean {
        val head = snakeSegments.first()

        for (i in 1 until snakeSegments.size) {
            val segment = snakeSegments[i]
            if (head.x == segment.x && head.y == segment.y) {
                return true
            }
        }
        return false
    }

    /** Checks whether the snake has collided with food.
     * A 'true' result means that there has been a collision.
     * Compares the coordinates of the first item in snakeSegments to foodX & foodY.
     * @return 'true': The coordinates for the first segment match foodX & foodY. 'false': The aforementioned coordinates do not match.*/
    private fun checkFoodCollision(): Boolean {
        val head = snakeSegments.first()
        return head.x == foodX && head.y == foodY
    }

    /** Increments the players score and sets snakeGrowth to 'true'.*/
    private fun increaseScore() {
        score++
        snakeGrowth = true
    }

    /** Sets foodX & foodY to a random integer within the bounds of 'view'.*/
    private fun generateFood() {
        val canvasWidth = view.width
        val canvasHeight = view.height

        foodX = (Math.random() * (canvasWidth - segmentSize)).toInt()
        foodY = (Math.random() * (canvasHeight - segmentSize)).toInt()

        foodX -= foodX % segmentSize
        foodY -= foodY % segmentSize
    }

    /** Sets currentDirection based on player input and itself.
     * If the input is the opposite direction of currentDirection it remains the same.
     * @param event the KeyEvent received.
     * @param keyCode the integer code of the KeyEvent.
     * @return 'true': On completion.*/
    fun handleInput(event: KeyEvent, keyCode: Int?): Boolean {
        currentDirection = when (keyCode) {
            KeyEvent.KEYCODE_DPAD_UP -> if (currentDirection != DirectionEnum.DOWN) DirectionEnum.UP else currentDirection
            KeyEvent.KEYCODE_DPAD_DOWN -> if (currentDirection != DirectionEnum.UP) DirectionEnum.DOWN else currentDirection
            KeyEvent.KEYCODE_DPAD_LEFT -> if (currentDirection != DirectionEnum.RIGHT) DirectionEnum.LEFT else currentDirection
            KeyEvent.KEYCODE_DPAD_RIGHT -> if (currentDirection != DirectionEnum.LEFT) DirectionEnum.RIGHT else currentDirection
            else -> currentDirection
        }
        return true
    }



    /** Model class for a single segment of the snake.
     * @constructor
     * @param x an integer for the X-coordinate of the segment.
     * @param y an integer for the Y-coordinate of the segment.*/
    data class SnakeSegment(val x: Int, val y: Int)

    /** An interface containing a method for when the game is over.*/
    interface GameOverListener {
        /** Function to provide the logic for what happens when a game is over.
         * @param score the integer of the score achieved in the game.*/
        fun onGameOver(score: Int)
    }
}
