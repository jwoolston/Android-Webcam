cmake_minimum_required(VERSION 3.4.1)

find_library( # Sets the name of the path variable.
              log-lib

              # Specifies the name of the NDK library that
              # you want CMake to locate.
              log )

include_directories(
    libusb/android
    libusb/libusb
    libusb/libusb/os)

# Defines the source code for the library
set(libusb_SRCS
    libusb/libusb/core.c
    libusb/libusb/descriptor.c
    libusb/libusb/hotplug.c
    libusb/libusb/io.c
    libusb/libusb/sync.c
    libusb/libusb/strerror.c
    libusb/libusb/os/linux_usbfs.c
    libusb/libusb/os/poll_posix.c
    libusb/libusb/os/threads_posix.c
    libusb/libusb/os/linux_netlink.c
)

add_library(
    # Sets the name of the library.
    usb-runtime

    # Sets the library as a shared library.
    SHARED

    # Provides a relative path to your source file(s).
    src/main/cpp/logging.h
    src/main/cpp/com_jwoolston_android_uvc_libusb_UsbDeviceIsoConnection.c
    src/main/cpp/com_jwoolston_android_uvc_libusb_UsbDeviceIsoConnection.h
    ${libusb_SRCS}
)

set_target_properties(usb-runtime PROPERTIES LINKER_LANGUAGE C)

# Specifies libraries CMake should link to your target library. You
# can link multiple libraries, such as libraries you define in this
# build script, prebuilt third-party libraries, or system libraries.
target_link_libraries(usb-runtime ${log-lib})