package com.example.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Agriculture
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
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
import com.example.ui.theme.GoldGlow
import com.example.ui.theme.HarvestGold
import com.example.ui.theme.Husk
import com.example.ui.theme.HuskBorder
import com.example.ui.theme.HuskCard
import com.example.ui.theme.Sprout
import com.example.ui.theme.SproutGlow
import com.example.viewmodel.AppDestination
import com.example.viewmodel.KrishiViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    viewModel: KrishiViewModel
) {
    val currentDestination by viewModel.currentDestination.collectAsState()
    val isMoreSheetOpen by viewModel.isMoreSheetOpen.collectAsState()
    val isLanguageDialogOpen by viewModel.isLanguageDialogOpen.collectAsState()
    val profile by viewModel.profile.collectAsState()
    val lang = profile?.languageCode ?: "en"

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding(),
        bottomBar = {
            if (currentDestination != AppDestination.Onboarding) {
                FloatingGlassDock(
                    currentDestination = currentDestination,
                    lang = lang,
                    onNavigate = { dest -> viewModel.navigateTo(dest) },
                    onMoreClick = { viewModel.setMoreSheetOpen(true) }
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            AnimatedContent(
                targetState = currentDestination,
                transitionSpec = { fadeIn() togetherWith fadeOut() },
                label = "ScreenTransition"
            ) { destination ->
                when (destination) {
                    AppDestination.Home -> HomeScreen(viewModel = viewModel)
                    AppDestination.Voice -> VoiceScreen(viewModel = viewModel)
                    AppDestination.Scan -> ScanScreen(viewModel = viewModel)
                    AppDestination.CropSoil -> CropSoilScreen(viewModel = viewModel)
                    AppDestination.Calendar -> CalendarScreen(viewModel = viewModel)
                    AppDestination.Sustainability -> SustainabilityScreen(viewModel = viewModel)
                    AppDestination.Weather -> WeatherScreen(viewModel = viewModel)
                    AppDestination.Market -> MarketScreen(viewModel = viewModel)
                    AppDestination.Profile -> ProfileScreen(viewModel = viewModel)
                    AppDestination.Onboarding -> OnboardingScreen(viewModel = viewModel)
                }
            }
        }
    }

    // More Sheet for Secondary Destinations
    if (isMoreSheetOpen) {
        ModalBottomSheet(
            onDismissRequest = { viewModel.setMoreSheetOpen(false) },
            sheetState = sheetState,
            containerColor = HuskCard
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .padding(bottom = 36.dp)
            ) {
                Text(
                    text = "Agricultural Modules",
                    style = MaterialTheme.typography.titleLarge,
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Bold,
                    color = Canopy
                )

                Spacer(modifier = Modifier.height(16.dp))

                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    item {
                        MoreTile("Agro Calendar", "Weather tasks", Icons.Default.CalendarMonth, Sprout) {
                            viewModel.navigateTo(AppDestination.Calendar)
                        }
                    }
                    item {
                        MoreTile("Eco Farming", "Subsidies & Score", Icons.Default.Eco, Sprout) {
                            viewModel.navigateTo(AppDestination.Sustainability)
                        }
                    }
                    item {
                        MoreTile("Agro Weather", "Spray safety", Icons.Default.Cloud, HarvestGold) {
                            viewModel.navigateTo(AppDestination.Weather)
                        }
                    }
                    item {
                        MoreTile("Mandi Intelligence", "Spot rates & advice", Icons.Default.Storefront, HarvestGold) {
                            viewModel.navigateTo(AppDestination.Market)
                        }
                    }
                    item {
                        MoreTile("Farmer Profile", "Settings & Helplines", Icons.Default.Person, Canopy) {
                            viewModel.navigateTo(AppDestination.Profile)
                        }
                    }
                    item {
                        MoreTile("Languages", "8 Regional Languages", Icons.Default.Language, Canopy) {
                            viewModel.setMoreSheetOpen(false)
                            viewModel.setLanguageDialogOpen(true)
                        }
                    }
                }
            }
        }
    }

    // Language Selector Dialog
    if (isLanguageDialogOpen) {
        LanguageDialog(
            currentLang = lang,
            onDismiss = { viewModel.setLanguageDialogOpen(false) },
            onSelect = { langCode -> viewModel.setLanguage(langCode) }
        )
    }
}

@Composable
fun FloatingGlassDock(
    currentDestination: AppDestination,
    lang: String,
    onNavigate: (AppDestination) -> Unit,
    onMoreClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        GlassCard(
            modifier = Modifier.fillMaxWidth(),
            isDarkBg = true,
            elevation = 10.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                DockItem(
                    label = Translations.get("nav_home", lang),
                    icon = Icons.Default.Home,
                    isSelected = currentDestination == AppDestination.Home,
                    testTag = "nav_tab_home",
                    onClick = { onNavigate(AppDestination.Home) }
                )

                DockItem(
                    label = Translations.get("nav_voice", lang),
                    icon = Icons.Default.Mic,
                    isSelected = currentDestination == AppDestination.Voice,
                    testTag = "nav_tab_voice",
                    onClick = { onNavigate(AppDestination.Voice) }
                )

                // Center Raised AI Doctor Scan Button
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(54.dp)
                        .testTag("nav_tab_scan")
                        .clickable { onNavigate(AppDestination.Scan) }
                ) {
                    if (currentDestination == AppDestination.Scan) {
                        GlowOrb(glowColor = GoldGlow, size = 54.dp)
                    }
                    Surface(
                        shape = CircleShape,
                        color = if (currentDestination == AppDestination.Scan) HarvestGold else Sprout,
                        shadowElevation = 6.dp,
                        border = BorderStroke(2.dp, Color.White),
                        modifier = Modifier.size(46.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.CameraAlt,
                                contentDescription = "Scan",
                                tint = if (currentDestination == AppDestination.Scan) CanopyDark else Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                }

                DockItem(
                    label = Translations.get("nav_crop_soil", lang),
                    icon = Icons.Default.Agriculture,
                    isSelected = currentDestination == AppDestination.CropSoil,
                    testTag = "nav_tab_crop_soil",
                    onClick = { onNavigate(AppDestination.CropSoil) }
                )

                DockItem(
                    label = Translations.get("nav_more", lang),
                    icon = Icons.Default.MoreHoriz,
                    isSelected = currentDestination in listOf(
                        AppDestination.Calendar,
                        AppDestination.Sustainability,
                        AppDestination.Weather,
                        AppDestination.Market,
                        AppDestination.Profile
                    ),
                    testTag = "nav_tab_more",
                    onClick = onMoreClick
                )
            }
        }
    }
}

@Composable
fun DockItem(
    label: String,
    icon: ImageVector,
    isSelected: Boolean,
    testTag: String,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clickable(onClick = onClick)
            .padding(horizontal = 8.dp, vertical = 4.dp)
            .testTag(testTag)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = if (isSelected) HarvestGold else Husk.copy(alpha = 0.6f),
            modifier = Modifier.size(22.dp)
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = if (isSelected) HarvestGold else Husk.copy(alpha = 0.6f),
            fontSize = 10.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
        )
    }
}

@Composable
fun MoreTile(
    title: String,
    subtitle: String,
    icon: ImageVector,
    accentColor: Color,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(14.dp),
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, HuskBorder),
        shadowElevation = 2.dp
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(12.dp)
        ) {
            Surface(
                shape = CircleShape,
                color = accentColor.copy(alpha = 0.15f),
                modifier = Modifier.size(36.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(imageVector = icon, contentDescription = null, tint = accentColor, modifier = Modifier.size(20.dp))
                }
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Canopy,
                    fontSize = 13.sp
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.labelMedium,
                    color = Canopy.copy(alpha = 0.6f),
                    fontSize = 10.sp
                )
            }
        }
    }
}

@Composable
fun LanguageDialog(
    currentLang: String,
    onDismiss: () -> Unit,
    onSelect: (String) -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Select Language / भाषा चुनें",
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Serif
            )
        },
        text = {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(Translations.supportedLanguages) { langItem ->
                    val isSelected = currentLang == langItem.code
                    Surface(
                        onClick = { onSelect(langItem.code) },
                        shape = RoundedCornerShape(10.dp),
                        color = if (isSelected) HarvestGold else HuskCard,
                        border = BorderStroke(1.dp, if (isSelected) HarvestGold else HuskBorder)
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.padding(vertical = 10.dp)
                        ) {
                            Text(
                                text = langItem.nativeName,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = if (isSelected) CanopyDark else Canopy
                            )
                            Text(
                                text = langItem.name,
                                style = MaterialTheme.typography.labelMedium,
                                color = if (isSelected) CanopyDark.copy(alpha = 0.7f) else Canopy.copy(alpha = 0.6f)
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Close")
            }
        }
    )
}
