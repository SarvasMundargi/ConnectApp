package com.example.connect.screens

import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import com.example.connect.AuthViewModel
import com.example.connect.R
import com.example.connect.navigation.Routes
import com.example.connect.utils.SharePref
import com.example.connect.viewmodel.AddThreadViewModel
import com.google.firebase.auth.FirebaseAuth

@Composable
fun AddThreads(navHostController: NavHostController) {
    ConstraintLayout(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        val context = LocalContext.current

        var thread by remember {
            mutableStateOf("")
        }

        var imageUri by remember {
            mutableStateOf<Uri?>(null)
        }

        val (crossPic, text, logo, username, editText, attachMedia, replyText, button, imageBox) = createRefs()

        val threadViewModel: AddThreadViewModel= viewModel()
        val isPosted by threadViewModel.isPosted.observeAsState(false)


        val permissionToRequest=if (Build.VERSION.SDK_INT>= Build.VERSION_CODES.TIRAMISU){
            android.Manifest.permission.READ_MEDIA_IMAGES
        }
        else{
            android.Manifest.permission.READ_EXTERNAL_STORAGE
        }

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

        LaunchedEffect(isPosted){
            if(isPosted == true){
                thread=""
                imageUri=null
                Toast.makeText(context,"Post Added",Toast.LENGTH_SHORT).show()

                navHostController.navigate(Routes.Home.routes) {
                    popUpTo(Routes.AddThread.routes){
                        inclusive=true
                    }
                }
            }
        }

        Image(
            painter = painterResource(id = R.drawable.close),
            contentDescription = "close",
            modifier = Modifier
                .constrainAs(crossPic) {
                    top.linkTo(parent.top)
                    start.linkTo(parent.start)
                }
                .clickable {
                    navHostController.navigate(Routes.Home.routes) {
                        popUpTo(Routes.AddThread.routes){
                            inclusive=true
                        }
                    }
                }
        )

        Text(
            text = "Add Thread",
            style = TextStyle(
                fontWeight = FontWeight.ExtraBold,
                fontSize = 30.sp
            ),
            modifier = Modifier.constrainAs(text) {
                top.linkTo(crossPic.top)
                start.linkTo(crossPic.end, margin = 12.dp)
                bottom.linkTo(crossPic.bottom)
            }
        )

        Image(
            painter = rememberAsyncImagePainter(model =SharePref.getImage(context)),
            contentDescription = "profile picture",
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .constrainAs(logo) {
                    top.linkTo(text.bottom)
                    start.linkTo(parent.start)
                }, contentScale = ContentScale.Crop
        )

        Text(
            text = SharePref.getUserName(context),
            style = TextStyle(
                fontSize = 20.sp
            ),
            modifier = Modifier.constrainAs(username) {
                top.linkTo(logo.top)
                start.linkTo(logo.end, margin = 12.dp)
                bottom.linkTo(logo.bottom)
            }
        )

        TextField(
            hint = "Start a thread...",
            value = thread,
            onValueChange = { thread = it },
            modifier = Modifier
                .constrainAs(editText) {
                    top.linkTo(username.bottom, margin = 12.dp)
                    start.linkTo(username.start)
                    end.linkTo(parent.end)
                }
                .padding(horizontal = 8.dp, vertical = 8.dp)
                .fillMaxWidth()
        )

        Log.i("uri", imageUri.toString())

        if (imageUri == null) {
            Image(
                painter = painterResource(id = R.drawable.attach),
                contentDescription = "attach media",
                modifier = Modifier
                    .constrainAs(attachMedia) {
                        top.linkTo(editText.bottom, margin = 12.dp)
                        start.linkTo(editText.start)
                    }
                    .size(36.dp)
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
        } else {
            Box(
                modifier = Modifier
                    .background(Color.Gray)
                    .padding(1.dp)
                    .constrainAs(imageBox) {
                        top.linkTo(editText.bottom)
                        start.linkTo(editText.start)
                        end.linkTo(parent.end)
                    }
                    .height(250.dp)
                    .fillMaxWidth()
            ) {
                Image(
                    painter = rememberAsyncImagePainter(model = imageUri),
                    contentDescription = "selected image",
                    modifier = Modifier
                        .fillMaxSize(),
                    contentScale = ContentScale.Crop
                )

                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "remove image",
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .clickable {
                            imageUri = null
                        }
                )
            }
        }

        Text(
            text = "Anyone Can Reply",
            style = TextStyle(
                fontSize = 20.sp
            ),
            modifier = Modifier.constrainAs(replyText) {
                start.linkTo(parent.start, margin = 12.dp)
                bottom.linkTo(parent.bottom)
            }
        )

        TextButton(onClick = {
                             if(imageUri==null){
                                 threadViewModel.saveData(thread,FirebaseAuth.getInstance().currentUser!!.uid,"")
                             }else{
                                 threadViewModel.saveImage(thread,FirebaseAuth.getInstance().currentUser!!.uid,imageUri!!)
                             }
        }
            ,modifier=Modifier.constrainAs(button){
                end.linkTo(parent.end)
                bottom.linkTo(parent.bottom,margin=12.dp)
            }) {
            Text(
                text = "Post",
                style = TextStyle(
                    fontSize = 20.sp
                )
            )
        }
    }
}

@Composable
fun TextField(hint: String, value: String, onValueChange: (String) -> Unit, modifier: Modifier = Modifier) {
    Box(modifier = modifier.padding(8.dp)) {
        if (value.isEmpty()) {
            Text(
                text = hint,
                color = Color.Gray,
                style = TextStyle(fontSize = 16.sp)
            )
        }

        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            textStyle = TextStyle.Default.copy(color = Color.Black),
            modifier = Modifier.fillMaxWidth() // Ensures the BasicTextField takes up the necessary space
        )
    }
}

@Preview( showBackground = true)
@Composable
fun View(){
    //AddThreads()
}