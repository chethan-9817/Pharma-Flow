package com.example

import com.example.data.model.CartItem
import com.example.data.model.Medicine
import com.example.data.model.Order
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

        // 50% discount check
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
            price = 32.0,
            mrp = 34.0,
            stock = 50
        )
        val cartItem = CartItem(medicine = med, quantity = 3)
        val itemTotal = cartItem.medicine.price * cartItem.quantity
        assertEquals(96.0, itemTotal, 0.001)
    }

    @Test
    fun testOrderStagesProgression() {
        val order = Order(
            id = "ord_1",
            orderNumber = "MED-1234",
            items = "2x Dolo 650",
            totalAmount = 64.0,
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
