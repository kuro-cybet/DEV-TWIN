package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.LocalOffer
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.service.AgroKnowledgeBase
import com.example.ui.components.CircularProgressGauge
import com.example.ui.components.GlassCard
import com.example.ui.components.SectionHeader
import com.example.ui.components.TerraceCard
import com.example.ui.theme.Canopy
import com.example.ui.theme.HarvestGold
import com.example.ui.theme.HuskBorder
import com.example.ui.theme.HuskCard
import com.example.ui.theme.SkyMist
import com.example.ui.theme.Sprout
import com.example.viewmodel.KrishiViewModel

@Composable
fun SustainabilityScreen(
    viewModel: KrishiViewModel,
    modifier: Modifier = Modifier
) {
    val profile by viewModel.profile.collectAsState()
    val score = profile?.sustainabilityScore ?: 86

    val completedPractices = remember {
        mutableStateListOf(0, 1, 3)
    }

    val practices = listOf(
        "Micro-Irrigation / Drip fertigation installed in orchard",
        "Crop residue incorporation (Zero Stubble Burning)",
        "Organic bio-stimulants (Jeevamrutha / Trichoderma) applied",
        "Soil Health Card testing conducted within last 12 months",
        "Solar Ag Pump (PM-KUSUM) commissioned"
    )

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 16.dp)
            .padding(bottom = 90.dp),
        contentPadding = PaddingValues(vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Top Header
        item {
            Column {
                Text(
                    text = "Eco-Farming & Sustainability",
                    style = MaterialTheme.typography.displaySmall,
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = "Track water conservation, soil carbon sequestration, and subsidies",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // Hero Score Card with Circular Gauge
        item {
            GlassCard(
                modifier = Modifier.fillMaxWidth(),
                elevation = 8.dp
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(20.dp)
                ) {
                    CircularProgressGauge(
                        percentage = score,
                        labelText = "Eco Score",
                        size = 90.dp,
                        strokeWidth = 9.dp
                    )

                    Spacer(modifier = Modifier.width(16.dp))

                    Column {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Sprout.copy(alpha = 0.15f),
                            border = BorderStroke(1.dp, Sprout)
                        ) {
                            Text(
                                text = "🏆 ECO-PIONEER TIER",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = Sprout,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Top 12% in Punjab",
                            style = MaterialTheme.typography.titleLarge,
                            fontFamily = FontFamily.Serif,
                            fontWeight = FontWeight.Bold,
                            color = Canopy
                        )
                        Text(
                            text = "Eligible for premium carbon credit offsets and subsidized seeds.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Canopy.copy(alpha = 0.75f),
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }

        // 3 Key Environmental Metrics
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                EcoMetricCard(
                    title = "Water Saved",
                    value = "45,000 L",
                    subtitle = "vs flood irrigation",
                    icon = Icons.Default.WaterDrop,
                    accentColor = SkyMist,
                    modifier = Modifier.weight(1f)
                )
                EcoMetricCard(
                    title = "Chemicals Cut",
                    value = "-32%",
                    subtitle = "via targeted spray",
                    icon = Icons.Default.Eco,
                    accentColor = Sprout,
                    modifier = Modifier.weight(1f)
                )
                EcoMetricCard(
                    title = "Carbon Offset",
                    value = "4.2 Tons",
                    subtitle = "stubble conserved",
                    icon = Icons.Default.Shield,
                    accentColor = HarvestGold,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Sustainable Farming Checklist
        item {
            SectionHeader(
                title = "Green Farming Milestones",
                badgeText = "${completedPractices.size}/${practices.size} Completed"
            )
        }

        items(practices.indices.toList()) { index ->
            val isChecked = completedPractices.contains(index)
            TerraceCard(
                accentColor = if (isChecked) Sprout else HuskBorder,
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    if (isChecked) completedPractices.remove(index) else completedPractices.add(index)
                }
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(12.dp)
                ) {
                    Checkbox(
                        checked = isChecked,
                        onCheckedChange = {
                            if (it) completedPractices.add(index) else completedPractices.remove(index)
                        },
                        colors = CheckboxDefaults.colors(
                            checkedColor = Sprout,
                            checkmarkColor = Color.White
                        )
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = practices[index],
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = if (isChecked) FontWeight.SemiBold else FontWeight.Normal,
                        color = Canopy,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // Government Subsidies & Schemes Guide
        item {
            SectionHeader(
                title = "Government Schemes & Subsidies",
                badgeText = "Direct Benefit Transfer"
            )
        }

        items(AgroKnowledgeBase.governmentSchemes) { scheme ->
            TerraceCard(
                accentColor = HarvestGold,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(
                            imageVector = Icons.Default.LocalOffer,
                            contentDescription = null,
                            tint = HarvestGold,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = scheme.title,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Canopy,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = HarvestGold.copy(alpha = 0.2f)
                    ) {
                        Text(
                            text = scheme.subsidy,
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = Canopy,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = scheme.description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Canopy.copy(alpha = 0.85f),
                        fontSize = 13.sp
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "📋 How to Apply: ${scheme.howToApply}",
                        style = MaterialTheme.typography.labelMedium,
                        color = Sprout,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 12.sp
                    )
                }
            }
        }
    }
}

@Composable
fun EcoMetricCard(
    title: String,
    value: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    accentColor: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = HuskCard,
        border = BorderStroke(1.dp, HuskBorder),
        shadowElevation = 3.dp,
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = accentColor,
                modifier = Modifier.size(22.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Canopy
            )
            Text(
                text = title,
                style = MaterialTheme.typography.labelMedium,
                color = Canopy.copy(alpha = 0.7f),
                fontSize = 11.sp
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.labelMedium,
                color = accentColor,
                fontSize = 9.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}
