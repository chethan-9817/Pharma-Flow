package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.components.AppBottomBar
import com.example.ui.components.AppDestination
import com.example.ui.components.AppTopBar
import com.example.ui.components.ConfettiEffect
import com.example.ui.screens.CartSheet
import com.example.ui.screens.OrderCelebrationDialog
import com.example.ui.screens.OrderTrackingSheet
import com.example.ui.screens.OrdersScreen
import com.example.ui.screens.QuestsScreen
import com.example.ui.screens.ScratchScreen
import com.example.ui.screens.ShopScreen
import com.example.ui.theme.BrandAmber
import com.example.ui.theme.BrandEmerald
import com.example.ui.theme.DarkBg
import com.example.ui.theme.DarkSurfaceElevated
import com.example.ui.theme.ScratchMartTheme
import com.example.ui.viewmodel.StoreViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: StoreViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            ScratchMartTheme {
                ScratchMartApp(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun ScratchMartApp(viewModel: StoreViewModel) {
    var currentDestination by remember { mutableStateOf(AppDestination.SHOP) }
    var isCartOpen by remember { mutableStateOf(false) }

    // State collections
    val products by viewModel.filteredProducts.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val selectedCategory by viewModel.selectedCategory.collectAsStateWithLifecycle()
    val scratchCards by viewModel.scratchCards.collectAsStateWithLifecycle()
    val unscratchedCount by viewModel.unscratchedCount.collectAsStateWithLifecycle()
    val availableCoupons by viewModel.availableCoupons.collectAsStateWithLifecycle()
    val allOrders by viewModel.allOrders.collectAsStateWithLifecycle()
    val quests by viewModel.quests.collectAsStateWithLifecycle()
    val activeScratchingCard by viewModel.activeScratchingCard.collectAsStateWithLifecycle()
    val cartItemsMap by viewModel.cartItems.collectAsStateWithLifecycle()
    val selectedCoupon by viewModel.selectedCoupon.collectAsStateWithLifecycle()
    val lastCompletedOrder by viewModel.lastCompletedOrder.collectAsStateWithLifecycle()
    val questRewardWon by viewModel.questRewardWon.collectAsStateWithLifecycle()
    val wishlistIds by viewModel.wishlistIds.collectAsStateWithLifecycle()
    val wishlistCount by viewModel.wishlistCount.collectAsStateWithLifecycle()
    val isSoundEnabled by viewModel.isSoundEnabled.collectAsStateWithLifecycle()
    val selectedOrderForTracking by viewModel.selectedOrderForTracking.collectAsStateWithLifecycle()

    val cartItemsList = cartItemsMap.values.toList()
    val cartItemCount = cartItemsList.sumOf { it.quantity }

    // Back button handling
    BackHandler(enabled = isCartOpen || selectedOrderForTracking != null || currentDestination != AppDestination.SHOP) {
        if (selectedOrderForTracking != null) {
            viewModel.closeOrderTracking()
        } else if (isCartOpen) {
            isCartOpen = false
        } else if (currentDestination != AppDestination.SHOP) {
            currentDestination = AppDestination.SHOP
        }
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBg)
            .statusBarsPadding()
            .navigationBarsPadding(),
        topBar = {
            AppTopBar(
                searchQuery = searchQuery,
                onSearchChanged = { viewModel.setSearchQuery(it) },
                unscratchedCount = unscratchedCount,
                cartItemCount = cartItemCount,
                wishlistCount = wishlistCount,
                isSoundEnabled = isSoundEnabled,
                onToggleSound = { viewModel.toggleSound() },
                onWishlistClicked = {
                    currentDestination = AppDestination.SHOP
                    viewModel.setSelectedCategory("❤️ Wishlist")
                },
                onCartClicked = { isCartOpen = true },
                onScratchClicked = { currentDestination = AppDestination.SCRATCH }
            )
        },
        bottomBar = {
            AppBottomBar(
                currentDestination = currentDestination,
                onDestinationSelected = { currentDestination = it },
                unscratchedCardsCount = unscratchedCount
            )
        },
        containerColor = DarkBg
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (currentDestination) {
                AppDestination.SHOP -> {
                    ShopScreen(
                        products = products,
                        wishlistIds = wishlistIds,
                        onToggleWishlist = { viewModel.toggleWishlist(it) },
                        searchQuery = searchQuery,
                        onSearchChange = { viewModel.setSearchQuery(it) },
                        selectedCategory = selectedCategory,
                        onCategoryChange = { viewModel.setSelectedCategory(it) },
                        onAddToCart = { product ->
                            viewModel.addToCart(product)
                        },
                        onNavigateToScratch = { currentDestination = AppDestination.SCRATCH }
                    )
                }

                AppDestination.SCRATCH -> {
                    ScratchScreen(
                        cards = scratchCards,
                        coupons = availableCoupons,
                        activeCard = activeScratchingCard,
                        onOpenCard = { viewModel.openScratchModal(it) },
                        onCloseCard = { viewModel.closeScratchModal() },
                        onCompleteScratch = { viewModel.completeScratch(it) },
                        onClaimDailyBonus = { viewModel.claimDailyBonusCard() }
                    )
                }

                AppDestination.QUESTS -> {
                    QuestsScreen(
                        quests = quests,
                        onClaimQuest = { questId ->
                            viewModel.claimQuest(questId)
                        }
                    )
                }

                AppDestination.ORDERS -> {
                    OrdersScreen(
                        orders = allOrders,
                        onTrackOrder = { order -> viewModel.openOrderTracking(order) },
                        onStartShopping = { currentDestination = AppDestination.SHOP }
                    )
                }
            }
        }
    }

    // Modal Shopping Cart Sheet
    if (isCartOpen) {
        CartSheet(
            cartItems = cartItemsList,
            availableCoupons = availableCoupons,
            selectedCoupon = selectedCoupon,
            subtotal = viewModel.calculateSubtotal(),
            discount = viewModel.calculateDiscount(),
            total = viewModel.calculateTotal(),
            onUpdateQuantity = { id, delta -> viewModel.updateCartQuantity(id, delta) },
            onRemoveItem = { id -> viewModel.removeCartItem(id) },
            onSelectCoupon = { coupon -> viewModel.selectCoupon(coupon) },
            onApplyManualPromo = { code -> viewModel.applyManualPromoCode(code) },
            onCheckout = {
                viewModel.checkout()
                isCartOpen = false
            },
            onDismiss = { isCartOpen = false }
        )
    }

    // Live Order Tracking Sheet
    selectedOrderForTracking?.let { order ->
        OrderTrackingSheet(
            order = order,
            onDismiss = { viewModel.closeOrderTracking() }
        )
    }

    // Order Checkout Celebration Dialog
    lastCompletedOrder?.let { order ->
        Dialog(onDismissRequest = { viewModel.dismissOrderCelebration() }) {
            OrderCelebrationDialog(
                order = order,
                onDismiss = { viewModel.dismissOrderCelebration() },
                onNavigateToScratch = {
                    viewModel.dismissOrderCelebration()
                    currentDestination = AppDestination.SCRATCH
                }
            )
        }
    }

    // Quest Reward Won Celebration Dialog
    questRewardWon?.let { tier ->
        Dialog(onDismissRequest = { viewModel.dismissQuestReward() }) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(24.dp))
                    .background(DarkSurfaceElevated)
                    .border(2.dp, BrandAmber, RoundedCornerShape(24.dp))
                    .padding(20.dp),
                contentAlignment = Alignment.Center
            ) {
                ConfettiEffect()
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Surface(
                        shape = CircleShape,
                        color = BrandAmber,
                        modifier = Modifier.size(56.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = null,
                                tint = Color.Black,
                                modifier = Modifier.size(32.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = "QUEST COMPLETED!",
                        color = BrandAmber,
                        fontWeight = FontWeight.Black,
                        fontSize = 14.sp,
                        letterSpacing = 1.sp
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "${tier.displayName} Awarded",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "A new mystery scratch voucher has been deposited directly into your vault!",
                        color = Color(0xFF94A3B8),
                        fontSize = 12.sp,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(18.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = {
                                viewModel.dismissQuestReward()
                                currentDestination = AppDestination.SCRATCH
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = BrandAmber),
                            modifier = Modifier.weight(1f).testTag("quest_dialog_scratch_button")
                        ) {
                            Text("Scratch Card", color = Color.Black, fontWeight = FontWeight.Bold)
                        }

                        Button(
                            onClick = { viewModel.dismissQuestReward() },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF334155)),
                            modifier = Modifier.weight(1f).testTag("quest_dialog_close_button")
                        ) {
                            Text("Awesome", color = Color.White)
                        }
                    }
                }
            }
        }
    }
}
