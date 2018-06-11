cmake_minimum_required(VERSION 3.4.1)

find_library( # Sets the name of the path variable.
              log-lib

              # Specifies the name of the NDK library that
              # you want CMake to locate.
              log )

MESSAGE( STATUS "CMAKE_CURRENT_BINARY_DIR: " ${CMAKE_CURRENT_BINARY_DIR} )

include_directories(libusb/android)

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
    ${libusb_SRCS}
)

set_target_properties(usb-runtime PROPERTIES LINKER_LANGUAGE C)

# Specifies libraries CMake should link to your target library. You
# can link multiple libraries, such as libraries you define in this
# build script, prebuilt third-party libraries, or system libraries.
target_link_libraries(usb-runtime log-lib)