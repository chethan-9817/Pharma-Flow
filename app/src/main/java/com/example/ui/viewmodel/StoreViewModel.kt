package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.model.CartItem
import com.example.data.model.Medicine
import com.example.data.model.MedicineDonation
import com.example.data.model.NgoPartner
import com.example.data.model.Order
import com.example.data.model.PrescriptionScanResult
import com.example.data.model.ReversePickupRequest
import com.example.data.model.ScratchCard
import com.example.data.model.UserProductListing
import com.example.data.repository.StoreRepository
import com.example.ui.audio.SoundManager
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlin.random.Random

class StoreViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: StoreRepository

    init {
        val database = AppDatabase.getInstance(application)
        repository = StoreRepository(database)
        viewModelScope.launch {
            repository.initializeDatabase()
        }
    }

    // Audio & Sound
    val isSoundEnabled: StateFlow<Boolean> = SoundManager.isSoundEnabled
    fun toggleSound() = SoundManager.toggleSound()

    // Supabase Live Sync Indicator
    private val _isSupabaseLive = MutableStateFlow(true)
    val isSupabaseLive: StateFlow<Boolean> = _isSupabaseLive.asStateFlow()

    fun toggleSupabaseStatus() {
        _isSupabaseLive.value = !_isSupabaseLive.value
    }

    // Wishlist
    val wishlistIds: StateFlow<Set<String>> = repository.wishlistIds
        .map { it.toSet() }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptySet())

    fun toggleWishlist(medicineId: String) {
        val isWishlisted = wishlistIds.value.contains(medicineId)
        viewModelScope.launch {
            repository.toggleWishlist(medicineId, isWishlisted)
            SoundManager.playHeartSound()
        }
    }

    // Medicines & Filtering
    val allMedicines: StateFlow<List<Medicine>> = repository.allMedicines.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
    )

    val nearExpiryMedicines: StateFlow<List<Medicine>> = repository.nearExpiryMedicines.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
    )

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedCategory = MutableStateFlow("All")
    val selectedCategory: StateFlow<String> = _selectedCategory.asStateFlow()

    val filteredMedicines: StateFlow<List<Medicine>> = combine(
        allMedicines,
        searchQuery,
        selectedCategory,
        wishlistIds
    ) { meds, query, cat, wishlisted ->
        meds.filter { med ->
            val matchesCategory = when (cat.lowercase()) {
                "all" -> true
                "wishlist", "❤️ wishlist" -> wishlisted.contains(med.id)
                "skincare & sunscreen", "sunscreen", "skincare" -> med.category.contains("Sunscreen", true) || med.category.contains("Skincare", true)
                "tablets" -> med.dosageForm.equals("Tablet", true) || med.category.contains("Tablet", true)
                "fever & pain" -> med.category.equals("Fever & Pain", true)
                "antibiotics" -> med.category.equals("Antibiotics", true)
                "vitamins" -> med.category.equals("Vitamins", true)
                "diabetes" -> med.category.equals("Diabetes", true)
                "first aid" -> med.category.equals("First Aid", true)
                else -> med.category.contains(cat, true)
            }
            val matchesQuery = query.isBlank() ||
                    med.brandName.contains(query, true) ||
                    med.genericSalt.contains(query, true) ||
                    med.manufacturer.contains(query, true)
            matchesCategory && matchesQuery
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Prescription AI Scanner
    private val _prescriptionResult = MutableStateFlow<PrescriptionScanResult?>(null)
    val prescriptionResult: StateFlow<PrescriptionScanResult?> = _prescriptionResult.asStateFlow()

    private val _isScanningRx = MutableStateFlow(false)
    val isScanningRx: StateFlow<Boolean> = _isScanningRx.asStateFlow()

    fun simulatePrescriptionScan(onCompleted: () -> Unit) {
        viewModelScope.launch {
            _isScanningRx.value = true
            SoundManager.playScratchSound()
            delay(2000) // 2-second scanning laser animation
            _prescriptionResult.value = PrescriptionScanResult(
                isVerified = true,
                doctorName = "Dr. Rajesh Shenoy, MD",
                clinicName = "City Health Clinic, Mangaluru",
                extractedMedicines = listOf("Augmentin 625 Duo", "Azithral 500", "Glycomet GP 1", "Pan-D")
            )
            _isScanningRx.value = false
            SoundManager.playRevealSound()
            onCompleted()
        }
    }

    fun clearPrescription() {
        _prescriptionResult.value = null
    }

    // Shopping Cart State
    private val _cartItems = MutableStateFlow<Map<String, CartItem>>(emptyMap())
    val cartItems: StateFlow<Map<String, CartItem>> = _cartItems.asStateFlow()

    fun addToCart(medicine: Medicine) {
        val current = _cartItems.value.toMutableMap()
        val existing = current[medicine.id]
        if (existing != null) {
            current[medicine.id] = existing.copy(quantity = existing.quantity + 1)
        } else {
            current[medicine.id] = CartItem(medicine, 1)
        }
        _cartItems.value = current
        SoundManager.playHeartSound()
    }

    fun updateCartQuantity(medicineId: String, delta: Int) {
        val current = _cartItems.value.toMutableMap()
        val existing = current[medicineId] ?: return
        val newQty = existing.quantity + delta
        if (newQty <= 0) {
            current.remove(medicineId)
        } else {
            current[medicineId] = existing.copy(quantity = newQty)
        }
        _cartItems.value = current
    }

    fun removeCartItem(medicineId: String) {
        val current = _cartItems.value.toMutableMap()
        current.remove(medicineId)
        _cartItems.value = current
    }

    // MedWallet & Eco-Karma
    private val _medWalletBalance = MutableStateFlow(120.0)
    val medWalletBalance: StateFlow<Double> = _medWalletBalance.asStateFlow()

    private val _ecoKarmaPoints = MutableStateFlow(350)
    val ecoKarmaPoints: StateFlow<Int> = _ecoKarmaPoints.asStateFlow()

    private val _useWalletCashback = MutableStateFlow(false)
    val useWalletCashback: StateFlow<Boolean> = _useWalletCashback.asStateFlow()

    fun toggleWalletCashback() {
        _useWalletCashback.value = !_useWalletCashback.value
    }

    fun calculateSubtotal(): Double {
        return _cartItems.value.values.sumOf { it.medicine.price * it.quantity }
    }

    fun calculateEcoSavings(): Double {
        return _cartItems.value.values.sumOf {
            if (it.medicine.isNearExpiry) (it.medicine.mrp - it.medicine.price) * it.quantity else 0.0
        }
    }

    fun calculateWalletDiscount(): Double {
        if (!_useWalletCashback.value) return 0.0
        val sub = calculateSubtotal()
        return _medWalletBalance.value.coerceAtMost(sub)
    }

    fun calculateGrandTotal(): Double {
        val sub = calculateSubtotal()
        val walletDeduction = calculateWalletDiscount()
        return (sub - walletDeduction).coerceAtLeast(0.0)
    }

    // Orders & Tracking
    val allOrders: StateFlow<List<Order>> = repository.allOrders.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
    )

    val latestOrder: StateFlow<Order?> = repository.latestOrder.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), null
    )

    private val _lastPlacedOrder = MutableStateFlow<Order?>(null)
    val lastPlacedOrder: StateFlow<Order?> = _lastPlacedOrder.asStateFlow()

    fun checkout(paymentMethod: String, onOrderPlaced: (Order) -> Unit) {
        val items = _cartItems.value.values.toList()
        if (items.isEmpty()) return

        val summary = items.joinToString(", ") { "${it.quantity}x ${it.medicine.brandName}" }
        val grandTotal = calculateGrandTotal()
        val ecoSavings = calculateEcoSavings()
        val rxUrl = _prescriptionResult.value?.doctorName

        viewModelScope.launch {
            if (_useWalletCashback.value) {
                _medWalletBalance.value = (_medWalletBalance.value - calculateWalletDiscount()).coerceAtLeast(0.0)
            }

            val order = repository.placeOrder(
                items = summary,
                totalAmount = grandTotal,
                ecoSavings = ecoSavings,
                prescriptionUrl = rxUrl
            )
            _cartItems.value = emptyMap()
            _useWalletCashback.value = false
            _lastPlacedOrder.value = order
            SoundManager.playCheckoutSound()
            onOrderPlaced(order)
        }
    }

    fun dismissOrderCelebration() {
        _lastPlacedOrder.value = null
    }

    // Evaluator Presenter Controls
    fun simulateChemistPackOrder() {
        val order = latestOrder.value ?: return
        viewModelScope.launch {
            repository.updateOrderStatus(order.id, "ready_for_pickup", 2)
            SoundManager.playRevealSound()
        }
    }

    fun simulateRiderOutForDelivery() {
        val order = latestOrder.value ?: return
        viewModelScope.launch {
            repository.updateOrderStatus(order.id, "out_for_delivery", 3)
            SoundManager.playRevealSound()
        }
    }

    fun simulateRiderMarkDelivered() {
        val order = latestOrder.value ?: return
        viewModelScope.launch {
            repository.updateOrderStatus(order.id, "delivered", 4)
            SoundManager.playCheckoutSound()
        }
    }

    fun grantInstantDemoScratchCard() {
        viewModelScope.launch {
            repository.addScratchCard(
                title = "Presenter Demo Jackpot",
                subtitle = "Evaluator reward! Scratch to reveal ₹75 Cashback",
                prizeType = "CASHBACK",
                prizeValue = 75.0,
                code = "DEMO${Random.nextInt(100, 999)}"
            )
            SoundManager.playRevealSound()
        }
    }

    // End-User Product Resale Listings (Legacy compatibility)
    val userListings: StateFlow<List<UserProductListing>> = repository.userListings.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
    )

    // NGO Medicine Donation & Community Health Hub
    val medicineDonations: StateFlow<List<MedicineDonation>> = repository.medicineDonations.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
    )

    val ngoPartners: List<NgoPartner> = repository.ngoPartners

    fun createMedicineDonation(
        medicineName: String,
        brandName: String,
        category: String,
        dosageOrUnits: String,
        batchNumber: String,
        expiryDate: String,
        packageCondition: String,
        recipientNgo: NgoPartner,
        photoUrl: String? = null,
        onSuccess: (earnedKarma: Int) -> Unit
    ) {
        viewModelScope.launch {
            val (donation, _) = repository.createMedicineDonation(
                medicineName = medicineName,
                brandName = brandName,
                category = category,
                dosageOrUnits = dosageOrUnits,
                batchNumber = batchNumber,
                expiryDate = expiryDate,
                packageCondition = packageCondition,
                recipientNgo = recipientNgo,
                photoUrl = photoUrl
            )
            _ecoKarmaPoints.value += donation.karmaPointsEarned
            SoundManager.playCelebrationSound()
            onSuccess(donation.karmaPointsEarned)
        }
    }

    fun markDonationDelivered(id: String) {
        viewModelScope.launch {
            repository.markDonationDelivered(id)
            SoundManager.playCelebrationSound()
        }
    }

    fun deleteDonation(id: String) {
        viewModelScope.launch {
            repository.deleteDonation(id)
        }
    }

    fun simulateNgoVerifyDonation() {
        val pledged = medicineDonations.value.firstOrNull { it.status == "PLEDGED" }
        if (pledged != null) {
            markDonationDelivered(pledged.id)
        }
    }

    fun createUserListing(
        productName: String,
        brandName: String,
        category: String,
        dosageOrVolume: String,
        batchNumber: String,
        expiryDate: String,
        originalMrp: Double,
        listingPrice: Double,
        packageCondition: String,
        photoUrl: String? = null,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            repository.createUserListing(
                productName = productName,
                brandName = brandName,
                category = category,
                dosageOrVolume = dosageOrVolume,
                batchNumber = batchNumber,
                expiryDate = expiryDate,
                originalMrp = originalMrp,
                listingPrice = listingPrice,
                packageCondition = packageCondition,
                photoUrl = photoUrl
            )
            SoundManager.playRevealSound()
            onSuccess()
        }
    }

    fun approveUserListing(id: String) {
        viewModelScope.launch {
            repository.approveUserListing(id)
            SoundManager.playRevealSound()
        }
    }

    fun deleteUserListing(id: String) {
        viewModelScope.launch {
            repository.deleteUserListing(id)
        }
    }

    fun simulateChemistApproveListing() {
        val pending = userListings.value.firstOrNull { it.verificationStatus == "PENDING_REVIEW" }
        if (pending != null) {
            approveUserListing(pending.id)
        }
    }

    // Reverse Logistics Pickups
    val reversePickups: StateFlow<List<ReversePickupRequest>> = repository.reversePickups.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
    )

    fun scheduleReverseDisposal(
        stripCount: Int,
        medicineNames: String,
        address: String,
        slot: String,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            repository.scheduleReverseDisposal(stripCount, medicineNames, address, slot)
            _ecoKarmaPoints.value += 50
            SoundManager.playRevealSound()
            onSuccess()
        }
    }

    // Scratch Cards
    val scratchCards: StateFlow<List<ScratchCard>> = repository.allScratchCards.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
    )

    val unscratchedCount: StateFlow<Int> = repository.unscratchedCardCount.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), 0
    )

    private val _activeScratchingCard = MutableStateFlow<ScratchCard?>(null)
    val activeScratchingCard: StateFlow<ScratchCard?> = _activeScratchingCard.asStateFlow()

    fun openScratchModal(card: ScratchCard) {
        _activeScratchingCard.value = card
    }

    fun closeScratchModal() {
        _activeScratchingCard.value = null
    }

    fun completeScratch(card: ScratchCard) {
        viewModelScope.launch {
            repository.markCardScratched(card)
            when (card.prizeType) {
                "CASHBACK" -> _medWalletBalance.value += card.prizeValue
                "ECO_POINTS" -> _ecoKarmaPoints.value += card.prizeValue.toInt()
            }
            SoundManager.playRevealSound()
        }
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setSelectedCategory(cat: String) {
        _selectedCategory.value = cat
    }
}
