package net.softglobe.groceryplanner.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import net.softglobe.groceryplanner.R
import net.softglobe.groceryplanner.ui.theme.Black
import net.softglobe.groceryplanner.ui.theme.Primary
import net.softglobe.groceryplanner.ui.theme.PrimaryDark

@Composable
fun AboutAppScreen(
    appVersion: String,
    copyrightText: String,
    onRateUsClick: () -> Unit,
    onContactClick: () -> Unit,
    onPrivacyPolicyClick: () -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(32.dp))

            Image(
                painter = painterResource(id = R.drawable.app_icon),
                contentDescription = null,
                modifier = Modifier.size(100.dp)
            )

            Text(
                text = stringResource(id = R.string.app_name),
                fontSize = 25.sp,
                fontWeight = FontWeight.Bold,
                color = Primary,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )

            Text(
                text = appVersion,
                fontSize = 18.sp,
                color = Black,
                modifier = Modifier.padding(top = 5.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            AboutItem(
                iconResId = R.drawable.rating_icon,
                text = "Rate us",
                onClick = onRateUsClick
            )

            HorizontalDivider(
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 5.dp),
                thickness = 1.dp,
                color = Black
            )

            AboutItem(
                iconResId = R.drawable.email_icon,
                text = "Contact",
                onClick = onContactClick
            )

            HorizontalDivider(
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 5.dp),
                thickness = 1.dp,
                color = Black
            )

            AboutItem(
                iconResId = R.drawable.lock_icon,
                text = "Privacy policy",
                onClick = onPrivacyPolicyClick
            )
        }

        Text(
            text = copyrightText,
            fontSize = 18.sp,
            color = Black,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 5.dp),
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun AboutItem(
    iconResId: Int,
    text: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(start = 20.dp, top = 4.dp, bottom = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(id = iconResId),
            contentDescription = null,
            tint = PrimaryDark,
            modifier = Modifier.size(24.dp)
        )
        Text(
            text = "  $text",
            fontSize = 25.sp,
            fontWeight = FontWeight.Bold,
            color = PrimaryDark
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun AboutAppScreenPreview() {
    AboutAppScreen(
        appVersion = "V 1.0.0",
        onRateUsClick = {},
        onContactClick = {},
        onPrivacyPolicyClick = {},
        copyrightText = "©2026 Softglobe technolgies"
    )
}
