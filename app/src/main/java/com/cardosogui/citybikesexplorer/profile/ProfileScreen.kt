package com.cardosogui.citybikesexplorer.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.cardosogui.citybikesexplorer.R
import com.cardosogui.citybikesexplorer.ui.theme.GreenActive
import com.cardosogui.citybikesexplorer.ui.theme.White

@Composable
fun ProfileRoute(
    modifier: Modifier = Modifier,
    viewModel: ProfileViewModel = hiltViewModel(),
) {
    val uriHandler = LocalUriHandler.current
    val profile = viewModel.profile
    ProfileScreen(
        modifier = modifier,
        profile = profile,
        onSupportClick = { uriHandler.openUri(profile.supportUrl) },
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    profile: ProfileViewModel.Profile,
    modifier: Modifier = Modifier,
    onSupportClick: () -> Unit = {},
) {
    Scaffold(
        modifier = modifier,
        containerColor = White,
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.profile_title)) },
                windowInsets = WindowInsets(0, 0, 0, 0),
            )
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(White),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .background(GreenActive, CircleShape),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_account_box),
                        contentDescription = null,
                        tint = White,
                        modifier = Modifier.size(44.dp),
                    )
                }
                Text(
                    text = "${profile.firstName} ${profile.lastName}",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 12.dp),
                )
            }

            HorizontalDivider()
            InfoRow(label = stringResource(R.string.profile_first_name), value = profile.firstName)
            HorizontalDivider()
            InfoRow(label = stringResource(R.string.profile_last_name), value = profile.lastName)
            HorizontalDivider()
            InfoRow(label = stringResource(R.string.payment_method), value = profile.paymentMethod)
            HorizontalDivider()
            SupportRow(onClick = onSupportClick)
            HorizontalDivider()
        }
    }
}

@Composable
private fun InfoRow(label: String, value: String, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge,
            color = Color.Gray,
            modifier = Modifier.weight(1f),
        )
        Text(text = value, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Medium)
    }
}

@Composable
private fun SupportRow(onClick: () -> Unit, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = stringResource(R.string.profile_support), style = MaterialTheme.typography.bodyLarge)
            Text(
                text = stringResource(R.string.profile_support_subtitle),
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray,
            )
        }
        Icon(
            painter = painterResource(R.drawable.chevron_forward_24dp),
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
@Preview
private fun ProfileScreenPreview() {
    ProfileScreen(
        profile = ProfileViewModel.Profile(
            firstName = "Guilherme",
            lastName = "Cardoso",
            paymentMethod = "Visa •••• 4242",
            supportUrl = "https://example.com",
        ),
    )
}
