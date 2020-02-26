package com.dasbikash.android_camera_utils

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.provider.MediaStore
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import java.io.File
import java.util.*

/**
 * Helper class to take photo using camera.
 *
 * #### Example code
 *
 * ##### To launch camera
 * ```
 *  // From activity
 *  CameraUtils.launchCameraForImage(launcherActivity, requestCode)
 *  // or From Fragment
 *  CameraUtils.launchCameraForImage(fragment, requestCode)
 * ```
 *
 * ##### To get photo
 * ```
 * // To access provided image from activity/fragment, "onActivityResult" call
 *  CameraUtils.processResultDataForFile(context: Context, doWithFile)
 *  //or
 *  CameraUtils.processResultDataForBitmap(context: Context, doWithBitmap)
 * ```
 * */
class CameraUtils {

    companion object {

        private const val JPG_FILE_EXT = ".jpg"
        private const val PNG_FILE_EXT = ".png"

        lateinit var mPhotoFile: File

        private fun resetPhotoFile(context: Context,fileName:String?) {
            val imageFileName = fileName ?: "image_${System.currentTimeMillis()}"
            mPhotoFile = File.createTempFile(
                imageFileName,
                JPG_FILE_EXT, context.filesDir
            )
        }

        private fun getAuthority(context: Context) =
            "${context.applicationContext.packageName}.fileprovider"

        private fun cameraLaunchPreProcess(
            context: Context,fileName:String?
        ): Intent? {
            resetPhotoFile(context,fileName)
            val uri = FileProvider.getUriForFile(
                context, getAuthority(context), mPhotoFile
            )
            val captureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            captureIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri)
            val cameraActivities = context.getPackageManager().queryIntentActivities(
                captureIntent, PackageManager.MATCH_DEFAULT_ONLY
            )
            if (cameraActivities.isNotEmpty()) {
                for (activity in cameraActivities) {
                    (context as ContextWrapper).grantUriPermission(
                        activity.activityInfo.packageName,
                        uri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                    )
                }
                return captureIntent
            }
            return null
        }

        /**
         * Method to launch Camera from "Activity"
         *
         * @param activity Caller Activity/AppCompatActivity
         * @param requestCode Unique request code that will be injected on "onActivityResult" method of caller Activity/AppCompatActivity
         * @param fileName Optional image file name
         * @return "true" on success
         * */
        @JvmStatic
        fun launchCameraForImage(activity: Activity, requestCode: Int,fileName:String?=null): Boolean {
            cameraLaunchPreProcess(activity,fileName)?.let {
                activity.startActivityForResult(it, requestCode)
                return true
            }
            return false
        }

        /**
         * Method to launch Camera from "Fragment"
         *
         * @param fragment Caller Fragment
         * @param requestCode Unique request code that will be injected on "onActivityResult" method of caller Fragment
         * @param fileName Optional image file name
         * @return "true" on success
         * */
        @JvmStatic
        fun launchCameraForImage(fragment: Fragment, requestCode: Int,fileName:String?=null): Boolean {
            fragment.context?.let {
                cameraLaunchPreProcess(it,fileName)?.let {
                    fragment.startActivityForResult(it, requestCode)
                    return true
                }
            }
            return false
        }

        /**
         * Method to retrieve image file captured by camera.
         * Should we called from "onActivityResult" method of caller Activity/Fragment on "Success"         *
         *
         * @param context Android Context
         * @param doWithFile Functional parameter onto which captured image file will be injected
         * */
        @JvmStatic
        fun handleCapturedImageFile(context: Context, doWithFile: ((File) -> Unit)?) {
            revokeUriPermission(context)
            doWithFile?.let { it(mPhotoFile) }
        }

        /**
         * Method to retrieve image captured by camera in bitmap format.
         * Should we called from "onActivityResult" method of caller Activity/Fragment on "Success"         *
         *
         * @param context Android Context
         * @param doWithFile Functional parameter onto which captured image bitmap will be injected
         * */
        @JvmStatic
        fun handleCapturedImageBitmap(context: Context, doWithBitmap: ((Bitmap) -> Unit)?) {
            revokeUriPermission(context)
            BitmapFactory.decodeFile(mPhotoFile.path)?.apply {
                doWithBitmap?.let { it(this) }
            }
        }

        private fun revokeUriPermission(context: Context) {
            val uri = FileProvider.getUriForFile(
                context, getAuthority(context), mPhotoFile
            )
            context.revokeUriPermission(
                uri,
                Intent.FLAG_GRANT_WRITE_URI_PERMISSION
            )
        }
    }
}

/**
 * Extension function to launch Camera from "Activity"
 *
 * @param requestCode Unique request code that will be injected on "onActivityResult" method of caller Activity
 * @return "true" on success
 * */
fun Activity.launchCameraForImage(requestCode: Int,fileName:String?=null): Boolean =
    CameraUtils.launchCameraForImage(this,requestCode,fileName)


/**
 * Extension function to launch Camera from "AppCompatActivity"
 *
 * @param requestCode Unique request code that will be injected on "onActivityResult" method of caller AppCompatActivity
 * @return "true" on success
 * */
fun AppCompatActivity.launchCameraForImage(requestCode: Int,fileName:String?=null): Boolean =
    CameraUtils.launchCameraForImage(this,requestCode,fileName)


/**
 * Extension function to launch Camera from "Fragment"
 *
 * @param requestCode Unique request code that will be injected on "onActivityResult" method of caller Fragment
 * @return "true" on success
 * */
fun Fragment.launchCameraForImage(requestCode: Int,fileName:String?=null): Boolean =
    CameraUtils.launchCameraForImage(this,requestCode,fileName)