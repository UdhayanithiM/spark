package com.example.sparkapp.data

// Defines the structure for a single question
data class PostTestQuestion(
    val section: String,
    val questionText: String,
    val options: List<String>,
    val answerIndex: Int
)

// This object holds all 30 of your hardcoded post-test questions
object PostTestQuestions {
    val allQuestions = listOf(
        PostTestQuestion(
            section = "Section 1: Knowledge",
            questionText = "Which behavioral pattern in a student most strongly suggests an underlying neurodevelopmental disorder rather than situational stress?",
            options = listOf(
                "Refusal to attend school after a bullying incident",
                "Difficulty adjusting to sudden changes in class schedule",
                "Mood changes during examination periods",
                "Fatigue following physical education class"
            ),
            answerIndex = 1
        ),
        PostTestQuestion(
            section = "Section 1: Knowledge",
            questionText = "A student with a history of academic success has suddenly started showing signs of underachievement, marked by increased irritability, social withdrawal, and complaints of physical discomfort. Which factor should be explored first?",
            options = listOf(
                "A medical condition, such as chronic pain or gastrointestinal issues",
                "A sudden shift in family dynamics, possibly indicating stress at home",
                "A possible depressive episode manifesting as somatic complaints",
                "A desire to avoid school due to bullying or peer rejection"
            ),
            answerIndex = 2
        ),
        PostTestQuestion(
            section = "Section 1: Knowledge",
            questionText = "What distinguishes Specific Learning Disability (SLD) from general poor academic performance?",
            options = listOf(
                "It occurs only when children are under stress",
                "It affects only verbal communication",
                "It persists despite adequate instruction and normal intelligence",
                "It disappears with additional homework"
            ),
            answerIndex = 2
        ),
        // ... (All other 27 questions from your Flutter file would go here) ...
        PostTestQuestion(
            section = "Section 3: Practice",
            questionText = "What should a counselor do when a child repeatedly says they feel unloved and want to disappear?",
            options = listOf(
                "Reassure them it’s just teenage mood",
                "Ask parents to restrict screen time",
                "Document expression, assess risk, and refer for mental health evaluation",
                "Suggest they speak to a friend"
            ),
            answerIndex = 2
        )
    )
}