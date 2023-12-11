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
import com.facilitation.view.utility.IGameOverListener
import com.facilitation.view.utility.ITapInput
import com.facilitation.view.utility.MyActivityLifecycleCallbacks
import com.facilitation.view.utility.Snake
import com.facilitation.view.utility.enums.TapToCommandEnum

/** Activity to handle the logic of Snake.
 * @property snakeGame the game logic.
 * @property binding the binding to the view XML.
 * @property handler a Handler created with a message Looper.
 * @property gameLoopRunnable a nullable Runnable for running the game.
 * @property gameOverDialog a nullable AlertDialog to display when the gaem is over.
 * @property activityLifecycleCallbacks custom implementation of activity lifecycle callbacks.
 * @property inputMethodManager manager to translate & manage the user input to the application.
 * @property receiver custom receiver for Tap device input.*/
class SnakeActivity : AppCompatActivity(), IGameOverListener, ITapInput {
    private lateinit var snakeGame: Snake
    private lateinit var binding: ActivitySnakeBinding
    private val handler = Handler(Looper.getMainLooper())
    private var gameLoopRunnable: Runnable? = null
    private var gameOverDialog: AlertDialog? = null
    private lateinit var activityLifecycleCallbacks: MyActivityLifecycleCallbacks
    private lateinit var inputMethodManager : InputMethodManager
    private lateinit var receiver: TapReceiver

    /** Initializes activityLifecycleCallbacks, inputMethodManager, receiver and snakeGame.
     * Sets on key listener for user input and a game over listener.
     * Calls startGameLoop().
     * @param savedInstanceState a Bundle containing the state the Activity was in last.*/
    override fun onCreate(savedInstanceState: Bundle?) {
        activityLifecycleCallbacks = intent.getSerializableExtra("callback") as MyActivityLifecycleCallbacks
        application.registerActivityLifecycleCallbacks(activityLifecycleCallbacks)
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

    /** If the gameLoopRunnable is instantiated: Callbacks on it are removed.
     * gameLoopRunnable is created with the function run() and the handler is called to post it
     * with a delay of a thousand divided by the device frame rate.*/
    private fun startGameLoop() {
        if (gameLoopRunnable != null) {
            handler.removeCallbacks(gameLoopRunnable!!)
        }
        val frameRate = 10L
        val delayMillis = 1000L / frameRate
        gameLoopRunnable = object : Runnable {
            /** If the snakeGame is active then update() is called with a posting delay of
             * a thousand divided by the device frame rate.
             * The canvas is locked and if it's not null then draw() is called on snakeGame &
             * thereafter the canvas is unlocked and posted to the UI.
             * Regardless of the state of the canvas, if the game is in active state then
             * postDelayed() is called on the handler with the same posting delay as earlier.*/
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

    /** If game over dialog is not already displayed: Creates & displays an un-cancelable dialog
     * with the score earned in the game.*/
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

    /** Calls showGameOverDialog(), removes the callbacks on the handler for the gameLoopRunnable
     * and sets the runnable to null.
     * @param score an integer value of the score earned in the game.*/
    override fun onGameOver(score: Int) {
        showGameOverDialog()
        handler.removeCallbacks(gameLoopRunnable!!)
        gameLoopRunnable = null
    }

    /** Dismisses the gameOverDialog, calls resetGame() on snakeGame and calls startGameLoop().
     * @param view the View item this method was called from.*/
    fun restartGame(view: View) {
        gameOverDialog?.dismiss()
        snakeGame.resetGame()
        startGameLoop()
    }

    /** Dismisses the gameOverDialog, calls finishAffinity() and creates & starts an intent for
     * MainActivity.
     * @param view the View item this method was called from.*/
    fun exitGame(view: View) {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        gameOverDialog?.dismiss()
        finishAffinity()
    }

    /** Delegating handling of the input from the Tap device to the inputMethodManager.
     * @param commandEnum a TapToCommandEnum containing the specific command to execute.*/
    override fun onInputReceived(commandEnum: TapToCommandEnum) {
        inputMethodManager.dispatchKeyEventFromInputMethod(binding.root, KeyEvent(
            KeyEvent.ACTION_DOWN, commandEnum.keyCode())
        )
        inputMethodManager.dispatchKeyEventFromInputMethod(binding.root, KeyEvent(
            KeyEvent.ACTION_UP, commandEnum.keyCode())
        )
    }
}
