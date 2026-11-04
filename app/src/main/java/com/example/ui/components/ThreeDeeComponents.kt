package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import kotlin.math.*

/**
 * Interactive 3D Tilt Card that responds to touch gestures with real-time 3D rotation,
 * camera perspective, dynamic specular highlight, and spring-back physics.
 */
@Composable
fun Interactive3DCard(
    modifier: Modifier = Modifier,
    shape: RoundedCornerShape = RoundedCornerShape(20.dp),
    backgroundColor: Color = MaterialTheme.colorScheme.surface,
    borderColor: Color = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
    highlightColor: Color = BrandPrimary,
    maxTiltDegrees: Float = 14f,
    elevation: Dp = 6.dp,
    onClick: (() -> Unit)? = null,
    content: @Composable BoxScope.() -> Unit
) {
    var dragOffsetX by remember { mutableFloatStateOf(0f) }
    var dragOffsetY by remember { mutableFloatStateOf(0f) }
    var isPressed by remember { mutableStateOf(false) }

    val animatedTiltX by animateFloatAsState(
        targetValue = if (isPressed) (-dragOffsetY * maxTiltDegrees).coerceIn(-maxTiltDegrees, maxTiltDegrees) else 0f,
        animationSpec = spring(dampingRatio = 0.65f, stiffness = Spring.StiffnessMediumLow),
        label = "tiltX"
    )
    val animatedTiltY by animateFloatAsState(
        targetValue = if (isPressed) (dragOffsetX * maxTiltDegrees).coerceIn(-maxTiltDegrees, maxTiltDegrees) else 0f,
        animationSpec = spring(dampingRatio = 0.65f, stiffness = Spring.StiffnessMediumLow),
        label = "tiltY"
    )
    val animatedScale by animateFloatAsState(
        targetValue = if (isPressed) 0.965f else 1f,
        animationSpec = spring(dampingRatio = 0.6f, stiffness = Spring.StiffnessLow),
        label = "scale"
    )
    val animatedElevation by animateDpAsState(
        targetValue = if (isPressed) elevation * 1.8f else elevation,
        animationSpec = tween(150),
        label = "elevation"
    )

    // Dynamic specular sheen highlight calculation based on tilt
    val sheenCenterOffset = remember(animatedTiltX, animatedTiltY) {
        Offset(
            x = 0.5f + (animatedTiltY / maxTiltDegrees) * 0.4f,
            y = 0.5f - (animatedTiltX / maxTiltDegrees) * 0.4f
        )
    }

    Box(
        modifier = modifier
            .graphicsLayer {
                rotationX = animatedTiltX
                rotationY = animatedTiltY
                scaleX = animatedScale
                scaleY = animatedScale
                cameraDistance = 16f * density
                shadowElevation = animatedElevation.toPx()
                this.shape = shape
                clip = true
            }
            .shadow(
                elevation = animatedElevation,
                shape = shape,
                spotColor = highlightColor.copy(alpha = 0.35f),
                ambientColor = Color.Black.copy(alpha = 0.2f)
            )
            .clip(shape)
            .background(backgroundColor)
            .border(
                width = 1.2.dp,
                brush = Brush.linearGradient(
                    colors = listOf(
                        highlightColor.copy(alpha = if (isPressed) 0.8f else 0.4f),
                        borderColor,
                        highlightColor.copy(alpha = if (isPressed) 0.4f else 0.1f)
                    ),
                    start = Offset(0f, 0f),
                    end = Offset(400f, 400f)
                ),
                shape = shape
            )
            .pointerInput(Unit) {
                detectTapGestures(
                    onPress = { offset ->
                        val centerX = size.width / 2f
                        val centerY = size.height / 2f
                        dragOffsetX = (offset.x - centerX) / centerX
                        dragOffsetY = (offset.y - centerY) / centerY
                        isPressed = true
                        tryAwaitRelease()
                        isPressed = false
                        dragOffsetX = 0f
                        dragOffsetY = 0f
                    },
                    onTap = {
                        onClick?.invoke()
                    }
                )
            }
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragStart = { offset ->
                        val centerX = size.width / 2f
                        val centerY = size.height / 2f
                        dragOffsetX = (offset.x - centerX) / centerX
                        dragOffsetY = (offset.y - centerY) / centerY
                        isPressed = true
                    },
                    onDrag = { change, _ ->
                        change.consume()
                        val centerX = size.width / 2f
                        val centerY = size.height / 2f
                        dragOffsetX = ((change.position.x - centerX) / centerX).coerceIn(-1f, 1f)
                        dragOffsetY = ((change.position.y - centerY) / centerY).coerceIn(-1f, 1f)
                    },
                    onDragEnd = {
                        isPressed = false
                        dragOffsetX = 0f
                        dragOffsetY = 0f
                    },
                    onDragCancel = {
                        isPressed = false
                        dragOffsetX = 0f
                        dragOffsetY = 0f
                    }
                )
            }
    ) {
        // Base Content
        content()

        // 3D Specular Sheen Overlay
        Box(
            modifier = Modifier
                .matchParentSize()
                .drawBehind {
                    if (isPressed) {
                        drawCircle(
                            brush = Brush.radialGradient(
                                colors = listOf(
                                    Color.White.copy(alpha = 0.22f),
                                    highlightColor.copy(alpha = 0.1f),
                                    Color.Transparent
                                ),
                                center = Offset(
                                    x = size.width * sheenCenterOffset.x,
                                    y = size.height * sheenCenterOffset.y
                                ),
                                radius = size.minDimension * 0.8f
                            )
                        )
                    }
                }
        )
    }
}

/**
 * A stunning 3D Holographic Learning Orb with multi-axis gyroscopic rings,
 * orbital glowing energy particles, and perspective-projected core.
 */
@Composable
fun ThreeDeeLearningOrb(
    modifier: Modifier = Modifier,
    primaryColor: Color = BrandPrimary,
    secondaryColor: Color = BrandSecondary,
    accentColor: Color = BrandTertiary,
    streakCount: Int = 3,
    xpAmount: Int = 1250,
    userLevel: Int = 4,
    rankTitle: String = "Grandmaster",
    onOrbClick: () -> Unit = {}
) {
    val infiniteTransition = rememberInfiniteTransition(label = "3DOrbLoop")

    // Multi-axis orbital rotation angles
    val rotationYAngle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 9000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotY"
    )
    val rotationXAngle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 14000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotX"
    )
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.94f,
        targetValue = 1.06f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    // Manual user spin gesture offset
    var manualYaw by remember { mutableFloatStateOf(0f) }
    var manualPitch by remember { mutableFloatStateOf(0f) }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(260.dp)
            .pointerInput(Unit) {
                detectDragGestures { change, dragAmount ->
                    change.consume()
                    manualYaw += dragAmount.x * 0.6f
                    manualPitch = (manualPitch - dragAmount.y * 0.6f).coerceIn(-45f, 45f)
                }
            }
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onOrbClick
            ),
        contentAlignment = Alignment.Center
    ) {
        // Deep background nebula radial glow
        Canvas(modifier = Modifier.fillMaxSize()) {
            val center = Offset(size.width / 2f, size.height / 2f)
            val maxR = size.minDimension * 0.48f

            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        primaryColor.copy(alpha = 0.28f),
                        secondaryColor.copy(alpha = 0.12f),
                        Color.Transparent
                    ),
                    center = center,
                    radius = maxR * 1.3f
                )
            )

            // Draw 3D Perspective Orbital Wireframe Rings
            val focalLength = 360f
            val ringRadius = maxR * 0.72f * pulseScale

            fun project3D(x: Float, y: Float, z: Float): Triple<Float, Float, Float> {
                // Apply manual yaw and pitch
                val radYaw = (rotationYAngle + manualYaw) * (PI / 180.0)
                val radPitch = (rotationXAngle * 0.35f + manualPitch) * (PI / 180.0)

                // Rotate around Y axis
                val x1 = (x * cos(radYaw) - z * sin(radYaw)).toFloat()
                val z1 = (x * sin(radYaw) + z * cos(radYaw)).toFloat()

                // Rotate around X axis
                val y2 = (y * cos(radPitch) - z1 * sin(radPitch)).toFloat()
                val z2 = (y * sin(radPitch) + z1 * cos(radPitch)).toFloat()

                val scale = focalLength / (focalLength + z2)
                val screenX = center.x + x1 * scale
                val screenY = center.y + y2 * scale
                return Triple(screenX, screenY, scale)
            }

            // Draw Ring 1 (Equatorial Gyroscope)
            val ringPoints = 48
            val pathRing1 = Path()
            var firstP = true
            for (i in 0..ringPoints) {
                val theta = (i.toDouble() / ringPoints) * 2.0 * PI
                val x = (ringRadius * cos(theta)).toFloat()
                val y = 0f
                val z = (ringRadius * sin(theta)).toFloat()
                val (sx, sy, _) = project3D(x, y, z)
                if (firstP) {
                    pathRing1.moveTo(sx, sy)
                    firstP = false
                } else {
                    pathRing1.lineTo(sx, sy)
                }
            }
            drawPath(
                path = pathRing1,
                brush = Brush.linearGradient(listOf(primaryColor, accentColor, primaryColor)),
                style = Stroke(width = 2.2f)
            )

            // Draw Ring 2 (Polar Orbit 60 degrees inclined)
            val pathRing2 = Path()
            firstP = true
            val inclineRad = 60.0 * (PI / 180.0)
            for (i in 0..ringPoints) {
                val theta = (i.toDouble() / ringPoints) * 2.0 * PI
                val x = (ringRadius * cos(theta)).toFloat()
                val y = (ringRadius * sin(theta) * sin(inclineRad)).toFloat()
                val z = (ringRadius * sin(theta) * cos(inclineRad)).toFloat()
                val (sx, sy, _) = project3D(x, y, z)
                if (firstP) {
                    pathRing2.moveTo(sx, sy)
                    firstP = false
                } else {
                    pathRing2.lineTo(sx, sy)
                }
            }
            drawPath(
                path = pathRing2,
                brush = Brush.linearGradient(listOf(secondaryColor, BrandEmerald, secondaryColor)),
                style = Stroke(width = 1.8f)
            )

            // Orbiting Energy Particles (Depth Sorted)
            val particleCount = 10
            for (p in 0 until particleCount) {
                val pAngle = ((p.toDouble() / particleCount) * 2.0 * PI + (rotationYAngle * 1.5 * PI / 180.0))
                val px = (ringRadius * cos(pAngle)).toFloat()
                val py = (ringRadius * sin(pAngle) * 0.4f).toFloat()
                val pz = (ringRadius * sin(pAngle)).toFloat()

                val (sx, sy, scale) = project3D(px, py, pz)
                val pRadius = (4.5f * scale).coerceIn(1.5f, 8f)
                val alpha = (0.9f * (scale / 1.2f)).coerceIn(0.25f, 1f)

                // Particle outer glow
                drawCircle(
                    color = accentColor.copy(alpha = alpha * 0.4f),
                    radius = pRadius * 2.2f,
                    center = Offset(sx, sy)
                )
                // Particle solid core
                drawCircle(
                    color = Color.White.copy(alpha = alpha),
                    radius = pRadius,
                    center = Offset(sx, sy)
                )
            }

            // Central Holographic Energy Diamond
            val coreSize = maxR * 0.38f * pulseScale
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color.White.copy(alpha = 0.9f),
                        primaryColor,
                        secondaryColor.copy(alpha = 0.7f),
                        Color.Transparent
                    ),
                    center = center,
                    radius = coreSize * 1.2f
                )
            )
        }

        // Central Hologram Icon + Level
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "⚡",
                fontSize = 32.sp
            )
            Text(
                text = "LVL $userLevel",
                fontSize = 11.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 1.2.sp,
                color = Color.White
            )
            Text(
                text = rankTitle.uppercase(),
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                color = BrandGold
            )
        }

        // Floating 3D HUD Satellite Badge 1 (Top Left: Streak)
        ThreeDeeFloatingSatellite(
            icon = "🔥",
            label = "$streakCount Days",
            subLabel = "Streak",
            color = BrandGold,
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(start = 12.dp, top = 16.dp)
        )

        // Floating 3D HUD Satellite Badge 2 (Top Right: XP)
        ThreeDeeFloatingSatellite(
            icon = "⚡",
            label = "$xpAmount",
            subLabel = "Total XP",
            color = BrandPrimary,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(end = 12.dp, top = 16.dp)
        )

        // Floating 3D HUD Satellite Badge 3 (Bottom Left: Rank)
        ThreeDeeFloatingSatellite(
            icon = "🏆",
            label = rankTitle,
            subLabel = "Tier",
            color = BrandEmerald,
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(start = 12.dp, bottom = 12.dp)
        )

        // Floating 3D HUD Satellite Badge 4 (Bottom Right: Status)
        ThreeDeeFloatingSatellite(
            icon = "🌐",
            label = "100%",
            subLabel = "Peer Active",
            color = BrandSecondary,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 12.dp, bottom = 12.dp)
        )
    }
}

/**
 * 3D Glassmorphic Satellite HUD tile that floats around the 3D core
 */
@Composable
fun ThreeDeeFloatingSatellite(
    icon: String,
    label: String,
    subLabel: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "hudFloat")
    val floatOffset by infiniteTransition.animateFloat(
        initialValue = -3f,
        targetValue = 3f,
        animationSpec = infiniteRepeatable(
            animation = tween(1800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "hudFloatOffset"
    )

    Surface(
        modifier = modifier
            .graphicsLayer {
                translationY = floatOffset
                shadowElevation = 8.dp.toPx()
                cameraDistance = 12f * density
            }
            .clip(RoundedCornerShape(14.dp)),
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.88f),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            Brush.verticalGradient(
                listOf(color.copy(alpha = 0.8f), color.copy(alpha = 0.2f))
            )
        ),
        shadowElevation = 4.dp
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 9.dp, vertical = 5.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(text = icon, fontSize = 14.sp)
            Column {
                Text(
                    text = label,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = subLabel,
                    fontSize = 9.sp,
                    color = color,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

/**
 * Interactive 3D Flip Card (180° rotation along Y axis with genuine camera perspective)
 */
@Composable
fun ThreeDeeFlipCard(
    modifier: Modifier = Modifier,
    shape: RoundedCornerShape = RoundedCornerShape(20.dp),
    frontContent: @Composable () -> Unit,
    backContent: @Composable () -> Unit
) {
    var isFlipped by remember { mutableStateOf(false) }

    val rotationY by animateFloatAsState(
        targetValue = if (isFlipped) 180f else 0f,
        animationSpec = spring(
            dampingRatio = 0.72f,
            stiffness = Spring.StiffnessMediumLow
        ),
        label = "flipRotation"
    )

    val isBackShowing = rotationY > 90f

    Box(
        modifier = modifier
            .graphicsLayer {
                this.rotationY = rotationY
                cameraDistance = 18f * density
                shadowElevation = 8.dp.toPx()
                this.shape = shape
                clip = true
            }
            .shadow(6.dp, shape = shape, spotColor = BrandPrimary.copy(alpha = 0.3f))
            .clip(shape)
            .clickable { isFlipped = !isFlipped }
    ) {
        if (!isBackShowing) {
            frontContent()
        } else {
            // Mirror back content horizontally so it appears right side up
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .graphicsLayer {
                        this.rotationY = 180f
                    }
            ) {
                backContent()
            }
        }
    }
}

/**
 * 3D Tactile Action Button with extruded isometric bevel and mechanical push spring
 */
@Composable
fun ThreeDeeActionButton(
    text: String,
    icon: String = "⚡",
    primaryColor: Color = BrandPrimary,
    bevelColor: Color = BrandPrimaryDark,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isPressed by remember { mutableStateOf(false) }

    val pushOffset by animateDpAsState(
        targetValue = if (isPressed) 4.dp else 0.dp,
        animationSpec = spring(dampingRatio = 0.5f, stiffness = Spring.StiffnessMedium),
        label = "pushOffset"
    )
    val bevelHeight by animateDpAsState(
        targetValue = if (isPressed) 1.dp else 5.dp,
        animationSpec = spring(dampingRatio = 0.5f, stiffness = Spring.StiffnessMedium),
        label = "bevelHeight"
    )

    Box(
        modifier = modifier
            .padding(bottom = 5.dp)
            .pointerInput(Unit) {
                detectTapGestures(
                    onPress = {
                        isPressed = true
                        tryAwaitRelease()
                        isPressed = false
                        onClick()
                    }
                )
            }
    ) {
        // 3D Bevel Base Layer
        Box(
            modifier = Modifier
                .matchParentSize()
                .offset(y = 5.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(bevelColor)
        )

        // 3D Front Face
        Surface(
            modifier = Modifier
                .offset(y = pushOffset)
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp)),
            color = primaryColor,
            shape = RoundedCornerShape(16.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 14.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = icon, fontSize = 18.sp)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = text,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
    }
}
