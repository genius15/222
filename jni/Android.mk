LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)
LOCAL_MODULE    := memCtrl
LOCAL_SRC_FILES := memCtrl.cpp

include $(BUILD_SHARED_LIBRARY)
