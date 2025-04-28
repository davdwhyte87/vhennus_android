package com.vhennus.general.presentation

import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.vhennus.profile.domain.UpdateProfileRequest
import com.vhennus.profile.presentation.updateBioValidation
import com.vhennus.ui.AnimatedPreloader


@Composable
fun AppBotton(onClick: Unit, name:String, loading:Boolean, modifier: Modifier){
    Button(onClick = {
        onClick
    },
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.surface
        ),
        shape = RoundedCornerShape(10.dp),
        modifier = modifier
    ) {
        if(loading){
            AnimatedPreloader(modifier = Modifier.size(size = 50.dp), MaterialTheme.colorScheme.surface)
        }else {
            Text(text = name)
        }

    }
}