package com.facilitation.view.utility

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.view.KeyEvent
import android.view.View

class Snake(private val view: View) {

    private val segmentSize = 20
    private var snakeSegments = mutableListOf(SnakeSegment(0, 0))
    private var foodX: Int = 0
    private var foodY: Int = 0
    var score: Int = 0
    private var snakeGrowth: Boolean = false
    var gameActive = true
    private var currentDirection: Direction = Direction.RIGHT
    private val snakePaint = Paint()
    private val foodPaint = Paint()
    private var isFirstMove: Boolean = true
    private var gameOverListener: GameOverListener? = null

    fun setGameOverListener(listener: GameOverListener) {
        gameOverListener = listener
    }

    init {
        snakePaint.color = Color.GREEN
        foodPaint.color = Color.RED
        generateFood()
    }

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

    fun startGame() {
        gameActive = true
    }

    fun stopGame() {
        gameActive = false
    }

    private fun gameOver() {
        gameActive = false
        gameOverListener?.onGameOver(score)
        println("Game Over! Your Score: $score")
    }

    fun resetGame() {
        snakeSegments.clear()
        snakeSegments.add(SnakeSegment(0, 0))
        foodX = 0
        foodY = 0
        score = 0
        snakeGrowth = false
        currentDirection = Direction.RIGHT
        gameActive = true
        isFirstMove = true
    }

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

    private fun moveSnake() {

        val newHeadX: Int
        val newHeadY: Int

        when (currentDirection) {
            Direction.UP -> {
                newHeadX = snakeSegments.first().x
                newHeadY = snakeSegments.first().y - segmentSize
            }
            Direction.DOWN -> {
                newHeadX = snakeSegments.first().x
                newHeadY = snakeSegments.first().y + segmentSize
            }
            Direction.LEFT -> {
                newHeadX = snakeSegments.first().x - segmentSize
                newHeadY = snakeSegments.first().y
            }
            Direction.RIGHT -> {
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

    private fun checkWallCollision(): Boolean {
        val headX = snakeSegments.first().x
        val headY = snakeSegments.first().y

        val canvasWidth = view.width
        val canvasHeight = view.height

        return headX < 0 || headY < 0 || headX >= canvasWidth || headY >= canvasHeight
    }

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

    private fun checkFoodCollision(): Boolean {
        val head = snakeSegments.first()
        return head.x == foodX && head.y == foodY
    }

    private fun increaseScore() {
        score++
        snakeGrowth = true
    }

    private fun generateFood() {
        val canvasWidth = view.width
        val canvasHeight = view.height

        foodX = (Math.random() * (canvasWidth - segmentSize)).toInt()
        foodY = (Math.random() * (canvasHeight - segmentSize)).toInt()

        foodX -= foodX % segmentSize
        foodY -= foodY % segmentSize
    }

    fun handleInput(event: KeyEvent, keyCode: Int?): Boolean {
        currentDirection = when (keyCode) {
            KeyEvent.KEYCODE_DPAD_UP -> if (currentDirection != Direction.DOWN) Direction.UP else currentDirection
            KeyEvent.KEYCODE_DPAD_DOWN -> if (currentDirection != Direction.UP) Direction.DOWN else currentDirection
            KeyEvent.KEYCODE_DPAD_LEFT -> if (currentDirection != Direction.RIGHT) Direction.LEFT else currentDirection
            KeyEvent.KEYCODE_DPAD_RIGHT -> if (currentDirection != Direction.LEFT) Direction.RIGHT else currentDirection
            else -> currentDirection
        }
        return true
    }

    enum class Direction {
        UP, DOWN, LEFT, RIGHT
    }

    data class SnakeSegment(val x: Int, val y: Int)

    interface GameOverListener {
        fun onGameOver(score: Int)
    }
}
