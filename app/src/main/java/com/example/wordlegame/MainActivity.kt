package com.example.wordlegame

import android.content.Context
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import com.github.jinatonic.confetti.CommonConfetti


class MainActivity : AppCompatActivity() {
    val wordList = FourLetterWordList()

    var wordToGuess = wordList.getRandomFourLetterWord()
    var counter = 0
    var correctCounter = 0
    var guessWord = ""
    // Define a darker yellow color
    val darkerYellow = Color.rgb(204, 204, 0)

    private lateinit var  userGuess1:TextView
    private lateinit var  userGuess2:TextView
    private lateinit var userGuess3 :TextView
    private lateinit var userResult1:TextView
    private lateinit var userResult2:TextView
    private lateinit var userResult3:TextView
    private lateinit var keyWord:TextView

    private lateinit var guessInput: EditText

    private lateinit var button:Button
    private lateinit var resetBtn:Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.title = "Wordle"
        configureViews()

        // Setup the dropdown menu (Spinner)
        val themes = arrayOf("Common", "Sports", "French", "Food", "Tech")
        val spinner: Spinner = findViewById(R.id.themeSpinner)
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, themes)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                wordList.switchList(themes[position])
                resetGame()
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // No action required here
            }
        }


        button.setOnClickListener {
            it.hideKeyboard()
            guessWord = guessInput.text.toString().uppercase()
            // Validate the guess word first, if invalid input, show error message and ask user for new input.
            if (!validGuessWord()) {
                return@setOnClickListener // Early return, stopping further execution
            }
            counter++
            println("🍏wordToGuess is $wordToGuess")

            var checkResult = checkGuess(guessWord)

            when (counter) {
                    1 -> {
                        userGuess1.text = guessWord
                        userResult1.text = checkResult
                    }

                    2 -> {
                        userGuess2.text = guessWord
                        userResult2.text = checkResult
                    }

                    3 -> {
                        userGuess3.text = guessWord
                        userResult3.text = checkResult
                    }

            }
            guessInput.getText().clear()

            if(guessWord==wordToGuess) {
                // 'constraintLayout' is the root layout of your activity, covering the entire screen
                val container = findViewById<ViewGroup>(R.id.constraintLayout)
                // This will trigger the confetti effect to cover the entire screen
                CommonConfetti.rainingConfetti(container, intArrayOf(Color.YELLOW, Color.GREEN, Color.MAGENTA))
                    .oneShot()
                correctCounter++
                Toast.makeText(this, "You have correctly guessed $correctCounter words!", Toast.LENGTH_SHORT).show()
            }

            if(guessWord==wordToGuess||counter ==3){
                    keyWord.text = wordToGuess
                    resetGuess()
                    counter = 0
                }

            }
        }

    private fun configureViews() {
        userGuess1 = findViewById<TextView>(R.id.userGuess1)
        userGuess2 = findViewById<TextView>(R.id.userGuess2)
        userGuess3 = findViewById<TextView>(R.id.userGuess3)
        userResult1 = findViewById<TextView>(R.id.checkResult1)
        userResult2 = findViewById<TextView>(R.id.checkResult2)
        userResult3 = findViewById<TextView>(R.id.checkResult3)
        keyWord = findViewById<TextView>(R.id.keyWord)
        guessInput = findViewById<EditText>(R.id.guessInput)
        button = findViewById<Button>(R.id.button)
        resetBtn = findViewById<Button>(R.id.resetBtn)
    }

    private fun validGuessWord():Boolean{
        if (guessWord.length != 4 || !guessWord.all { it.isLetter() }){
            Log.d("WordleGame", "Invalid input detected") // Add this line for debugging
            Toast.makeText(this,"Error!Your guess must be exactly 4 alphabetical letters(A-Z).",Toast.LENGTH_SHORT).show()
            guessInput.getText().clear()
            return false
        }
        return true
    }

        /**
         * Parameters / Fields:
         *   wordToGuess : String - the target word the user is trying to guess
         *   guess : String - what the user entered as their guess
         *
         * Returns a String of 'O', '+', and 'X', where:
         *   'O' represents the right letter in the right place
         *   '+' represents the right letter in the wrong place
         *   'X' represents a letter not in the target word
         */
        /*
         private fun checkGuess(guess: String): String {
            var result = ""
            for (i in 0..3) {
                if (guess[i] == wordToGuess[i]) {
                    result += "O"
                } else if (guess[i] in wordToGuess) {
                    result += "+"
                } else {
                    result += "X"
                }
            }
            return result
        }
        */

        private fun checkGuess(guess: String): SpannableString {
            val spannable = SpannableString(guess)
            for (i in guess.indices) {
                when {
                    guess[i] == wordToGuess[i] -> {
                        // Correct letter in the correct place - green color
                        spannable.setSpan(ForegroundColorSpan(Color.GREEN), i, i + 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                    }
                    guess[i] in wordToGuess -> {
                        // Correct letter in the wrong place - darker yellow color
                        spannable.setSpan(ForegroundColorSpan(darkerYellow), i, i + 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                    }
                    else -> {
                        // Incorrect letter - red color
                        spannable.setSpan(ForegroundColorSpan(Color.RED), i, i + 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                    }
                }
            }
            return spannable
        }
    private fun resetGame(){
        wordToGuess = wordList.getRandomFourLetterWord()
        userGuess1.text = ""
        userGuess2.text = ""
        userGuess3.text = ""
        userResult1.text = ""
        userResult2.text = ""
        userResult3.text = ""
        guessInput.getText().clear()
        keyWord.text = ""
        button.visibility = View.VISIBLE
        resetBtn.visibility = View.INVISIBLE
        counter = 0
       // correctCounter = 0
    }


    private fun resetGuess() {
            configureViews()
            button.visibility = View.INVISIBLE
            resetBtn.visibility = View.VISIBLE
            resetBtn.setOnClickListener {
                userGuess1.text = " "
                userGuess2.text = " "
                userGuess3.text = " "
                userResult1.text = " "
                userResult2.text = " "
                userResult3.text = " "
                guessInput.getText().clear()
                keyWord.text = " "
                resetBtn.visibility = View.INVISIBLE
                button.visibility = View.VISIBLE
                wordToGuess = FourLetterWordList().getRandomFourLetterWord()
            }


        }
    private fun View.hideKeyboard() {
        val inputManager = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputManager.hideSoftInputFromWindow(windowToken, 0)
    }

    }

