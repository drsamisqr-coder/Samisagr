package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Stars
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.R
import com.example.ui.MasterCoderViewModel
import com.example.ui.components.CodeCard
import com.example.ui.theme.BrandAmber
import com.example.ui.theme.BrandCyan
import com.example.ui.theme.BrandEmerald
import com.example.ui.theme.BrandIndigo
import com.example.ui.theme.BrandRose

@Composable
fun AcademyScreen(
    viewModel: MasterCoderViewModel,
    modifier: Modifier = Modifier
) {
    val isArabic by viewModel.isArabic.collectAsStateWithLifecycle()
    val progress by viewModel.quizProgress.collectAsStateWithLifecycle()
    val currentIndex by viewModel.currentQuestionIndex.collectAsStateWithLifecycle()
    val selectedOption by viewModel.selectedOption.collectAsStateWithLifecycle()
    val isSubmitted by viewModel.isSubmitted.collectAsStateWithLifecycle()

    val currentChallenge = viewModel.currentChallenge
    val xp = progress?.xp ?: 0
    val streak = progress?.currentStreak ?: 1
    val solvedCount = progress?.completedChallengesCount ?: 0

    val rankTitle = when {
        xp >= 1000 -> stringResource(R.string.rank_master)
        xp >= 500 -> stringResource(R.string.rank_staff)
        xp >= 250 -> stringResource(R.string.rank_senior)
        xp >= 100 -> stringResource(R.string.rank_mid)
        else -> stringResource(R.string.rank_junior)
    }

    val rankProgress = ((xp % 250) / 250f).coerceIn(0f, 1f)

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Hero Developer Status Card with Generated Banner
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF131B2E)),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF1E293B))
        ) {
            Column {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(110.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.img_hero_mastercoder),
                        contentDescription = "MasterCoder Academy Banner",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    listOf(Color.Transparent, Color(0xFF131B2E))
                                )
                            )
                    )
                }

                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = rankTitle,
                                color = BrandCyan,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = stringResource(R.string.xp_format, xp),
                                color = Color(0xFF94A3B8),
                                fontSize = 13.sp
                            )
                        }

                        // Streak Badge
                        Surface(
                            color = Color(0xFF2C1910),
                            shape = RoundedCornerShape(20.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, BrandAmber)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.LocalFireDepartment,
                                    contentDescription = "Streak",
                                    tint = BrandAmber,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = stringResource(R.string.streak_format, streak),
                                    color = BrandAmber,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Progress bar
                    LinearProgressIndicator(
                        progress = { rankProgress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp)),
                        color = BrandCyan,
                        trackColor = Color(0xFF1E293B)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = stringResource(R.string.challenges_completed_format, solvedCount),
                            color = Color(0xFF64748B),
                            fontSize = 12.sp
                        )
                        Text(
                            text = stringResource(R.string.next_level_xp),
                            color = Color(0xFF64748B),
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }

        // Active Challenge Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("quiz_challenge_card"),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF131B2E)),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF2E3D5C))
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                // Category & XP Reward Tag
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        color = Color(0xFF0C2B4E),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = currentChallenge.category,
                            color = BrandCyan,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Stars,
                            contentDescription = "XP",
                            tint = BrandAmber,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "+${currentChallenge.xpReward} XP",
                            color = BrandAmber,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Question Title
                Text(
                    text = if (isArabic) currentChallenge.questionAr else currentChallenge.questionEn,
                    color = Color(0xFFF8FAFC),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    lineHeight = 22.sp
                )

                // Optional Code Snippet inside question
                if (currentChallenge.codeSnippet != null) {
                    Spacer(modifier = Modifier.height(10.dp))
                    CodeCard(
                        code = currentChallenge.codeSnippet,
                        language = "code"
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Options List
                val options = if (isArabic) currentChallenge.optionsAr else currentChallenge.optionsEn
                options.forEachIndexed { index, optionText ->
                    val isSelected = selectedOption == index
                    val isCorrect = index == currentChallenge.correctIndex

                    val borderColor = when {
                        !isSubmitted && isSelected -> BrandCyan
                        isSubmitted && isCorrect -> BrandEmerald
                        isSubmitted && isSelected && !isCorrect -> BrandRose
                        else -> Color(0xFF1E293B)
                    }

                    val containerColor = when {
                        !isSubmitted && isSelected -> Color(0xFF0C2B4E)
                        isSubmitted && isCorrect -> Color(0xFF063622)
                        isSubmitted && isSelected && !isCorrect -> Color(0xFF3B1019)
                        else -> Color(0xFF0F172A)
                    }

                    Surface(
                        color = containerColor,
                        shape = RoundedCornerShape(12.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, borderColor),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .clickable(enabled = !isSubmitted) {
                                viewModel.selectOption(index)
                            }
                            .testTag("quiz_option_$index")
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(24.dp)
                                    .clip(CircleShape)
                                    .border(
                                        1.dp,
                                        if (isSelected || (isSubmitted && isCorrect)) borderColor else Color(0xFF64748B),
                                        CircleShape
                                    )
                                    .background(
                                        if (isSelected || (isSubmitted && isCorrect)) borderColor else Color.Transparent
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                if (isSubmitted && isCorrect) {
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = "Correct",
                                        tint = Color.White,
                                        modifier = Modifier.size(18.dp)
                                    )
                                } else if (isSubmitted && isSelected && !isCorrect) {
                                    Icon(
                                        imageVector = Icons.Default.Cancel,
                                        contentDescription = "Wrong",
                                        tint = Color.White,
                                        modifier = Modifier.size(18.dp)
                                    )
                                } else {
                                    Text(
                                        text = ('A' + index).toString(),
                                        color = if (isSelected) Color(0xFF04101E) else Color(0xFF94A3B8),
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Text(
                                text = optionText,
                                color = Color(0xFFF1F5F9),
                                fontSize = 13.sp,
                                lineHeight = 19.sp,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Action Buttons (Submit / Next)
                if (!isSubmitted) {
                    Button(
                        onClick = { viewModel.submitAnswer() },
                        enabled = selectedOption != null,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = BrandCyan,
                            contentColor = Color(0xFF04101E)
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(46.dp)
                            .testTag("submit_quiz_btn")
                    ) {
                        Text(
                            text = stringResource(R.string.quiz_confirm_answer),
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }
                } else {
                    // Explanation Section
                    Surface(
                        color = Color(0xFF0B1324),
                        shape = RoundedCornerShape(12.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF1E293B)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = if (selectedOption == currentChallenge.correctIndex)
                                        Icons.Default.CheckCircle
                                    else
                                        Icons.Default.Cancel,
                                    contentDescription = null,
                                    tint = if (selectedOption == currentChallenge.correctIndex)
                                        BrandEmerald
                                    else
                                        BrandRose
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = if (selectedOption == currentChallenge.correctIndex)
                                        stringResource(R.string.quiz_correct_title)
                                    else
                                        stringResource(R.string.quiz_incorrect_title),
                                    color = if (selectedOption == currentChallenge.correctIndex)
                                        BrandEmerald
                                    else
                                        BrandRose,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = if (isArabic) currentChallenge.explanationAr else currentChallenge.explanationEn,
                                color = Color(0xFFCBD5E1),
                                fontSize = 13.sp,
                                lineHeight = 19.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Button(
                        onClick = { viewModel.nextQuestion() },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = BrandIndigo,
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(46.dp)
                            .testTag("next_quiz_btn")
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = stringResource(R.string.quiz_next_challenge),
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, modifier = Modifier.size(16.dp))
                        }
                    }
                }
            }
        }
    }
}
