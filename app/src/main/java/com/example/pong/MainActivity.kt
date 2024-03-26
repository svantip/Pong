package com.example.pong

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.method.Touch
import android.view.View
import com.example.pong.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnPlay.setOnClickListener {
            binding.btnPlay.visibility = View.INVISIBLE
            binding.btnPlay.isEnabled = false
            binding.difficultyButtonContainer.visibility = View.VISIBLE
        }

        binding.btnEasy.setOnClickListener {
            val intent = Intent(this, GameActivity::class.java)
            intent.putExtra("difficulty", 0.1f)
            startActivity(intent)
        }
        binding.btnMedium.setOnClickListener {
            val intent = Intent(this, GameActivity::class.java)
            intent.putExtra("difficulty", 0.5f)
            startActivity(intent)
        }
        binding.btnHard.setOnClickListener {
            val intent = Intent(this, GameActivity::class.java)
            intent.putExtra("difficulty", 2.0f)
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()

        binding.difficultyButtonContainer.visibility = View.GONE
        binding.btnPlay.visibility = View.VISIBLE
        binding.btnPlay.isEnabled = true
    }

}
