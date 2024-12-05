package com.github.lookupgroup27.lookup.ui.register.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation

/**
 * A custom OutlinedTextField component with additional features like password visibility toggle and
 * inline error message display.
 *
 * This component extends the standard [OutlinedTextField] by adding support for:
 * - Password fields with visibility toggle.
 * - Displaying error messages directly below the text field.
 *
 * @param value The current text value of the text field.
 * @param onValueChange Callback invoked when the text value changes.
 * @param label The label to display inside the text field.
 * @param errorMessage An optional error message to display below the text field.
 * @param isPassword Whether the text field is for password input.
 * @param modifier A [Modifier] for this text field.
 * @param testTag An optional test tag for UI testing.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomOutlinedTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    errorMessage: String? = null,
    isPassword: Boolean = false,
    modifier: Modifier = Modifier,
    testTag: String = ""
) {
  // State to manage the visibility of the password text.
  var passwordVisible by remember { mutableStateOf(false) }

  // Determine the visual transformation based on the password visibility.
  val visualTransformation =
      if (isPassword && !passwordVisible) {
        // Hide the password text.
        PasswordVisualTransformation()
      } else {
        // Show the password text.
        VisualTransformation.None
      }

  // Trailing icon to toggle password visibility.
  val trailingIcon: @Composable (() -> Unit)? =
      if (isPassword) {
        {
          IconButton(onClick = { passwordVisible = !passwordVisible }) {
            val icon = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
            Icon(
                imageVector = icon,
                contentDescription = if (passwordVisible) "Hide password" else "Show password",
                tint = Color.White)
          }
        }
      } else null

  // Render the OutlinedTextField with custom configurations.
  OutlinedTextField(
      value = value,
      onValueChange = onValueChange,
      label = {
        Text(
            text = label,
            color = if (errorMessage == null) Color.White else MaterialTheme.colorScheme.error)
      },
      isError = errorMessage != null,
      visualTransformation = visualTransformation,
      trailingIcon = trailingIcon,
      supportingText =
          errorMessage?.let { { Text(text = it, color = MaterialTheme.colorScheme.error) } },
      modifier = modifier.fillMaxWidth().testTag(testTag),
      textStyle = MaterialTheme.typography.bodyLarge.copy(color = Color.White),
      colors =
          TextFieldDefaults.outlinedTextFieldColors(
              focusedTextColor = Color.White,
              unfocusedTextColor = Color.White,
              focusedLabelColor =
                  if (errorMessage == null) Color.White else MaterialTheme.colorScheme.error,
              unfocusedLabelColor =
                  if (errorMessage == null) Color.Gray else MaterialTheme.colorScheme.error,
              focusedBorderColor =
                  if (errorMessage == null) Color.White else MaterialTheme.colorScheme.error,
              unfocusedBorderColor =
                  if (errorMessage == null) Color.Gray else MaterialTheme.colorScheme.error,
              cursorColor = Color.White))
}
