package com.example.pong

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.Log
import android.view.MotionEvent
import android.view.View


class GameView(context: Context) : View(context) {

    private var isGameOver: Boolean = false
    private var winner: String = ""

    var aiDifficulty: Float = 0.5f

    var isGameRunning: Boolean = true

    private val paint: Paint = Paint()

    // Scores
    private var scorePlayer1: Int = 0
    private var scorePlayer2: Int = 0

    // Ball properties
    private var ballX: Int = width / 2
    private var ballY: Int = height / 2
    private var ballRadius: Int = 30
    private val initialBallSpeedX: Int = 10
    private val initialBallSpeedY: Int = 10
    private var ballSpeedX: Int = 10
    private var ballSpeedY: Int = 10

    // Paddle properties
    private var paddleWidth: Int = 30
    private var paddleHeight: Int = 300
    private var paddle1X: Int = (width - paddleWidth) / 2
    private var paddle1Y: Int = height - paddleHeight * 2
    private var paddle2X: Int = (width - paddleWidth) / 2
    private var paddle2Y: Int = paddleHeight

    private fun resetScores(){
        scorePlayer1 = 0
        scorePlayer2 = 0
        Log.d("GameView", "Scores reset to 0-0")
    }

    fun resetGame(){
        resetScores()
        postInvalidate()
    }

    init {
        isGameRunning = true
        resetGame()
        // Paint settings
        paint.color = Color.parseColor("#FF6750A4")
        paint.style = Paint.Style.FILL
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

        // Set the ball position
        ballX = w / 2
        ballY = h / 2

        // Set the left paddle position (paddle1)
        paddle1X = paddleWidth // A bit of padding from the left edge
        paddle1Y = (h - paddleHeight) / 2 // Vertically centered

        // Set the right paddle position (paddle2)
        paddle2X = w - paddleWidth * 2 // Near the right edge, accounting for the paddle's width
        paddle2Y = (h - paddleHeight) / 2 // Vertically centered

        gameLoop()
    }



    @SuppressLint("DrawAllocation")
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        // Draw the ball
        canvas.drawCircle(ballX.toFloat(), ballY.toFloat(), ballRadius.toFloat(), paint)

        // Define the corner radius for the pill shape
        val cornerRadius = paddleWidth / 2f  

        // Draw the first paddle (paddle1)
        val paddle1Rect = RectF(paddle1X.toFloat(), paddle1Y.toFloat(), (paddle1X + paddleWidth).toFloat(), (paddle1Y + paddleHeight).toFloat())
        canvas.drawRoundRect(paddle1Rect, cornerRadius, cornerRadius, paint)

        // Draw the second paddle (paddle2)
        val paddle2Rect = RectF(paddle2X.toFloat(), paddle2Y.toFloat(), (paddle2X + paddleWidth).toFloat(), (paddle2Y + paddleHeight).toFloat())
        canvas.drawRoundRect(paddle2Rect, cornerRadius, cornerRadius, paint)

        // Draw a frame around the play area
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 5f
        val halfStrokeWidth = paint.strokeWidth / 2
        canvas.drawRect(halfStrokeWidth, halfStrokeWidth, width.toFloat() - halfStrokeWidth, height.toFloat() - halfStrokeWidth, paint)

        // Draw score in the middle of the screen
        paint.style = Paint.Style.FILL
        paint.textSize = 400f
        paint.alpha = 40
        paint.textAlign = Paint.Align.CENTER
        val scoreText = "$scorePlayer1 | $scorePlayer2"
        val centerX = width / 2f
        val textY = height / 2f + 100f
        canvas.drawText(scoreText, centerX, textY, paint)

        // If the game is lost display text
        if (isGameOver) {
            paint.textSize = 200f
            paint.alpha = 255
            canvas.drawText(winner, centerX, textY-350, paint)
        }

        // Reset paint style
        paint.style = Paint.Style.FILL
        paint.style = Paint.Style.FILL
        paint.alpha = 255
    }


    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_MOVE -> {
                if (event.x < width / 2) {
                    // Move the left paddle (paddle1)
                    paddle1Y = (event.y - paddleHeight / 2).toInt().coerceIn(0, height - paddleHeight)
                }
                // Redraw the view
                invalidate()
            }
        }
        return true
    }


    private fun updateGame() {
        val previousBallX = ballX - ballSpeedX
        val previousBallY = ballY - ballSpeedY

        // Update the ball's position
        ballX += ballSpeedX
        ballY += ballSpeedY


        if (ballSpeedX > 0 && ballX > (width / 2) - aiDifficulty * width/3) {
            val targetY = ballY - paddleHeight / 2
            val deltaY = targetY - paddle2Y
            val maxSpeed = when(aiDifficulty) {
                0.1F -> 7
                0.5F -> 10
                2.0F -> 20
                else -> 10
            }

            val movement = (deltaY * aiDifficulty).toInt().coerceIn(-maxSpeed, maxSpeed)

            paddle2Y += movement
            paddle2Y = paddle2Y.coerceIn(0, height - paddleHeight)
        }


        // Check for collision with left and right walls
        if (ballX - ballRadius <= 0 || ballX + ballRadius >= width) {
            // First, check who scores before resetting the ball position
            if (ballX - ballRadius <= 0) {
                scorePlayer2++  // Player 2 scores
                Log.d("paddle2 scored", "Drawing score: $scorePlayer1 | $scorePlayer2")
            } else if (ballX + ballRadius >= width) {
                scorePlayer1++  // Player 1 scores
                Log.d("paddle1 scored", "Drawing score: $scorePlayer1 | $scorePlayer2")
            }

            // Then reset the ball position and speed
            ballX = width / 2
            ballY = height / 2
            ballSpeedY = initialBallSpeedY

            // Randomly determine direction of the ball on start
            val random = java.util.Random()
            ballSpeedX = if (random.nextBoolean()) initialBallSpeedX else -initialBallSpeedX

            postInvalidate()
        }

        val margin = 50

        // Check for collision with top and bottom walls with margin
        if (ballY - ballRadius <= margin || ballY + ballRadius >= height - margin) {
            ballSpeedY = -ballSpeedY
        }

        // Check for collision with paddle2
        if (ballX + ballRadius >= paddle2X && previousBallX < paddle2X + paddleWidth) {
            if (ballY + ballRadius >= paddle2Y && ballY - ballRadius <= paddle2Y + paddleHeight) {
                // Collision detected, reverse the ball's horizontal direction
                ballSpeedX = -ballSpeedX
                // Correct the position
                ballX = paddle2X - ballRadius
            }
        }
        // Check for collision with paddle1
        if ((ballX - ballRadius <= paddle1X + paddleWidth) &&
            (ballY + ballRadius >= paddle1Y) &&
            (ballY - ballRadius <= paddle1Y + paddleHeight)) {

            if (ballSpeedX < 0) {  // Ensure the ball is moving towards paddle1
                // Collision detected, reverse the ball's horizontal direction
                ballSpeedX = -(ballSpeedX * 1.1).toInt()

                // Correct the position
                ballX = paddle1X + paddleWidth + ballRadius
            }
        }

        if (scorePlayer1 >= 5 || scorePlayer2 >= 5) {
            isGameOver = true
            winner = if (scorePlayer1 >= 5) "You win!" else "You lose!"

            // Stop the game loop
            isGameRunning = false
        }
        // Trigger a redraw of the view to update the score display
        postInvalidate()
    }


    private fun gameLoop() {
        Thread {
            while (isGameRunning) {
                updateGame()
                postInvalidate() // Redraw the view on the UI thread

                try {
                    Thread.sleep(16) // 60 frames per second
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }
            }
        }.start()
    }

}
