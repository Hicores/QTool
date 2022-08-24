//
// Created by Hicore on 2022/8/23.
//

#include <dex_kit.h>
#include <jni.h>
#include <android/log.h>
extern "C"
JNIEXPORT jobjectArray JNICALL
Java_cc_hicore_DexFinder_DexFinderNative_findMethodUsingString(JNIEnv *env, jclass clazz, jlong dexkitInstance,
                                                               jstring str) {
    auto dexHelper = (dexkit::DexKit*)dexkitInstance;
    const char* findStr = env->GetStringUTFChars(str, nullptr);
    std::vector<size_t> p{};
    auto findResult = dexHelper->FindMethodUsedString(findStr, {}, {}, {}, {}, p, true, false);
    jobjectArray result = env->NewObjectArray(findResult.size(), env->FindClass("java/lang/String"),
                                              env->NewStringUTF(""));
    for (int i = 0; i < findResult.size(); i++) {
        env->SetObjectArrayElement(result,i,env->NewStringUTF(findResult[i].c_str()));
    }
    return result;

}
extern "C"
JNIEXPORT jobjectArray JNICALL
Java_cc_hicore_DexFinder_DexFinderNative_findMethodInvoked(JNIEnv *env, jclass clazz,jlong dexkitInstance,
                                                           jstring invoke_method_desc) {
    auto dexHelper = (dexkit::DexKit*)dexkitInstance;
    const char* methodDesc = env->GetStringUTFChars(invoke_method_desc, nullptr);
    std::vector<size_t> p{};
    std::vector<std::string> v;
    auto findResult = dexHelper->FindMethodInvoked(methodDesc,
                                               "", "", "", v, p, true);
    jobjectArray result = env->NewObjectArray(findResult.size(), env->FindClass("java/lang/String"),
                                              env->NewStringUTF(""));
    for (int i = 0; i < findResult.size(); i++) {
        env->SetObjectArrayElement(result,i,env->NewStringUTF(findResult[i].c_str()));
    }
    return result;
}

extern "C"
JNIEXPORT jlong JNICALL
Java_cc_hicore_DexFinder_DexFinderNative_InitPath(JNIEnv *env, jclass clazz, jstring apk_path) {
    auto dexKitHelper = new dexkit::DexKit(env->GetStringUTFChars(apk_path,nullptr));
    return (jlong)dexKitHelper;
}