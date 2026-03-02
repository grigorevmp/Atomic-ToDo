package com.grigorevmp.simpletodo.ui.settings.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.grigorevmp.simpletodo.ui.settings.common.AccountPill
import com.grigorevmp.simpletodo.ui.settings.common.SettingsCard
import org.jetbrains.compose.resources.stringResource
import simpletodo.composeapp.generated.resources.Res
import simpletodo.composeapp.generated.resources.settings_account_devices
import simpletodo.composeapp.generated.resources.settings_account_profile
import simpletodo.composeapp.generated.resources.settings_account_soon
import simpletodo.composeapp.generated.resources.settings_account_subscription
import simpletodo.composeapp.generated.resources.settings_account_subtitle
import simpletodo.composeapp.generated.resources.settings_account_title


@Composable
fun AccountComingSoonCard() {
    Box(Modifier.fillMaxWidth()) {
        SettingsCard(
            title = stringResource(Res.string.settings_account_title),
            description = stringResource(Res.string.settings_account_subtitle)
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                AccountPill(text = stringResource(Res.string.settings_account_profile))
                AccountPill(text = stringResource(Res.string.settings_account_devices))
                AccountPill(text = stringResource(Res.string.settings_account_subscription))
            }
        }
        RepairOverlay(
            text = stringResource(Res.string.settings_account_soon),
            modifier = Modifier.align(Alignment.Center)
        )
    }
}


@Composable
private fun RepairOverlay(
    text: String,
    modifier: Modifier = Modifier
) {
    val stripeColor = Color(0xFF111111)
    val baseColor = Color(0xFF7F6300)

    Box(
        modifier
            .fillMaxSize()
            .clip(MaterialTheme.shapes.large)
    ) {
        Box(
            Modifier
                .padding(vertical = 12.dp)
                .align(Alignment.Center)
                .graphicsLayer(rotationZ = -6f)
                .clip(MaterialTheme.shapes.medium)
                .background(baseColor)
                .drawBehind {
                    val stripeWidth = 14.dp.toPx()
                    val gap = 10.dp.toPx()
                    var x = -size.width
                    while (x < size.width * 2) {
                        drawLine(
                            color = stripeColor,
                            start = androidx.compose.ui.geometry.Offset(x, -40f),
                            end = androidx.compose.ui.geometry.Offset(
                                x + size.height,
                                size.height + 40
                            ),
                            strokeWidth = stripeWidth
                        )
                        x += stripeWidth + gap
                    }
                }
                .padding(horizontal = 18.dp, vertical = 10.dp)
        ) {
            Text(
                text,
                style = MaterialTheme.typography.titleSmall,
                color = Color.White,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 6.dp)
            )
        }
    }
}

@Preview
@Composable
fun RepairOverlayPreview() {
    RepairOverlay(
        text = "Test"
    )
}
