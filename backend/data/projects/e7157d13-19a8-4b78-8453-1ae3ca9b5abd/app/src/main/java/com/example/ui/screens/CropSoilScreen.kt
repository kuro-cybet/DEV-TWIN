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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Agriculture
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.service.AgroKnowledgeBase
import com.example.ui.components.GlassCard
import com.example.ui.components.NutrientRadialMetric
import com.example.ui.components.SectionHeader
import com.example.ui.components.TerraceCard
import com.example.ui.theme.Canopy
import com.example.ui.theme.HarvestGold
import com.example.ui.theme.HuskBorder
import com.example.ui.theme.HuskCard
import com.example.ui.theme.HuskSurface
import com.example.ui.theme.SkyMist
import com.example.ui.theme.Sprout
import com.example.ui.theme.StatusWarning
import com.example.ui.theme.Terracotta
import com.example.viewmodel.KrishiViewModel

@Composable
fun CropSoilScreen(
    viewModel: KrishiViewModel,
    modifier: Modifier = Modifier
) {
    val fields by viewModel.fields.collectAsState()
    val selectedFieldId by viewModel.selectedFieldId.collectAsState()
    val selectedField = fields.find { it.id == selectedFieldId } ?: fields.firstOrNull()

    var isAddFieldOpen by remember { mutableStateOf(false) }

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
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Crop & Soil Health",
                        style = MaterialTheme.typography.displaySmall,
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = "Real-time NPK nutrients, soil moisture, and phenological stage advice",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Surface(
                    onClick = { isAddFieldOpen = true },
                    shape = RoundedCornerShape(12.dp),
                    color = Sprout,
                    modifier = Modifier.testTag("add_field_button")
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Add Field", color = Color.White, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelMedium)
                    }
                }
            }
        }

        // Plot / Field Selector Glass Tabs
        item {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(fields) { field ->
                    val isSelected = field.id == selectedField?.id
                    Surface(
                        onClick = { viewModel.selectField(field.id) },
                        shape = RoundedCornerShape(14.dp),
                        color = if (isSelected) Canopy else HuskCard,
                        border = BorderStroke(1.dp, if (isSelected) HarvestGold else HuskBorder),
                        modifier = Modifier.testTag("field_tab_${field.id}")
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Agriculture,
                                contentDescription = null,
                                tint = if (isSelected) HarvestGold else Canopy.copy(alpha = 0.6f),
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = field.name,
                                    style = MaterialTheme.typography.labelLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSelected) Color.White else Canopy
                                )
                                Text(
                                    text = "${field.cropType} (${field.areaAcres} Ac)",
                                    style = MaterialTheme.typography.labelMedium,
                                    fontSize = 11.sp,
                                    color = if (isSelected) Color.White.copy(alpha = 0.7f) else Canopy.copy(alpha = 0.6f)
                                )
                            }
                        }
                    }
                }
            }
        }

        selectedField?.let { field ->
            // Soil Health Card & Radial Gauges
            item {
                GlassCard(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = 6.dp
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Science,
                                contentDescription = null,
                                tint = Terracotta,
                                modifier = Modifier.size(22.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Soil Nutrient Matrix (ICAR Lab Tested)",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = Canopy
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // NPK Radial Metric Row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            NutrientRadialMetric(
                                nutrientName = "N",
                                level = field.nitrogenLevel
                            )
                            NutrientRadialMetric(
                                nutrientName = "P",
                                level = field.phosphorusLevel
                            )
                            NutrientRadialMetric(
                                nutrientName = "K",
                                level = field.potassiumLevel
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Secondary Parameters (pH, Carbon, Moisture)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            SecondaryMetricPill(
                                label = "Soil pH",
                                value = "${field.soilPh} (Neutral)",
                                color = Sprout,
                                modifier = Modifier.weight(1f)
                            )
                            SecondaryMetricPill(
                                label = "Org. Carbon",
                                value = "${field.organicCarbonPercent}%",
                                color = HarvestGold,
                                modifier = Modifier.weight(1f)
                            )
                            SecondaryMetricPill(
                                label = "Moisture",
                                value = "${field.moisturePercent}%",
                                color = SkyMist,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }

            // Crop-Stage Phenological Timeline
            item {
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
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Crop Growth Pipeline: ${field.cropType}",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = Canopy
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        val stages = listOf("Sowing", "Vegetative", "Flowering", "Grain Filling", "Harvesting")
                        val currentStageIndex = stages.indexOf(field.stage).coerceAtLeast(0)

                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            stages.forEachIndexed { index, stageName ->
                                val isDone = index < currentStageIndex
                                val isCurrent = index == currentStageIndex

                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Box(
                                        contentAlignment = Alignment.Center,
                                        modifier = Modifier
                                            .size(22.dp)
                                            .clip(CircleShape)
                                            .background(
                                                when {
                                                    isDone -> Sprout
                                                    isCurrent -> HarvestGold
                                                    else -> HuskBorder
                                                }
                                            )
                                    ) {
                                        if (isDone) {
                                            Icon(
                                                imageVector = Icons.Default.CheckCircle,
                                                contentDescription = null,
                                                tint = Color.White,
                                                modifier = Modifier.size(16.dp)
                                            )
                                        } else {
                                            Text(
                                                text = "${index + 1}",
                                                style = MaterialTheme.typography.labelMedium,
                                                fontSize = 11.sp,
                                                color = if (isCurrent) Canopy else Color.Gray,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.width(10.dp))

                                    Text(
                                        text = stageName,
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Normal,
                                        color = if (isCurrent) Canopy else Canopy.copy(alpha = 0.6f),
                                        modifier = Modifier.weight(1f)
                                    )

                                    if (isCurrent) {
                                        Surface(
                                            shape = RoundedCornerShape(8.dp),
                                            color = HarvestGold.copy(alpha = 0.2f),
                                            border = BorderStroke(1.dp, HarvestGold)
                                        ) {
                                            Text(
                                                text = "ACTIVE STAGE",
                                                style = MaterialTheme.typography.labelMedium,
                                                fontWeight = FontWeight.Bold,
                                                color = Canopy,
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                                fontSize = 10.sp
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Fertilizer & Input Recommendation Engine
            item {
                GlassCard(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = 6.dp
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.WaterDrop,
                                contentDescription = null,
                                tint = Sprout,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Recommended Nutrition & Inputs",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = Canopy
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        val recommendation = AgroKnowledgeBase.calculateFertilizerDose(
                            crop = field.cropType,
                            stage = field.stage,
                            nLevel = field.nitrogenLevel,
                            pLevel = field.phosphorusLevel,
                            kLevel = field.potassiumLevel
                        )

                        Text(
                            text = recommendation,
                            style = MaterialTheme.typography.bodyMedium,
                            color = Canopy.copy(alpha = 0.88f),
                            lineHeight = 22.sp
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = "💡 Pro-Tip: Incorporate 2 tonnes/acre of well-decomposed Farm Yard Manure (FYM) before next seasonal sowing.",
                            style = MaterialTheme.typography.labelMedium,
                            color = Terracotta,
                            lineHeight = 18.sp
                        )
                    }
                }
            }
        }
    }

    if (isAddFieldOpen) {
        AddPlotDialog(
            onDismiss = { isAddFieldOpen = false },
            onConfirm = { name, acres, crop, stage ->
                viewModel.addField(name, acres, crop, stage)
                isAddFieldOpen = false
            }
        )
    }
}

@Composable
fun SecondaryMetricPill(
    label: String,
    value: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = HuskSurface,
        border = BorderStroke(1.dp, HuskBorder),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                fontSize = 11.sp,
                color = Canopy.copy(alpha = 0.6f)
            )
            Text(
                text = value,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                color = color
            )
        }
    }
}

@Composable
fun AddPlotDialog(
    onDismiss: () -> Unit,
    onConfirm: (name: String, acres: Double, crop: String, stage: String) -> Unit
) {
    var plotName by remember { mutableStateOf("") }
    var plotAcres by remember { mutableStateOf("2.5") }
    var cropType by remember { mutableStateOf("Wheat") }
    var stage by remember { mutableStateOf("Sowing") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Register New Farm Plot", fontWeight = FontWeight.Bold, fontFamily = FontFamily.Serif)
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = plotName,
                    onValueChange = { plotName = it },
                    label = { Text("Plot Name (e.g. South Canal Farm)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = plotAcres,
                    onValueChange = { plotAcres = it },
                    label = { Text("Area (Acres)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = cropType,
                    onValueChange = { cropType = it },
                    label = { Text("Crop Variety (e.g. Mustard, Wheat)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val acres = plotAcres.toDoubleOrNull() ?: 1.0
                    onConfirm(plotName.ifBlank { "New Field Plot" }, acres, cropType, stage)
                },
                colors = ButtonDefaults.buttonColors(containerColor = Sprout)
            ) {
                Text("Save Plot", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
