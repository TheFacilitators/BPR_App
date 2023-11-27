package com.facilitation.view.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.facilitation.view.R
import com.facilitation.view.databinding.ActivitySnakeBinding
import com.facilitation.view.receivers.TapReceiver
import com.facilitation.view.utility.CacheHelper
import com.facilitation.view.utility.interfaces.ITapInput
import com.facilitation.view.utility.MyActivityLifecycleCallbacks
import com.facilitation.view.utility.Snake
import com.facilitation.view.utility.enums.TapToCommandEnum

class SnakeActivity : AppCompatActivity(), Snake.GameOverListener, ITapInput {

    private lateinit var snakeGame: Snake
    private lateinit var binding: ActivitySnakeBinding
    private val handler = Handler(Looper.getMainLooper())
    private var gameLoopRunnable: Runnable? = null
    private var gameOverDialog: AlertDialog? = null
    private lateinit var activityLifecycleCallbacks: MyActivityLifecycleCallbacks
    private lateinit var inputMethodManager : InputMethodManager
    private lateinit var receiver: TapReceiver
    private val cacheHelper: CacheHelper = CacheHelper()

    override fun onCreate(savedInstanceState: Bundle?) {
        initCallback()
        super.onCreate(savedInstanceState)
        binding = ActivitySnakeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        snakeGame = Snake(binding.snakeView)
        snakeGame.setGameOverListener(this)
        binding.snakeView.isFocusable = true
        binding.snakeView.requestFocus()

        inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        receiver = TapReceiver(this, activityLifecycleCallbacks)

        binding.snakeView.setOnKeyListener { _, keyCode, event ->
            snakeGame.handleInput(event, keyCode)
            return@setOnKeyListener true
        }
        startGameLoop()
    }

    private fun initCallback() {
        var callback = cacheHelper.getCachedActivityLifecycleCallback(this)
        if (callback == null) {
            callback = MyActivityLifecycleCallbacks(this)
            cacheHelper.setCachedActivityLifecycleCallback(this, callback)
        }

        activityLifecycleCallbacks = callback
        application.registerActivityLifecycleCallbacks(activityLifecycleCallbacks)
    }

    private fun startGameLoop() {
        if (gameLoopRunnable != null) {
            handler.removeCallbacks(gameLoopRunnable!!)
        }
        val frameRate = 10L
        val delayMillis = 1000L / frameRate
        gameLoopRunnable = object : Runnable {
            override fun run() {
                if(snakeGame.gameActive){
                    snakeGame.update()
                    val canvas = binding.snakeView.holder.lockCanvas()
                    if (canvas != null) {
                        snakeGame.draw(canvas)
                        binding.snakeView.holder.unlockCanvasAndPost(canvas)
                    }
                    handler.postDelayed(this, delayMillis)
                    }
            }
        }
        handler.postDelayed(gameLoopRunnable as Runnable, delayMillis)
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
        handler.removeCallbacks(gameLoopRunnable!!)
        gameLoopRunnable = null
    }

    fun restartGame(view: View) {
        gameOverDialog?.dismiss()
        snakeGame.resetGame()
        startGameLoop()
    }

    fun exitGame(view: View) {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        gameOverDialog?.dismiss()
        finishAffinity()
    }

    override fun onInputReceived(commandEnum: TapToCommandEnum) {
        inputMethodManager.dispatchKeyEventFromInputMethod(binding.root, KeyEvent(
            KeyEvent.ACTION_DOWN, commandEnum.keyCode())
        )
        inputMethodManager.dispatchKeyEventFromInputMethod(binding.root, KeyEvent(
            KeyEvent.ACTION_UP, commandEnum.keyCode())
        )
    }
}
