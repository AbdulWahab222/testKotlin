package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.MyApplicationTheme

// Premium Palette for Slate Deep Cosmic Theme
val SpaceSlateBg = Color(0xFF0F172A)
val CardSlateBg = Color(0xFF1E293B)
val RadiantMint = Color(0xFF10B981)
val CosmicSky = Color(0xFF3B82F6)
val BrightAmber = Color(0xFFF59E0B)
val LightText = Color(0xFFF8FAFC)
val DarkText = Color(0xFF1E293B)
val MutedSlate = Color(0xFF94A3B8)
val BrightIndigo = Color(0xFF6366F1)

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
          Column(
            modifier = Modifier
              .fillMaxSize()
              .padding(innerPadding)
          ) {
            MainDevCompanionScreen()
          }
        }
      }
    }
  }
}

// Model for Career roadmap learning step
data class LearningMilestone(
  val id: Int,
  val title: String,
  val description: String,
  val category: String,
  val difficulty: String,
  var isCompleted: Boolean = false
)

@Composable
fun MainDevCompanionScreen() {
  // Application State
  var milestones by remember {
    mutableStateOf(
      listOf(
        LearningMilestone(
          id = 1,
          title = "Jetpack Compose Basics",
          description = "Master composable functions, layout modifier chaining, rows, columns, and declarative rendering.",
          category = "UI Design",
          difficulty = "Beginner"
        ),
        LearningMilestone(
          id = 2,
          title = "State and Recomposition",
          description = "Use remember, mutableStateOf, and state hoisting to construct interactive dynamic reactive states.",
          category = "Architecture",
          difficulty = "Beginner"
        ),
        LearningMilestone(
          id = 3,
          title = "Clean Architecture & ViewModel",
          description = "Structure single-source-of-truth architectures using ViewModel, StateFlow, and LiveData properties.",
          category = "Data Flow",
          difficulty = "Intermediate"
        ),
        LearningMilestone(
          id = 4,
          title = "Room Database Integration",
          description = "Interface local SQLite persistence layer using modern Kotlin coroutines, flows, and type-safe DAOs.",
          category = "Persistence",
          difficulty = "Intermediate"
        ),
        LearningMilestone(
          id = 5,
          title = "Retrofit & REST API Sync",
          description = "Connect external servers to download web data safely with defensive error handling and models.",
          category = "Network",
          difficulty = "Advanced"
        ),
        LearningMilestone(
          id = 6,
          title = "AI Model Orchestration",
          description = "Integrate secure Gemini API instructions into Android features using native AI Studio platform mechanics.",
          category = "AI Engine",
          difficulty = "Advanced"
        )
      )
    )
  }

  // Interactive sandbox state
  var playgroundCount by remember { mutableStateOf(0) }
  var isHighContrastEnabled by remember { mutableStateOf(false) }
  var isDailyChallengeDone by remember { mutableStateOf(false) }
  var activeSandboxTab by remember { mutableStateOf(0) } // 0: Live Sandbox, 1: AI Prompt Guide

  // Compute stats
  val completedUnits = milestones.count { it.isCompleted }
  val totalUnits = milestones.size
  val progressPercent = if (totalUnits == 0) 0 else (completedUnits * 100) / totalUnits

  LazyColumn(
    modifier = Modifier
      .fillMaxSize()
      .padding(horizontal = 16.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    // Elegant Dark Theme Banner Item
    item {
      Spacer(modifier = Modifier.height(16.dp))
      DevCompanionHeader(progressPercent = progressPercent, completed = completedUnits, total = totalUnits)
    }

    // Sandboxed Interactive Sandbox Playground
    item {
      Card(
        modifier = Modifier
          .fillMaxWidth()
          .animateContentSize(),
        colors = CardDefaults.cardColors(containerColor = CardSlateBg),
        shape = RoundedCornerShape(24.dp),
        border = BorderStroke(1.dp, MutedSlate.copy(alpha = 0.2f))
      ) {
        Column(modifier = Modifier.padding(20.dp)) {
          // Row Selector for Sandbox Tabs
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .background(SpaceSlateBg.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
              .padding(4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
          ) {
            val tabModifier = Modifier.weight(1f)
            SandboxTabButton(
              title = "Kotlin Sandbox",
              active = activeSandboxTab == 0,
              onClick = { activeSandboxTab = 0 },
              modifier = tabModifier
            )
            SandboxTabButton(
              title = "AI Prompt Guide",
              active = activeSandboxTab == 1,
              onClick = { activeSandboxTab = 1 },
              modifier = tabModifier
            )
          }

          Spacer(modifier = Modifier.height(16.dp))

          if (activeSandboxTab == 0) {
            // Live reactive interactive counters
            Text(
              text = "Live Component Sandbox",
              style = MaterialTheme.typography.titleMedium,
              color = LightText,
              fontWeight = FontWeight.Bold
            )
            Text(
              text = "Tap interactive states below to see Jetpack Compose dynamically update variables instantly and trigger UI recompositions.",
              style = MaterialTheme.typography.bodySmall,
              color = MutedSlate,
              modifier = Modifier.padding(top = 4.dp, bottom = 12.dp)
            )

            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.spacedBy(12.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              // Custom Click Button with Ripple
              Button(
                onClick = { playgroundCount++ },
                colors = ButtonDefaults.buttonColors(
                  containerColor = if (isHighContrastEnabled) Color.White else CosmicSky
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                  .weight(1.3f)
                  .testTag("increment_sandbox_button"),
                contentPadding = ButtonDefaults.ContentPadding
              ) {
                Icon(
                  imageNameAndType(activeSandboxTab),
                  contentDescription = "Interact",
                  tint = if (isHighContrastEnabled) Color.Black else Color.White,
                  modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                  text = "State Count: $playgroundCount",
                  fontSize = 13.sp,
                  color = if (isHighContrastEnabled) Color.Black else Color.White,
                  fontWeight = FontWeight.Bold
                )
              }

              // Color toggle / preview state
              Button(
                onClick = { isHighContrastEnabled = !isHighContrastEnabled },
                colors = ButtonDefaults.buttonColors(
                  containerColor = if (isHighContrastEnabled) RadiantMint else SpaceSlateBg
                ),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, MutedSlate.copy(alpha = 0.5f)),
                modifier = Modifier
                  .weight(1f)
                  .testTag("toggle_contrast_button")
              ) {
                Text(
                  text = if (isHighContrastEnabled) "Neon Accent" else "Sleek Accent",
                  fontSize = 11.sp,
                  color = LightText,
                  textAlign = TextAlign.Center
                )
              }

              // Reset Button
              IconButton(
                onClick = {
                  playgroundCount = 0
                  isHighContrastEnabled = false
                },
                modifier = Modifier
                  .size(48.dp)
                  .background(SpaceSlateBg, CircleShape)
                  .testTag("reset_sandbox_button")
              ) {
                Icon(
                  imageVector = Icons.Default.Refresh,
                  contentDescription = "Reset State",
                  tint = RadiantMint,
                  modifier = Modifier.size(20.dp)
                )
              }
            }

            Spacer(modifier = Modifier.height(16.dp))
            Divider(color = MutedSlate.copy(alpha = 0.15f))
            Spacer(modifier = Modifier.height(12.dp))

            // Dynamic challenge preview card depending on sandbox active stats
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .background(SpaceSlateBg.copy(alpha = 0.4f), RoundedCornerShape(16.dp))
                .padding(12.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Icon(
                imageVector = Icons.Default.Star,
                contentDescription = "Daily Prompt",
                tint = BrightAmber,
                modifier = Modifier.size(24.dp)
              )
              Spacer(modifier = Modifier.width(12.dp))
              Column(modifier = Modifier.weight(1f)) {
                Text(
                  text = "First Daily Milestone Task",
                  fontWeight = FontWeight.SemiBold,
                  fontSize = 13.sp,
                  color = LightText
                )
                Text(
                  text = "Increment reactive sandbox counter to 5+ times to complete starter milestone.",
                  fontSize = 11.sp,
                  color = MutedSlate
                )
              }
              Spacer(modifier = Modifier.width(4.dp))
              
              // Validate dynamic target accomplishment
              val targetMet = playgroundCount >= 5
              if (targetMet) {
                Icon(
                  imageVector = Icons.Default.CheckCircle,
                  contentDescription = "Target met",
                  tint = RadiantMint,
                  modifier = Modifier.size(28.dp)
                )
              } else {
                Box(
                  modifier = Modifier
                    .size(24.dp)
                    .background(Color.Transparent, CircleShape)
                    .border(2.dp, MutedSlate.copy(alpha = 0.4f), CircleShape),
                  contentAlignment = Alignment.Center
                ) {
                  Text(
                    text = "${playgroundCount}/5",
                    fontSize = 9.sp,
                    color = MutedSlate,
                    fontWeight = FontWeight.Bold
                  )
                }
              }
            }

          } else {
            // Interactive Copilot Quick Action Planner
            Text(
              text = "Copilot Assistant Quick Planner",
              style = MaterialTheme.typography.titleMedium,
              color = LightText,
              fontWeight = FontWeight.Bold
            )
            Text(
              text = "Copy these optimal structural prompts direct into AI Studio chat to quickly auto-build rich modules from scratch:",
              style = MaterialTheme.typography.bodySmall,
              color = MutedSlate,
              modifier = Modifier.padding(top = 4.dp, bottom = 12.dp)
            )

            PromptGuideItem(
              title = "1. To Add Local Room SQLite Persistence",
              promptText = "\"Integrate standard SQLite persistence using the local Room Database skill. Setup a high-density, material layout database for entries.\""
            )
            Spacer(modifier = Modifier.height(8.dp))
            PromptGuideItem(
              title = "2. To Sync Live API Rest Services",
              promptText = "\"Construct a robust Retrofit network client syncing real REST api servers. Provide smooth pull-to-refresh loaders and success visualizers.\""
            )
          }
        }
      }
    }

    // Interactive Checklist Roadmap Steppers
    item {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column {
          Text(
            text = "Your Interactive Roadmap",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = LightText
          )
          Text(
            text = "Check off milestones as you master key Concepts.",
            style = MaterialTheme.typography.bodySmall,
            color = MutedSlate
          )
        }
        
        // Fast mock progress stats
        Button(
          onClick = {
            // Reset all checklists
            val refreshed = milestones.map { it.copy(isCompleted = false) }
            milestones = refreshed
          },
          colors = ButtonDefaults.buttonColors(containerColor = SpaceSlateBg),
          border = BorderStroke(1.dp, MutedSlate.copy(alpha = 0.3f)),
          contentPadding = PaddingValues(horizontal = 12.dp, vertical = 2.dp),
          modifier = Modifier.height(32.dp)
        ) {
          Text("Reset Roadmaps", fontSize = 11.sp, color = MutedSlate)
        }
      }
    }

    // Dynamic items displaying Roadmap details
    items(milestones, key = { it.id }) { milestone ->
      MilestoneCardItem(
        milestone = milestone,
        onCheckboxClick = { checked ->
          val updatedList = milestones.map {
            if (it.id == milestone.id) {
              it.copy(isCompleted = checked)
            } else {
              it
            }
          }
          milestones = updatedList
        }
      )
    }

    // Footer spacing
    item {
      Spacer(modifier = Modifier.height(32.dp))
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .background(SpaceSlateBg.copy(alpha = 0.4f), RoundedCornerShape(16.dp))
          .padding(20.dp),
        contentAlignment = Alignment.Center
      ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
          Icon(
            imageVector = Icons.Default.Info,
            contentDescription = "Tips",
            tint = MutedSlate,
            modifier = Modifier.size(24.dp)
          )
          Spacer(modifier = Modifier.height(8.dp))
          Text(
            text = "Tip: You are connected to a high-speed AI Studio server in the cloud! Simply type what features or layout designs you want to add next, and I will build them completely for you.",
            color = MutedSlate,
            fontSize = 12.sp,
            textAlign = TextAlign.Center,
            lineHeight = 18.sp
          )
        }
      }
      Spacer(modifier = Modifier.height(32.dp))
    }
  }
}

@Composable
fun DevCompanionHeader(progressPercent: Int, completed: Int, total: Int) {
  Box(
    modifier = Modifier
      .fillMaxWidth()
      .background(
        brush = Brush.verticalGradient(
          colors = listOf(BrightIndigo.copy(alpha = 0.3f), SpaceSlateBg)
        ),
        shape = RoundedCornerShape(28.dp)
      )
      .border(1.dp, BrightIndigo.copy(alpha = 0.25f), RoundedCornerShape(28.dp))
      .padding(24.dp)
  ) {
    Column(modifier = Modifier.fillMaxWidth()) {
      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier.fillMaxWidth()
      ) {
        Column {
          Surface(
            color = RadiantMint.copy(alpha = 0.15f),
            shape = RoundedCornerShape(100.dp)
          ) {
            Text(
              text = "CAREER MASTERCLASS",
              fontSize = 11.sp,
              color = RadiantMint,
              fontWeight = FontWeight.Bold,
              modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
            )
          }
          Spacer(modifier = Modifier.height(8.dp))
          Text(
            text = "Welcome to your Android Journey!",
            fontSize = 22.sp,
            fontWeight = FontWeight.ExtraBold,
            color = LightText
          )
        }
      }

      Spacer(modifier = Modifier.height(10.dp))
      Text(
        text = "This interactive career companion outlines standard pathways to building premium apps. Start exploring milestones or test reactive state variables in the live sandbox.",
        color = MutedSlate,
        fontSize = 13.sp,
        lineHeight = 20.sp
      )

      Spacer(modifier = Modifier.height(20.dp))

      // Custom Graphical Progress Bar with percentages
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = "Roadmap Completion Progress",
          fontSize = 12.sp,
          color = LightText,
          fontWeight = FontWeight.SemiBold
        )
        Text(
          text = "$progressPercent% ($completed/$total milestones done)",
          fontSize = 12.sp,
          color = RadiantMint,
          fontWeight = FontWeight.Bold
        )
      }

      Spacer(modifier = Modifier.height(8.dp))

      // Sleek Progress Track background
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .height(10.dp)
          .clip(CircleShape)
          .background(SpaceSlateBg)
      ) {
        // Active bar with state color
        Box(
          modifier = Modifier
            .fillMaxWidth(fraction = if (progressPercent == 0) 0.01f else progressPercent.toFloat() / 100f)
            .fillVertical()
            .clip(CircleShape)
            .background(
              brush = Brush.horizontalGradient(
                colors = listOf(CosmicSky, RadiantMint)
              )
            )
        )
      }
    }
  }
}

@Composable
fun SandboxTabButton(title: String, active: Boolean, onClick: () -> Unit, modifier: Modifier = Modifier) {
  val backgroundColor by animateColorAsState(
    targetValue = if (active) CardSlateBg else Color.Transparent,
    animationSpec = spring(),
    label = "tab_bg"
  )
  val textColor by animateColorAsState(
    targetValue = if (active) LightText else MutedSlate,
    animationSpec = spring(),
    label = "tab_text"
  )

  Box(
    modifier = modifier
      .clip(RoundedCornerShape(8.dp))
      .background(backgroundColor)
      .clickable { onClick() }
      .padding(vertical = 10.dp, horizontal = 12.dp),
    contentAlignment = Alignment.Center
  ) {
    Text(
      text = title,
      fontWeight = if (active) FontWeight.Bold else FontWeight.Medium,
      fontSize = 12.sp,
      color = textColor,
      textAlign = TextAlign.Center
    )
  }
}

@Composable
fun PromptGuideItem(title: String, promptText: String) {
  Column(
    modifier = Modifier
      .fillMaxWidth()
      .background(SpaceSlateBg.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
      .border(1.dp, MutedSlate.copy(alpha = 0.1f), RoundedCornerShape(12.dp))
      .padding(12.dp)
  ) {
    Text(
      text = title,
      fontWeight = FontWeight.Bold,
      fontSize = 12.sp,
      color = RadiantMint
    )
    Spacer(modifier = Modifier.height(4.dp))
    Text(
      text = promptText,
      fontSize = 11.sp,
      fontFamily = FontFamily.Monospace,
      color = LightText.copy(alpha = 0.9f),
      lineHeight = 16.sp
    )
  }
}

@Composable
fun MilestoneCardItem(milestone: LearningMilestone, onCheckboxClick: (Boolean) -> Unit) {
  Card(
    modifier = Modifier
      .fillMaxWidth()
      .animateContentSize(),
    colors = CardDefaults.cardColors(
      containerColor = if (milestone.isCompleted) CardSlateBg.copy(alpha = 0.6f) else CardSlateBg
    ),
    shape = RoundedCornerShape(18.dp),
    border = BorderStroke(
      width = 1.dp,
      color = if (milestone.isCompleted) RadiantMint.copy(alpha = 0.3f) else MutedSlate.copy(alpha = 0.15f)
    )
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      // Checked Checkbox widget
      Checkbox(
        checked = milestone.isCompleted,
        onCheckedChange = { onCheckboxClick(it) },
        colors = CheckboxDefaults.colors(
          checkedColor = RadiantMint,
          uncheckedColor = MutedSlate.copy(alpha = 0.5f),
          checkmarkColor = SpaceSlateBg
        ),
        modifier = Modifier
          .size(48.dp)
          .testTag("checkbox_milestone_${milestone.id}")
      )

      Spacer(modifier = Modifier.width(12.dp))

      Column(modifier = Modifier.weight(1f)) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          Text(
            text = milestone.title,
            fontWeight = FontWeight.Bold,
            fontSize = 15.sp,
            color = if (milestone.isCompleted) MutedSlate else LightText
          )
          
          Surface(
            color = when (milestone.difficulty) {
              "Beginner" -> CosmicSky.copy(alpha = 0.15f)
              "Intermediate" -> BrightAmber.copy(alpha = 0.15f)
              else -> RadiantMint.copy(alpha = 0.15f)
            },
            shape = RoundedCornerShape(6.dp)
          ) {
            Text(
              text = milestone.difficulty,
              fontSize = 9.sp,
              color = when (milestone.difficulty) {
                "Beginner" -> CosmicSky
                "Intermediate" -> BrightAmber
                else -> RadiantMint
              },
              fontWeight = FontWeight.ExtraBold,
              modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
            )
          }
        }

        Spacer(modifier = Modifier.height(4.dp))
        Text(
          text = milestone.description,
          fontSize = 12.sp,
          color = MutedSlate,
          lineHeight = 18.sp
        )
      }
    }
  }
}

@Composable
fun imageNameAndType(activeTab: Int) = if (activeTab == 0) Icons.Default.PlayArrow else Icons.Default.Build

// Inline helper to support percent scale logic sizing container properly in Compose
fun Modifier.fillVertical() = this.fillMaxHeight()
