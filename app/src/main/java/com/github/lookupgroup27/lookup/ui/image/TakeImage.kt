package com.github.lookupgroup27.lookup.ui.image

import android.Manifest
import android.content.pm.ActivityInfo
import android.net.Uri
import android.os.Environment
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import com.github.lookupgroup27.lookup.ui.navigation.NavigationActions
import com.github.lookupgroup27.lookup.ui.navigation.Route
import com.github.lookupgroup27.lookup.ui.navigation.Screen
import java.io.File
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

/**
 * Composable function for capturing images using the device's camera.
 *
 * The `CameraCapture` composable provides a simple interface for capturing images, handling camera
 * permissions, and saving the captured image to a file. It uses Jetpack CameraX for camera
 * functionality and includes options for navigating back and reviewing the captured image.
 *
 * @param navigationActions The [NavigationActions] object for handling navigation between screens.
 */
@Composable
fun CameraCapture(
    navigationActions: NavigationActions,
) {
  val context = LocalContext.current

  // Lock the screen orientation to portrait mode.
  DisposableEffect(Unit) {
    val activity = context as? ComponentActivity
    activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
    onDispose { activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED }
  }

  var imageCapture: ImageCapture? by remember { mutableStateOf(null) }
  var isCameraPermissionGranted by remember { mutableStateOf(false) }

  // Executor for camera operations
  val cameraExecutor: ExecutorService = remember { Executors.newSingleThreadExecutor() }
  val lifecycleOwner = LocalContext.current as LifecycleOwner

  // Permission launcher for requesting camera permission
  val launcher =
      rememberLauncherForActivityResult(contract = ActivityResultContracts.RequestPermission()) {
          granted ->
        isCameraPermissionGranted = granted
        if (!granted) {
          Toast.makeText(context, "Camera permission is required", Toast.LENGTH_LONG).show()
        }
      }

  // Request camera permission on launch
  LaunchedEffect(Unit) { launcher.launch(Manifest.permission.CAMERA) }

  // Display camera preview and controls if permission is granted
  if (isCameraPermissionGranted) {
    Box(modifier = Modifier.fillMaxSize().testTag("camera_capture")) {
      // Camera preview using AndroidView
      AndroidView(
          modifier = Modifier.fillMaxSize(),
          factory = { viewContext ->
            val previewView = PreviewView(viewContext)
            val cameraProviderFuture = ProcessCameraProvider.getInstance(viewContext)

            cameraProviderFuture.addListener(
                {
                  val cameraProvider = cameraProviderFuture.get()
                  val preview =
                      Preview.Builder().build().apply {
                        setSurfaceProvider(previewView.surfaceProvider)
                      }

                  imageCapture = ImageCapture.Builder().build()
                  val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

                  try {
                    cameraProvider.unbindAll()
                    cameraProvider.bindToLifecycle(
                        lifecycleOwner, cameraSelector, preview, imageCapture)
                  } catch (exc: Exception) {
                    Toast.makeText(context, "Failed to bind camera", Toast.LENGTH_SHORT).show()
                  }
                },
                ContextCompat.getMainExecutor(viewContext))
            previewView
          })

      // Back button
      IconButton(
          onClick = { navigationActions.navigateTo(Screen.GOOGLE_MAP) },
          modifier =
              Modifier.padding(16.dp).align(Alignment.TopStart).testTag("go_back_button_camera")) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back",
                tint = Color.White)
          }

      // Take picture button
      Button(
          onClick = {
            val imageCapture = imageCapture ?: return@Button

            // Create a file to save the image
            val photoFile =
                File(
                    context.getExternalFilesDir(Environment.DIRECTORY_PICTURES),
                    "captured_image_${System.currentTimeMillis()}.jpg")

            val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()
            imageCapture.takePicture(
                outputOptions,
                ContextCompat.getMainExecutor(context),
                object : ImageCapture.OnImageSavedCallback {
                  override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                    val savedUri =
                        Uri.encode(photoFile.absolutePath) // Encoded URI of the saved image
                    val timestamp = System.currentTimeMillis()
                    navigationActions.navigateToWithImage(savedUri, Route.IMAGE_REVIEW, timestamp)
                  }

                  override fun onError(exc: ImageCaptureException) {
                    Toast.makeText(
                            context, "Failed to capture image: ${exc.message}", Toast.LENGTH_SHORT)
                        .show()
                  }
                })
          },
          modifier = Modifier.align(Alignment.BottomCenter).testTag("take_picture_button")) {
            Text(text = "Take Picture")
          }
    }
  }

  // Shutdown the camera executor when the composable is disposed
  DisposableEffect(Unit) { onDispose { cameraExecutor.shutdown() } }
}
