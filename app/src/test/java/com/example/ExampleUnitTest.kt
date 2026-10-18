package com.example

import com.example.data.model.CartItem
import com.example.data.model.Medicine
import com.example.data.model.Order
import com.example.data.model.UserProductListing
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }

    @Test
    fun testNearExpiryDiscountCalculation() {
        val nearExpiryMed = Medicine(
            id = "eco_test",
            brandName = "Amoxicillin 250mg",
            genericSalt = "Amoxicillin Trihydrate 250mg",
            manufacturer = "Cipla",
            price = 45.0,
            mrp = 90.0,
            stock = 10,
            isNearExpiry = true,
            expiryDaysLeft = 22,
            wastePreventedGrams = 42
        )

        val discount = nearExpiryMed.mrp - nearExpiryMed.price
        assertEquals(45.0, discount, 0.001)
        assertEquals(0.5, discount / nearExpiryMed.mrp, 0.001)
        assertTrue(nearExpiryMed.isNearExpiry)
        assertEquals(42, nearExpiryMed.wastePreventedGrams)
    }

    @Test
    fun testCartItemCalculations() {
        val med = Medicine(
            id = "med_test",
            brandName = "Dolo 650",
            genericSalt = "Paracetamol 650mg",
            manufacturer = "Micro Labs",
            price = 30.0,
            mrp = 34.0,
            stock = 50
        )
        val cartItem = CartItem(medicine = med, quantity = 3)
        val itemTotal = cartItem.medicine.price * cartItem.quantity
        assertEquals(90.0, itemTotal, 0.001)
    }

    @Test
    fun testSunscreenProductAttributes() {
        val dermaCoSunscreen = Medicine(
            id = "sun_1",
            brandName = "The Derma Co 1% Hyaluronic Sunscreen Aqua Gel",
            genericSalt = "Hyaluronic Acid 1% + Vitamin E (SPF 50 PA++++)",
            manufacturer = "Honasa Consumer Ltd.",
            price = 449.0,
            mrp = 499.0,
            stock = 75,
            requiresRx = false,
            dosageForm = "Aqua Gel",
            category = "Skincare & Sunscreen"
        )

        assertEquals("Skincare & Sunscreen", dermaCoSunscreen.category)
        assertTrue(dermaCoSunscreen.genericSalt.contains("SPF 50"))
        assertTrue(dermaCoSunscreen.genericSalt.contains("PA++++"))
        assertFalse(dermaCoSunscreen.requiresRx)
        assertEquals(50.0, dermaCoSunscreen.mrp - dermaCoSunscreen.price, 0.001)
    }

    @Test
    fun testUserProductListingPayoutAndStatus() {
        val userListing = UserProductListing(
            id = "listing_123",
            productName = "Minimalist SPF 50 Sunscreen (50g)",
            brandName = "Minimalist",
            category = "Skincare & Sunscreen",
            dosageOrVolume = "50g Fluid Gel",
            batchNumber = "MNL-SN2024",
            expiryDate = "11/2027",
            originalMrp = 399.0,
            listingPrice = 280.0,
            packageCondition = "Factory Sealed Box",
            verificationStatus = "PENDING_REVIEW"
        )

        // 90% doorstep payout verification
        val expectedPayout = userListing.listingPrice * 0.90
        assertEquals(expectedPayout, userListing.estimatedPayout, 0.001)
        assertEquals("PENDING_REVIEW", userListing.verificationStatus)

        // Approved state transition
        val approvedListing = userListing.copy(verificationStatus = "APPROVED")
        assertEquals("APPROVED", approvedListing.verificationStatus)
        assertEquals("Factory Sealed Box", approvedListing.packageCondition)
    }

    @Test
    fun testOrderStagesProgression() {
        val order = Order(
            id = "ord_1",
            orderNumber = "MED-1234",
            items = "2x Dolo 650",
            totalAmount = 60.0,
            status = "ready_for_pickup",
            currentStage = 2,
            otp = "4829"
        )

        assertEquals("MED-1234", order.orderNumber)
        assertEquals(2, order.currentStage)
        assertEquals(4, order.otp.length)
        assertFalse(order.currentStage == 4)
    }
}
