# android_camera_utils

### Library containing utility classes for Android `Camera` related tasks.

[![](https://jitpack.io/v/dasBikash84/android_camera_utils.svg)](https://jitpack.io/#dasBikash84/android_camera_utils)

## Dependency

Add this in your root `build.gradle` file (**not** your module `build.gradle` file):

```gradle
allprojects {
	repositories {
        maven { url "https://jitpack.io" }
    }
}
```

Then, add the library to your module `build.gradle`
```gradle
dependencies {
    implementation 'com.github.dasBikash84:android_camera_utils:latest.release.here'
}
```

## Features
- Image [`capture`](https://github.com/dasBikash84/android_camera_utils/blob/master/android_camera_utils/src/main/java/com/dasbikash/android_camera_utils/CameraUtils.kt) using camera api made very simple (`don't need Camera usage permission`).

## Usage example

### Image capture using Camera:

##### Step: 1
Add provider tag inside your app manifest file as shown below:
```
<manifest  <!--...--> >

    <application <!--......-->>
        <!--.....-->
        <provider
            android:name="androidx.core.content.FileProvider"
            android:authorities="${applicationId}.fileprovider"
            android:exported="false"
            android:grantUriPermissions="true">
            <meta-data
                android:name="android.support.FILE_PROVIDER_PATHS"
                android:resource="@xml/files" />
        </provider>
        <!--..........-->
    </application>

</manifest>
```
##### Step: 2
Add file path info for saving image inside your app module's `src/main/res/xml/files.xml` file as shown below:
```
    <paths>
        <!--......-->
        <files-path name="captured_image_dir" path="."/>
    </paths>
```

##### Step: 3
Launch Camera from Activity/Fragment
```
    // From activity
    launchCameraForImage(this, REQUEST_TAKE_PHOTO) //REQUEST_TAKE_PHOTO is a unique Integer constant
    
            // Or
            
    //From Fragment
    launchCameraForImage(this, REQUEST_TAKE_PHOTO) 
    
            // Or
    
    // From inside of activity/fragment using kotlin extension
    launchCameraForImage(REQUEST_TAKE_PHOTO)
```

##### Step: 4
Capture image using camera.

##### Step: 5
Retrieve captured image:

```
    // inside launcher Activity/Fragment class to retrieve captured image file 
    
     override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                REQUEST_TAKE_PHOTO -> {
                    CameraUtils.handleCapturedImageFile(
                        context,
                        { doWithCapturedImage(it) })
                    //where processImportedImage has signature like "fun doWithCapturedImage(file:File)"
                } 
                //.............
            }
        }
    }
    
                                    // Or
        
    // inside launcher Activity/Fragment class to retrieve captured image bitmap 
    
     override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                REQUEST_TAKE_PHOTO -> {
                    CameraUtils.handleCapturedImageBitmap(
                        context,
                        { doWithCapturedImageBitmap(it) })
                    // where processImportedImage has signature like "fun doWithCapturedImageBitmap(bitmap:Bitmap)"
                }
                //.............
            }
        }
    }
```
---

License
--------

    Copyright 2020 Bikash Das(das.bikash.dev@gmail.com)

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
