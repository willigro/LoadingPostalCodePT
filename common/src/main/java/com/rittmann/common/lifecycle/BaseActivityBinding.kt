package com.rittmann.common.lifecycle

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.FragmentResultListener
import com.rittmann.androidtools.log.log
import com.rittmann.common.R
import com.rittmann.common.extensions.toast
import com.rittmann.common.navigation.ScreenNavigator
import dagger.android.support.DaggerAppCompatActivity


// Storage Permissions
private const val REQUEST_EXTERNAL_STORAGE = 1

private val PERMISSIONS_STORAGE = mutableListOf(
    Manifest.permission.READ_EXTERNAL_STORAGE,
    Manifest.permission.WRITE_EXTERNAL_STORAGE,
)

abstract class BaseBindingActivity<T : ViewDataBinding>(private val resId: Int) :
    DaggerAppCompatActivity(), FragmentResultListener {

    private val storagePermissionResultLauncher: ActivityResultLauncher<Intent> by lazy {
        registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                if (Environment.isExternalStorageManager()) {
                    requestPermissionsGranted()
                } else {
                    requestPermissionsDeniedAndBlocked()
                }
            }
        }
    }
    protected lateinit var binding: T

    abstract val screenHolder: Int

    val screenNavigator: ScreenNavigator by lazy {
        ScreenNavigator(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, resId)
        binding.lifecycleOwner = this
    }

    // TODO: move the permissions code
    fun requestPermissions() {
        // Request all permissions at once of External Storage Manager in case of Build.VERSION_CODES.R or higher
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (Environment.isExternalStorageManager()) {
                requestPermissionsGranted()
            } else {
                openSettingsToGrantPermissions()
            }

            return
        }

        // Request the basic permissions
        for (permission in PERMISSIONS_STORAGE) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    permission
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    PERMISSIONS_STORAGE.toTypedArray(),
                    REQUEST_EXTERNAL_STORAGE,
                )
                return
            }
        }

        // All permissions were already grant
        requestPermissionsGranted()
    }

    protected fun openSettingsToGrantPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            storagePermissionResultLauncher.launch(
                Intent(
                    Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION,
                    Uri.parse("package:$packageName")
                )
            )
        } else {
            // TODO get a different OS version to test it properly
            startActivity(
                Intent(
                    Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                    Uri.parse("package:$packageName")
                )
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray,
    ) {
        grantResults.forEach {
            it.toString().log()
        }
        when (requestCode) {
            REQUEST_EXTERNAL_STORAGE -> {
                for (i in permissions.indices) {
                    if (PackageManager.PERMISSION_GRANTED == grantResults[i]) {
                        continue
                    } else {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            val showRationale = shouldShowRequestPermissionRationale(permissions[i])
                            if (showRationale) {
                                // First deny
                                requestPermissionsDenied()
                            } else {
                                // Second, don't ask again
                                requestPermissionsDeniedAndBlocked()
                            }
                        } else {
                            requestPermissionsDenied()
                        }

                        // finish and do not call the permissionsGranted
                        return
                    }
                }

                // All permissions were grant
                requestPermissionsGranted()
            }
            else -> super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }

    /**
     * I don't wanna make a listener for it, so I'm going to call this open functions and
     * implement them if I wanna to
     * */
    open fun requestPermissionsGranted() {}

    open fun requestPermissionsDenied() {
        toast(getString(R.string.permissions_were_denied))
    }

    open fun requestPermissionsDeniedAndBlocked() {}
}