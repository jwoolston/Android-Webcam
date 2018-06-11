cmake_minimum_required(VERSION 3.6.4)

include_directories(libusb/android)

add_library(usb-runtime SHARED
  libusb/core.c
  libusb/descriptor.c
  libusb/hotplug.c
  libusb/io.c
  libusb/sync.c
  libusb/strerror.c
  libusb/os/linux_usbfs.c
  libusb/os/poll_posix.c
  libusb/os/threads_posix.c
  libusb/os/linux_netlink.c)


# add lib dependencies
target_link_libraries(usb-runtime log)