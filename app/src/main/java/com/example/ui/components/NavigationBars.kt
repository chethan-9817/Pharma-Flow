package com.example.ui.components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.Sell
import androidx.compose.material.icons.filled.VolunteerActivism
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.EcoEmerald
import com.example.ui.theme.EcoEmeraldDark
import com.example.ui.theme.ExpiryAmber
import com.example.ui.theme.MedBorder
import com.example.ui.theme.MedTextSecondary
import com.example.ui.theme.MedWhite
import com.example.ui.theme.MedicalBlue
import com.example.ui.theme.MedicalBlueDark

enum class AppDestination(val label: String, val icon: ImageVector, val tag: String) {
    HOME("Home", Icons.Default.MedicalServices, "nav_home"),
    ECO_DEALS("Eco-Deals", Icons.Default.Eco, "nav_eco"),
    DONATE_RETURN("Donate Hub", Icons.Default.VolunteerActivism, "nav_donate"),
    TRACK("Track", Icons.Default.LocalShipping, "nav_track"),
    REWARDS("Rewards", Icons.Default.AutoAwesome, "nav_rewards")
}

@Composable
fun AppBottomBar(
    currentDestination: AppDestination,
    onDestinationSelected: (AppDestination) -> Unit,
    unscratchedCardsCount: Int,
    nearExpiryCount: Int,
    activeListingsCount: Int = 0,
    hasActiveDelivery: Boolean,
    modifier: Modifier = Modifier
) {
    NavigationBar(
        modifier = modifier
            .fillMaxWidth()
            .border(1.dp, MedBorder, RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
            .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)),
        containerColor = MedWhite,
        tonalElevation = 8.dp
    ) {
        AppDestination.values().forEach { destination ->
            val isSelected = destination == currentDestination
            NavigationBarItem(
                selected = isSelected,
                onClick = { onDestinationSelected(destination) },
                icon = {
                    when (destination) {
                        AppDestination.REWARDS -> {
                            if (unscratchedCardsCount > 0) {
                                BadgedBox(
                                    badge = {
                                        Badge(
                                            containerColor = ExpiryAmber,
                                            contentColor = Color.White
                                        ) {
                                            Text(
                                                text = "$unscratchedCardsCount",
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 9.sp
                                            )
                                        }
                                    }
                                ) {
                                    Icon(destination.icon, contentDescription = destination.label)
                                }
                            } else {
                                Icon(destination.icon, contentDescription = destination.label)
                            }
                        }
                        AppDestination.ECO_DEALS -> {
                            if (nearExpiryCount > 0) {
                                BadgedBox(
                                    badge = {
                                        Badge(
                                            containerColor = EcoEmerald,
                                            contentColor = Color.White
                                        ) {
                                            Text(
                                                text = "50%",
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 9.sp
                                            )
                                        }
                                    }
                                ) {
                                    Icon(destination.icon, contentDescription = destination.label)
                                }
                            } else {
                                Icon(destination.icon, contentDescription = destination.label)
                            }
                        }
                        AppDestination.DONATE_RETURN -> {
                            if (activeListingsCount > 0) {
                                BadgedBox(
                                    badge = {
                                        Badge(
                                            containerColor = EcoEmeraldDark,
                                            contentColor = Color.White
                                        ) {
                                            Text(
                                                text = "$activeListingsCount",
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 9.sp
                                            )
                                        }
                                    }
                                ) {
                                    Icon(destination.icon, contentDescription = destination.label)
                                }
                            } else {
                                Icon(destination.icon, contentDescription = destination.label)
                            }
                        }
                        AppDestination.TRACK -> {
                            if (hasActiveDelivery) {
                                BadgedBox(
                                    badge = {
                                        Badge(
                                            containerColor = MedicalBlue,
                                            contentColor = Color.White
                                        ) {
                                            Text(
                                                text = "LIVE",
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 8.sp
                                            )
                                        }
                                    }
                                ) {
                                    Icon(destination.icon, contentDescription = destination.label)
                                }
                            } else {
                                Icon(destination.icon, contentDescription = destination.label)
                            }
                        }
                        else -> {
                            Icon(destination.icon, contentDescription = destination.label)
                        }
                    }
                },
                label = {
                    Text(
                        text = destination.label,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        fontSize = 10.sp,
                        maxLines = 1
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MedicalBlue,
                    unselectedIconColor = MedTextSecondary,
                    selectedTextColor = MedicalBlueDark,
                    unselectedTextColor = MedTextSecondary,
                    indicatorColor = MedicalBlue.copy(alpha = 0.15f)
                ),
                modifier = Modifier.testTag(destination.tag)
            )
        }
    }
}
