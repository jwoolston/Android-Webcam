/* Source for class com_jwoolston_usb_webcam_libusb_UsbDeviceIsoConnection */

#include <jni.h>
#include <android/log.h>

#include "libusb/libusb.h"
#include "com_jwoolston_usb_webcam_libusb_UsbDeviceIsoConnection.h"


#define  LOG_TAG    "UsbDeviceIsoConnection-Native"

#define  LOGD(...)  __android_log_print(ANDROID_LOG_DEBUG, LOG_TAG, __VA_ARGS__)
#define  LOGE(...)  __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, __VA_ARGS__)

static JavaVM *java_vm = NULL;

static jclass java_lang_Throwable = NULL;
static jmethodID java_lang_Throwable_toString;

static jclass android_hardware_usb_UsbDeviceConnection = NULL;
static jmethodID android_hardware_usb_UsbDeviceConnection_getFileDescriptor;

static void log_exception(JNIEnv * env, jthrowable exception, const char * function) {
	jstring exc_string = (*env)->CallObjectMethod(env, exception, java_lang_Throwable_toString);

	if ((*env)->ExceptionCheck(env)) {
		(*env)->ExceptionClear(env);
		LOGE("%s: a Java exception occurred, but toString() failed", function);
	} else if (!exc_string) {
		LOGE("%s: a Java exception occurred, but toString() is null", function);
	} else {
		const char * exc_string_chars = (*env)->GetStringUTFChars(env, exc_string, NULL);
		LOGE("%s: a Java exception occurred: %s", function, exc_string_chars);
		(*env)->ReleaseStringUTFChars(env, exc_string, exc_string_chars);
	}
}

/*
 * Class:     com_jwoolston_usb_webcam_libusb_UsbDeviceIsoConnection
 * Method:    initialize
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_com_jwoolston_usb_webcam_libusb_UsbDeviceIsoConnection_initialize(JNIEnv *env, jobject thisObj) {
    LOGD("UsbDeviceIsoConnection.initialize()");
}

/*
 * Class:     com_jwoolston_usb_webcam_libusb_UsbDeviceIsoConnection
 * Method:    deinitialize
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_com_jwoolston_usb_webcam_libusb_UsbDeviceIsoConnection_deinitialize(JNIEnv *env, jobject thisObj) {
    LOGD("UsbDeviceIsoConnection.deinitialize()");
}

/*
 * Class:     com_jwoolston_usb_webcam_libusb_UsbDeviceIsoConnection
 * Method:    isochronousTransfer
 * Signature: (Landroid/hardware/usb/UsbDeviceConnection;Ljava/nio/ByteBuffer;)V
 */
JNIEXPORT void JNICALL Java_com_jwoolston_usb_webcam_libusb_UsbDeviceIsoConnection_isochronousTransfer(JNIEnv *env, jobject thisObj, jobject connection, jobject buffer) {
    LOGD("UsbDeviceIsoConnection.isochronousTransfer()");
}