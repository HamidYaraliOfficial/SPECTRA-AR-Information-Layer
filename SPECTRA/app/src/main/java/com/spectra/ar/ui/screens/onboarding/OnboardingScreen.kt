package com.spectra.ar.ui.screens.onboarding

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.spectra.ar.R

private data class OnboardingPage(val titleRes: Int, val bodyRes: Int)

private val pages = listOf(
    OnboardingPage(R.string.onboarding_welcome_title, R.string.onboarding_welcome_body),
    OnboardingPage(R.string.onboarding_privacy_title, R.string.onboarding_privacy_body),
    OnboardingPage(R.string.onboarding_permissions_title, R.string.onboarding_permissions_body)
)

@Composable
fun OnboardingScreen(onFinished: () -> Unit, viewModel: OnboardingViewModel = hiltViewModel()) {
    val pagerState = rememberPagerState(pageCount = { pages.size })

    Scaffold { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).padding(24.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            HorizontalPager(state = pagerState, modifier = Modifier.fillMaxSize().weight(1f)) { index ->
                val page = pages[index]
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.Start
                ) {
                    Text(stringResource(page.titleRes), style = MaterialTheme.typography.headlineMedium)
                    Text(stringResource(page.bodyRes), style = MaterialTheme.typography.bodyLarge, modifier = Modifier.padding(top = 16.dp))
                }
            }
            Column {
                Button(
                    onClick = {
                        viewModel.completeOnboarding()
                        onFinished()
                    },
                    modifier = Modifier.fillMaxWidthCompat()
                ) { Text(stringResource(if (pagerState.currentPage == pages.lastIndex) R.string.onboarding_get_started else R.string.onboarding_next)) }
                TextButton(onClick = { viewModel.completeOnboarding(); onFinished() }) {
                    Text(stringResource(R.string.onboarding_skip))
                }
            }
        }
    }
}

private fun Modifier.fillMaxWidthCompat() = this.then(androidx.compose.foundation.layout.fillMaxWidth())
