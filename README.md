Android-Webcam
==============

## Overview
This is a (almost) pure Java implementation of a USB webcam driver for Android. The goal of this project is to avoid the shortcomings of other available libraries and deliver cross device support for a spectrum of webcam devices with minimal "tuning" requirements.

## Main Feature Set (Planned)
1. Dependency free Mavenized Android library with Java only interface
2. Support for any USB Host capable Android device
3. Support for USB webcam implementations that adhere to the UVC 1.5 classification
4. Support for full range of device resolutions (240p and greater)
5. Simple integration to 3rd party applications with multiple implementation options dependant on use case requirements.
 
## NDK Usage
Due to an unfortunate oversight in Android's Java USB Host API, support for isochronous endpoints is non-existant. To get around this shortcoming, this library includes a straight copy of **libusb**, along with some custom JNI and Java wrappers inspired by SpecLad's repostiory [libusb-android](https://github.com/SpecLad/libusb-android). All platform ABIs are built and deployed with the Maven artifact to help with our commitment to cross device support. Use of this artifact is through a simple, straightforward Java interface - no handling of native code or JNI required.

## Building
Due to the NDK requirement, building this project will require you to have the Android NDK on your machine. Setup of the NDK is left to the user. See [Google's NDK website](https://developer.android.com/tools/sdk/ndk/index.html) for more information on this. 

After setting up the NDK, you will need to reference it in your version of `local.properties` by declaring `ndk.dir`.

Following this, the project should build successfully.

### Supporting Documentation
1. [UVC Class Article on Wikipedia](http://en.wikipedia.org/wiki/USB_video_device_class)
2. [UVC Class Specification](http://www.usb.org/developers/docs/devclass_docs/USB_Video_Class_1_5.zip)
