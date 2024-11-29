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
/**
 * A reusable custom-styled `OutlinedTextField` composable component. This component supports both
 * standard text input and password input, with options for customization and tagging for UI
 * testing.
 *
 * @param value The current text displayed in the field.
 * @param onValueChange Callback triggered whenever the text value changes.
 * @param label The label to display above the text field.
 * @param modifier A [Modifier] for customizing the layout or behavior of this component.
 * @param isPassword A flag to indicate whether the text field is for password input. If true, the
 *   input will be masked (e.g., with dots).
 * @param testTag A unique tag used for UI testing to identify this text field.
 */
@Composable
fun CustomOutlinedTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier, // Default modifier for flexibility in layout.
    isPassword: Boolean = false, // Indicates if the field is for password input.
    testTag: String = "" // Optional test tag for UI testing.
) {
  // The actual OutlinedTextField with additional styling and behavior.
  OutlinedTextField(
      value = value, // The current text displayed in the text field.
      onValueChange = onValueChange, // Invoked when the user modifies the text.

      // A composable label displayed above the text field when it gains focus.
      label = {
        Text(
            text = label, // The label text, e.g., "Email" or "Password".
            color = Color.White // Ensures the label text is visible against a dark background.
            )
      },

      // Determines how the text input is visually transformed.
      // If the field is for passwords, the input will be masked with dots.
      visualTransformation =
          if (isPassword) PasswordVisualTransformation() else VisualTransformation.None,

      // Modifier for layout customization and adding a test tag.
      modifier =
          modifier
              .fillMaxWidth() // Ensures the text field stretches across the available width.
              .testTag(testTag), // Adds a unique identifier for UI tests.

      // Customizes the style of the input text.
      textStyle =
          MaterialTheme.typography.bodyLarge.copy(
              color = Color.White // Makes the text white to match the app's dark theme.
              ),

      // Customizes the appearance of the text field, including border and label colors.
      colors =
          TextFieldDefaults.outlinedTextFieldColors(
              // Colors for the text inside the field.
              focusedTextColor = Color.White, // Color when the text field is focused.
              unfocusedTextColor = Color.White, // Color when the text field is not focused.

              // Colors for the label above the field.
              focusedLabelColor = Color.White, // Label color when the field is focused.
              unfocusedLabelColor = Color.Gray, // Label color when the field is not focused.

              // Colors for the field's border.
              focusedBorderColor = Color.White, // Border color when the field is focused.
              unfocusedBorderColor = Color.Gray, // Border color when the field is not focused.

              // Color for the blinking cursor inside the field.
              cursorColor = Color.White))
}
