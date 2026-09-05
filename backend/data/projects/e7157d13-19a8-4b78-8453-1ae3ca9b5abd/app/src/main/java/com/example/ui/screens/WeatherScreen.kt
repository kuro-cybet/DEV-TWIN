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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Air
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.Grain
import androidx.compose.material.icons.filled.Thermostat
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.Icon
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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
import com.example.ui.theme.SkyMist
import com.example.ui.theme.Sprout
import com.example.ui.theme.StatusWarning
import com.example.viewmodel.KrishiViewModel

@Composable
fun WeatherScreen(
    viewModel: KrishiViewModel,
    modifier: Modifier = Modifier
) {
    val profile by viewModel.profile.collectAsState()
    val district = profile?.district ?: "Ludhiana"
    val state = profile?.state ?: "Punjab"

    val hourlyForecast = listOf(
        HourlyItem("6 AM", "21°C", Icons.Default.WbSunny, 5),
        HourlyItem("9 AM", "25°C", Icons.Default.WbSunny, 10),
        HourlyItem("12 PM", "29°C", Icons.Default.Cloud, 20),
        HourlyItem("3 PM", "31°C", Icons.Default.Cloud, 35),
        HourlyItem("6 PM", "27°C", Icons.Default.Grain, 60),
        HourlyItem("9 PM", "24°C", Icons.Default.Grain, 80)
    )

    val dailyForecast = listOf(
        DailyItem("Today (Tue)", "Partly Cloudy", "31°", "19°", 15, "Safe for foliar spray (Wind: 7 km/h)"),
        DailyItem("Wed (Tomorrow)", "Thunderstorm Alert", "27°", "18°", 85, "Postpone spray & irrigation · High wind (24 km/h)"),
        DailyItem("Thu", "Scattered Showers", "26°", "17°", 70, "Soil saturation high · Check drainage channels"),
        DailyItem("Fri", "Clear Sky", "28°", "18°", 10, "Ideal window for post-rain fungicide spray"),
        DailyItem("Sat", "Sunny & Warm", "30°", "19°", 5, "Resume normal tubewell irrigation cycle"),
        DailyItem("Sun", "Sunny", "31°", "20°", 5, "Favorable weather for field operations"),
        DailyItem("Mon", "Clear", "32°", "20°", 10, "Optimal crop growth conditions")
    )

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(bottom = 90.dp),
        contentPadding = PaddingValues(bottom = 24.dp)
    ) {
        // Dynamic Sky Gradient Hero
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(bottomStart = 28.dp, bottomEnd = 28.dp))
                    .background(
                        Brush.verticalGradient(
                            listOf(Color(0xFF1E3C4E), Color(0xFF2E6375), SkyMist)
                        )
                    )
                    .padding(20.dp)
            ) {
                Column {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "$district, $state",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                text = "Updated: 10 mins ago via IMD Agromet",
                                style = MaterialTheme.typography.labelMedium,
                                color = Color.White.copy(alpha = 0.8f)
                            )
                        }

                        Icon(
                            imageVector = Icons.Default.WbSunny,
                            contentDescription = null,
                            tint = HarvestGold,
                            modifier = Modifier.size(48.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        verticalAlignment = Alignment.Bottom,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "28°",
                            style = MaterialTheme.typography.displayLarge,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            fontSize = 68.sp
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.padding(bottom = 12.dp)) {
                            Text(
                                text = "Partly Cloudy",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = Husk
                            )
                            Text(
                                text = "High 31° · Low 19°",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.White.copy(alpha = 0.85f)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // 3 Metric Pills
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        WeatherPill("Humidity", "64%", Icons.Default.WaterDrop, Modifier.weight(1f))
                        WeatherPill("Wind", "7 km/h", Icons.Default.Air, Modifier.weight(1f))
                        WeatherPill("Rain Prob.", "15%", Icons.Default.Grain, Modifier.weight(1f))
                    }
                }
            }
        }

        // Spray Safety Index Card
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .padding(top = 16.dp)
            ) {
                GlassCard(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = 6.dp
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = Sprout,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "Spray Safety Index: SAFE (Morning Window)",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = Sprout
                                )
                                Text(
                                    text = "Wind < 10 km/h and rain probability < 20% until 2 PM",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Canopy.copy(alpha = 0.75f)
                                )
                            }
                        }
                    }
                }
            }
        }

        // Hourly Scroller
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                SectionHeader(title = "Hourly Farm Forecast")

                Spacer(modifier = Modifier.height(8.dp))

                LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    items(hourlyForecast) { item ->
                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = HuskCard,
                            border = BorderStroke(1.dp, HuskBorder),
                            modifier = Modifier.width(72.dp)
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.padding(vertical = 12.dp)
                            ) {
                                Text(
                                    text = item.time,
                                    style = MaterialTheme.typography.labelMedium,
                                    color = Canopy.copy(alpha = 0.7f)
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Icon(
                                    imageVector = item.icon,
                                    contentDescription = null,
                                    tint = HarvestGold,
                                    modifier = Modifier.size(22.dp)
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = item.temp,
                                    style = MaterialTheme.typography.labelLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = Canopy
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "${item.rainProb}%",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = SkyMist,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }
                }
            }
        }

        // 7-Day Daily Forecast List
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                SectionHeader(title = "7-Day Agronomic Forecast")

                Spacer(modifier = Modifier.height(8.dp))

                dailyForecast.forEach { item ->
                    TerraceCard(
                        accentColor = if (item.rainProb > 50) SkyMist else HarvestGold,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = item.day,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = Canopy,
                                    modifier = Modifier.weight(1f)
                                )

                                Text(
                                    text = "🌧️ ${item.rainProb}%",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = SkyMist,
                                    fontWeight = FontWeight.Bold
                                )

                                Spacer(modifier = Modifier.width(12.dp))

                                Text(
                                    text = "${item.maxTemp} / ${item.minTemp}",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = Canopy
                                )
                            }

                            Spacer(modifier = Modifier.height(4.dp))

                            Text(
                                text = item.agriAdvice,
                                style = MaterialTheme.typography.bodyMedium,
                                color = Canopy.copy(alpha = 0.8f),
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

data class HourlyItem(
    val time: String,
    val temp: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val rainProb: Int
)

data class DailyItem(
    val day: String,
    val condition: String,
    val maxTemp: String,
    val minTemp: String,
    val rainProb: Int,
    val agriAdvice: String
)

@Composable
fun WeatherPill(
    label: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = Color.Black.copy(alpha = 0.25f),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.2f)),
        modifier = modifier
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Husk,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Column {
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelMedium,
                    fontSize = 10.sp,
                    color = Husk.copy(alpha = 0.75f)
                )
                Text(
                    text = value,
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
    }
}
