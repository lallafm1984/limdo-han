package com.example.limdo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            LimDoApp()
        }
    }
}

private val LimDoColorScheme = lightColorScheme(
    primary = Color(0xFF3F725E),
    onPrimary = Color.White,
    secondary = Color(0xFFF0A660),
    background = Color(0xFFFFF8EC),
    surface = Color(0xFFFFFEFA),
    onSurface = Color(0xFF26332D),
)

@Composable
private fun LimDoApp() {
    MaterialTheme(colorScheme = LimDoColorScheme) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background,
        ) {
            LearningShell()
        }
    }
}

@Composable
private fun LearningShell() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp, vertical = 16.dp),
    ) {
        LessonHeader()

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(vertical = 14.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            GuideCharacterCard(
                modifier = Modifier
                    .weight(LearningShellSpec.GUIDE_WEIGHT)
                    .fillMaxHeight(),
            )
            WritingBoardPlaceholder(
                modifier = Modifier
                    .weight(LearningShellSpec.WRITING_BOARD_WEIGHT)
                    .fillMaxHeight(),
            )
        }

        ActionShelf()
    }
}

@Composable
private fun LessonHeader() {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 64.dp),
        color = Color(0xFFE4F1EA),
        shape = RoundedCornerShape(24.dp),
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 24.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = stringResource(R.string.journey_label),
                    color = MaterialTheme.colorScheme.primary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    text = stringResource(R.string.lesson_prompt),
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                )
            }
            Text(
                text = stringResource(R.string.lesson_progress),
                color = MaterialTheme.colorScheme.primary,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
            )
        }
    }
}

@Composable
private fun GuideCharacterCard(modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier,
        color = Color(0xFFFFEBCB),
        shape = RoundedCornerShape(30.dp),
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Box(
                modifier = Modifier
                    .size(88.dp)
                    .background(Color(0xFFFFF8EC), CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                Text(text = "🐰", fontSize = 48.sp)
            }
            Text(
                modifier = Modifier.padding(top = 10.dp),
                text = stringResource(R.string.guide_character),
                color = Color(0xFF7A4A22),
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
            )
            Text(
                modifier = Modifier.padding(top = 6.dp),
                text = stringResource(R.string.guide_message),
                fontSize = 17.sp,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center,
            )
        }
    }
}

@Composable
private fun WritingBoardPlaceholder(modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier.border(
            width = 3.dp,
            color = Color(0xFFB8D8C9),
            shape = RoundedCornerShape(30.dp),
        ),
        color = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(30.dp),
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Text(
                text = stringResource(R.string.writing_board_label),
                color = MaterialTheme.colorScheme.primary,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
            )
            Text(
                modifier = Modifier.padding(vertical = 8.dp),
                text = "✦",
                color = MaterialTheme.colorScheme.secondary,
                fontSize = 56.sp,
            )
            Text(
                text = stringResource(R.string.writing_board_placeholder),
                color = Color(0xFF58645E),
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center,
            )
        }
    }
}

@Composable
private fun ActionShelf() {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 72.dp),
        color = Color(0xFFEDE9E1),
        shape = RoundedCornerShape(24.dp),
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            ActionPlaceholder(
                label = stringResource(R.string.action_replay),
                modifier = Modifier.weight(1f),
            )
            ActionPlaceholder(
                label = stringResource(R.string.action_clear),
                modifier = Modifier.weight(1f),
            )
            Spacer(modifier = Modifier.width(8.dp))
            ActionPlaceholder(
                label = stringResource(R.string.action_next),
                modifier = Modifier.weight(1f),
                emphasized = true,
            )
        }
    }
}

@Composable
private fun ActionPlaceholder(
    label: String,
    modifier: Modifier = Modifier,
    emphasized: Boolean = false,
) {
    Surface(
        modifier = modifier.heightIn(min = 56.dp),
        color = if (emphasized) Color(0xFFCBE7D8) else Color(0xFFFFFEFA),
        shape = RoundedCornerShape(20.dp),
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Text(
                text = label,
                color = if (emphasized) MaterialTheme.colorScheme.primary else Color(0xFF68716C),
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
            )
            Text(
                text = stringResource(R.string.not_available_yet),
                color = Color(0xFF7C857F),
                fontSize = 11.sp,
            )
        }
    }
}
