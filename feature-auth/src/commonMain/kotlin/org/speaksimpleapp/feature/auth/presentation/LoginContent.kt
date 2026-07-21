package org.speaksimpleapp.feature.auth.presentation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import speak_simple_app.feature_auth.generated.resources.Res
import speak_simple_app.feature_auth.generated.resources.google_g
import speak_simple_app.feature_auth.generated.resources.warning

@Composable
fun LoginContent(
    component: LoginComponent,
    modifier: Modifier = Modifier,
) {
    val state by component.uiState.collectAsState()
    LoginScreen(
        state = state,
        onGoogleLoginClicked = { component.dispatch(LoginComponent.Event.GoogleLoginClicked) },
        onErrorDismissed = { component.dispatch(LoginComponent.Event.ErrorDismissed) },
        modifier = modifier,
    )
}

@Composable
internal fun LoginScreen(
    state: LoginUiState,
    onGoogleLoginClicked: () -> Unit,
    onErrorDismissed: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding(),
    ) {
        LoginBody(
            state = state,
            onGoogleLoginClicked = onGoogleLoginClicked,
            modifier = Modifier
                .align(Alignment.Center)
                .fillMaxWidth()
                .padding(horizontal = 32.dp),
        )
        LoginErrorSnackbar(
            error = state.error,
            onDismissed = onErrorDismissed,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .navigationBarsPadding()
                .padding(horizontal = 24.dp, vertical = 16.dp),
        )
    }
}

@Composable
private fun LoginBody(
    state: LoginUiState,
    onGoogleLoginClicked: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.widthIn(max = 520.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        BrandMark(state.brandMark)
        Spacer(Modifier.height(28.dp))
        Text(
            text = stringResource(state.title),
            color = MaterialTheme.colorScheme.onBackground,
            fontSize = 30.sp,
            lineHeight = 32.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
        )
        Spacer(Modifier.height(20.dp))
        Text(
            text = stringResource(state.subtitle),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = 18.sp,
            lineHeight = 24.sp,
            fontWeight = FontWeight.Normal,
            textAlign = TextAlign.Center,
        )
        Spacer(Modifier.height(28.dp))
        GoogleSignInButton(
            title = state.googleButtonTitle,
            iconContentDescription = state.googleIconContentDescription,
            isSigningIn = state.isGoogleSignInInProgress,
            onClick = onGoogleLoginClicked,
        )
        Spacer(Modifier.height(20.dp))
        Text(
            text = stringResource(state.privacyMessage),
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.82f),
            fontSize = 15.sp,
            lineHeight = 20.sp,
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
private fun BrandMark(text: StringResource) {
    val shape = RoundedCornerShape(28.dp)
    val glowColor = Color(0xFF914FCE)
    val glowAlpha = if (MaterialTheme.colorScheme.background.luminance() < 0.5f) 0.72f else 0.34f
    Box(
        modifier = Modifier.size(80.dp),
        contentAlignment = Alignment.Center,
    ) {
        Box(
            modifier = Modifier
                .requiredSize(120.dp)
                .background(
                    brush = Brush.radialGradient(
                        colorStops = arrayOf(
                            0f to glowColor.copy(alpha = glowAlpha),
                            0.58f to glowColor.copy(alpha = glowAlpha * 0.72f),
                            1f to Color.Transparent,
                        ),
                    ),
                    shape = CircleShape,
                ),
        )
        Box(
            modifier = Modifier
                .size(80.dp)
                .clip(shape)
                .background(
                    Brush.linearGradient(
                        listOf(Color(0xFFA66DE3), Color(0xFF713AA9)),
                    ),
                ),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = stringResource(text),
                color = Color.White,
                fontSize = 38.sp,
                fontWeight = FontWeight.Bold,
            )
        }
    }
}

@Composable
private fun GoogleSignInButton(
    title: StringResource,
    iconContentDescription: StringResource,
    isSigningIn: Boolean,
    onClick: () -> Unit,
) {
    val colorScheme = MaterialTheme.colorScheme
    val isDark = colorScheme.background.luminance() < 0.5f
    val background = if (isDark) colorScheme.surfaceVariant else colorScheme.surface
    val shape = RoundedCornerShape(28.dp)
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.97f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium,
        ),
        label = "Google sign-in button press",
    )
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 56.dp)
            .scale(scale)
            .clip(shape)
            .background(background)
            .border(1.dp, colorScheme.outline, shape)
            .clickable(
                enabled = !isSigningIn,
                role = Role.Button,
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick,
            )
            .padding(horizontal = 24.dp, vertical = 14.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (isSigningIn) {
            CircularProgressIndicator(
                modifier = Modifier.size(22.dp),
                strokeWidth = 2.dp,
                color = colorScheme.primary,
            )
        } else {
            Icon(
                painter = painterResource(Res.drawable.google_g),
                contentDescription = stringResource(iconContentDescription),
                modifier = Modifier.size(24.dp),
                tint = Color.Unspecified,
            )
            Spacer(Modifier.width(16.dp))
            Text(
                text = stringResource(title),
                color = colorScheme.onSurface,
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
            )
        }
    }
}

@Composable
private fun LoginErrorSnackbar(
    error: LoginErrorUiState?,
    onDismissed: () -> Unit,
    modifier: Modifier = Modifier,
) {
    LaunchedEffect(error) {
        if (error != null) {
            delay(8_000)
            onDismissed()
        }
    }
    AnimatedVisibility(
        visible = error != null,
        modifier = modifier.fillMaxWidth().widthIn(max = 560.dp),
        enter = fadeIn() + slideInVertically { it / 2 },
        exit = fadeOut() + slideOutVertically { it / 2 },
    ) {
        val displayedError = error ?: return@AnimatedVisibility
        val shape = RoundedCornerShape(22.dp)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surfaceVariant, shape)
                .border(1.dp, MaterialTheme.colorScheme.outline, shape)
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(Color(0x33FF7657), CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    painter = painterResource(Res.drawable.warning),
                    contentDescription = stringResource(displayedError.iconContentDescription),
                    modifier = Modifier.size(22.dp),
                    tint = Color.Unspecified,
                )
            }
            Spacer(Modifier.width(14.dp))
            Column(Modifier.weight(1f)) {
                Text(
                    text = stringResource(displayedError.title),
                    color = MaterialTheme.colorScheme.onSurface,
                    fontSize = 15.sp,
                    lineHeight = 18.sp,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    text = stringResource(displayedError.message),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 14.sp,
                    lineHeight = 18.sp,
                )
            }
        }
    }
}
