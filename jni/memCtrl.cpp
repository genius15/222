#include <jni.h>
#include <string.h>
char* p = 0;
extern "C" {
    JNIEXPORT jstring JNICALL Java_com_sogou_mobiletoolassist_service_CoreService_memcreate(JNIEnv * env, jobject obj,int mem);
    JNIEXPORT jstring JNICALL Java_com_sogou_mobiletoolassist_service_CoreService_memfree(JNIEnv * env, jobject obj);
}

JNIEXPORT jstring JNICALL Java_com_sogou_mobiletoolassist_service_CoreService_memcreate(JNIEnv * env, jobject obj,int mem) {
    if(p){
    	delete[] p;
    }
	p = new char[1024*1024*mem];
    if(p){
    	memset(p,1,1024*1024*mem);
    }
	return env->NewStringUTF("memory created");
}

JNIEXPORT jstring JNICALL Java_com_sogou_mobiletoolassist_service_CoreService_memfree(JNIEnv * env, jobject obj) {
    if(p){
    	delete [] p;
    	p = 0;
    }
	return env->NewStringUTF("memory freed");
}


