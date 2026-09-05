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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddAlert
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
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
import com.example.ui.components.GlassCard
import com.example.ui.components.SectionHeader
import com.example.ui.components.SparklineChart
import com.example.ui.components.TerraceCard
import com.example.ui.theme.Canopy
import com.example.ui.theme.HarvestGold
import com.example.ui.theme.HuskBorder
import com.example.ui.theme.HuskCard
import com.example.ui.theme.Sprout
import com.example.ui.theme.StatusDanger
import com.example.ui.theme.StatusSuccess
import com.example.ui.theme.StatusWarning
import com.example.viewmodel.KrishiViewModel

@Composable
fun MarketScreen(
    viewModel: KrishiViewModel,
    modifier: Modifier = Modifier
) {
    val commodities by viewModel.commodities.collectAsState()
    val selectedMandi by viewModel.selectedMandiName.collectAsState()
    var isAlertModalOpen by remember { mutableStateOf(false) }

    val mandis = listOf("All Mandis", "Khanna APMC", "Abohar APMC", "Kotkapura APMC", "Karnal APMC", "Lasalgaon APMC")

    val filteredCommodities = if (selectedMandi == "All Mandis") {
        commodities
    } else {
        commodities.filter { it.mandiName.equals(selectedMandi, ignoreCase = true) }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 16.dp)
            .padding(bottom = 90.dp),
        contentPadding = PaddingValues(vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Top Header
        item {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "APMC Mandi Intelligence",
                        style = MaterialTheme.typography.displaySmall,
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = "Real-time modal spot prices, 7-day sparklines & AI Sell/Hold advice",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Surface(
                    onClick = { isAlertModalOpen = true },
                    shape = RoundedCornerShape(12.dp),
                    color = HarvestGold.copy(alpha = 0.2f),
                    border = BorderStroke(1.dp, HarvestGold),
                    modifier = Modifier.testTag("set_price_alert")
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Icon(Icons.Default.AddAlert, contentDescription = null, tint = HarvestGold, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Set Alert", color = Canopy, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelMedium)
                    }
                }
            }
        }

        // Mandi Filter Chips
        item {
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(mandis) { mandi ->
                    val isSelected = mandi == selectedMandi
                    Surface(
                        onClick = { viewModel.selectMandi(mandi) },
                        shape = RoundedCornerShape(14.dp),
                        color = if (isSelected) Canopy else HuskCard,
                        border = BorderStroke(1.dp, if (isSelected) HarvestGold else HuskBorder)
                    ) {
                        Text(
                            text = mandi,
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = if (isSelected) Color.White else Canopy,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                        )
                    }
                }
            }
        }

        // Commodity Cards List
        items(filteredCommodities) { item ->
            val isPositive = item.priceChange >= 0
            val suggestionColor = when (item.trendSuggestion) {
                "SELL NOW" -> Sprout
                "HOLD (RISING)" -> HarvestGold
                else -> StatusWarning
            }

            GlassCard(
                modifier = Modifier.fillMaxWidth(),
                elevation = 4.dp
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    // Top Row: Commodity name & Distance
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = item.commodity,
                                style = MaterialTheme.typography.titleLarge,
                                fontFamily = FontFamily.Serif,
                                fontWeight = FontWeight.Bold,
                                color = Canopy
                            )
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.LocationOn,
                                    contentDescription = null,
                                    tint = Canopy.copy(alpha = 0.6f),
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(2.dp))
                                Text(
                                    text = "${item.mandiName} (${item.distanceKm} km)",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = Canopy.copy(alpha = 0.65f)
                                )
                            }
                        }

                        // AI Recommendation Badge
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = suggestionColor.copy(alpha = 0.15f),
                            border = BorderStroke(1.dp, suggestionColor)
                        ) {
                            Text(
                                text = item.trendSuggestion,
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = suggestionColor,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Price & Sparkline
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column {
                            Text(
                                text = "₹${item.currentPrice}",
                                style = MaterialTheme.typography.displayMedium,
                                fontWeight = FontWeight.Bold,
                                color = Canopy
                            )
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = if (isPositive) Icons.Default.ArrowUpward else Icons.Default.ArrowDownward,
                                    contentDescription = null,
                                    tint = if (isPositive) Sprout else StatusDanger,
                                    modifier = Modifier.size(14.dp)
                                )
                                Text(
                                    text = "${if (isPositive) "+" else ""}₹${item.priceChange}/q today",
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isPositive) Sprout else StatusDanger
                                )
                            }
                        }

                        Spacer(modifier = Modifier.weight(1f))

                        // 7-day sparkline
                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = "7-Day Trend",
                                style = MaterialTheme.typography.labelMedium,
                                fontSize = 10.sp,
                                color = Canopy.copy(alpha = 0.5f)
                            )
                            SparklineChart(
                                valuesString = item.sparklineValues,
                                isPositive = isPositive,
                                height = 36.dp,
                                width = 100.dp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // AI Rational Breakdown
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = HuskBorder.copy(alpha = 0.35f),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(10.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.TrendingUp,
                                contentDescription = null,
                                tint = HarvestGold,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = item.trendReason,
                                style = MaterialTheme.typography.bodyMedium,
                                color = Canopy.copy(alpha = 0.85f),
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            }
        }
    }

    if (isAlertModalOpen) {
        PriceAlertDialog(
            onDismiss = { isAlertModalOpen = false },
            onConfirm = { isAlertModalOpen = false }
        )
    }
}

@Composable
fun PriceAlertDialog(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    var targetPrice by remember { mutableStateOf("2600") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Set Mandi Price Alert", fontWeight = FontWeight.Bold, fontFamily = FontFamily.Serif)
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "Notify me via notification when Wheat (HD-2967) crosses target price in Khanna APMC.",
                    style = MaterialTheme.typography.bodyMedium
                )
                OutlinedTextField(
                    value = targetPrice,
                    onValueChange = { targetPrice = it },
                    label = { Text("Target Price (₹ / quintal)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(containerColor = Sprout)
            ) {
                Text("Activate Alert", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
