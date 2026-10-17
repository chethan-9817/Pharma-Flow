package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.components.AppBottomBar
import com.example.ui.components.AppDestination
import com.example.ui.components.EvaluatorPresenterBar
import com.example.ui.screens.CartSheet
import com.example.ui.screens.EcoDealsScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.LiveTrackingScreen
import com.example.ui.screens.OrderCelebrationDialog
import com.example.ui.screens.RewardsScreen
import com.example.ui.theme.MedLifecycleTheme
import com.example.ui.theme.MedSlate
import com.example.ui.viewmodel.StoreViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: StoreViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            MedLifecycleTheme {
                MedLifecycleApp(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun MedLifecycleApp(viewModel: StoreViewModel) {
    var currentDestination by remember { mutableStateOf(AppDestination.HOME) }
    var isCartOpen by remember { mutableStateOf(false) }

    // State collections from ViewModel
    val medicines by viewModel.filteredMedicines.collectAsStateWithLifecycle()
    val nearExpiryMedicines by viewModel.nearExpiryMedicines.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val selectedCategory by viewModel.selectedCategory.collectAsStateWithLifecycle()
    val wishlistIds by viewModel.wishlistIds.collectAsStateWithLifecycle()
    val prescriptionResult by viewModel.prescriptionResult.collectAsStateWithLifecycle()
    val isScanningRx by viewModel.isScanningRx.collectAsStateWithLifecycle()

    val cartItemsMap by viewModel.cartItems.collectAsStateWithLifecycle()
    val cartItemsList = cartItemsMap.values.toList()

    val medWalletBalance by viewModel.medWalletBalance.collectAsStateWithLifecycle()
    val ecoKarmaPoints by viewModel.ecoKarmaPoints.collectAsStateWithLifecycle()
    val useWalletCashback by viewModel.useWalletCashback.collectAsStateWithLifecycle()

    val latestOrder by viewModel.latestOrder.collectAsStateWithLifecycle()
    val lastPlacedOrder by viewModel.lastPlacedOrder.collectAsStateWithLifecycle()
    val isSupabaseLive by viewModel.isSupabaseLive.collectAsStateWithLifecycle()

    val reversePickups by viewModel.reversePickups.collectAsStateWithLifecycle()
    val scratchCards by viewModel.scratchCards.collectAsStateWithLifecycle()
    val unscratchedCount by viewModel.unscratchedCount.collectAsStateWithLifecycle()
    val activeScratchingCard by viewModel.activeScratchingCard.collectAsStateWithLifecycle()

    // Back button handling
    BackHandler(enabled = isCartOpen || currentDestination != AppDestination.HOME) {
        if (isCartOpen) {
            isCartOpen = false
        } else if (currentDestination != AppDestination.HOME) {
            currentDestination = AppDestination.HOME
        }
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(MedSlate)
            .statusBarsPadding()
            .navigationBarsPadding(),
        topBar = {
            Column {
                // Fixed Evaluator Presenter Toolbar docked at the top
                EvaluatorPresenterBar(
                    isLiveConnected = isSupabaseLive,
                    onToggleSyncMode = { viewModel.toggleSupabaseStatus() },
                    onSimulatePack = { viewModel.simulateChemistPackOrder() },
                    onSimulateRiderOut = { viewModel.simulateRiderOutForDelivery() },
                    onSimulateDelivered = { viewModel.simulateRiderMarkDelivered() },
                    onGrantScratchCard = { viewModel.grantInstantDemoScratchCard() }
                )
            }
        },
        bottomBar = {
            AppBottomBar(
                currentDestination = currentDestination,
                onDestinationSelected = { currentDestination = it },
                unscratchedCardsCount = unscratchedCount,
                nearExpiryCount = nearExpiryMedicines.size,
                hasActiveDelivery = latestOrder != null && (latestOrder?.currentStage ?: 0) in 1..3
            )
        },
        containerColor = MedSlate
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (currentDestination) {
                AppDestination.HOME -> {
                    HomeScreen(
                        medicines = medicines,
                        wishlistIds = wishlistIds,
                        onToggleWishlist = { viewModel.toggleWishlist(it) },
                        searchQuery = searchQuery,
                        onSearchChange = { viewModel.setSearchQuery(it) },
                        selectedCategory = selectedCategory,
                        onCategoryChange = { viewModel.setSelectedCategory(it) },
                        medWalletBalance = medWalletBalance,
                        prescriptionResult = prescriptionResult,
                        isScanningRx = isScanningRx,
                        onSimulateScanRx = { onDone ->
                            viewModel.simulatePrescriptionScan(onDone)
                        },
                        onAddToCart = { med ->
                            viewModel.addToCart(med)
                            isCartOpen = true
                        },
                        onNavigateToRewards = {
                            currentDestination = AppDestination.REWARDS
                        }
                    )
                }

                AppDestination.ECO_DEALS -> {
                    EcoDealsScreen(
                        nearExpiryMedicines = nearExpiryMedicines,
                        ecoKarmaPoints = ecoKarmaPoints,
                        reversePickups = reversePickups,
                        onAddToCart = { med ->
                            viewModel.addToCart(med)
                            isCartOpen = true
                        },
                        onScheduleDisposal = { count, meds, address, slot ->
                            viewModel.scheduleReverseDisposal(count, meds, address, slot) {}
                        }
                    )
                }

                AppDestination.TRACK -> {
                    LiveTrackingScreen(
                        activeOrder = latestOrder,
                        onNavigateToHome = { currentDestination = AppDestination.HOME }
                    )
                }

                AppDestination.REWARDS -> {
                    RewardsScreen(
                        cards = scratchCards,
                        medWalletBalance = medWalletBalance,
                        ecoKarmaPoints = ecoKarmaPoints,
                        activeCard = activeScratchingCard,
                        onOpenCard = { viewModel.openScratchModal(it) },
                        onCloseCard = { viewModel.closeScratchModal() },
                        onCompleteScratch = { viewModel.completeScratch(it) },
                        onNavigateToHome = { currentDestination = AppDestination.HOME }
                    )
                }
            }
        }
    }

    // Modal Medicine Shopping Bag Sheet
    if (isCartOpen) {
        CartSheet(
            cartItems = cartItemsList,
            subtotal = viewModel.calculateSubtotal(),
            ecoSavings = viewModel.calculateEcoSavings(),
            walletDiscount = viewModel.calculateWalletDiscount(),
            grandTotal = viewModel.calculateGrandTotal(),
            medWalletBalance = medWalletBalance,
            useWalletCashback = useWalletCashback,
            onToggleWalletCashback = { viewModel.toggleWalletCashback() },
            prescriptionResult = prescriptionResult,
            onScanRxPrompt = {
                isCartOpen = false
                currentDestination = AppDestination.HOME
            },
            onUpdateQuantity = { id, delta -> viewModel.updateCartQuantity(id, delta) },
            onRemoveItem = { id -> viewModel.removeCartItem(id) },
            onCheckout = { method ->
                viewModel.checkout(method) {
                    isCartOpen = false
                }
            },
            onDismiss = { isCartOpen = false }
        )
    }

    // Order Placed Celebration Dialog with Mystery Scratch Card Unlock
    lastPlacedOrder?.let { order ->
        Dialog(onDismissRequest = { viewModel.dismissOrderCelebration() }) {
            OrderCelebrationDialog(
                order = order,
                onDismiss = {
                    viewModel.dismissOrderCelebration()
                    currentDestination = AppDestination.TRACK
                },
                onNavigateToRewards = {
                    viewModel.dismissOrderCelebration()
                    currentDestination = AppDestination.REWARDS
                }
            )
        }
    }
}
