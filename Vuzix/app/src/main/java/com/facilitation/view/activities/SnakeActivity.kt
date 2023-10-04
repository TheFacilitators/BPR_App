package com.facilitation.view.activities

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.facilitation.view.R
import com.facilitation.view.databinding.ActivitySnakeBinding
import com.facilitation.view.utility.Snake

class SnakeActivity : AppCompatActivity(), Snake.GameOverListener {

    private lateinit var snakeGame: Snake
    private lateinit var binding: ActivitySnakeBinding
    private val handler = Handler(Looper.getMainLooper())
    private var gameLoopRunnable: Runnable? = null
    private var gameOverDialog: AlertDialog? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySnakeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        snakeGame = Snake(binding.snakeView)
        snakeGame.setGameOverListener(this)
        binding.snakeView.isFocusable = true
        binding.snakeView.requestFocus()
        binding.snakeView.setOnKeyListener { _, keyCode, event ->
            snakeGame.handleInput(event, keyCode)
            return@setOnKeyListener true
        }
        startGameLoop()
    }

    private fun startGameLoop() {
        if (gameLoopRunnable != null) {
            handler.removeCallbacks(gameLoopRunnable!!)
        }
        val frameRate = 10L
        val delayMillis = 1000L / frameRate
        gameLoopRunnable = object : Runnable {
            override fun run() {
                snakeGame.update()
                val canvas = binding.snakeView.holder.lockCanvas()
                if (canvas != null) {
                    snakeGame.draw(canvas)
                    binding.snakeView.holder.unlockCanvasAndPost(canvas)
                }
                handler.postDelayed(this, delayMillis)
            }
        }
        handler.postDelayed(gameLoopRunnable as Runnable, delayMillis)
    }

    fun restartGame() {
        if (gameLoopRunnable != null) {
            handler.removeCallbacks(gameLoopRunnable!!)
        }
        snakeGame.resetGame()
        startGameLoop()
    }

    private fun showGameOverDialog() {
        if (gameOverDialog != null && gameOverDialog!!.isShowing) {
            return
        }
        val dialogView = layoutInflater.inflate(R.layout.snake_game_over, null)
        val textViewScore = dialogView.findViewById<TextView>(R.id.textViewScore)
        textViewScore.text = "Your Score: ${snakeGame.score}"
        val builder = AlertDialog.Builder(this)
        builder.setView(dialogView)
        builder.setCancelable(false)
        gameOverDialog = builder.create()
        gameOverDialog?.show()
    }

    override fun onGameOver(score: Int) {
        showGameOverDialog()
    }

    fun restartGame(view: View) {
        gameOverDialog?.dismiss()
        restartGame()
    }
    fun exitGame(view: View) {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finishAffinity()
    }
}
