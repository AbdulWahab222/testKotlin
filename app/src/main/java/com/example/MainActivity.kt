package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.MyApplicationTheme
import kotlinx.coroutines.delay

// Premium Slate Deep Cosmic Theme Palette
val SpaceSlateBg = Color(0xFF0F172A)
val CardSlateBg = Color(0xFF1E293B)
val RadiantMint = Color(0xFF10B981)
val CosmicSky = Color(0xFF3B82F6)
val BrightAmber = Color(0xFFF59E0B)
val CoralRed = Color(0xFFEF4444)
val LightText = Color(0xFFF8FAFC)
val MutedSlate = Color(0xFF94A3B8)
val BrightIndigo = Color(0xFF6366F1)
val GlassWhite = Color(0xFFFFFFFF).copy(alpha = 0.05f)

// Models
enum class ScreenState {
    Configuring, Loading, Playing, Finished
}

data class QuizQuestion(
    val id: Int,
    val questionText: String,
    val options: List<String>,
    val correctAnswerIndex: Int,
    var selectedAnswerIndex: Int? = null,
    val explanation: String
)

data class QuizConfig(
    val topic: String = "Kotlin Coroutines",
    val difficulty: String = "Intermediate",
    val questionCount: Int = 5
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme(darkTheme = true) {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    containerColor = SpaceSlateBg
                ) { innerPadding ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    ) {
                        AIQuizAppMainScreen()
                    }
                }
            }
        }
    }
}

@Composable
fun AIQuizAppMainScreen() {
    var screenState by remember { mutableStateOf(ScreenState.Configuring) }
    var currentQuestions by remember { mutableStateOf<List<QuizQuestion>>(emptyList()) }
    var currentQuestionIndex by remember { mutableStateOf(0) }
    var quizConfig by remember { mutableStateOf(QuizConfig()) }
    var score by remember { mutableStateOf(0) }

    // Mock Database for popular topics
    val mockCoroutinesQuestions = listOf(
        QuizQuestion(
            id = 1,
            questionText = "Which dispatcher is optimized for disk or network I/O operations?",
            options = listOf("Dispatchers.Main", "Dispatchers.Default", "Dispatchers.IO", "Dispatchers.Unconfined"),
            correctAnswerIndex = 2,
            explanation = "Dispatchers.IO is designed to offload blocking I/O tasks to a shared pool of threads created on demand."
        ),
        QuizQuestion(
            id = 2,
            questionText = "What is the primary difference between launch and async in Kotlin Coroutines?",
            options = [
                "launch returns a Deferred while async returns a Job",
                "launch returns a Job while async returns a Deferred",
                "launch blocks the thread while async does not",
                "async is only used inside classes while launch is top-level"
            ].toList(),
            correctAnswerIndex = 1,
            explanation = "launch is 'fire and forget' and returns a Job, while async is used for asynchronous computations where a result is expected, returning a Deferred."
        ),
        QuizQuestion(
            id = 3,
            questionText = "What suspend modifier does under the hood in Kotlin?",
            options = [
                "It spawns a new Java OS Thread immediately",
                "It converts the function to use callbacks via a Continuation parameter",
                "It prevents thread context switches entirely",
                "It locks the CPU register to prevent interruption"
            ].toList(),
            correctAnswerIndex = 1,
            explanation = "Under the hood, the Kotlin compiler transforms suspending functions to pass a Continuation parameter, allowing execution to pause and resume using a state machine."
        ),
        QuizQuestion(
            id = 4,
            questionText = "How do you achieve structured concurrency in Kotlin Coroutines?",
            options = [
                "By launching coroutines using GlobalScope",
                "By using thread locks and semaphore primitives manually",
                "By running all coroutines inside a defined CoroutineScope parent",
                "Structured concurrency is not supported in Kotlin"
            ].toList(),
            correctAnswerIndex = 2,
            explanation = "Structured concurrency ensures that new coroutines are only launched in a specific CoroutineScope, establishing a parent-child relationship for robust cancellation and error propagation."
        ),
        QuizQuestion(
            id = 5,
            questionText = "What happens if a child coroutine throws an unhandled exception inside a supervisorScope?",
            options = [
                "The entire application crashes instantly",
                "The parent coroutine and all siblings are cancelled automatically",
                "The exception is suppressed and ignored silently",
                "Only the failing child is cancelled; siblings and the parent remain active"
            ].toList(),
            correctAnswerIndex = 3,
            explanation = "supervisorScope installs a SupervisorJob where a failure of a child coroutine does not propagate upwards to cancel siblings or parent execution."
        )
    )

    val mockComposeQuestions = listOf(
        QuizQuestion(
            id = 1,
            questionText = "What is the main purpose of Recomposition in Jetpack Compose?",
            options = [
                "To restart the entire Activity from scratch",
                "To re-run composable functions with updated state to refresh the UI",
                "To optimize bytecode compression size",
                "To clean up garbage collected variables"
            ].toList(),
            correctAnswerIndex = 1,
            explanation = "Recomposition is the process of calling your composable functions again when their underlying inputs/state change so the UI is updated dynamically."
        ),
        QuizQuestion(
            id = 2,
            questionText = "Why should we use 'remember' inside Composable functions?",
            options = [
                "To store objects in persistence SQLite database automatically",
                "To retain the value across recompositions so it is not re-initialized",
                "To speed up networking fetch calls asynchronously",
                "To bypass Compose compilers runtime checks"
            ].toList(),
            correctAnswerIndex = 1,
            explanation = "A composable function can store a single value or object in the Composition by using 'remember'. During recomposition, it returns the cached value rather than re-creating it."
        ),
        QuizQuestion(
            id = 3,
            questionText = "Which of the following is correct regarding Modifiers order?",
            options = [
                "Modifiers order does not matter; the compiler sorts them alphabetically",
                "Modifiers order is evaluated from right to left",
                "Modifiers order is highly critical as modifications are applied sequentially",
                "Modifiers can only be declared inside Theme definitions"
            ].toList(),
            correctAnswerIndex = 2,
            explanation = "In Jetpack Compose, the order of modifier functions is critical. Modifiers are applied from left to right (sequentially), affecting padding, background, layouts, and click boundaries directly."
        ),
        QuizQuestion(
            id = 4,
            questionText = "What is 'State Hoisting' in Compose UI architecture?",
            options = [
                "Moving state up to make a component stateless, reusable, and easier to test",
                "Saving state variables into SharedPreferences dynamically",
                "Lifting UI components into standard XML layout pools",
                "Terminating background threads to free up system memory"
            ].toList(),
            correctAnswerIndex = 0,
            explanation = "State hoisting in Compose is a pattern of moving state to a composable's caller to make the composable stateless, allowing it to be easily reused, tested, and controlled."
        ),
        QuizQuestion(
            id = 5,
            questionText = "Which side-effect API is ideal for launching coroutines that match the Composable lifecycle?",
            options = ["LaunchedEffect", "DisposableEffect", "SideEffect", "rememberCoroutineScope"],
            correctAnswerIndex = 0,
            explanation = "LaunchedEffect is designed to run suspend functions safely within a Composable lifecycle. It automatically cancels the coroutine if the key parameters change or the composable leaves the composition."
        )
    )

    val mockGeneralQuestions = listOf(
        QuizQuestion(
            id = 1,
            questionText = "What is the primary benefit of Clean Architecture in Android?",
            options = [
                "It makes the application run significantly faster on old devices",
                "It separates UI, domain logic, and data sources for independent scaling and testing",
                "It reduces the compilation times to under 1 second",
                "It eliminates the need for Gradle dependency configurations"
            ].toList(),
            correctAnswerIndex = 1,
            explanation = "Clean Architecture decouples the core business logic (domain) from UI details and data delivery channels, allowing independent testing and ease of system modifications."
        ),
        QuizQuestion(
            id = 2,
            questionText = "Which Android components are responsible for hosting and managing UI-related data in a lifecycle-conscious way?",
            options = ["BroadcastReceiver", "ViewModel", "ContentProvider", "IntentService"],
            correctAnswerIndex = 1,
            explanation = "ViewModel is designed to store and manage UI-related data in a lifecycle-conscious way. It allows data to survive configuration changes such as screen rotations."
        ),
        QuizQuestion(
            id = 3,
            questionText = "How does Room Database handle background query execution?",
            options = [
                "It runs all transactions directly on the Android Main UI Thread",
                "It blocks CPU cycles until queries are fully completed",
                "It supports coroutine features (Flows/suspend) to execute queries asynchronously on background threads",
                "It requires manual JDBC thread pooling setup"
            ].toList(),
            correctAnswerIndex = 2,
            explanation = "Room supports standard Kotlin Coroutines (`suspend` functions and `Flow`) to safely execute relational persistence operations on background threads without locking the UI thread."
        )
    )

    // Handle artificial AI generation wait loading
    LaunchedEffect(screenState) {
        if (screenState == ScreenState.Loading) {
            delay(2200) // Simulating high-tech AI generation
            // Select questions pool based on topic
            currentQuestions = when {
                quizConfig.topic.lowercase().contains("coroutine") -> mockCoroutinesQuestions
                quizConfig.topic.lowercase().contains("compose") -> mockComposeQuestions
                else -> {
                    // Custom fallback: Generate questions with titles modified dynamically
                    mockGeneralQuestions.map { q ->
                        q.copy(questionText = "${q.questionText} (${quizConfig.topic} Domain)")
                    }
                }
            }.take(quizConfig.questionCount).map { it.copy(selectedAnswerIndex = null) } // Reset answers
            currentQuestionIndex = 0
            score = 0
            screenState = ScreenState.Playing
        }
    }

    AnimatedVisibility(
        visible = true,
        enter = fadeIn() + slideInVertically(initialOffsetY = { 50 })
    ) {
        when (screenState) {
            ScreenState.Configuring -> {
                QuizConfigScreen(
                    config = quizConfig,
                    onConfigChange = { quizConfig = it },
                    onStart = { screenState = ScreenState.Loading }
                )
            }
            ScreenState.Loading -> {
                QuizLoadingScreen(topic = quizConfig.topic)
            }
            ScreenState.Playing -> {
                QuizPlayerScreen(
                    questions = currentQuestions,
                    currentIndex = currentQuestionIndex,
                    onOptionSelected = { questionIdx, optionIdx ->
                        val updated = currentQuestions.toMutableList()
                        updated[questionIdx] = updated[questionIdx].copy(selectedAnswerIndex = optionIdx)
                        currentQuestions = updated
                    },
                    onNext = {
                        if (currentQuestionIndex < currentQuestions.lastIndex) {
                            currentQuestionIndex++
                        } else {
                            // Calculate final score
                            score = currentQuestions.count { it.selectedAnswerIndex == it.correctAnswerIndex }
                            screenState = ScreenState.Finished
                        }
                    },
                    onBack = {
                        if (currentQuestionIndex > 0) {
                            currentQuestionIndex--
                        }
                    }
                )
            }
            ScreenState.Finished -> {
                QuizFinishedScreen(
                    score = score,
                    total = currentQuestions.size,
                    topic = quizConfig.topic,
                    questions = currentQuestions,
                    onRestart = { screenState = ScreenState.Configuring }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuizConfigScreen(
    config: QuizConfig,
    onConfigChange: (QuizConfig) -> Unit,
    onStart: () -> Unit
) {
    var customTopicInput by remember { mutableStateOf("") }
    val recommendedTopics = listOf("Kotlin Coroutines", "Jetpack Compose", "Android Architecture", "Room Database")

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(top = 24.dp, bottom = 40.dp)
    ) {
        // App Premium Header
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(BrightIndigo.copy(alpha = 0.25f), SpaceSlateBg)
                        ),
                        shape = RoundedCornerShape(24.dp)
                    )
                    .border(1.dp, BrightIndigo.copy(alpha = 0.2f), RoundedCornerShape(24.dp))
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Surface(
                    color = RadiantMint.copy(alpha = 0.15f),
                    shape = RoundedCornerShape(100.dp)
                ) {
                    Text(
                        text = "POWERED BY GEMINI ENGINE",
                        fontSize = 10.sp,
                        color = RadiantMint,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "AI Quiz Generator",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = LightText
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Select or type any technical topic to generate a high-density customized multiple choice quiz instantly.",
                    fontSize = 13.sp,
                    color = MutedSlate,
                    textAlign = TextAlign.Center,
                    lineHeight = 18.sp
                )
            }
        }

        // Section: Select Topic
        item {
            Text(
                text = "1. Choose Quiz Topic",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = LightText
            )
            Spacer(modifier = Modifier.height(8.dp))

            // Grid layout using Row for preset chips
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                val chunks = recommendedTopics.chunked(2)
                chunks.forEach { rowChips ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        rowChips.forEach { preset ->
                            val isSelected = config.topic == preset && customTopicInput.isEmpty()
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(if (isSelected) CosmicSky.copy(alpha = 0.15f) else CardSlateBg)
                                    .border(
                                        width = 1.dp,
                                        color = if (isSelected) CosmicSky else MutedSlate.copy(alpha = 0.15f),
                                        shape = RoundedCornerShape(16.dp)
                                    )
                                    .clickable {
                                        customTopicInput = ""
                                        onConfigChange(config.copy(topic = preset))
                                    }
                                    .padding(vertical = 14.dp, horizontal = 12.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = preset,
                                    color = if (isSelected) CosmicSky else LightText,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                }
            }
        }

        // Custom Topic Input
        item {
            Text(
                text = "Or type custom topic details:",
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = MutedSlate
            )
            Spacer(modifier = Modifier.height(6.dp))

            OutlinedTextField(
                value = customTopicInput,
                onValueChange = {
                    customTopicInput = it
                    if (it.isNotEmpty()) {
                        onConfigChange(config.copy(topic = it))
                    }
                },
                placeholder = { Text("e.g. Kotlin Coroutines Advanced Flow, Android Security, Room DAOs") },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("custom_topic_text_field"),
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = LightText,
                    unfocusedTextColor = LightText,
                    focusedContainerColor = CardSlateBg,
                    unfocusedContainerColor = CardSlateBg,
                    focusedBorderColor = CosmicSky,
                    unfocusedBorderColor = MutedSlate.copy(alpha = 0.2f),
                    placeholderColor = MutedSlate
                ),
                singleLine = true
            )
        }

        // Section: Difficulty
        item {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "2. Select Difficulty Level",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = LightText
            )
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(CardSlateBg, RoundedCornerShape(16.dp))
                    .padding(6.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                val levels = listOf("Beginner", "Intermediate", "Advanced")
                levels.forEach { level ->
                    val isSelected = config.difficulty == level
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (isSelected) CosmicSky else Color.Transparent)
                            .clickable { onConfigChange(config.copy(difficulty = level)) }
                            .padding(vertical = 12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = level,
                            color = if (isSelected) SpaceSlateBg else LightText,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        // Section: Questions Count Slider
        item {
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "3. Number of Questions",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = LightText
                )
                Surface(
                    color = CosmicSky.copy(alpha = 0.15f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "${config.questionCount} Questions",
                        color = CosmicSky,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(4.dp))

            Slider(
                value = config.questionCount.toFloat(),
                onValueChange = { onConfigChange(config.copy(questionCount = it.toInt())) },
                valueRange = 3f..5f, // Restrict range to fit available mockup dataset size securely
                steps = 1,
                colors = SliderDefaults.colors(
                    activeTrackColor = CosmicSky,
                    inactiveTrackColor = CardSlateBg,
                    thumbColor = CosmicSky,
                    activeTickColor = CosmicSky.copy(alpha = 0.5f)
                ),
                modifier = Modifier.testTag("questions_count_slider")
            )
        }

        // Action Trigger
        item {
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = onStart,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .testTag("start_quiz_generation_button"),
                colors = ButtonDefaults.buttonColors(containerColor = RadiantMint),
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = "Start",
                    tint = SpaceSlateBg,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Generate AI Quiz",
                    color = SpaceSlateBg,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun QuizLoadingScreen(topic: String) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.size(100.dp)
        ) {
            CircularProgressIndicator(
                modifier = Modifier.size(80.dp),
                color = RadiantMint,
                strokeWidth = 6.dp,
                strokeCap = StrokeCap.Round
            )
            Icon(
                imageVector = Icons.Default.Star,
                contentDescription = "AI Loading",
                tint = BrightAmber,
                modifier = Modifier.size(32.dp)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = "Gemini AI Engine Generating...",
            fontSize = 20.sp,
            fontWeight = FontWeight.ExtraBold,
            color = LightText
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Analyzing deep semantic core for \"$topic\" and structuring standard material multi-choice layouts.",
            fontSize = 13.sp,
            color = MutedSlate,
            textAlign = TextAlign.Center,
            lineHeight = 18.sp,
            modifier = Modifier.padding(horizontal = 24.dp)
        )
    }
}

@Composable
fun QuizPlayerScreen(
    questions: List<QuizQuestion>,
    currentIndex: Int,
    onOptionSelected: (questionIdx: Int, optionIdx: Int) -> Unit,
    onNext: () -> Unit,
    onBack: () -> Unit
) {
    val question = questions.getOrNull(currentIndex) ?: return
    val selectedOption = question.selectedAnswerIndex
    val isAnswered = selectedOption != null

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
    ) {
        // Player Header Info
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onBack,
                enabled = currentIndex > 0,
                modifier = Modifier
                    .background(if (currentIndex > 0) CardSlateBg else Color.Transparent, CircleShape)
                    .size(40.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = if (currentIndex > 0) LightText else MutedSlate.copy(alpha = 0.3f)
                )
            }

            Text(
                text = "Question ${currentIndex + 1} of ${questions.size}",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = LightText
            )

            // Dynamic mini indicator
            Surface(
                color = CosmicSky.copy(alpha = 0.15f),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = "${((currentIndex + 1) * 100) / questions.size}% Done",
                    color = CosmicSky,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Linear Progress Bar track
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(CircleShape)
                .background(CardSlateBg)
        ) {
            val progressFraction = (currentIndex + 1).toFloat() / questions.size.toFloat()
            Box(
                modifier = Modifier
                    .fillMaxWidth(fraction = progressFraction)
                    .fillMaxHeight()
                    .clip(CircleShape)
                    .background(
                        brush = Brush.horizontalGradient(
                            colors = listOf(CosmicSky, RadiantMint)
                        )
                    )
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Question Details elevated Container Box
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .animateContentSize(),
            colors = CardDefaults.cardColors(containerColor = CardSlateBg),
            shape = RoundedCornerShape(24.dp),
            border = BorderStroke(1.dp, MutedSlate.copy(alpha = 0.15f))
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    text = question.questionText,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = LightText,
                    lineHeight = 24.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Options List Wrapper
        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            itemsIndexed(question.options) { optionIdx, optionText ->
                val isSelected = selectedOption == optionIdx
                val isCorrect = question.correctAnswerIndex == optionIdx

                // Card dynamic styles on state answer completion
                val cardBorderColor = when {
                    isAnswered && isCorrect -> RadiantMint
                    isAnswered && isSelected -> CoralRed
                    isSelected -> CosmicSky
                    else -> MutedSlate.copy(alpha = 0.15f)
                }

                val cardBg = when {
                    isAnswered && isCorrect -> RadiantMint.copy(alpha = 0.12f)
                    isAnswered && isSelected -> CoralRed.copy(alpha = 0.12f)
                    isSelected -> CosmicSky.copy(alpha = 0.12f)
                    else -> CardSlateBg
                }

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .clickable(!isAnswered) { onOptionSelected(currentIndex, optionIdx) }
                        .testTag("quiz_option_${optionIdx}"),
                    colors = CardDefaults.cardColors(containerColor = cardBg),
                    border = BorderStroke(1.dp, cardBorderColor),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = optionText,
                            fontSize = 14.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            color = LightText,
                            modifier = Modifier.weight(1f)
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        // Visual feedback status indicator
                        if (isAnswered) {
                            if (isCorrect) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = "Correct",
                                    tint = RadiantMint,
                                    modifier = Modifier.size(22.dp)
                                )
                            } else if (isSelected) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Incorrect",
                                    tint = CoralRed,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                        } else {
                            Box(
                                modifier = Modifier
                                    .size(20.dp)
                                    .border(
                                        width = 2.dp,
                                        color = if (isSelected) CosmicSky else MutedSlate.copy(alpha = 0.4f),
                                        shape = CircleShape
                                    )
                                    .background(if (isSelected) CosmicSky else Color.Transparent, CircleShape)
                            )
                        }
                    }
                }
            }

            // Interactive Explanation reveal once answered
            if (isAnswered) {
                item {
                    Spacer(modifier = Modifier.height(12.dp))
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(SpaceSlateBg)
                            .animateContentSize(),
                        colors = CardDefaults.cardColors(containerColor = SpaceSlateBg),
                        border = BorderStroke(1.dp, BrightAmber.copy(alpha = 0.3f)),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = "Explanation",
                                tint = BrightAmber,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "Explanation",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = BrightAmber
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = question.explanation,
                                    fontSize = 12.sp,
                                    color = MutedSlate,
                                    lineHeight = 16.sp
                                )
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Action Trigger Footer
        Button(
            onClick = onNext,
            enabled = isAnswered,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .testTag("quiz_action_next_button"),
            colors = ButtonDefaults.buttonColors(
                containerColor = CosmicSky,
                disabledContainerColor = CardSlateBg.copy(alpha = 0.5f)
            ),
            shape = RoundedCornerShape(16.dp)
        ) {
            Text(
                text = if (currentIndex == questions.lastIndex) "Finish and See Results" else "Next Question",
                color = if (isAnswered) LightText else MutedSlate.copy(alpha = 0.5f),
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.width(6.dp))
            Icon(
                imageVector = Icons.Default.ArrowForward,
                contentDescription = "Next",
                tint = if (isAnswered) LightText else MutedSlate.copy(alpha = 0.5f),
                modifier = Modifier.size(18.dp)
            )
        }
    }
}

@Composable
fun QuizFinishedScreen(
    score: Int,
    total: Int,
    topic: String,
    questions: List<QuizQuestion>,
    onRestart: () -> Unit
) {
    val scorePercentage = if (total == 0) 0 else (score * 100) / total
    val animatedProgress by animateFloatAsState(
        targetValue = scorePercentage.toFloat() / 100f,
        animationSpec = tween(durationMillis = 1500)
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(top = 24.dp, bottom = 40.dp)
    ) {
        // High fidelity Score Circular visualizer
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(BrightIndigo.copy(alpha = 0.25f), SpaceSlateBg)
                        ),
                        shape = RoundedCornerShape(28.dp)
                    )
                    .border(1.dp, BrightIndigo.copy(alpha = 0.2f), RoundedCornerShape(28.dp))
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Quiz Completed!",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = LightText
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Topic: $topic",
                    fontSize = 13.sp,
                    color = MutedSlate
                )

                Spacer(modifier = Modifier.height(24.dp))

                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.size(160.dp)
                ) {
                    CircularProgressIndicator(
                        progress = animatedProgress,
                        modifier = Modifier.fillMaxSize(),
                        color = if (scorePercentage >= 70) RadiantMint else BrightAmber,
                        trackColor = CardSlateBg,
                        strokeWidth = 10.dp,
                        strokeCap = StrokeCap.Round
                    )
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "$scorePercentage%",
                            fontSize = 32.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = LightText
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "$score / $total Correct",
                            fontSize = 13.sp,
                            color = MutedSlate,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Quality Badge feedback rating
                val evaluationText = when {
                    scorePercentage >= 90 -> "Outstanding masterclass performance! You're ready to deploy production environments."
                    scorePercentage >= 70 -> "Great grasp of concepts! Solid intermediate skills achieved."
                    else -> "Solid try! A little more study on basic design variables will help you master details."
                }

                Text(
                    text = evaluationText,
                    fontSize = 13.sp,
                    color = LightText,
                    textAlign = TextAlign.Center,
                    lineHeight = 18.sp,
                    modifier = Modifier.padding(horizontal = 12.dp)
                )
            }
        }

        // Section: AI Performance Metric analysis summary recommendations
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = CardSlateBg),
                shape = RoundedCornerShape(20.dp),
                border = BorderStroke(1.dp, MutedSlate.copy(alpha = 0.15f))
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "AI Suggestion",
                            tint = RadiantMint,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "AI Study Recommendation",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = LightText
                        )
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "Based on your answer telemetry logs, we recommend spending 15 minutes reviewing functional mechanics and error flow exception context boundaries to secure deeper state compliance.",
                        fontSize = 12.sp,
                        color = MutedSlate,
                        lineHeight = 18.sp
                    )
                }
            }
        }

        // Action controls
        item {
            Button(
                onClick = onRestart,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("restart_quiz_app_button"),
                colors = ButtonDefaults.buttonColors(containerColor = CosmicSky),
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = "Try Another",
                    tint = LightText,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Generate Another Quiz",
                    color = LightText,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
