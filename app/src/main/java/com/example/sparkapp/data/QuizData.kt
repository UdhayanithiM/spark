package com.example.sparkapp.data

// This data class defines the structure for a single question
data class Question(
    val questionText: String,
    val options: List<String>,
    val answerIndex: Int
)

// This object holds all 30 of your hardcoded pre-test questions
object PreTestQuestions {
    val allQuestions = listOf(
        Question(
            questionText = "Which behavioral pattern in a student most strongly suggests an underlying neurodevelopmental disorder rather than situational stress?",
            options = listOf(
                "Refusal to attend school after a bullying incident",
                "Difficulty adjusting to sudden changes in class schedule",
                "Mood changes during examination periods",
                "Fatigue following physical education class"
            ),
            answerIndex = 1
        ),
        Question(
            questionText = "A student with a history of academic success has suddenly started showing signs of underachievement, marked by increased irritability, social withdrawal, and complaints of physical discomfort. Which factor should be explored first?",
            options = listOf(
                "A medical condition, such as chronic pain or gastrointestinal issues",
                "A sudden shift in family dynamics, possibly indicating stress at home",
                "A possible depressive episode manifesting as somatic complaints",
                "A desire to avoid school due to bullying or peer rejection"
            ),
            answerIndex = 2
        ),
        Question(
            questionText = "What distinguishes Specific Learning Disability (SLD) from general poor academic performance?",
            options = listOf(
                "It occurs only when children are under stress",
                "It affects only verbal communication",
                "It persists despite adequate instruction and normal intelligence",
                "It disappears with additional homework"
            ),
            answerIndex = 2
        ),
        Question(
            questionText = "Which of the following best explains the long-term impact of untreated childhood depression?",
            options = listOf(
                "Reduced interest in academic competition",
                "Risk of developing antisocial traits",
                "Increased chance of developing chronic mood disorders",
                "Poor muscle coordination and handwriting"
            ),
            answerIndex = 2
        ),
        Question(
            questionText = "Why is structured observation preferred in early identification of emotional issues in school children?",
            options = listOf(
                "It helps evaluate physical growth milestones",
                "It eliminates the need for academic testing",
                "It captures behavioral patterns in real-time settings",
                "It ensures children follow school rules"
            ),
            answerIndex = 2
        ),
        Question(
            questionText = "Which of the following would be the clearest indicator for referring a student for possible Intellectual Disability (ID)?",
            options = listOf(
                "Avoids reading aloud due to fear of embarrassment",
                "Shows physical complaints like headaches during exams",
                "Struggles with basic academic skills despite repeated help and support",
                "Often breaks rules and shows defiance toward authority figures"
            ),
            answerIndex = 2
        ),
        Question(
            questionText = "A student frequently complains of headaches and stomachaches, yet medical evaluations show no physical cause. They also demonstrate a high level of perfectionism, are often anxious about making mistakes, and avoid group activities. What might be the underlying issue?",
            options = listOf(
                "A mood disorder with somatic symptoms",
                "Generalized anxiety disorder",
                "A primary somatic disorder with anxiety as a secondary feature",
                "A personality disorder with a focus on academic performance"
            ),
            answerIndex = 1
        ),
        Question(
            questionText = "A student consistently displays aggressive behavior towards others, takes pleasure in seeing others upset, and has a history of vandalism and rule-breaking. Despite frequent disciplinary actions, the behavior does not seem to improve. What is the most likely explanation?",
            options = listOf(
                "Exposure to significant trauma or abuse leading to maladaptive coping",
                "A conduct disorder, with an emphasis on a lack of empathy for others",
                "A personality disorder, particularly antisocial personality traits",
                "A learned behavior based on observing aggressive role models"
            ),
            answerIndex = 1
        ),
        Question(
            questionText = "A student repeatedly complains of headaches and stomachaches with no medical basis, particularly during tests or presentations. This suggests:",
            options = listOf(
                "Social skills deficit",
                "Autism Spectrum traits",
                "Anxiety-related somatic symptoms",
                "Oppositional behavior"
            ),
            answerIndex = 2
        ),
        Question(
            questionText = "When evaluating the emotional wellbeing of a student who has been withdrawn and irritable for weeks, which method is most likely to provide a comprehensive understanding of their mental health?",
            options = listOf(
                "Direct interviews with the student’s peers",
                "A series of self-reported questionnaires",
                "A combination of teacher observations, parental reports, and a structured psychological assessment",
                "Observation of the student’s behavior during unstructured play"
            ),
            answerIndex = 2
        ),
        Question(
            questionText = "Some counselors worry that addressing suicide in schools may increase the risk of such behavior. According to recent perspectives, the more accurate understanding is:",
            options = listOf(
                "It’s safer not to mention suicide at all",
                "Open, sensitive discussion with proper support reduces risk and stigma",
                "Suicide should only be discussed in clinical settings",
                "Only teachers should handle these conversations"
            ),
            answerIndex = 1
        ),
        Question(
            questionText = "A counselor believes that emotional health concerns should only be addressed if a student shows severe behavioral issues. This belief may:",
            options = listOf(
                "Help focus resources on high-need cases",
                "Risk missing internalizing problems like anxiety or depression",
                "Encourage discipline-oriented approaches",
                "Reflect best practices in school-based triage"
            ),
            answerIndex = 1
        ),
        Question(
            questionText = "A counselor avoids discussing emotional well-being because they assume children are 'too young.' This belief:",
            options = listOf(
                "Aligns with age-appropriate communication",
                "Protects students from stress",
                "Limits early emotional literacy and normalization of support",
                "Encourages children to solve their own problems"
            ),
            answerIndex = 2
        ),
        Question(
            questionText = "A counselor who encourages open discussions about emotional stress in class is most likely to:",
            options = listOf(
                "Make students uncomfortable",
                "Increase emotional problems",
                "Support help-seeking and reduce stigma",
                "Shift responsibility from parents to teachers"
            ),
            answerIndex = 2
        ),
        Question(
            questionText = "Believing that a child’s academic difficulties are purely motivational — without exploring emotional or cognitive causes — may result in:",
            options = listOf(
                "Improved classroom management",
                "Missed identification of learning disabilities or depression",
                "More effective peer discipline",
                "Better time management for teachers"
            ),
            answerIndex = 1
        ),
        Question(
            questionText = "If a counselor views referral to a mental health professional as a last resort, it may:",
            options = listOf(
                "Strengthen school autonomy",
                "Support student confidentiality",
                "Delay access to early intervention",
                "Reduce dependency on outside help"
            ),
            answerIndex = 2
        ),
        Question(
            questionText = "A belief that students with psychological issues should adjust to regular school routines without support may result in:",
            options = listOf(
                "Faster classroom integration",
                "Greater independence and resilience",
                "Missed opportunities for inclusive interventions",
                "More effective time management for teachers"
            ),
            answerIndex = 2
        ),
        Question(
            questionText = "If a counselor assumes that discussing emotions in school will reduce discipline and focus, this attitude:",
            options = listOf(
                "Encourages emotional expression",
                "Misunderstands the role of mental health in academic success",
                "Enhances behavior regulation strategies",
                "Aligns with structured classroom routines"
            ),
            answerIndex = 1
        ),
        Question(
            questionText = "Which of the following reflects an inclusive and growth-focused attitude?",
            options = listOf(
                "Students with psychological needs should be separated",
                "All students should be treated the same",
                "With support, most children can learn and thrive in regular classrooms",
                "It’s better to avoid interventions that highlight a child’s issues"
            ),
            answerIndex = 2
        ),
        Question(
            questionText = "A counselor worries that parents will react negatively to a mental health referral. Which attitude best balances concern and responsibility?",
            options = listOf(
                "Unless parents ask, I won’t bring it up",
                "Early conversations with sensitivity can help families understand and accept support",
                "Let the teacher talk to them first",
                "It’s better to wait until the problem becomes obvious"
            ),
            answerIndex = 1
        ),
        Question(
            questionText = "You observe a student displaying social withdrawal, frequent mood swings, and verbal expressions of hopelessness. What should be your first structured step?",
            options = listOf(
                "Inform classmates to offer more support",
                "Ask the student to write about their feelings",
                "Initiate observation, document patterns, and prepare for referral",
                "Wait to see if symptoms resolve during exam periods"
            ),
            answerIndex = 2
        ),
        Question(
            questionText = "A student with ADHD is disrupting class repeatedly despite previous behavioral reinforcement. What should your next practical action include?",
            options = listOf(
                "Recommend suspension",
                "Increase unstructured break times",
                "Provide movement breaks and adjust instruction delivery",
                "Shift to written instruction only"
            ),
            answerIndex = 2
        ),
        Question(
            questionText = "When a child shows sensory distress and meltdown during assemblies, what immediate school-level intervention is appropriate?",
            options = listOf(
                "Refer for disciplinary review",
                "Force exposure to overcome avoidance",
                "Offer quiet sensory regulation space and visual schedule alternatives",
                "Send them home for the day"
            ),
            answerIndex = 2
        ),
        Question(
            questionText = "You suspect a student has Specific Learning Disability, but they are still in mainstream classes. What is your most appropriate course of action?",
            options = listOf(
                "Recommend repeating the academic year",
                "Initiate multi-sensory strategies and recommend screening referral",
                "Limit reading and writing assignments entirely",
                "Ask parents to increase home tutoring"
            ),
            answerIndex = 1
        ),
        Question(
            questionText = "During play observation, a child repeats patterns, avoids interaction, and lines up objects. What is your practical next step?",
            options = listOf(
                "Redirect the child to competitive group games",
                "Monitor over time using structured observation and teacher input",
                "Suggest changing their seating in class",
                "Conduct an immediate IQ test"
            ),
            answerIndex = 1
        ),
        Question(
            questionText = "A child is constantly failing to follow classroom routines and shows signs of low IQ and adaptive delays. What’s the most appropriate intervention plan?",
            options = listOf(
                "Introduce advanced academic material",
                "Use repetitive instructions with peer mentorship and routine training",
                "Refer immediately to a behavior therapist",
                "Shift the student to home-schooling"
            ),
            answerIndex = 1
        ),
        Question(
            questionText = "You observe a student bullying peers, skipping classes, and showing no empathy. After repeated interventions fail, your next best action is to:",
            options = listOf(
                "Request a parent-teacher meeting and enforce detention",
                "Refer to psychiatrist for conduct evaluation and intervention planning",
                "Let the behavior pass unless violence occurs",
                "Place them in special education directly"
            ),
            answerIndex = 1
        ),
        Question(
            questionText = "A student with anxiety avoids tests and complains of stomachaches before class. Which intervention aligns with best school-based practice?",
            options = listOf(
                "Use relaxation techniques and provide extended time for tasks",
                "Remove them from all performance-based assessments",
                "Enforce timed exams to train their stamina",
                "Ignore complaints unless they faint"
            ),
            answerIndex = 0
        ),
        Question(
            questionText = "What is the best way to assess if classroom behavior is due to emotional distress or a neurological condition?",
            options = listOf(
                "Monitor performance in only one subject",
                "Use teacher interviews, peer analysis, and structured observations",
                "Assign the child to remedial tuition",
                "Wait to see long-term academic scores"
            ),
            answerIndex = 1
        ),
        Question(
            questionText = "What should a counselor do when a child repeatedly says they feel unloved and wants to disappear?",
            options = listOf(
                "Reassure them it’s just teenage mood",
                "Ask parents to restrict screen time",
                "Document the expression, assess risk, and refer for mental health evaluation",
                "Suggest they speak to a friend"
            ),
            answerIndex = 2
        )
    )
}