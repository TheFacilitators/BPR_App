package com.facilitation.view.utility

/** An interface containing a method for when the game is over.*/
interface IGameOverListener {
    /** Function to provide the logic for what happens when a game is over.
     * @param score the integer of the score achieved in the game.*/
    fun onGameOver(score: Int)
}