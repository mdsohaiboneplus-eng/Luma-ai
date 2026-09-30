package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.LiquidDarkSurface
import com.example.ui.theme.LiquidGlassBorder
import com.example.ui.theme.LumaCyan
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OnboardingDialog(
    onRequestNotificationPermission: () -> Unit,
    onComplete: (userName: String, firstTaskTitle: String?) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var firstTask by remember { mutableStateOf("") }

    BasicAlertDialog(
        onDismissRequest = {},
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(28.dp))
            .background(LiquidDarkSurface)
            .padding(24.dp)
            .testTag("onboarding_dialog")
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            // Glowing badge icon
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.linearGradient(
                            listOf(
                                MaterialTheme.colorScheme.primary,
                                LumaCyan
                            )
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "✨", fontSize = 30.sp)
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Welcome to LumaTask",
                color = TextPrimary,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Plan your day. Build your streak. Stay focused.",
                color = TextSecondary,
                fontSize = 14.sp,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Name field
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                placeholder = { Text("What should we call you? (e.g. Alex)", color = TextMuted) },
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = LiquidGlassBorder,
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary
                ),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("onboarding_name_input")
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Optional First Task
            OutlinedTextField(
                value = firstTask,
                onValueChange = { firstTask = it },
                placeholder = { Text("Add your first task for today (optional)", color = TextMuted) },
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = LiquidGlassBorder,
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary
                ),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Notification permission request
            LiquidGlassButton(
                text = "Enable Reminders & Notifications",
                isSecondary = true,
                icon = { Icon(Icons.Default.Notifications, contentDescription = null, tint = Color(0xFFFBBF24), modifier = Modifier.size(16.dp)) },
                onClick = onRequestNotificationPermission,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Get Started Button
            LiquidGlassButton(
                text = "Get Started ✨",
                onClick = {
                    onComplete(
                        if (name.isBlank()) "Friend" else name.trim(),
                        firstTask.ifBlank { null }
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("onboarding_start_button")
            )
        }
    }
}
