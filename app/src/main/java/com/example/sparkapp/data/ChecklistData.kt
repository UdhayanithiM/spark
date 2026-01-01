package com.example.sparkapp.data

data class ChecklistSection(
    val title: String,
    val items: List<String>
)

object ChecklistData {
    val sections = listOf(
        ChecklistSection(
            title = "SCHOOL REFUSAL",
            items = listOf(
                "Frequent complaints of physical symptoms (headache, stomachache) only on school days",
                "Crying, tantrums, or clinging to parents in the morning before school",
                "Sudden drop in attendance without medical cause",
                "Requests to stay home despite no signs of illness",
                "Anxiety or distress about separation from parent/caregiver",
                "Recent history of bullying or peer conflict",
                "Avoids specific classes or activities at school",
                "Appears cheerful and relaxed when allowed to stay home",
            )
        ),
        ChecklistSection(
            title = "ACADEMIC DIFFICULTIES",
            items = listOf(
                "Consistently poor grades despite adequate effort",
                "Difficulty in reading, spelling, or writing compared to peers",
                "Slow completion of written work or frequent unfinished assignments",
                "Reversal of letters or numbers (e.g., b/d, p/q) after early grades",
                "Struggles with basic arithmetic or problem-solving",
                "Poor comprehension despite adequate decoding skills",
                "Avoidance of academic tasks (e.g., 'forgetting' homework)",
                "Teacher reports of attention and concentration problems during lessons",
            )
        ),
        ChecklistSection(
            title = "EMOTIONAL PROBLEMS",
            items = listOf(
                "Persistent sadness, tearfulness, or withdrawal from peers",
                "Loss of interest in activities previously enjoyed",
                "Excessive worry, fearfulness, or restlessness",
                "Frequent reassurance-seeking",
                "Irritability or mood swings disproportionate to the situation",
                "Changes in appetite or sleep patterns",
                "Expressions of hopelessness or low self-worth",
                "Somatic complaints (headaches, stomachaches) without medical cause",
            )
        ),
        ChecklistSection(
            title = "BEHAVIORAL ISSUES",
            items = listOf(
                "Frequent temper outbursts or defiance toward authority figures",
                "Aggression toward peers or school staff (verbal or physical)",
                "Repeated rule-breaking or lying",
                "Bullying behaviour (physical, verbal, social exclusion)",
                "Vandalism or destruction of property",
                "Skipping classes or roaming outside classrooms without permission",
                "Impulsivity leading to conflicts or accidents",
                "Disrespectful language or deliberate disobedience",
            )
        )
    )
}