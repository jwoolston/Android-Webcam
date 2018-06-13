/* Source for class com_jwoolston_android_uvc_libusb_UsbDeviceIsoConnection */

#include <jni.h>
#include <libusbi.h>
#include "logging.h"

#include "libusb.h"
#include "isochronous_connection.h"

#define  LOG_TAG    "UsbDeviceIsoConnection-Native"

static JavaVM *java_vm = NULL;

static jclass java_lang_Throwable = NULL;
static jmethodID java_lang_Throwable_toString;

static jclass android_hardware_usb_UsbDeviceConnection = NULL;
static jmethodID android_hardware_usb_UsbDeviceConnection_getFileDescriptor;

static void log_exception(JNIEnv *env, jthrowable exception, const char *function) {
    jstring exc_string = (*env)->CallObjectMethod(env, exception, java_lang_Throwable_toString);

    if ((*env)->ExceptionCheck(env)) {
        (*env)->ExceptionClear(env);
        LOGE("%s: a Java exception occurred, but toString() failed", function);
    } else if (!exc_string) {
        LOGE("%s: a Java exception occurred, but toString() is null", function);
    } else {
        const char *exc_string_chars = (*env)->GetStringUTFChars(env, exc_string, NULL);
        LOGE("%s: a Java exception occurred: %s", function, exc_string_chars);
        (*env)->ReleaseStringUTFChars(env, exc_string, exc_string_chars);
    }
}

static void printDevice(libusb_device *dev) {
    int j = 0;
    uint8_t path[8];

    struct libusb_device_descriptor desc;
    int r = libusb_get_device_descriptor(dev, &desc);
    if (r < 0) {
        LOGE("Failed to get device descriptor");
        return;
    }

    LOGD("Native USB Initialized for Device %04x:%04x (bus %d, device %d)",
         desc.idVendor, desc.idProduct,
         libusb_get_bus_number(dev), libusb_get_device_address(dev));

    r = libusb_get_port_numbers(dev, path, sizeof(path));
    if (r > 0) {
        LOGI(" path: %d", path[0]);
        for (j = 1; j < r; j++)
            LOGI(".%d", path[j]);
    }
}

/*
 * Class:     com_jwoolston_usb_webcam_libusb_UsbDeviceIsoConnection
 * Method:    initialize
 * Signature: ()V
 */
JNIEXPORT jint JNICALL
Java_com_jwoolston_android_uvc_libusb_IsochronousConnection_initialize(JNIEnv *env, jobject instance, jint fd) {
    LOGD("UsbDeviceIsoConnection.initialize(%i)", fd);
    struct libusb_context *ctx;
    struct libusb_device_handle *dev_handle;
    int r = libusb_init(&ctx);
    if (r < 0) {
        LOGE("Initialization returned: %i", r);
        return r;
    }
    libusb_wrap_fd(ctx, fd, &dev_handle);

    if (dev_handle == NULL) {
        return LIBUSB_ERROR_NO_DEVICE;
    }
    printDevice(dev_handle->dev);
    return 0;
}

/*
 * Class:     com_jwoolston_usb_webcam_libusb_UsbDeviceIsoConnection
 * Method:    deinitialize
 * Signature: ()V
 */
JNIEXPORT jint JNICALL
Java_com_jwoolston_android_uvc_libusb_IsochronousConnection_deinitialize(JNIEnv *env, jobject instance) {
    LOGD("UsbDeviceIsoConnection.deinitialize()");
    libusb_exit(NULL);
    return 0;
}

/*
 * Class:     com_jwoolston_usb_webcam_libusb_UsbDeviceIsoConnection
 * Method:    isochronousTransfer
 * Signature: (Landroid/hardware/usb/UsbDeviceConnection;Ljava/nio/ByteBuffer;)V
 */
JNIEXPORT jint JNICALL
Java_com_jwoolston_android_uvc_libusb_IsochronousConnection_isochronousTransfer(JNIEnv *env, jobject instance,
                                                                                 jobject connection, jobject buffer) {
    LOGD("UsbDeviceIsoConnection.isochronousTransfer()");
    return 0;
}