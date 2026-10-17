package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.model.CardTier
import com.example.data.model.CartItem
import com.example.data.model.Coupon
import com.example.data.model.DiscountType
import com.example.data.model.Order
import com.example.data.model.Product
import com.example.data.model.Quest
import com.example.data.model.ScratchCard
import com.example.data.repository.StoreRepository
import com.example.ui.audio.SoundManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class StoreViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: StoreRepository

    init {
        val database = AppDatabase.getInstance(application)
        repository = StoreRepository(database)
        viewModelScope.launch {
            repository.initializeDatabase()
        }
    }

    // Sound toggle state
    val isSoundEnabled: StateFlow<Boolean> = SoundManager.isSoundEnabled

    fun toggleSound() {
        SoundManager.toggleSound()
    }

    fun playScratchSound() {
        SoundManager.playScratchSound()
    }

    // Wishlist State
    val wishlistIds: StateFlow<Set<String>> = repository.wishlistIds
        .map { it.toSet() }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptySet()
        )

    val wishlistCount: StateFlow<Int> = repository.wishlistCount.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = 0
    )

    fun toggleWishlist(productId: String) {
        val isWishlisted = wishlistIds.value.contains(productId)
        viewModelScope.launch {
            repository.toggleWishlist(productId, isWishlisted)
            SoundManager.playHeartSound()
        }
    }

    // Products & Filtering
    val allProducts: StateFlow<List<Product>> = repository.allProducts.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedCategory = MutableStateFlow("All")
    val selectedCategory: StateFlow<String> = _selectedCategory.asStateFlow()

    val filteredProducts: StateFlow<List<Product>> = combine(
        allProducts,
        searchQuery,
        selectedCategory,
        wishlistIds
    ) { products, query, category, wishlisted ->
        products.filter { product ->
            val matchesCategory = when (category.lowercase()) {
                "all" -> true
                "wishlist", "❤️ wishlist" -> wishlisted.contains(product.id)
                else -> product.category.equals(category, ignoreCase = true)
            }
            val matchesQuery = query.isBlank() ||
                    product.title.contains(query, ignoreCase = true) ||
                    product.description.contains(query, ignoreCase = true)
            matchesCategory && matchesQuery
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    // Scratch Cards & Coupons
    val scratchCards: StateFlow<List<ScratchCard>> = repository.allScratchCards.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val unscratchedCount: StateFlow<Int> = repository.unscratchedCardCount.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = 0
    )

    val availableCoupons: StateFlow<List<Coupon>> = repository.availableCoupons.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val allOrders: StateFlow<List<Order>> = repository.allOrders.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val quests: StateFlow<List<Quest>> = repository.quests

    // Active Scratching Modal
    private val _activeScratchingCard = MutableStateFlow<ScratchCard?>(null)
    val activeScratchingCard: StateFlow<ScratchCard?> = _activeScratchingCard.asStateFlow()

    // Shopping Cart State
    private val _cartItems = MutableStateFlow<Map<String, CartItem>>(emptyMap())
    val cartItems: StateFlow<Map<String, CartItem>> = _cartItems.asStateFlow()

    private val _selectedCoupon = MutableStateFlow<Coupon?>(null)
    val selectedCoupon: StateFlow<Coupon?> = _selectedCoupon.asStateFlow()

    // Order Tracking Modal State
    private val _selectedOrderForTracking = MutableStateFlow<Order?>(null)
    val selectedOrderForTracking: StateFlow<Order?> = _selectedOrderForTracking.asStateFlow()

    // Order Success Celebration State
    private val _lastCompletedOrder = MutableStateFlow<Order?>(null)
    val lastCompletedOrder: StateFlow<Order?> = _lastCompletedOrder.asStateFlow()

    // Quest claim celebration
    private val _questRewardWon = MutableStateFlow<CardTier?>(null)
    val questRewardWon: StateFlow<CardTier?> = _questRewardWon.asStateFlow()

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setSelectedCategory(category: String) {
        _selectedCategory.value = category
    }

    fun openScratchModal(card: ScratchCard) {
        _activeScratchingCard.value = card
    }

    fun closeScratchModal() {
        _activeScratchingCard.value = null
    }

    fun completeScratch(card: ScratchCard) {
        viewModelScope.launch {
            repository.markCardScratched(card)
            SoundManager.playRevealSound()
        }
    }

    fun addToCart(product: Product) {
        val current = _cartItems.value.toMutableMap()
        val existing = current[product.id]
        if (existing != null) {
            current[product.id] = existing.copy(quantity = existing.quantity + 1)
        } else {
            current[product.id] = CartItem(product = product, quantity = 1)
        }
        _cartItems.value = current
        SoundManager.playHeartSound()
    }

    fun updateCartQuantity(productId: String, delta: Int) {
        val current = _cartItems.value.toMutableMap()
        val existing = current[productId] ?: return
        val newQty = existing.quantity + delta
        if (newQty <= 0) {
            current.remove(productId)
        } else {
            current[productId] = existing.copy(quantity = newQty)
        }
        _cartItems.value = current
    }

    fun removeCartItem(productId: String) {
        val current = _cartItems.value.toMutableMap()
        current.remove(productId)
        _cartItems.value = current
    }

    fun selectCoupon(coupon: Coupon?) {
        _selectedCoupon.value = coupon
    }

    /**
     * Allows typing manual promo codes like LUCKY2026, CYBERDROP, FREESHIP, VIP50.
     * Returns null on success or error string message.
     */
    fun applyManualPromoCode(code: String): String? {
        val clean = code.trim().uppercase()
        if (clean.isBlank()) return "Please enter a valid code."

        val coupon = when (clean) {
            "LUCKY2026" -> Coupon(
                code = "LUCKY2026",
                description = "20% OFF Special Mystery Drop",
                discountType = DiscountType.PERCENTAGE,
                discountValue = 20.0,
                minOrder = 0.0
            )
            "CYBERDROP" -> Coupon(
                code = "CYBERDROP",
                description = "$20 FLAT Instant Savings",
                discountType = DiscountType.FLAT_AMOUNT,
                discountValue = 20.0,
                minOrder = 30.0
            )
            "FREESHIP" -> Coupon(
                code = "FREESHIP",
                description = "100% Free Priority Delivery",
                discountType = DiscountType.FREE_SHIPPING,
                discountValue = 12.0,
                minOrder = 0.0
            )
            "VIP50" -> Coupon(
                code = "VIP50",
                description = "50% OFF High-Roller Haul",
                discountType = DiscountType.PERCENTAGE,
                discountValue = 50.0,
                minOrder = 80.0
            )
            else -> return "Invalid promo code. Try 'LUCKY2026' or 'CYBERDROP'!"
        }

        val subtotal = calculateSubtotal()
        if (subtotal < coupon.minOrder) {
            return "Requires minimum cart value of $${coupon.minOrder.toInt()}."
        }

        _selectedCoupon.value = coupon
        SoundManager.playRevealSound()
        return null
    }

    fun calculateSubtotal(): Double {
        return _cartItems.value.values.sumOf { it.product.price * it.quantity }
    }

    fun calculateDiscount(): Double {
        val subtotal = calculateSubtotal()
        val coupon = _selectedCoupon.value ?: return 0.0
        if (subtotal < coupon.minOrder) return 0.0

        return when (coupon.discountType) {
            DiscountType.PERCENTAGE -> (subtotal * (coupon.discountValue / 100.0))
            DiscountType.FLAT_AMOUNT -> coupon.discountValue.coerceAtMost(subtotal)
            DiscountType.FREE_SHIPPING -> 12.0
        }
    }

    fun calculateTotal(): Double {
        return (calculateSubtotal() - calculateDiscount()).coerceAtLeast(0.0)
    }

    fun checkout() {
        val items = _cartItems.value.values.toList()
        if (items.isEmpty()) return

        val subtotal = calculateSubtotal()
        val discount = calculateDiscount()
        val total = calculateTotal()
        val appliedCoupon = _selectedCoupon.value

        val summary = items.joinToString(", ") { "${it.quantity}x ${it.product.title}" }

        viewModelScope.launch {
            val order = repository.placeOrder(
                itemsSummary = summary,
                subtotal = subtotal,
                discount = discount,
                total = total,
                appliedCoupon = appliedCoupon
            )
            _cartItems.value = emptyMap()
            _selectedCoupon.value = null
            _lastCompletedOrder.value = order
            SoundManager.playCheckoutSound()
        }
    }

    fun openOrderTracking(order: Order) {
        _selectedOrderForTracking.value = order
    }

    fun closeOrderTracking() {
        _selectedOrderForTracking.value = null
    }

    fun dismissOrderCelebration() {
        _lastCompletedOrder.value = null
    }

    fun claimQuest(questId: String) {
        viewModelScope.launch {
            val tier = repository.claimQuest(questId)
            if (tier != null) {
                _questRewardWon.value = tier
                SoundManager.playRevealSound()
            }
        }
    }

    fun dismissQuestReward() {
        _questRewardWon.value = null
    }

    fun claimDailyBonusCard() {
        viewModelScope.launch {
            repository.createAndAwardScratchCard(CardTier.GOLD, "Daily Mystery Login")
            SoundManager.playRevealSound()
        }
    }
}
