package com.example.ui.screens

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.service.Translations
import com.example.ui.components.GlassCard
import com.example.ui.components.GlowOrb
import com.example.ui.theme.Canopy
import com.example.ui.theme.CanopyDark
import com.example.ui.theme.CanopyLight
import com.example.ui.theme.GoldGlow
import com.example.ui.theme.HarvestGold
import com.example.ui.theme.Husk
import com.example.ui.theme.HuskBorder
import com.example.ui.theme.HuskCard
import com.example.ui.theme.HuskSurface
import com.example.ui.theme.Sprout
import com.example.ui.theme.SproutGlow
import com.example.viewmodel.AppDestination
import com.example.viewmodel.KrishiViewModel

@Composable
fun VoiceScreen(
    viewModel: KrishiViewModel,
    modifier: Modifier = Modifier
) {
    val messages by viewModel.voiceMessages.collectAsState()
    val isListening by viewModel.isListening.collectAsState()
    val isAiThinking by viewModel.isAiThinking.collectAsState()
    val profile by viewModel.profile.collectAsState()
    val lang = profile?.languageCode ?: "en"

    var textInput by remember { mutableStateOf("") }
    val listState = rememberLazyListState()

    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    val suggestions = when (lang) {
        "hi" -> listOf(
            "कपास में गुलाबी सुंडी का उपाय बताएं",
            "आज खन्ना मंडी में गेहूं का भाव क्या है?",
            "क्या आज कीटनाशक का छिड़काव करना सुरक्षित है?",
            "टमाटर में झुलसा रोग की दवा बताएं"
        )
        "pa" -> listOf(
            "ਕਣਕ 'ਚ ਪੀਲੀ ਕੁੰਗੀ ਦਾ ਇਲਾਜ ਦੱਸੋ",
            "ਅੱਜ ਮੰਡੀ ਵਿੱਚ ਕਣਕ ਦਾ ਰੇਟ ਕੀ ਹੈ?",
            "ਕੀ ਅੱਜ ਸਪਰੇਅ ਕਰਨਾ ਠੀਕ ਰਹੇਗਾ?",
            "ਨਰਮੇ ਵਿੱਚ ਚਿੱਟੀ ਮੱਖੀ ਦੀ ਰੋਕਥਾਮ"
        )
        else -> listOf(
            "Wheat yellow rust fungicide remedy",
            "Today's wheat mandi price in Khanna",
            "Is today safe for pesticide spray?",
            "Dosage of Urea top dressing for paddy"
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(bottom = 90.dp)
    ) {
        // Voice Hero Header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp))
                .background(
                    Brush.verticalGradient(
                        listOf(CanopyDark, Canopy, CanopyLight)
                    )
                )
                .padding(top = 16.dp, bottom = 20.dp, start = 16.dp, end = 16.dp)
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "AI Voice Companion",
                        style = MaterialTheme.typography.titleLarge,
                        fontFamily = FontFamily.Serif,
                        color = Husk,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    IconButton(
                        onClick = { viewModel.clearVoiceHistory() },
                        modifier = Modifier.testTag("clear_voice_history")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Clear History",
                            tint = Husk.copy(alpha = 0.7f)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Hero Animated Pulsing Mic Button
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(100.dp)
                        .testTag("voice_mic_button")
                        .clickable {
                            viewModel.toggleListening()
                            if (!isListening) {
                                // Simulate prompt or speech recognition
                                viewModel.sendVoiceQuery("Wheat grain filling fertilizer recommendation")
                            }
                        }
                ) {
                    if (isListening || isAiThinking) {
                        GlowOrb(glowColor = GoldGlow, size = 96.dp)
                    }
                    Surface(
                        shape = CircleShape,
                        color = if (isListening) HarvestGold else Sprout,
                        shadowElevation = 8.dp,
                        modifier = Modifier.size(72.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = if (isListening) Icons.Default.GraphicEq else Icons.Default.Mic,
                                contentDescription = "Mic",
                                tint = Color.White,
                                modifier = Modifier.size(34.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Live Audio Waveform Bars
                if (isListening) {
                    LiveWaveformVisualizer()
                } else {
                    Text(
                        text = if (isAiThinking) "Consulting AI Agro-Expert..." else "Tap Mic to Speak (Supports Hindi, Punjabi, Tamil...)",
                        style = MaterialTheme.typography.labelMedium,
                        color = Husk.copy(alpha = 0.85f)
                    )
                }
            }
        }

        // Quick Suggestion Chips
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(suggestions) { chipText ->
                Surface(
                    onClick = { viewModel.sendVoiceQuery(chipText) },
                    shape = RoundedCornerShape(16.dp),
                    color = MaterialTheme.colorScheme.surface,
                    border = BorderStroke(1.dp, HuskBorder)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.GraphicEq,
                            contentDescription = null,
                            tint = HarvestGold,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = chipText,
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurface,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }

        // Conversation History
        LazyColumn(
            state = listState,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(vertical = 12.dp)
        ) {
            items(messages) { message ->
                VoiceChatBubble(
                    message = message,
                    onPlayTts = { viewModel.speakText(message.text) },
                    onActionClick = { actionType ->
                        when (actionType) {
                            "CALENDAR" -> viewModel.navigateTo(AppDestination.Calendar)
                            "SCAN" -> viewModel.navigateTo(AppDestination.Scan)
                            "MARKET" -> viewModel.navigateTo(AppDestination.Market)
                            "WEATHER" -> viewModel.navigateTo(AppDestination.Weather)
                        }
                    }
                )
            }

            if (isAiThinking) {
                item {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = Sprout,
                            strokeWidth = 2.dp
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "Analyzing crop pathology and regional advisory...",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        // Bottom Voice & Text Input Bar
        Surface(
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp,
            border = BorderStroke(1.dp, HuskBorder),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp)
            ) {
                OutlinedTextField(
                    value = textInput,
                    onValueChange = { textInput = it },
                    placeholder = {
                        Text(
                            text = "Type or speak your farming question...",
                            style = MaterialTheme.typography.bodyMedium,
                            fontSize = 14.sp
                        )
                    },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("voice_text_input"),
                    shape = RoundedCornerShape(24.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Sprout,
                        unfocusedBorderColor = HuskBorder,
                        focusedContainerColor = HuskCard,
                        unfocusedContainerColor = HuskCard
                    ),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                    keyboardActions = KeyboardActions(
                        onSend = {
                            if (textInput.isNotBlank()) {
                                viewModel.sendVoiceQuery(textInput)
                                textInput = ""
                            }
                        }
                    ),
                    singleLine = true
                )

                Spacer(modifier = Modifier.width(8.dp))

                IconButton(
                    onClick = {
                        if (textInput.isNotBlank()) {
                            viewModel.sendVoiceQuery(textInput)
                            textInput = ""
                        }
                    },
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(if (textInput.isNotBlank()) Sprout else Canopy.copy(alpha = 0.3f))
                        .testTag("voice_send_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Send,
                        contentDescription = "Send",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun VoiceChatBubble(
    message: com.example.data.models.VoiceMessage,
    onPlayTts: () -> Unit,
    onActionClick: (String) -> Unit
) {
    val isUser = message.isUser

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
    ) {
        if (!isUser) {
            Surface(
                shape = CircleShape,
                color = Canopy,
                modifier = Modifier
                    .size(34.dp)
                    .padding(top = 2.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.GraphicEq,
                        contentDescription = null,
                        tint = HarvestGold,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.width(8.dp))
        }

        Column(
            modifier = Modifier.widthIn(max = 280.dp),
            horizontalAlignment = if (isUser) Alignment.End else Alignment.Start
        ) {
            Surface(
                shape = RoundedCornerShape(
                    topStart = 16.dp,
                    topEnd = 16.dp,
                    bottomStart = if (isUser) 16.dp else 4.dp,
                    bottomEnd = if (isUser) 4.dp else 16.dp
                ),
                color = if (isUser) Sprout else HuskCard,
                border = if (!isUser) BorderStroke(1.dp, HuskBorder) else null,
                shadowElevation = 2.dp
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        text = message.text,
                        style = MaterialTheme.typography.bodyMedium,
                        color = if (isUser) Color.White else Canopy,
                        lineHeight = 20.sp
                    )

                    if (!isUser) {
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            IconButton(
                                onClick = onPlayTts,
                                modifier = Modifier.size(28.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.VolumeUp,
                                    contentDescription = "Read Aloud",
                                    tint = HarvestGold,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            Spacer(modifier = Modifier.weight(1f))
                            Text(
                                text = "Krishi AI",
                                style = MaterialTheme.typography.labelMedium,
                                fontSize = 10.sp,
                                color = Canopy.copy(alpha = 0.5f)
                            )
                        }
                    }
                }
            }

            // Voice to Action Button Handoff
            if (!isUser && message.actionLabel != null && message.actionType != null) {
                Spacer(modifier = Modifier.height(4.dp))
                Surface(
                    onClick = { onActionClick(message.actionType) },
                    shape = RoundedCornerShape(12.dp),
                    color = HarvestGold.copy(alpha = 0.2f),
                    border = BorderStroke(1.dp, HarvestGold),
                    modifier = Modifier.padding(top = 2.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                    ) {
                        val icon = when (message.actionType) {
                            "CALENDAR" -> Icons.Default.CalendarToday
                            "SCAN" -> Icons.Default.CameraAlt
                            "MARKET" -> Icons.Default.Storefront
                            else -> Icons.Default.Cloud
                        }
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            tint = HarvestGold,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = message.actionLabel,
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = Canopy
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            imageVector = Icons.Default.ArrowForward,
                            contentDescription = null,
                            tint = HarvestGold,
                            modifier = Modifier.size(12.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun LiveWaveformVisualizer() {
    val infiniteTransition = rememberInfiniteTransition(label = "waveform")
    val heights = (0..6).map { index ->
        val anim by infiniteTransition.animateFloat(
            initialValue = 6f,
            targetValue = 24f,
            animationSpec = infiniteRepeatable(
                animation = tween(400 + (index * 120), easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "bar_$index"
        )
        anim
    }

    Row(
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.height(28.dp)
    ) {
        heights.forEach { h ->
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .height(h.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(HarvestGold)
            )
        }
    }
}

fun Modifier.widthIn(max: androidx.compose.ui.unit.Dp): Modifier = this.then(
    Modifier.width(max)
)
