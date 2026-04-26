package com.numerical.analysis.solver.ui.screens.splash

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.sqrt

// ─────────────────────────────────────────────────────────────────────────────
// DESIGN TOKENS — Deep Obsidian + Pure Chrome
// No warm tones. No yellows. No organic shaping.
// ─────────────────────────────────────────────────────────────────────────────

private val CHROME_MID     = Color(0xFFD8DCE8)   // cool gray mid-ring
private val CHROME_OUTER   = Color(0xFF8A909E)   // steel gray outer halo
private val CHROME_FIELD   = Color(0xFF3A3F4D)   // dark field — keeps cool tone

// ─────────────────────────────────────────────────────────────────────────────
// SPLASH SCREEN
// ─────────────────────────────────────────────────────────────────────────────

/**
 * "Prismatic Core" Cinematic Splash Screen
 *
 * Design rules enforced throughout:
 *  • Sphere rendered entirely on [Canvas] — pure geometry, zero Box distortion.
 *  • ScaleX and ScaleY always move together — no oval shapes ever.
 *  • Palette is strictly cool-gray/white — no warm or organic tones.
 *  • Recoil is a fast elastic snap, not a squash/stretch (no egg shapes).
 *
 * Timeline:
 *  Phase 1 → 0.00 – 1.60 s  : Chrome title shimmers in → fades
 *  Phase 2 → 1.60 – 2.40 s  : Prismatic sphere launched from bottom → mag-lock
 *  Pause   → 2.40 – 2.70 s  : Magnetic snap recoil (symmetric, no oval)
 *  Phase 3 → 2.70 – 3.70 s  : Light portal detonation — black consumed by white
 *
 * @param onAnimationFinished  Called the instant the canvas is fully white.
 */
@Composable
fun SplashScreen(onAnimationFinished: () -> Unit) {

    val configuration = LocalConfiguration.current
    val density       = LocalDensity.current

    val obsidian = MaterialTheme.colorScheme.background
    val chrome = MaterialTheme.colorScheme.onBackground
    val portalWhite = MaterialTheme.colorScheme.surface

    val screenDiagonalPx = with(density) {
        val w = configuration.screenWidthDp.dp.toPx()
        val h = configuration.screenHeightDp.dp.toPx()
        sqrt(w * w + h * h) / 2f + 20f
    }

    val startOffsetPx = with(density) {
        (configuration.screenHeightDp.dp / 2f + 80.dp).toPx()
    }

    // ── Animatables ──────────────────────────────────────────────────────────
    val titleAlpha    = remember { Animatable(0f) }
    val shimmerPos    = remember { Animatable(0f) }   // 0..1, drives highlight sweep
    val sphereOffsetY = remember { Animatable(startOffsetPx) }
    val sphereAlpha   = remember { Animatable(0f) }
    val sphereScale   = remember { Animatable(1f) }   // UNIFORM scale only — no X/Y split
    val portalRadius  = remember { Animatable(0f) }
    val portalAlpha   = remember { Animatable(0f) }

    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {

        // ── PHASE 1: Chrome title fade-in + shimmer sweep ─────────────────────
        titleAlpha.animateTo(1f, tween(700, easing = FastOutSlowInEasing))

        scope.launch {
            delay(300)
            shimmerPos.animateTo(
                targetValue   = 1f,
                animationSpec = tween(800, easing = CubicBezierEasing(0.4f, 0f, 0.6f, 1f))
            )
        }

        delay(1000)
        titleAlpha.animateTo(0f, tween(500, easing = FastOutSlowInEasing))

        // ── PHASE 2: Sphere launch with mag-lock deceleration ─────────────────
        //
        // EaseOutExpo — extremely fast approach, near-instant halt at center.
        // Feels like a high-tech magnetic rail stopping.
        val easeOutExpo = CubicBezierEasing(0.16f, 1f, 0.3f, 1f)

        scope.launch { sphereAlpha.animateTo(1f, tween(140)) }

        sphereOffsetY.animateTo(
            targetValue   = 0f,
            animationSpec = tween(750, easing = easeOutExpo)
        )

        // ── PAUSE: Magnetic snap recoil — symmetric ring pulse, no oval ───────
        //
        // Sphere scales UP then snaps back to 1.0 uniformly.
        // Because scaleX == scaleY at all times, it stays a perfect circle.
        sphereScale.animateTo(1.22f, tween(70,  easing = FastOutLinearInEasing))
        sphereScale.animateTo(0.92f, tween(80,  easing = FastOutSlowInEasing))
        sphereScale.animateTo(1.04f, tween(100, easing = FastOutSlowInEasing))
        sphereScale.animateTo(1.00f, tween(80,  easing = FastOutSlowInEasing))

        delay(220)   // stillness — the orb is locked

        // ── PHASE 3: Light portal detonation ─────────────────────────────────
        //
        // CubicBezier(0.4, 0, 0.2, 1) — Material motion "standard" easing.
        // Confident start, smooth but decisive finish. Clinical, not flashy.
        scope.launch { sphereAlpha.animateTo(0f, tween(100)) }
        scope.launch { portalAlpha.animateTo(1f, tween(60)) }

        portalRadius.animateTo(
            targetValue   = screenDiagonalPx,
            animationSpec = tween(
                durationMillis = 1000,
                easing         = CubicBezierEasing(0.4f, 0f, 0.2f, 1f)
            )
        )

        onAnimationFinished()
    }

    // ── UI ────────────────────────────────────────────────────────────────────
    Box(
        modifier         = Modifier.fillMaxSize().background(obsidian),
        contentAlignment = Alignment.Center
    ) {

        // ── Layer 1: Portal expansion circle (Phase 3) ────────────────────────
        Canvas(modifier = Modifier.fillMaxSize()) {
            if (portalRadius.value <= 0f) return@Canvas

            val cx = size.width  / 2f
            val cy = size.height / 2f
            val r  = portalRadius.value

            // Thin cool-gray leading edge — the portal "frontier"
            if (r < screenDiagonalPx * 0.97f) {
                drawCircle(
                    brush = Brush.radialGradient(
                        colorStops = arrayOf(
                            0.00f to portalWhite,
                            0.91f to portalWhite,
                            0.96f to CHROME_MID.copy(alpha = 0.45f),
                            0.99f to CHROME_OUTER.copy(alpha = 0.15f),
                            1.00f to Color.Transparent
                        ),
                        center = Offset(cx, cy),
                        radius = r * 1.05f
                    ),
                    radius = r * 1.05f,
                    center = Offset(cx, cy),
                    alpha  = portalAlpha.value
                )
            }

            drawCircle(
                color  = portalWhite,
                radius = r,
                center = Offset(cx, cy),
                alpha  = portalAlpha.value
            )
        }

        // ── Layer 2: Prismatic sphere (Phase 2 + early Phase 3) ───────────────
        //
        // Drawn entirely on Canvas — Compose Box with radialGradient can
        // introduce sub-pixel oval artifacts at small sizes. Canvas drawCircle
        // is always a geometrically perfect circle.
        Canvas(modifier = Modifier.fillMaxSize()) {
            if (sphereAlpha.value <= 0f) return@Canvas

            val cx = size.width  / 2f
            val cy = size.height / 2f + sphereOffsetY.value
            val s  = sphereScale.value

            // Ring 4 — outermost field haze (barely visible, sets the "presence")
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        CHROME_FIELD.copy(alpha = 0.12f),
                        Color.Transparent
                    ),
                    center = Offset(cx, cy),
                    radius = 110f * s
                ),
                radius = 110f * s,
                center = Offset(cx, cy),
                alpha  = sphereAlpha.value
            )

            // Ring 3 — steel diffusion ring
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        CHROME_OUTER.copy(alpha = 0.30f),
                        CHROME_OUTER.copy(alpha = 0.08f),
                        Color.Transparent
                    ),
                    center = Offset(cx, cy),
                    radius = 60f * s
                ),
                radius = 60f * s,
                center = Offset(cx, cy),
                alpha  = sphereAlpha.value
            )

            // Ring 2 — cool-gray inner glow (uniform, no directional bias)
            drawCircle(
                brush = Brush.radialGradient(
                    colorStops = arrayOf(
                        0.00f to portalWhite,
                        0.35f to CHROME_MID.copy(alpha = 0.90f),
                        0.70f to CHROME_OUTER.copy(alpha = 0.55f),
                        1.00f to Color.Transparent
                    ),
                    center = Offset(cx, cy),
                    radius = 34f * s
                ),
                radius = 34f * s,
                center = Offset(cx, cy),
                alpha  = sphereAlpha.value
            )

            // Ring 1 — pure electric white core (the "prismatic" nucleus)
            // Very small, perfectly circular. No offset, no shading bias.
            drawCircle(
                color  = portalWhite,
                radius = 9f * s,
                center = Offset(cx, cy),
                alpha  = sphereAlpha.value
            )

            // Rim light — a faint chrome ring at the sphere edge
            // Drawn as a stroked circle, ensuring perfect symmetry
            drawCircle(
                color       = CHROME_MID.copy(alpha = 0.35f),
                radius      = 20f * s,
                center      = Offset(cx, cy),
                style       = androidx.compose.ui.graphics.drawscope.Stroke(
                    width = 1.2f * s
                ),
                alpha       = sphereAlpha.value
            )
        }

        // ── Layer 3: Chrome title (Phase 1) ───────────────────────────────────
        if (titleAlpha.value > 0f) {
            val p            = shimmerPos.value        // 0..1
            val shimmerAlpha = when {
                p < 0.1f -> 0f
                p > 0.9f -> 0f
                else     -> {
                    // Bell curve: peaks at p=0.5
                    val norm = (p - 0.1f) / 0.8f      // 0..1
                    val bell = 1f - (2f * norm - 1f) * (2f * norm - 1f)
                    bell * 0.50f
                }
            }

            Box(contentAlignment = Alignment.Center) {
                // Base chrome layer — cool gray, not silver-warm
                Text(
                    text          = "NUMERICAL SOLVER",
                    color         = CHROME_OUTER,
                    fontSize      = 17.sp,
                    fontWeight    = FontWeight.W200,
                    fontFamily    = FontFamily.SansSerif,
                    letterSpacing = 10.sp,
                    modifier      = Modifier.alpha(titleAlpha.value)
                )
                // Shimmer pass — white, centered bell-curve alpha
                Text(
                    text          = "NUMERICAL SOLVER",
                    color         = portalWhite,
                    fontSize      = 17.sp,
                    fontWeight    = FontWeight.W200,
                    fontFamily    = FontFamily.SansSerif,
                    letterSpacing = 10.sp,
                    modifier      = Modifier.alpha(shimmerAlpha * titleAlpha.value)
                )
            }
        }
    }
}