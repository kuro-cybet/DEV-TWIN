package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Agriculture
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import com.example.ui.theme.Sprout
import com.example.ui.theme.SproutGlow
import com.example.viewmodel.KrishiViewModel

@Composable
fun OnboardingScreen(
    viewModel: KrishiViewModel,
    modifier: Modifier = Modifier
) {
    var step by remember { mutableIntStateOf(0) }
    var selectedLang by remember { mutableStateOf("en") }
    var selectedState by remember { mutableStateOf("Punjab") }
    var selectedCrops by remember { mutableStateOf("Wheat, Cotton, Mustard") }

    val states = listOf("Punjab", "Haryana", "Maharashtra", "Uttar Pradesh", "Madhya Pradesh", "Rajasthan", "Gujarat", "Andhra Pradesh", "Tamil Nadu", "Karnataka")

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(CanopyDark, Canopy, CanopyLight)
                )
            )
            .padding(24.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(32.dp))

            // Brand Header with Glow Orb
            Box(contentAlignment = Alignment.Center) {
                GlowOrb(glowColor = GoldGlow, size = 80.dp)
                Surface(
                    shape = CircleShape,
                    color = HarvestGold,
                    modifier = Modifier.size(64.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.Agriculture,
                            contentDescription = null,
                            tint = CanopyDark,
                            modifier = Modifier.size(36.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "KrishiMitra",
                style = MaterialTheme.typography.displayMedium,
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.Bold,
                color = Husk
            )
            Text(
                text = "AI Agricultural Companion for Indian Farmers",
                style = MaterialTheme.typography.bodyMedium,
                color = Husk.copy(alpha = 0.8f)
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Step Card
            GlassCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                isDarkBg = true
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    when (step) {
                        0 -> {
                            // Step 1: Select Language
                            Text(
                                text = "अपनी भाषा चुनें / Select Language",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = Husk,
                                fontFamily = FontFamily.Serif
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            LazyVerticalGrid(
                                columns = GridCells.Fixed(2),
                                verticalArrangement = Arrangement.spacedBy(10.dp),
                                horizontalArrangement = Arrangement.spacedBy(10.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                items(Translations.supportedLanguages) { langItem ->
                                    val isSelected = selectedLang == langItem.code
                                    Surface(
                                        onClick = { selectedLang = langItem.code },
                                        shape = RoundedCornerShape(12.dp),
                                        color = if (isSelected) HarvestGold else Color.White.copy(alpha = 0.1f),
                                        border = BorderStroke(1.dp, if (isSelected) HarvestGold else Color.White.copy(alpha = 0.2f))
                                    ) {
                                        Column(
                                            horizontalAlignment = Alignment.CenterHorizontally,
                                            modifier = Modifier.padding(vertical = 12.dp)
                                        ) {
                                            Text(
                                                text = langItem.nativeName,
                                                style = MaterialTheme.typography.titleMedium,
                                                fontWeight = FontWeight.Bold,
                                                color = if (isSelected) CanopyDark else Husk
                                            )
                                            Text(
                                                text = langItem.name,
                                                style = MaterialTheme.typography.labelMedium,
                                                color = if (isSelected) CanopyDark.copy(alpha = 0.7f) else Husk.copy(alpha = 0.6f)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                        1 -> {
                            // Step 2: Select State & Region
                            Text(
                                text = "Select Your Farming State",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = Husk,
                                fontFamily = FontFamily.Serif
                            )

                            Spacer(modifier = Modifier.height(14.dp))

                            LazyVerticalGrid(
                                columns = GridCells.Fixed(2),
                                verticalArrangement = Arrangement.spacedBy(8.dp),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                items(states) { st ->
                                    val isSelected = selectedState == st
                                    Surface(
                                        onClick = { selectedState = st },
                                        shape = RoundedCornerShape(12.dp),
                                        color = if (isSelected) Sprout else Color.White.copy(alpha = 0.1f),
                                        border = BorderStroke(1.dp, if (isSelected) SproutGlow else Color.White.copy(alpha = 0.2f))
                                    ) {
                                        Text(
                                            text = st,
                                            style = MaterialTheme.typography.labelLarge,
                                            fontWeight = FontWeight.Bold,
                                            color = if (isSelected) Color.White else Husk,
                                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 12.dp),
                                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                        )
                                    }
                                }
                            }
                        }
                        2 -> {
                            // Step 3: Primary Crops & AI Rationale
                            Text(
                                text = "Primary Farm Crops",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = Husk,
                                fontFamily = FontFamily.Serif
                            )

                            Spacer(modifier = Modifier.height(14.dp))

                            OutlinedTextField(
                                value = selectedCrops,
                                onValueChange = { selectedCrops = it },
                                label = { Text("Crops (e.g. Wheat, Cotton, Paddy)", color = Husk.copy(alpha = 0.7f)) },
                                modifier = Modifier.fillMaxWidth()
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            FeatureItemRow(Icons.Default.Mic, "AI Voice Assistant in your regional language")
                            Spacer(modifier = Modifier.height(8.dp))
                            FeatureItemRow(Icons.Default.PhotoCamera, "Instant Crop Disease Pathology via Camera")
                            Spacer(modifier = Modifier.height(8.dp))
                            FeatureItemRow(Icons.Default.Eco, "Soil NPK & Weather-smart spray forecasts")
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Next / Complete Button
                    Button(
                        onClick = {
                            if (step < 2) {
                                step++
                            } else {
                                viewModel.completeOnboarding(selectedState, selectedCrops, selectedLang)
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("onboarding_next_button"),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = HarvestGold)
                    ) {
                        Text(
                            text = if (step < 2) "Continue" else "Start Farming Smarter",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = CanopyDark
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun FeatureItemRow(icon: androidx.compose.ui.graphics.vector.ImageVector, text: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = HarvestGold,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(10.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            color = Husk.copy(alpha = 0.9f)
        )
    }
}
