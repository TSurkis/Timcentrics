package com.timcentrics.screens.user_consent_screen

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.timcentrics.R
import com.timcentrics.ui.theme.TimcentricsTheme
import com.timcentrics.ui.theme.Typography
import org.koin.android.ext.android.inject

class UserConsentActivity : ComponentActivity() {

    private val userConsentCostViewModel: UserConsentCostViewModel by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TimcentricsTheme {
                Scaffold(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                ) { innerPadding ->
                    val screenStatus =
                        userConsentCostViewModel.userConsentScreenState.observeAsState()///by rememberSaveable { mutableStateOf(userConsentCostViewModel.userConsentScreenState) }
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.White)
                    ) {
                        ShowConsentScore(
                            modifier = Modifier
                                .padding(innerPadding)
                                .align(Alignment.Center),
                            score = screenStatus.value?.consentScore ?: 0
                        )
                        ShowConsentButton(
                            modifier = Modifier
                                .padding(innerPadding)
                                .fillMaxWidth()
                                .align(Alignment.BottomCenter),
                            showLoader = screenStatus.value?.status == UserConsentCostStatus.LOADING
                        )
                    }

                    if (screenStatus.value?.status == UserConsentCostStatus.GENERAL_ERROR) {
                        Toast.makeText(this, getString(R.string.general_error), Toast.LENGTH_LONG)
                            .show()
                    }
                }
            }
        }
    }

    @Composable
    private fun ShowConsentButton(modifier: Modifier, showLoader: Boolean) {
        Box(
            modifier = modifier
                .fillMaxWidth()
                .heightIn(0.dp)
        ) {
            Button(
                modifier = modifier.height(75.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Blue),
                onClick = {
                    userConsentCostViewModel.showConsentPrompt()
                })
            {
                if (showLoader) {
                    CircularProgressIndicator(
                        color = Color.White
                    )
                } else {
                    Text(
                        getString(R.string.show_consent),
                        style = Typography.labelSmall
                    )
                }

            }
        }
    }

    @Composable
    private fun ShowConsentScore(modifier: Modifier, score: Int) {
        Column(
            modifier = modifier
                .fillMaxWidth()
                .heightIn(0.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top,
        ) {
            Text(
                score.toString(),
                style = Typography.titleLarge
            )
            Text(
                getString(R.string.consent_score),
                style = Typography.bodyLarge
            )
        }
    }
}