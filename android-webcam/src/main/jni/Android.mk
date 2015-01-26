LOCAL_PATH := $(call my-dir)
include $(CLEAR_VARS)

LOCAL_MODULE    := android-libusb

# The source files to compile
LOCAL_SRC_FILES := \
	$(LOCAL_PATH)/core.c \
	$(LOCAL_PATH)/descriptor.c \
	$(LOCAL_PATH)/io.c \
	$(LOCAL_PATH)/sync.c \
	$(LOCAL_PATH)/os/linux_usbfs.c \
	$(LOCAL_PATH)/os/threads_posix.c
 	
# The include directories
LOCAL_C_INCLUDES += \
	$(LOCAL_PATH) \
	$(LOCAL_PATH)/os
	
# The target android platform
TARGET_PLATFORM := android-9

# Libraries to link in
LOCAL_LDLIBS := -llog

include $(BUILD_SHARED_LIBRARY)