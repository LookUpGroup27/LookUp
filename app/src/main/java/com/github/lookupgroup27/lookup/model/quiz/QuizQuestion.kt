package com.github.lookupgroup27.lookup.model.quiz

/**
 * Data class representing a quiz question.
 *
 * @property question The text of the question being asked.
 * @property answers A list of possible answers for the question. This includes both correct and
 *   incorrect answers, ideally shuffled for display.
 * @property correctAnswer The correct answer for the question, used to evaluate user responses.
 */
data class QuizQuestion(val question: String, val answers: List<String>, val correctAnswer: String)
