package com.github.lookupgroup27.lookup.ui.register.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomOutlinedTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    isPassword: Boolean = false,
    testTag: String = ""
) {
  OutlinedTextField(
      value = value,
      onValueChange = onValueChange,
      label = { Text(label, color = Color.White) },
      visualTransformation =
          if (isPassword) PasswordVisualTransformation() else VisualTransformation.None,
      modifier = modifier.fillMaxWidth().testTag(testTag),
      textStyle = MaterialTheme.typography.bodyLarge.copy(color = Color.White),
      colors =
          TextFieldDefaults.outlinedTextFieldColors(
              focusedTextColor = Color.White,
              unfocusedTextColor = Color.White,
              focusedLabelColor = Color.White,
              unfocusedLabelColor = Color.Gray,
              focusedBorderColor = Color.White,
              unfocusedBorderColor = Color.Gray,
              cursorColor = Color.White))
}
