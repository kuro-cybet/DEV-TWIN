package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.FlipCameraAndroid
import androidx.compose.material.icons.filled.Healing
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.service.AgroKnowledgeBase
import com.example.ui.components.CircularProgressGauge
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
import com.example.ui.theme.HuskSurface
import com.example.ui.theme.Sprout
import com.example.ui.theme.SproutGlow
import com.example.ui.theme.StatusDanger
import com.example.ui.theme.StatusSuccess
import com.example.ui.theme.StatusWarning
import com.example.ui.theme.Terracotta
import com.example.viewmodel.AppDestination
import com.example.viewmodel.KrishiViewModel

@Composable
fun ScanScreen(
    viewModel: KrishiViewModel,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableIntStateOf(0) } // 0: Camera Scanner, 1: Scan History
    var selectedPresetKey by remember { mutableStateOf("tomato_blight") }
    var isFlashOn by remember { mutableStateOf(false) }

    val isScanning by viewModel.isScanning.collectAsState()
    val activeDiagnosis by viewModel.activeDiagnosis.collectAsState()
    val scans by viewModel.scans.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(bottom = 90.dp)
    ) {
        // Tab Header
        Surface(
            color = CanopyDark,
            modifier = Modifier.fillMaxWidth()
        ) {
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = CanopyDark,
                contentColor = Husk,
                indicator = { tabPositions ->
                    Box(
                        modifier = Modifier
                            .tabIndicatorOffset(tabPositions[selectedTab])
                            .height(3.dp)
                            .background(HarvestGold)
                    )
                }
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.PhotoCamera, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("AI Camera Diagnostic", fontWeight = FontWeight.Bold)
                        }
                    },
                    selectedContentColor = HarvestGold,
                    unselectedContentColor = Husk.copy(alpha = 0.6f)
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.History, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Scan History (${scans.size})", fontWeight = FontWeight.Bold)
                        }
                    },
                    selectedContentColor = HarvestGold,
                    unselectedContentColor = Husk.copy(alpha = 0.6f)
                )
            }
        }

        if (selectedTab == 0) {
            // Live Camera Viewport
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .background(Color(0xFF0D1C16))
            ) {
                // Viewfinder Backdrop with Crosshairs
                CameraViewfinderOverlay(isFlashOn = isFlashOn)

                // Top Controls (Flash, Switch, Mode)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color.Black.copy(alpha = 0.5f),
                        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.2f))
                    ) {
                        Text(
                            text = "AI Crop Doctor 2.0",
                            style = MaterialTheme.typography.labelMedium,
                            color = HarvestGold,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                        )
                    }

                    Row {
                        IconButton(
                            onClick = { isFlashOn = !isFlashOn },
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(if (isFlashOn) HarvestGold else Color.Black.copy(alpha = 0.5f))
                        ) {
                            Icon(
                                imageVector = Icons.Default.FlashOn,
                                contentDescription = "Flash",
                                tint = if (isFlashOn) CanopyDark else Color.White
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        IconButton(
                            onClick = { /* Switch camera */ },
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(Color.Black.copy(alpha = 0.5f))
                        ) {
                            Icon(
                                imageVector = Icons.Default.FlipCameraAndroid,
                                contentDescription = "Switch Camera",
                                tint = Color.White
                            )
                        }
                    }
                }

                // Sample Crop Presets Strip (Ideal for direct emulator testing & one-tap scanning)
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth()
                        .padding(bottom = 90.dp)
                ) {
                    Text(
                        text = "Select Crop Leaf Preset or Aim Camera:",
                        style = MaterialTheme.typography.labelMedium,
                        color = Color.White.copy(alpha = 0.85f),
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                    )

                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(AgroKnowledgeBase.diagnosticPresets) { preset ->
                            val isSelected = selectedPresetKey == preset.key
                            Surface(
                                onClick = {
                                    selectedPresetKey = preset.key
                                    viewModel.performScan(preset.key)
                                },
                                shape = RoundedCornerShape(12.dp),
                                color = if (isSelected) HarvestGold else Color.Black.copy(alpha = 0.65f),
                                border = BorderStroke(1.dp, if (isSelected) HarvestGold else Color.White.copy(alpha = 0.3f)),
                                modifier = Modifier.testTag("preset_${preset.key}")
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Eco,
                                        contentDescription = null,
                                        tint = if (isSelected) CanopyDark else SproutGlow,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = preset.cropName.split(" ").first(),
                                        style = MaterialTheme.typography.labelMedium,
                                        color = if (isSelected) CanopyDark else Color.White,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }

                // Floating Glass Bottom Capture Bar
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    GlassCard(
                        modifier = Modifier.fillMaxWidth(),
                        isDarkBg = true
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceEvenly,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 10.dp)
                        ) {
                            IconButton(onClick = { viewModel.performScan("cotton_curl") }) {
                                Icon(
                                    imageVector = Icons.Default.Image,
                                    contentDescription = "Gallery",
                                    tint = Husk,
                                    modifier = Modifier.size(26.dp)
                                )
                            }

                            // Shutter Button with Glow Orb
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .size(68.dp)
                                    .testTag("camera_shutter_button")
                                    .clickable { viewModel.performScan(selectedPresetKey) }
                            ) {
                                GlowOrb(glowColor = SproutGlow, size = 68.dp)
                                Surface(
                                    shape = CircleShape,
                                    color = Sprout,
                                    border = BorderStroke(3.dp, Color.White),
                                    modifier = Modifier.size(56.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        if (isScanning) {
                                            CircularProgressIndicator(
                                                color = Color.White,
                                                modifier = Modifier.size(24.dp),
                                                strokeWidth = 2.dp
                                            )
                                        } else {
                                            Icon(
                                                imageVector = Icons.Default.PhotoCamera,
                                                contentDescription = "Capture",
                                                tint = Color.White,
                                                modifier = Modifier.size(26.dp)
                                            )
                                        }
                                    }
                                }
                            }

                            IconButton(onClick = { viewModel.performScan("rice_blast") }) {
                                Icon(
                                    imageVector = Icons.Default.Science,
                                    contentDescription = "Laboratory",
                                    tint = HarvestGold,
                                    modifier = Modifier.size(26.dp)
                                )
                            }
                        }
                    }
                }

                // Slide-Up Glass Sheet with Diagnosis Result
                if (activeDiagnosis != null) {
                    activeDiagnosis?.let { scan ->
                        DiagnosisResultSheet(
                            scan = scan,
                            onClose = { viewModel.clearActiveDiagnosis() },
                            onAskVoice = {
                                viewModel.clearActiveDiagnosis()
                                viewModel.sendVoiceQuery("Tell me more remedies for ${scan.cropName} ${scan.diseaseName}")
                                viewModel.navigateTo(AppDestination.Voice)
                            },
                            onAddCalendarTask = {
                                viewModel.addTask(
                                    title = "Apply treatment for ${scan.diseaseName.split("(").first()}",
                                    description = scan.chemicalTreatment,
                                    category = "Spray",
                                    dueDate = "Tomorrow, 7:00 AM",
                                    priority = "Urgent"
                                )
                                viewModel.clearActiveDiagnosis()
                                viewModel.navigateTo(AppDestination.Calendar)
                            }
                        )
                    }
                }
            }
        } else {
            // Scan History List
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    SectionHeader(
                        title = "Saved Pathogen Scans",
                        badgeText = "${scans.count { !it.isResolved }} Unresolved"
                    )
                }

                if (scans.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "No crop scans recorded yet. Use the AI Camera to diagnose leaves.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                items(scans) { scan ->
                    TerraceCard(
                        accentColor = when (scan.severity.lowercase()) {
                            "severe", "high" -> StatusDanger
                            "moderate" -> StatusWarning
                            "mild" -> HarvestGold
                            else -> Sprout
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "${scan.cropName} · ${scan.diseaseName}",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = Canopy
                                    )
                                    Text(
                                        text = "Severity: ${scan.severity} · Confidence: ${scan.confidencePercent}%",
                                        style = MaterialTheme.typography.labelMedium,
                                        color = Canopy.copy(alpha = 0.7f)
                                    )
                                }

                                Surface(
                                    onClick = { viewModel.markScanResolved(scan.id, !scan.isResolved) },
                                    shape = RoundedCornerShape(12.dp),
                                    color = if (scan.isResolved) Sprout.copy(alpha = 0.15f) else StatusWarning.copy(alpha = 0.15f),
                                    border = BorderStroke(1.dp, if (scan.isResolved) Sprout else StatusWarning)
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    ) {
                                        Icon(
                                            imageVector = if (scan.isResolved) Icons.Default.CheckCircle else Icons.Default.Warning,
                                            contentDescription = null,
                                            tint = if (scan.isResolved) Sprout else StatusWarning,
                                            modifier = Modifier.size(14.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = if (scan.isResolved) "Resolved" else "Needs Action",
                                            style = MaterialTheme.typography.labelMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = if (scan.isResolved) Sprout else StatusWarning
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = "💊 ${scan.chemicalTreatment}",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Canopy.copy(alpha = 0.85f),
                                fontSize = 13.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DiagnosisResultSheet(
    scan: com.example.data.models.CropScan,
    onClose: () -> Unit,
    onAskVoice: () -> Unit,
    onAddCalendarTask: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        color = HuskCard,
        tonalElevation = 12.dp,
        border = BorderStroke(1.dp, HuskBorder),
        modifier = Modifier
            .fillMaxWidth()
            .height(520.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp)
        ) {
            // Header with Close & Confidence Ring
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = scan.cropName,
                        style = MaterialTheme.typography.labelLarge,
                        color = HarvestGold,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = scan.diseaseName,
                        style = MaterialTheme.typography.titleLarge,
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.Bold,
                        color = Canopy
                    )
                }

                CircularProgressGauge(
                    percentage = scan.confidencePercent,
                    labelText = "Match",
                    size = 64.dp,
                    strokeWidth = 6.dp
                )

                IconButton(onClick = onClose) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = Canopy
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Severity Badge
            Row(verticalAlignment = Alignment.CenterVertically) {
                val badgeColor = when (scan.severity.lowercase()) {
                    "severe" -> StatusDanger
                    "moderate" -> StatusWarning
                    "mild" -> HarvestGold
                    else -> Sprout
                }
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = badgeColor.copy(alpha = 0.15f),
                    border = BorderStroke(1.dp, badgeColor)
                ) {
                    Text(
                        text = "Severity: ${scan.severity.uppercase()}",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = badgeColor,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Immediate foliar intervention advised",
                    style = MaterialTheme.typography.labelMedium,
                    color = Canopy.copy(alpha = 0.6f)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Treatment recommendations scrollable
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    Text(
                        text = "Symptoms:",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Canopy
                    )
                    Text(
                        text = scan.symptoms,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Canopy.copy(alpha = 0.8f)
                    )
                }

                item {
                    Text(
                        text = "🧪 Chemical Treatment & Dosage:",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Canopy
                    )
                    Text(
                        text = "${scan.chemicalTreatment}\nDosage: ${scan.dosageGuideline}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Canopy.copy(alpha = 0.85f)
                    )
                }

                item {
                    Text(
                        text = "🌿 Organic & Bio-Control Remedy:",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Sprout
                    )
                    Text(
                        text = scan.organicRemedy,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Canopy.copy(alpha = 0.85f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    onClick = onAskVoice,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = Canopy)
                ) {
                    Icon(Icons.Default.Mic, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Ask AI Voice", color = Husk, fontWeight = FontWeight.Bold)
                }

                Button(
                    onClick = onAddCalendarTask,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = Sprout)
                ) {
                    Icon(Icons.Default.CalendarMonth, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Add Task", color = Color.White, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun CameraViewfinderOverlay(isFlashOn: Boolean) {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val w = size.width
        val h = size.height
        val boxWidth = w * 0.72f
        val boxHeight = h * 0.44f
        val left = (w - boxWidth) / 2
        val top = (h - boxHeight) / 2.3f

        // Draw Corner Target Brackets
        val cornerLength = 28.dp.toPx()
        val stroke = 3.5.dp.toPx()
        val cornerColor = if (isFlashOn) HarvestGold else SproutGlow

        // Top-Left
        drawLine(cornerColor, Offset(left, top), Offset(left + cornerLength, top), stroke)
        drawLine(cornerColor, Offset(left, top), Offset(left, top + cornerLength), stroke)

        // Top-Right
        drawLine(cornerColor, Offset(left + boxWidth, top), Offset(left + boxWidth - cornerLength, top), stroke)
        drawLine(cornerColor, Offset(left + boxWidth, top), Offset(left + boxWidth, top + cornerLength), stroke)

        // Bottom-Left
        drawLine(cornerColor, Offset(left, top + boxHeight), Offset(left + cornerLength, top + boxHeight), stroke)
        drawLine(cornerColor, Offset(left, top + boxHeight), Offset(left, top + boxHeight - cornerLength), stroke)

        // Bottom-Right
        drawLine(cornerColor, Offset(left + boxWidth, top + boxHeight), Offset(left + boxWidth - cornerLength, top + boxHeight), stroke)
        drawLine(cornerColor, Offset(left + boxWidth, top + boxHeight), Offset(left + boxWidth, top + boxHeight - cornerLength), stroke)

        // Center Crosshair
        val cx = w / 2
        val cy = top + (boxHeight / 2)
        drawLine(
            cornerColor.copy(alpha = 0.5f),
            Offset(cx - 16.dp.toPx(), cy),
            Offset(cx + 16.dp.toPx(), cy),
            1.5.dp.toPx()
        )
        drawLine(
            cornerColor.copy(alpha = 0.5f),
            Offset(cx, cy - 16.dp.toPx()),
            Offset(cx, cy + 16.dp.toPx()),
            1.5.dp.toPx()
        )
    }
}
