package com.vhennus.general.presentation

import android.graphics.drawable.Icon
import android.view.View
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.vhennus.ui.theme.Gray2


@Composable
fun InputFieldWithLabel(
    data: MutableState<String>,
    labelText:String,
    placeHolderText: String,
){
    Column (
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ){

        Text(labelText,
            style = MaterialTheme.typography.titleSmall
        )

        OutlinedTextField(
            value = data.value,
            onValueChange = {data.value = it},
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
            ,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = Gray2
            ),
            shape = RoundedCornerShape(10.dp),
            placeholder = {Text(placeHolderText,
                style = MaterialTheme.typography.titleSmall,
                modifier = Modifier.alpha(0.6f)
            )}
        )
    }
}


@Composable
fun InputField(
    data: MutableState<String>,
    placeHolderText: String,
    isLeadIcon: Boolean=false,
    isTrailingIcon: Boolean = false,
    trailingIcon: @Composable ()->Unit = {},
    leadingIcon: @Composable ()->Unit = { },
    bottomView: @Composable ()->Unit = {},

    ){
    Column (
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ){

        OutlinedTextField(
            value = data.value,
            onValueChange = {data.value = it},
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
            ,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = Gray2
            ),
            shape = RoundedCornerShape(10.dp),
            trailingIcon = if(isTrailingIcon) trailingIcon else null,
            leadingIcon =if (isLeadIcon) leadingIcon else null,
            placeholder = {Text(placeHolderText,
                style = MaterialTheme.typography.titleSmall,
                modifier = Modifier.alpha(0.6f)
            )}
        )
        bottomView
    }
}