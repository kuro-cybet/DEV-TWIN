package com.example.ui.screens

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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Agriculture
import androidx.compose.material.icons.filled.Air
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material.icons.filled.Thermostat
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.service.AgroKnowledgeBase
import com.example.service.Translations
import com.example.ui.components.GlassCard
import com.example.ui.components.GlowOrb
import com.example.ui.components.SectionHeader
import com.example.ui.components.TerraceCard
import com.example.ui.theme.Canopy
import com.example.ui.theme.CanopyDark
import com.example.ui.theme.CanopyLight
import com.example.ui.theme.GoldGlow
import com.example.ui.theme.HarvestGold
import com.example.ui.theme.Husk
import com.example.ui.theme.HuskBorder
import com.example.ui.theme.HuskCard
import com.example.ui.theme.SkyMist
import com.example.ui.theme.Sprout
import com.example.ui.theme.SproutGlow
import com.example.ui.theme.StatusSuccess
import com.example.ui.theme.StatusWarning
import com.example.viewmodel.AppDestination
import com.example.viewmodel.KrishiViewModel

@Composable
fun HomeScreen(
    viewModel: KrishiViewModel,
    modifier: Modifier = Modifier
) {
    val profile by viewModel.profile.collectAsState()
    val fields by viewModel.fields.collectAsState()
    val tasks by viewModel.tasks.collectAsState()
    val isOffline by viewModel.isOffline.collectAsState()

    val lang = profile?.languageCode ?: "en"
    val farmerName = profile?.name ?: "Rajesh Kumar"
    val farmerState = profile?.state ?: "Punjab"
    val farmerDistrict = profile?.district ?: "Ludhiana"

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(bottom = 90.dp),
        contentPadding = PaddingValues(bottom = 24.dp)
    ) {
        // Hero Header with Canopy Gradient
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(bottomStart = 28.dp, bottomEnd = 28.dp))
                    .background(
                        Brush.verticalGradient(
                            listOf(CanopyDark, Canopy, CanopyLight)
                        )
                    )
                    .padding(top = 16.dp, bottom = 44.dp, start = 20.dp, end = 20.dp)
            ) {
                Column {
                    // Top Bar inside Hero
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color.White.copy(alpha = 0.12f),
                            border = BorderStroke(1.dp, Color.White.copy(alpha = 0.2f))
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Place,
                                    contentDescription = null,
                                    tint = HarvestGold,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "$farmerDistrict, $farmerState",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = Husk,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }

                        Spacer(modifier = Modifier.weight(1f))

                        // Language Switcher Pill
                        Surface(
                            onClick = { viewModel.setLanguageDialogOpen(true) },
                            shape = RoundedCornerShape(12.dp),
                            color = HarvestGold.copy(alpha = 0.25f),
                            border = BorderStroke(1.dp, HarvestGold.copy(alpha = 0.6f)),
                            modifier = Modifier.testTag("language_switch_button")
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Language,
                                    contentDescription = "Language",
                                    tint = HarvestGold,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = Translations.supportedLanguages.find { it.code == lang }?.nativeName ?: "English",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = HarvestGold,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    // Farmer Greeting
                    Text(
                        text = "${Translations.get("greeting_morning", lang)},",
                        style = MaterialTheme.typography.headlineMedium,
                        color = HarvestGold,
                        fontFamily = FontFamily.Serif
                    )
                    Text(
                        text = "$farmerName 🙏",
                        style = MaterialTheme.typography.displayMedium,
                        color = Husk,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Serif
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(if (isOffline) StatusWarning else SproutGlow)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (isOffline) Translations.get("offline_indicator", lang) else "AI Agro-Doctor Online · Kharif & Rabi Sync",
                            style = MaterialTheme.typography.labelMedium,
                            color = Husk.copy(alpha = 0.85f)
                        )
                    }
                }
            }
        }

        // Floating Glass Weather Summary Card (Overlapping Hero)
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .offset(y = (-26).dp)
            ) {
                GlassCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("weather_summary_card"),
                    elevation = 8.dp,
                    onClick = { viewModel.navigateTo(AppDestination.Weather) }
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column {
                                Text(
                                    text = "28°C",
                                    style = MaterialTheme.typography.displayMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = Canopy
                                )
                                Text(
                                    text = "Partly Sunny · Feels 29°C",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Canopy.copy(alpha = 0.75f)
                                )
                            }

                            Spacer(modifier = Modifier.weight(1f))

                            Icon(
                                imageVector = Icons.Default.WbSunny,
                                contentDescription = null,
                                tint = HarvestGold,
                                modifier = Modifier.size(44.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Spray & Rain Status Strip
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(Sprout.copy(alpha = 0.12f))
                                .border(BorderStroke(1.dp, Sprout.copy(alpha = 0.3f)), RoundedCornerShape(12.dp))
                                .padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = Sprout,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = Translations.get("spray_safe", lang) + " (Wind 7 km/h)",
                                style = MaterialTheme.typography.labelMedium,
                                color = Sprout,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.weight(1f))
                            Text(
                                text = "15% Rain",
                                style = MaterialTheme.typography.labelMedium,
                                color = SkyMist,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }

        // 2x2 Quick Action Grid
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .offset(y = (-10).dp)
            ) {
                SectionHeader(
                    title = "Quick Farm Tools",
                    badgeText = "AI Powered"
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    QuickActionTile(
                        title = Translations.get("quick_voice", lang),
                        subtitle = "Ask in Hindi/Punjabi",
                        icon = Icons.Default.Mic,
                        accentColor = Sprout,
                        glowColor = SproutGlow,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("quick_action_voice"),
                        onClick = { viewModel.navigateTo(AppDestination.Voice) }
                    )
                    QuickActionTile(
                        title = Translations.get("quick_scan", lang),
                        subtitle = "Scan Leaves & Pests",
                        icon = Icons.Default.CameraAlt,
                        accentColor = HarvestGold,
                        glowColor = GoldGlow,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("quick_action_scan"),
                        onClick = { viewModel.navigateTo(AppDestination.Scan) }
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    QuickActionTile(
                        title = Translations.get("quick_weather", lang),
                        subtitle = "7-Day & Spray Index",
                        icon = Icons.Default.Cloud,
                        accentColor = SkyMist,
                        glowColor = SkyMist,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("quick_action_weather"),
                        onClick = { viewModel.navigateTo(AppDestination.Weather) }
                    )
                    QuickActionTile(
                        title = Translations.get("quick_market", lang),
                        subtitle = "APMC Mandi Rates",
                        icon = Icons.Default.Storefront,
                        accentColor = HarvestGold,
                        glowColor = GoldGlow,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("quick_action_market"),
                        onClick = { viewModel.navigateTo(AppDestination.Market) }
                    )
                }
            }
        }

        // State-Specific Agronomy Tip
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                SectionHeader(
                    title = "Agronomy Advisory",
                    badgeText = farmerState
                )

                Spacer(modifier = Modifier.height(8.dp))

                TerraceCard(
                    accentColor = HarvestGold,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Eco,
                                contentDescription = null,
                                tint = HarvestGold,
                                modifier = Modifier.size(22.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "$farmerState Crop Calendar Tip",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        val tip = AgroKnowledgeBase.stateTips[farmerState]
                            ?: "Ensure timely irrigation during morning hours and scout for sucking pests."
                        Text(
                            text = tip,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.85f),
                            lineHeight = 22.sp
                        )
                    }
                }
            }
        }

        // Active Fields Snapshot
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                SectionHeader(
                    title = "My Farm Plots",
                    badgeText = "${fields.size} Fields"
                )

                Spacer(modifier = Modifier.height(8.dp))

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(fields) { field ->
                        GlassCard(
                            modifier = Modifier
                                .width(220.dp)
                                .testTag("field_card_${field.id}"),
                            onClick = {
                                viewModel.selectField(field.id)
                                viewModel.navigateTo(AppDestination.CropSoil)
                            }
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Text(
                                    text = field.name,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = Canopy
                                )
                                Text(
                                    text = "${field.cropType} · ${field.areaAcres} Acres",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Canopy.copy(alpha = 0.75f)
                                )

                                Spacer(modifier = Modifier.height(10.dp))

                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(
                                        text = field.stage,
                                        style = MaterialTheme.typography.labelMedium,
                                        color = Sprout,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Spacer(modifier = Modifier.weight(1f))
                                    Text(
                                        text = "${(field.stageProgress * 100).toInt()}%",
                                        style = MaterialTheme.typography.labelMedium,
                                        color = Canopy.copy(alpha = 0.6f)
                                    )
                                }

                                Spacer(modifier = Modifier.height(4.dp))

                                LinearProgressIndicator(
                                    progress = { field.stageProgress },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(6.dp)
                                        .clip(RoundedCornerShape(3.dp)),
                                    color = Sprout,
                                    trackColor = HuskBorder
                                )
                            }
                        }
                    }
                }
            }
        }

        // Today's Priority Tasks
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                SectionHeader(
                    title = "Scheduled Tasks",
                    badgeText = "${tasks.count { !it.isCompleted }} Pending"
                )

                Spacer(modifier = Modifier.height(8.dp))

                tasks.take(3).forEach { task ->
                    TerraceCard(
                        accentColor = if (task.priority == "Urgent") StatusWarning else Sprout,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp)
                            .testTag("home_task_${task.id}"),
                        onClick = { viewModel.toggleTaskCompletion(task.id, task.isCompleted) }
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(14.dp)
                        ) {
                            Icon(
                                imageVector = if (task.isCompleted) Icons.Default.CheckCircle else Icons.Default.Schedule,
                                contentDescription = null,
                                tint = if (task.isCompleted) Sprout else Canopy.copy(alpha = 0.5f),
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = task.title,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = if (task.isCompleted) Canopy.copy(alpha = 0.5f) else Canopy
                                )
                                Text(
                                    text = task.dueDate,
                                    style = MaterialTheme.typography.labelMedium,
                                    color = Canopy.copy(alpha = 0.6f)
                                )
                                if (task.isWeatherDeferred) {
                                    Text(
                                        text = "⚠️ ${task.weatherNote}",
                                        style = MaterialTheme.typography.labelMedium,
                                        color = StatusWarning,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun QuickActionTile(
    title: String,
    subtitle: String,
    icon: ImageVector,
    accentColor: Color,
    glowColor: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    GlassCard(
        modifier = modifier,
        elevation = 6.dp,
        onClick = onClick
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.size(44.dp)
            ) {
                GlowOrb(glowColor = glowColor, size = 44.dp)
                Surface(
                    shape = CircleShape,
                    color = accentColor,
                    modifier = Modifier.size(38.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Canopy
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.labelMedium,
                color = Canopy.copy(alpha = 0.65f),
                fontSize = 11.sp
            )
        }
    }
}
