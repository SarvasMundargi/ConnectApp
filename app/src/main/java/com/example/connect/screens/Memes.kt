package com.example.connect.screens

import android.content.Intent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import com.example.connect.NetworkResponse
import com.example.connect.viewmodel.MemeViewModel

@Composable
fun Memes(navHostController: NavHostController) {
    val memeViewModel: MemeViewModel = viewModel()
    val meme by memeViewModel.results.observeAsState(null)

    // Load meme when composable is first displayed
    LaunchedEffect(Unit) {
        memeViewModel.getUrl()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        when (val state = meme) {
            is NetworkResponse.Loading -> {
                Box(
                    modifier = Modifier.align(Alignment.Center)
                ) {
                    Text("Loading...")
                }
            }

            is NetworkResponse.Success -> {
                val memeData = state.data
                memeData.url.let { url ->
                    // Meme image at the top
                    Image(
                        painter = rememberAsyncImagePainter(model = url),
                        contentDescription = "Meme",
                        modifier = Modifier
                            .fillMaxSize()
                            .align(Alignment.Center)
                            .padding(bottom = 16.dp)
                    )

                    // Buttons at the bottom
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.BottomCenter)
                            .padding(bottom = 16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        // Share button
                        Button(
                            onClick = {
                                val intent = Intent().apply {
                                    action = Intent.ACTION_SEND
                                    putExtra(Intent.EXTRA_TEXT, url)
                                    type = "text/plain"
                                }
                                navHostController.context.startActivity(Intent.createChooser(intent, "Share Meme"))
                            },
                            modifier = Modifier.weight(1f) // 50% width
                        ) {
                            Text("Share")
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        // Next button
                        Button(
                            onClick = {
                                memeViewModel.getUrl() // Load next meme
                            },
                            modifier = Modifier.weight(1f) // 50% width
                        ) {
                            Text("Next")
                        }
                    }
                } ?: run {
                    Box(
                        modifier = Modifier.align(Alignment.Center)
                    ) {
                        Text("URL is not available")
                    }
                }
            }

            is NetworkResponse.Error -> {
                Box(
                    modifier = Modifier.align(Alignment.Center)
                ) {
                    Text("Error: ${state.message}")
                }
            }

            else -> { /* No-Op */ }
        }
    }
}
