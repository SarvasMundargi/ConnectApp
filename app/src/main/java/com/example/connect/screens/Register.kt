package com.example.connect.screens

import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.Button
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import com.example.connect.AuthViewModel
import com.example.connect.R
import com.example.connect.navigation.Routes

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Register(navController: NavHostController){
    Column(modifier= Modifier
        .fillMaxSize()
        .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center){
        var email by remember {
            mutableStateOf("")
        }

        var name by remember {
            mutableStateOf("")
        }

        var username by remember {
            mutableStateOf("")
        }

        var bio by remember {
            mutableStateOf("")
        }

        var password by remember {
            mutableStateOf("")
        }

        var imageUri by remember {
            mutableStateOf<Uri?>(null)
        }

        val authViewModel: AuthViewModel= viewModel()
        val firebaseUser by authViewModel.firebaseUser.observeAsState(null)

        LaunchedEffect(firebaseUser) {
            if (firebaseUser != null) {
                navController.navigate(Routes.BottomNav.routes) {
                    popUpTo(navController.graph.startDestinationId)
                    launchSingleTop = true
                }
            }
        }

        val permissionToRequest=if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.TIRAMISU){
            android.Manifest.permission.READ_MEDIA_IMAGES
        }
        else{
            android.Manifest.permission.READ_EXTERNAL_STORAGE
        }

        val context= LocalContext.current

        val launcher= rememberLauncherForActivityResult(contract = ActivityResultContracts.GetContent()){
            uri: Uri? ->
            imageUri=uri
        }


        val permissionLauncher=
            rememberLauncherForActivityResult(contract = ActivityResultContracts.RequestPermission()){
                isGranted: Boolean->
                if(isGranted){

                }
                else{

                }
            }

        Text(text = "Register Here",style= TextStyle(
            fontWeight = FontWeight.ExtraBold,
            fontSize = 30.sp
        ))

        Spacer(modifier = Modifier.height(16.dp))

        Image(
            painter = if (imageUri == null) {
                painterResource(id = R.drawable.person) // Default icon resource
            } else {
                rememberAsyncImagePainter(model = imageUri) // Image from the URI
            },
            contentDescription = "person",
            modifier = Modifier
                .size(96.dp)
                .clip(CircleShape)
                .clickable {
                    val isGranted = ContextCompat.checkSelfPermission(
                        context, permissionToRequest
                    ) == PackageManager.PERMISSION_GRANTED

                    if (isGranted) {
                        launcher.launch("image/*")
                    } else {
                        permissionLauncher.launch(permissionToRequest)
                    }
                }
        )

        Spacer(modifier = Modifier.height(30.dp))

        OutlinedTextField(value = name, onValueChange = {
            name=it
        },label={
            Text(text = "Enter Your Name")
        }, keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Text
        ),modifier=Modifier.fillMaxWidth()
            , singleLine = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(value = username, onValueChange = {
            username=it
        },label={
            Text(text = "Enter Your Username")
        }, keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Text
        ),modifier=Modifier.fillMaxWidth()
            , singleLine = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(value = bio, onValueChange = {
            bio=it
        },label={
            Text(text = "Enter Bio")
        }, keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Email
        ),modifier=Modifier.fillMaxWidth()
            , singleLine = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(value = email, onValueChange = {
            email=it
        },label={
            Text(text = "Enter Your Email")
        }, keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Email
        ),modifier=Modifier.fillMaxWidth()
            , singleLine = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(value = password, onValueChange = {
            password=it
        },label={
            Text(text = "Enter Password")
        }, keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Password
        ), visualTransformation = PasswordVisualTransformation(),
            modifier=Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(30.dp))

        ElevatedButton(onClick = {
            if(name.isEmpty() || email.isEmpty() ||
                username.isEmpty() || bio.isEmpty() || password.isEmpty()){
                Toast.makeText(context,"Please fill all the details",Toast.LENGTH_SHORT).show()
            }
            else{
                imageUri?.let { authViewModel.register(email,password,name,bio,username, it,context) }

            }
        },
            modifier=Modifier.fillMaxWidth()) {
            Text(text = "Register", style = TextStyle(
                fontSize = 30.sp,
                fontWeight = FontWeight.ExtraBold
            ), modifier = Modifier.padding(vertical = 6.dp))
        }

        Spacer(modifier = Modifier.height(16.dp))

        TextButton(onClick = {
            navController.navigate(Routes.Login.routes){
                popUpTo(navController.graph.startDestinationId)
                launchSingleTop=true
            }
        }) {
            Text(text = "Already have an account? Login",
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun RegisterView(){
    Register(navController = NavHostController(LocalContext.current))
}