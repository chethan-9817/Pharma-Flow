package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.LocalOffer
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material.icons.filled.VolumeOff
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.BrandAmber
import com.example.ui.theme.BrandCyan
import com.example.ui.theme.BrandEmerald
import com.example.ui.theme.DarkBg
import com.example.ui.theme.DarkBorder
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceElevated

enum class AppDestination(val label: String, val icon: ImageVector, val tag: String) {
    SHOP("Shop", Icons.Default.Storefront, "nav_shop"),
    SCRATCH("Scratch", Icons.Default.AutoAwesome, "nav_scratch"),
    QUESTS("Quests", Icons.Default.EmojiEvents, "nav_quests"),
    ORDERS("Orders", Icons.Default.ReceiptLong, "nav_orders")
}

@Composable
fun AppTopBar(
    searchQuery: String,
    onSearchChanged: (String) -> Unit,
    unscratchedCount: Int,
    cartItemCount: Int,
    wishlistCount: Int,
    isSoundEnabled: Boolean,
    onToggleSound: () -> Unit,
    onWishlistClicked: () -> Unit,
    onCartClicked: () -> Unit,
    onScratchClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        color = DarkBg,
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Brand Logo & Title
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = BrandEmerald,
                    modifier = Modifier.size(36.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.LocalOffer,
                            contentDescription = null,
                            tint = Color.Black,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = "SCRATCHMART",
                        fontWeight = FontWeight.Black,
                        fontSize = 17.sp,
                        color = Color.White,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "Shop & Scratch Rewards",
                        fontSize = 11.sp,
                        color = Color(0xFF94A3B8)
                    )
                }
            }

            // Quick Badges: Sound Toggle, Wishlist, Cards & Cart
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                // Sound Mute Toggle
                IconButton(
                    onClick = onToggleSound,
                    modifier = Modifier.size(36.dp).testTag("sound_toggle_button")
                ) {
                    Icon(
                        imageVector = if (isSoundEnabled) Icons.Default.VolumeUp else Icons.Default.VolumeOff,
                        contentDescription = "Toggle Audio",
                        tint = if (isSoundEnabled) BrandEmerald else Color(0xFF64748B),
                        modifier = Modifier.size(20.dp)
                    )
                }

                // Wishlist Badge
                IconButton(
                    onClick = onWishlistClicked,
                    modifier = Modifier.size(36.dp).testTag("top_bar_wishlist_button")
                ) {
                    BadgedBox(
                        badge = {
                            if (wishlistCount > 0) {
                                Badge(
                                    containerColor = Color(0xFFEF4444),
                                    contentColor = Color.White
                                ) {
                                    Text(
                                        text = "$wishlistCount",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 10.sp
                                    )
                                }
                            }
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Favorite,
                            contentDescription = "Wishlist",
                            tint = if (wishlistCount > 0) Color(0xFFEF4444) else Color(0xFF94A3B8),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                // Scratch card badge pill
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = BrandAmber.copy(alpha = 0.15f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, BrandAmber.copy(alpha = 0.4f)),
                    modifier = Modifier
                        .clickable { onScratchClicked() }
                        .testTag("top_bar_cards_badge")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = "Available cards",
                            tint = BrandAmber,
                            modifier = Modifier.size(15.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "$unscratchedCount",
                            color = BrandAmber,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }
                }

                // Cart Icon with Badge
                IconButton(
                    onClick = onCartClicked,
                    modifier = Modifier.testTag("top_bar_cart_button")
                ) {
                    BadgedBox(
                        badge = {
                            if (cartItemCount > 0) {
                                Badge(
                                    containerColor = BrandEmerald,
                                    contentColor = Color.Black
                                ) {
                                    Text(
                                        text = "$cartItemCount",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 11.sp
                                    )
                                }
                            }
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.ShoppingBag,
                            contentDescription = "Shopping Cart",
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun AppBottomBar(
    currentDestination: AppDestination,
    onDestinationSelected: (AppDestination) -> Unit,
    unscratchedCardsCount: Int,
    modifier: Modifier = Modifier
) {
    NavigationBar(
        modifier = modifier
            .fillMaxWidth()
            .border(1.dp, DarkBorder, RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
            .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)),
        containerColor = DarkSurface,
        tonalElevation = 8.dp
    ) {
        AppDestination.values().forEach { destination ->
            val isSelected = destination == currentDestination
            NavigationBarItem(
                selected = isSelected,
                onClick = { onDestinationSelected(destination) },
                icon = {
                    if (destination == AppDestination.SCRATCH && unscratchedCardsCount > 0) {
                        BadgedBox(
                            badge = {
                                Badge(
                                    containerColor = BrandAmber,
                                    contentColor = Color.Black
                                ) {
                                    Text(
                                        text = "$unscratchedCardsCount",
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        ) {
                            Icon(destination.icon, contentDescription = destination.label)
                        }
                    } else {
                        Icon(destination.icon, contentDescription = destination.label)
                    }
                },
                label = {
                    Text(
                        text = destination.label,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        fontSize = 12.sp
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Color.Black,
                    unselectedIconColor = Color(0xFF94A3B8),
                    selectedTextColor = BrandEmerald,
                    unselectedTextColor = Color(0xFF94A3B8),
                    indicatorColor = BrandEmerald
                ),
                modifier = Modifier.testTag(destination.tag)
            )
        }
    }
}
