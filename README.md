# Moonlight Android

此项目实现对[Moonlight安卓端 阿西西修改版](https://github.com/Axixi2233/moonlight-android)的功能改进<br><br>


# 目前已实现功能

* **自动获取无障碍权限**
  > *需要通过ADB授权android.permission.WRITE_SECURE_SETTINGS*

* **安卓本地鼠标特殊滚轮的识别**
  > *在使用安卓本地鼠标光标时，部分设备无法正确获取滚轮事件，如华为安卓平板，滚动滚动时会产生触屏效果造成大范围滑动*

* **非本地鼠标模式也支持绘制本地鼠标光标**
  > *延迟高于本地鼠标，但略低于远程鼠标，如果使用本地鼠标光标仍有问题，可以使用此方法作为替代*

* **自动切换远程电脑的光标显示**
  > *在本地鼠标模式和光标绘制模式下，自动根据状态发送电脑光标显示开关请求*

* **适用于部分平板键盘的按键映射**
  > * Home → **Esc**
  > * Esc + [1-0, -, =] → **F1 - F12**
  > * Esc + Q → **呼出串流菜单**
  > * 安卓截图键 → **Windows 截图 (Win+Shift+S)**
  > * 安卓媒体键 (上一首/播放暂停/下一首) → **F5/F10/F11**

* **支持鼠标中键**
  > *部分设备将鼠标中键识别为返回键，此功能开启可以区分触摸返回和鼠标中键造成的返回*

<br><br>

# Moonlight Android

This project implements functional improvements to [Moonlight Android client, modified by Axixi](https://github.com/Axixi2233/moonlight-android)<br><br>


# Key Features

* **Auto-grant Accessibility Permissions**
  > *Requires ADB authorization for android.permission.WRITE_SECURE_SETTINGS.*
* **Android Local Mouse Scroll Wheel Fix**
  > *Resolves scrolling issues on specific devices (e.g., MatePad with Android) where scroll wheel inputs are incorrectly interpreted as touch swipes, causing excessive screen movement.*
* **Local Cursor Rendering in Non-Local Mouse Mode**
  > *Provides a latency-optimized alternative. Latency is higher than the system local cursor but slightly lower than the remote stream cursor. Ideal as a fallback if the local mouse mode encounters issues.*
* **Smart Remote Cursor Visibility Sync**
  > *Dynamically toggles the host computer's cursor visibility based on the current local cursor state.*
* **Keyboard Remapping for Tablets**
  > * Home → **Esc**
  > * Esc + [1-0, -, =] → **F1 - F12**
  > * Esc + Q → **Streaming Menu**
  > * Android Screenshot Key → **Windows Screenshot**
  > * Media Keys (Prev/Pause/Next) → **F5/F10/F11**
* **Middle Mouse Button Support**
  > *Some devices recognize the middle mouse button as the return key. Turning this function on can distinguish between touch return and return caused by the middle mouse button.*

<br><br>

# Moonlight Android

[![AppVeyor Build Status](https://ci.appveyor.com/api/projects/status/232a8tadrrn8jv0k/branch/master?svg=true)](https://ci.appveyor.com/project/cgutman/moonlight-android/branch/master)
[![Translation Status](https://hosted.weblate.org/widgets/moonlight/-/moonlight-android/svg-badge.svg)](https://hosted.weblate.org/projects/moonlight/moonlight-android/)

[Moonlight for Android](https://moonlight-stream.org) is an open source client for NVIDIA GameStream and [Sunshine](https://github.com/LizardByte/Sunshine).

Moonlight for Android will allow you to stream your full collection of games from your Windows PC to your Android device,
whether in your own home or over the internet.

Moonlight also has a [PC client](https://github.com/moonlight-stream/moonlight-qt) and [iOS/tvOS client](https://github.com/moonlight-stream/moonlight-ios).

You can follow development on our [Discord server](https://moonlight-stream.org/discord) and help translate Moonlight into your language on [Weblate](https://hosted.weblate.org/projects/moonlight/moonlight-android/).

## Downloads
* [Google Play Store](https://play.google.com/store/apps/details?id=com.limelight)
* [Amazon App Store](https://www.amazon.com/gp/product/B00JK4MFN2)
* [F-Droid](https://f-droid.org/packages/com.limelight)
* [APK](https://github.com/moonlight-stream/moonlight-android/releases)

## Building
* Install Android Studio and the Android NDK
* Run ‘git submodule update --init --recursive’ from within moonlight-android/
* In moonlight-android/, create a file called ‘local.properties’. Add an ‘ndk.dir=’ property to the local.properties file and set it equal to your NDK directory.
* Build the APK using Android Studio or gradle

## Authors

* [Cameron Gutman](https://github.com/cgutman)  
* [Diego Waxemberg](https://github.com/dwaxemberg)  
* [Aaron Neyer](https://github.com/Aaronneyer)  
* [Andrew Hennessy](https://github.com/yetanothername)

Moonlight is the work of students at [Case Western](http://case.edu) and was
started as a project at [MHacks](http://mhacks.org).
