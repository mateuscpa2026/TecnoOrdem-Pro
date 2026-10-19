package com.example.domain.util

import android.content.Context
import android.net.Uri
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object PhotoCaptureHelper {

    private fun getPhotosDirectory(context: Context): File {
        val dir = File(context.filesDir, "os_photos")
        if (!dir.exists()) {
            dir.mkdirs()
        }
        return dir
    }

    /**
     * Creates a new temporary file in the app's internal filesDir and returns both the File
     * and the content:// Uri provided by FileProvider for passing to ActivityResultContracts.TakePicture()
     */
    fun createImageCaptureUri(context: Context, prefix: String = "os_photo"): Pair<File, Uri> {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val photoFile = File.createTempFile(
            "${prefix}_${timeStamp}_",
            ".jpg",
            getPhotosDirectory(context)
        )
        val uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            photoFile
        )
        return Pair(photoFile, uri)
    }

    /**
     * Copies an image picked from the Android Photo Picker (content://...) into internal storage
     * so that the app retains permanent access to the photo file across reboots.
     */
    fun copyPickedImageToAppStorage(context: Context, sourceUri: Uri, prefix: String = "os_photo"): String? {
        return try {
            val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
            val destinationFile = File.createTempFile(
                "${prefix}_${timeStamp}_",
                ".jpg",
                getPhotosDirectory(context)
            )

            context.contentResolver.openInputStream(sourceUri)?.use { input ->
                FileOutputStream(destinationFile).use { output ->
                    input.copyTo(output)
                }
            }
            Uri.fromFile(destinationFile).toString()
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /**
     * Safely deletes an existing photo file from internal storage when removed by the user.
     */
    fun deletePhoto(photoUriString: String) {
        try {
            if (photoUriString.isNotBlank()) {
                val uri = Uri.parse(photoUriString)
                val path = uri.path
                if (path != null) {
                    val file = File(path)
                    if (file.exists()) {
                        file.delete()
                    }
                }
            }
        } catch (_: Exception) {}
    }
}
