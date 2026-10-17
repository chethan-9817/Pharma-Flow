package com.example.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.rotate
import kotlin.random.Random

private data class ConfettiParticle(
    val initialX: Float,
    val speedX: Float,
    val speedY: Float,
    val rotationSpeed: Float,
    val color: Color,
    val size: Float,
    val isCircle: Boolean
)

@Composable
fun ConfettiEffect(
    modifier: Modifier = Modifier,
    particleCount: Int = 75,
    onFinished: (() -> Unit)? = null
) {
    val progress = remember { Animatable(0f) }

    val colors = listOf(
        Color(0xFF10B981), // Emerald
        Color(0xFFF59E0B), // Gold
        Color(0xFF06B6D4), // Cyan
        Color(0xFFEC4899), // Pink
        Color(0xFF8B5CF6), // Purple
        Color(0xFFFBBF24)  // Amber
    )

    val particles = remember {
        List(particleCount) {
            ConfettiParticle(
                initialX = Random.nextFloat(),
                speedX = (Random.nextFloat() - 0.5f) * 350f,
                speedY = Random.nextFloat() * 600f + 650f,
                rotationSpeed = (Random.nextFloat() - 0.5f) * 720f,
                color = colors.random(),
                size = Random.nextFloat() * 14f + 8f,
                isCircle = Random.nextBoolean()
            )
        }
    }

    LaunchedEffect(Unit) {
        progress.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 2800, easing = LinearEasing)
        )
        onFinished?.invoke()
    }

    Canvas(modifier = modifier.fillMaxSize()) {
        val t = progress.value
        val canvasWidth = size.width
        val canvasHeight = size.height

        particles.forEach { particle ->
            val x = (particle.initialX * canvasWidth) + (particle.speedX * t)
            val y = (t * particle.speedY) - 50f
            val alpha = (1f - (t * 0.85f)).coerceIn(0f, 1f)

            rotate(
                degrees = particle.rotationSpeed * t,
                pivot = Offset(x, y)
            ) {
                if (particle.isCircle) {
                    drawCircle(
                        color = particle.color.copy(alpha = alpha),
                        radius = particle.size / 2,
                        center = Offset(x, y)
                    )
                } else {
                    drawRect(
                        color = particle.color.copy(alpha = alpha),
                        topLeft = Offset(x - particle.size / 2, y - particle.size / 2),
                        size = Size(particle.size, particle.size * 0.6f)
                    )
                }
            }
        }
    }
}
