[![UT](https://img.shields.io/badge/Unit_Test-pass-green)]()
[![license](https://img.shields.io/badge/License-MIT-green.svg)](https://github.com/verygoodsecurity/vgs-show-android/blob/master/LICENSE)
[ ![Download](https://api.bintray.com/packages/vg/vgs-collect-android/vgsshow/images/download.svg) ](https://bintray.com/vg/vgs-collect-android/vgsshow/_latestVersion)
<img src="./ZeroDataLogo.png" width="21" hspace="8">

VGS Show Android SDK allows you to securely reveal data for your users without having to have that data pass through your systems. It provides customizable UI elements for showing users' sensitive data securely on Android devices.

Table of contents
=================

<!--ts-->
   * [Dependencies](#dependencies)
   * [Structure](#structure)
   * [Integration](#integration)
   * [Next steps](#next-steps)
   * [Releases](#releases)
   * [License](#license)
<!--te-->


## Dependencies

| Dependency | Version |
| :--- | :---: |
| Min SDK | 19 |
| Target SDK | 30 |
| androidx.appcompat:appcompat | 1.2.0 |
| com.google.android.material:material | 1.2.0 |
| com.squareup.okhttp3:okhttp | 4.8.0 |

## Structure
* **VGSShow SDK** - provides an API for interacting with the VGS Vault
* **app** - sample application to act as the host app for testing the SDK during development


## Integration
For integration you need to install the [Android Studio](http://developer.android.com/sdk/index.html) and a [JDK](http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html) on your machine.


<table>
  <tr>
    <td colspan="2">
      <b>Integrate the VGS Show SDK to your project</b>. <br/>
      If you are using Maven, add the following to your <code>build.gradle</code> file.
    </td>
  </tr>
  <tr>
    <td colspan="2">

```gradle
dependencies {
    implementation "androidx.appcompat:appcompat:<version>"
    implementation "com.google.android.material:material:<version>"

    implementation "com.verygoodsecurity:vgsshow:<version>"
}
```
  </td>
  </tr>


  <tr>
    <td colspan="2">
    <b>Add input fields to <code>R.layout.activity_main</code> layout file </b>.
    </td>
  </tr>
  <tr>
    <td colspan="2">

```xml
<?xml version="1.0" encoding="utf-8"?>

<LinearLayout
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:orientation="vertical">
  
    <com.verygoodsecurity.vgsshow.widget.VGSTextView
        android:id="@+id/infoField"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        app:contentPath="<CONTENT_PATH>"
        app:gravity="center"
        app:textColor="@android:color/black"
        app:textStyle="bold" 

//Other fields..

</LinearLayout>
```
  </td>
  </tr>

  <tr>
    <td colspan="2">
      <b>
      To initialize VGSShow you have to set your <a href="https://www.verygoodsecurity.com/docs/terminology/nomenclature#vault">vault id</a> and <a href="https://www.verygoodsecurity.com/docs/getting-started/going-live#sandbox-vs-live">Environment</a> type.</b>
      </br>
      Use <code>subscribe(VGSView)</code> to attach view to <code>VGSShow</code>.
    </td>

  </tr>
  <tr>
    <td colspan="2">

```kotlin
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.verygoodsecurity.demoshow.R
import com.verygoodsecurity.vgsshow.VGSShow
import com.verygoodsecurity.vgsshow.widget.VGSTextView
import com.verygoodsecurity.vgsshow.core.VGSEnvironment

class MainActivity : AppCompatActivity() {
    private lateinit var vgsShow: VGSShow
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        vgsShow = VGSShow(this, "<VAULT_ID>", VGSEnvironment.Sandbox())

        val view = findViewById<VGSTextView>(R.id.infoField)
        vgsShow.subscribe(view)
    }
}
```
  </td>
  </tr>


  <tr>
    <td colspan="2">
    <b>Revealing Sensitive Data.</b> </br> Call <code>requestAsync</code> to reveal and send data on VGS Server.
    </td>

  </tr>
  <tr>
    <td colspan="2">

```kotlin
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.verygoodsecurity.demoshow.R
import com.verygoodsecurity.vgsshow.VGSShow
import com.verygoodsecurity.vgsshow.widget.VGSTextView
import com.verygoodsecurity.vgsshow.core.VGSEnvironment

class MainActivity : AppCompatActivity() {

    private lateinit var vgsShow: VGSShow
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        
        vgsShow = VGSShow(this, "<VAULT_ID>", VGSEnvironment.Sandbox())
        vgsShow.subscribe(findViewById(R.id.infoField))

        findViewById<Button>(R.id.revealButton)?.setOnClickListener {
            vgsShow.requestAsync("/post",
                VGSHttpMethod.POST,
                mapOf("<FIELD_NAME>" to "<REVEAL_ALIAS>")
            )
        }
    }
}
```
  </td>
  </tr>


  <tr>
    <td colspan="2">
    <b> Receive responses. </b>
    </br>
    To retrieve response you need to implement <code>VGSOnResponseListener</code>.
    </td>

  </tr>
  <tr>
    <td colspan="2">

```kotlin
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.verygoodsecurity.demoshow.R
import com.verygoodsecurity.vgsshow.VGSShow
import com.verygoodsecurity.vgsshow.widget.VGSTextView
import com.verygoodsecurity.vgsshow.core.VGSEnvironment
import com.verygoodsecurity.vgsshow.core.network.model.VGSResponse
import com.verygoodsecurity.vgsshow.core.listener.VGSOnResponseListener

class MainActivity : AppCompatActivity() {

    private lateinit var vgsShow: VGSShow

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        vgsShow = VGSShow(this, "<VAULT_ID>", VGSEnvironment.Sandbox())
        vgsShow.subscribe(findViewById(R.id.infoField))

        findViewById<Button>(R.id.revealButton)?.setOnClickListener {
            vgsShow.requestAsync("/post",
                VGSHttpMethod.POST,
                mapOf("<FIELD_NAME>" to "<REVEAL_ALIAS>")
            )
        }

        showVgs.addOnResponseListeners(object : VGSOnResponseListener {
            override fun onResponse(response: VGSResponse) {
                when(response) {
                    is VGSResponse.Success -> {
                        val code = response.code
                    }
                    is VGSResponse.Error -> {
                        val code = response.code
                        val message = response.message
                    }
                }
            }
        })
    }
}
```
  </td>
  </tr>

</table>


## Releases
To follow **VGS Show SDK** updates and changes check the [releases](https://github.com/verygoodsecurity/vgs-show-android/releases) page.

## License
VGSShow Android SDK is released under the MIT license. [See LICENSE](https://github.com/verygoodsecurity/vgs-show-android/blob/master/LICENSE) for details.
