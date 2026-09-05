package com.example.ui.screens

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Agriculture
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import com.example.ui.components.SectionHeader
import com.example.ui.components.TerraceCard
import com.example.ui.theme.Canopy
import com.example.ui.theme.CanopyDark
import com.example.ui.theme.CanopyLight
import com.example.ui.theme.HarvestGold
import com.example.ui.theme.Husk
import com.example.ui.theme.HuskBorder
import com.example.ui.theme.HuskCard
import com.example.ui.theme.Sprout
import com.example.ui.theme.StatusWarning
import com.example.viewmodel.KrishiViewModel

@Composable
fun ProfileScreen(
    viewModel: KrishiViewModel,
    modifier: Modifier = Modifier
) {
    val profile by viewModel.profile.collectAsState()
    val isOffline by viewModel.isOffline.collectAsState()
    val lastSync by viewModel.lastSyncTime.collectAsState()

    val lang = profile?.languageCode ?: "en"

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(bottom = 90.dp),
        contentPadding = PaddingValues(bottom = 24.dp)
    ) {
        // Hero Farmer Card
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
                    .padding(20.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = CircleShape,
                        color = HarvestGold,
                        border = BorderStroke(3.dp, Color.White),
                        modifier = Modifier.size(72.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = null,
                                tint = CanopyDark,
                                modifier = Modifier.size(40.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Column {
                        Text(
                            text = profile?.name ?: "Rajesh Kumar",
                            style = MaterialTheme.typography.titleLarge,
                            fontFamily = FontFamily.Serif,
                            fontWeight = FontWeight.Bold,
                            color = Husk
                        )
                        Text(
                            text = "${profile?.village ?: "Kisanpur"}, ${profile?.district ?: "Ludhiana"}, ${profile?.state ?: "Punjab"}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Husk.copy(alpha = 0.8f)
                        )
                        Text(
                            text = "Landholding: ${profile?.totalAcres ?: 7.5} Acres · ${profile?.primaryCrops ?: "Wheat, Cotton"}",
                            style = MaterialTheme.typography.labelMedium,
                            color = HarvestGold,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }

        // Language & App Settings
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                SectionHeader(title = "App Preferences & Localization")

                Spacer(modifier = Modifier.height(8.dp))

                GlassCard(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = 4.dp,
                    onClick = { viewModel.setLanguageDialogOpen(true) }
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Language,
                            contentDescription = null,
                            tint = HarvestGold,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "App Display Language",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = Canopy
                            )
                            Text(
                                text = Translations.supportedLanguages.find { it.code == lang }?.let { "${it.name} (${it.nativeName})" } ?: "English",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Canopy.copy(alpha = 0.7f)
                            )
                        }
                        Text(
                            text = "Change",
                            style = MaterialTheme.typography.labelMedium,
                            color = Sprout,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        // Offline Mode & Room Local Cache Manager
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                SectionHeader(title = "Offline & Data Synchronization")

                Spacer(modifier = Modifier.height(8.dp))

                TerraceCard(
                    accentColor = if (isOffline) StatusWarning else Sprout,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(
                                imageVector = if (isOffline) Icons.Default.CloudSync else Icons.Default.CloudDone,
                                contentDescription = null,
                                tint = if (isOffline) StatusWarning else Sprout,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = if (isOffline) "Offline-First Mode Active" else "Connected to Cloud & Mandi Servers",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = Canopy
                                )
                                Text(
                                    text = "All crop scans & tasks saved to Room Local Database",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = Canopy.copy(alpha = 0.65f)
                                )
                            }
                            Switch(
                                checked = !isOffline,
                                onCheckedChange = { viewModel.toggleOfflineMode() },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = Sprout,
                                    checkedTrackColor = Sprout.copy(alpha = 0.4f)
                                )
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "Last Synced: $lastSync",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Canopy.copy(alpha = 0.7f),
                                modifier = Modifier.weight(1f)
                            )
                            Button(
                                onClick = { viewModel.forceSync() },
                                colors = ButtonDefaults.buttonColors(containerColor = Canopy),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Icon(Icons.Default.Sync, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Sync Now", style = MaterialTheme.typography.labelMedium)
                            }
                        }
                    }
                }
            }
        }

        // Farmer Support & Helpline
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                SectionHeader(title = "Agricultural Helplines")

                Spacer(modifier = Modifier.height(8.dp))

                GlassCard(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = 4.dp
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Call,
                                contentDescription = null,
                                tint = Sprout,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "Kisan Call Centre (Toll Free)",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = Canopy
                                )
                                Text(
                                    text = "1800-180-1551 (6 AM - 10 PM daily in 22 languages)",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Canopy.copy(alpha = 0.8f)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
