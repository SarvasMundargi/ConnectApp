package com.example.connect.screens

import android.content.Intent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicText
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.semantics.Role.Companion.Button
import androidx.compose.ui.semantics.Role.Companion.Image
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

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        when (val state = meme) {

            is NetworkResponse.Loading -> {
                Text("Loading...")
            }

            is NetworkResponse.Success -> {
               // handleData(state.data)
                val memeData = state.data
                memeData.url?.let { url ->
                    // Displaying the meme image
                    Image(
                        painter = rememberAsyncImagePainter(model = url),
                        contentDescription = "Meme",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(300.dp)
                            .padding(bottom = 16.dp),
                        contentScale = ContentScale.Crop
                    )

                    // Share button
                    Button(
                        onClick = {
                            val intent = Intent().apply {
                                action = Intent.ACTION_SEND
                                putExtra(Intent.EXTRA_TEXT, url)
                                type = "text/plain"
                            }
                            navHostController.context.startActivity(Intent.createChooser(intent, "Share Meme"))
                        }
                    ) {
                        Text("Share")
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Next button
                    Button(
                        onClick = {
                            memeViewModel.getUrl() // Load next meme
                        }
                    ) {
                        Text("Next")
                    }
                } ?: run {
                    Text("URL is not available")
                }
            }
            is NetworkResponse.Error -> {
                Text("Error: ${state.message}")
            }

            else -> {null}
        }
    }
}

