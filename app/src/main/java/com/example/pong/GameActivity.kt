package com.example.pong

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.pong.databinding.ActivityGameBinding

class GameActivity : AppCompatActivity() {

    private lateinit var binding: ActivityGameBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGameBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val gameView = GameView(this)

        val extras = intent.extras
        if (extras != null) {
            if (extras.containsKey("difficulty")) {
                gameView.aiDifficulty = extras.getFloat("difficulty")
            } else {
                gameView.aiDifficulty = 0.5f // Set a default difficulty level
            }
        }


        binding.btnBack.setOnClickListener {
            gameView.isGameRunning = false
            this.finish()
        }

        gameView.resetGame()
        binding.gameContainer.addView(gameView) // Replace 'gameContainer' with the actual ID of your container view

        gameView.postInvalidate()

        if(!gameView.isGameRunning){
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            this.finish()
        }

    }
}

