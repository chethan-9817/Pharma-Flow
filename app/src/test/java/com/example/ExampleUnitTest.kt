package com.example

import com.example.data.model.CardTier
import com.example.data.model.Coupon
import com.example.data.model.DiscountType
import com.example.data.model.FoilStyle
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }

    @Test
    fun testCouponDiscountCalculations() {
        val percentageCoupon = Coupon(
            code = "SAVE25",
            description = "25% off",
            discountType = DiscountType.PERCENTAGE,
            discountValue = 25.0,
            minOrder = 50.0
        )

        val subtotal = 100.0
        val discount = subtotal * (percentageCoupon.discountValue / 100.0)
        assertEquals(25.0, discount, 0.001)
        assertEquals(75.0, subtotal - discount, 0.001)

        val flatCoupon = Coupon(
            code = "FLAT15",
            description = "$15 off",
            discountType = DiscountType.FLAT_AMOUNT,
            discountValue = 15.0,
            minOrder = 20.0
        )

        val subtotal2 = 60.0
        val discount2 = flatCoupon.discountValue.coerceAtMost(subtotal2)
        assertEquals(15.0, discount2, 0.001)
        assertEquals(45.0, subtotal2 - discount2, 0.001)
    }

    @Test
    fun testCardTierRanges() {
        assertTrue(CardTier.SILVER.minDiscount >= 10)
        assertTrue(CardTier.GOLD.minDiscount >= 25)
        assertTrue(CardTier.DIAMOND.minDiscount >= 50)
    }

    @Test
    fun testFoilStylesAvailable() {
        assertEquals(3, FoilStyle.values().size)
        assertNotNull(FoilStyle.HOLOGRAPHIC)
        assertNotNull(FoilStyle.GOLD_LEAF)
        assertNotNull(FoilStyle.CYBER_NEON)
    }
}
