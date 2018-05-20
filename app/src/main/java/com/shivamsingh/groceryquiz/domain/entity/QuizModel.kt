package com.shivamsingh.groceryquiz.domain.entity

data class QuizModel(val question: String,
                     val options: List<QuizOptionModel>,
                     val correctOption: QuizOptionModel)