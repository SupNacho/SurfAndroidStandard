package ru.surfstudio.android.picturechooser

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.support.media.ExifInterface
import android.support.v4.content.FileProvider
import io.reactivex.Observable
import ru.surfstudio.android.core.ui.navigation.activity.navigator.ActivityNavigator
import ru.surfstudio.android.core.ui.navigation.activity.route.ActivityWithResultRoute
import ru.surfstudio.android.dagger.scope.PerScreen
import ru.surfstudio.android.logger.Logger
import ru.surfstudio.android.picturechooser.exceptions.ActionInterruptedException
import ru.surfstudio.android.picturechooser.exceptions.ExternalStorageException
import java.io.File
import java.io.Serializable
import java.text.SimpleDateFormat
import java.util.*

/**
 *  Позволяет получить данные с камеры стороннего приложения
 */
@PerScreen
class CameraPictureProvider(private val activityNavigator: ActivityNavigator,
                            private val activity: Activity) {

    fun startCameraIntent(): Observable<CameraResult> {
        val image = generatePicturePath()
        val route = CameraRoute(image)

        val result = activityNavigator.observeResult<ResultData>(route)
                .flatMap { result ->
                    if (result.isSuccess) {
                        Observable.just(CameraResult(image.absolutePath, extractRotation(image.absolutePath)))
                    } else {
                        Observable.error(ActionInterruptedException())
                    }
                }
                .doOnNext { _ ->
                    addMediaToGallery(image.absolutePath)
                }

        activityNavigator.startForResult(route)
        return result
    }

    private fun extractRotation(photoPath: String): Int {
        try {
            val ei = ExifInterface(photoPath)
            val exif = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)
            return when (exif) {
                ExifInterface.ORIENTATION_ROTATE_90 -> 90
                ExifInterface.ORIENTATION_ROTATE_180 -> 180
                ExifInterface.ORIENTATION_ROTATE_270 -> 270
                else -> 0
            }
        } catch (e: Exception) {
            Logger.w(e)
        }
        return 0
    }

    private fun addMediaToGallery(fromPath: String?) {
        if (fromPath == null) {
            return
        }
        val f = File(fromPath)
        val contentUri = Uri.fromFile(f)
        addMediaToGallery(contentUri)
    }

    private fun addMediaToGallery(uri: Uri?) {
        uri ?: return
        try {
            val mediaScanIntent = Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE)
            mediaScanIntent.data = uri
            activity.sendBroadcast(mediaScanIntent)
        } catch (e: Exception) {
            Logger.w(e)
        }
    }

    private fun generatePicturePath(): File {
        val storageDir = getAlbumDir()
        val date = Date()
        date.time = System.currentTimeMillis() + Random().nextInt(1000) + 1
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss_SSS", Locale.US).format(date)
        return File(storageDir, "IMG_$timeStamp.jpg")
    }

    private fun getAlbumDir(): File {
        val sharedPicturesDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
        val appDirName = activity.packageManager.getApplicationLabel(activity.applicationInfo).toString()
        if (Environment.MEDIA_MOUNTED == Environment.getExternalStorageState()) {
            return File(sharedPicturesDir, appDirName)
                    .apply {
                        if (!mkdirs()) {
                            if (!exists()) {
                                throw ExternalStorageException("Failed to create directory")
                            }
                        }
                    }
        } else {
            throw ExternalStorageException("External storage is not mounted READ/WRITE.")
        }
    }

    private inner class CameraRoute(val image: File) : ActivityWithResultRoute<ResultData>() {
        override fun prepareIntent(context: Context?): Intent {
            val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            if (Build.VERSION.SDK_INT >= 24) {
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, FileProvider.getUriForFile(activity,
                        activity.applicationContext.packageName + ".provider",
                        image))
                takePictureIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
                takePictureIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            } else {
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(image))
            }
            return Intent.createChooser(takePictureIntent, activity.getString(R.string.choose_app))
        }
    }

    private data class ResultData(val photoPath: String) : Serializable

    data class CameraResult(val photoUrl: String, val rotation: Int)
}