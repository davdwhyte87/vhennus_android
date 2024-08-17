package com.amorgens.feed.presentation

import android.content.Context
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey


@Composable
fun FeedScreen(){
  val conext= LocalContext.current
  val userName = remember {
    mutableStateOf("")
  }

  val token = remember {
    mutableStateOf("")
  }

  Column {
    OutlinedTextField(
      value = userName.value,
      onValueChange ={
      userName.value = it
    } )
    OutlinedTextField(
      value = token.value,
      onValueChange ={
        token.value = it
      }
    )
    Button(onClick = {
      keyIn(conext, userName.value, token.value)
    }) {
      Text(text = "key in")
    }
  }
}

fun keyIn(context: Context, userName:String, token:String){

  val mshared = context.getSharedPreferences("user_data", Context.MODE_PRIVATE)
  val mSharedEditor = mshared.edit()
  mSharedEditor.putString("user_name", userName)
  mSharedEditor.apply()


  val masterKey = MasterKey.Builder(context)
    .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
    .build()

  val sharedPreferences = EncryptedSharedPreferences.create(
    context,
    "secure_prefs",
    masterKey,
    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
  )

  val editor = sharedPreferences.edit()
  editor.putString("auth_token",
    token)
  editor.apply()
}