//
// Created by Hicore on 2022/8/23.
//

#include <dex_kit.h>
#include <jni.h>
extern "C"
JNIEXPORT jobjectArray JNICALL
Java_cc_hicore_DexFinder_DexFinderNative_findMethodUsingString(JNIEnv *env, jclass clazz, jstring apk_path,
                                                               jstring str) {
    dexkit::DexKit dexKit(env->GetStringUTFChars(apk_path,nullptr));
    const char* findStr = env->GetStringUTFChars(str, nullptr);
    std::vector<size_t> p{};
    auto findResult = dexKit.FindMethodUsedString(findStr, {}, {}, {}, {}, p, true, false);
    jobjectArray result = env->NewObjectArray(findResult.size(), env->FindClass("java/lang/String"),
                                              env->NewStringUTF(""));
    for (int i = 0; i < findResult.size(); i++) {
        env->SetObjectArrayElement(result,i,env->NewStringUTF(findResult[i].c_str()));
    }
    return result;

}
extern "C"
JNIEXPORT jobjectArray JNICALL
Java_cc_hicore_DexFinder_DexFinderNative_findMethodInvoked(JNIEnv *env, jclass clazz,jstring apk_path,
                                                           jstring invoke_method_desc) {
    dexkit::DexKit dexKit(env->GetStringUTFChars(apk_path,nullptr));
    const char* methodDesc = env->GetStringUTFChars(invoke_method_desc, nullptr);
    std::vector<size_t> p{};
    std::vector<std::string> v;
    auto findResult = dexKit.FindMethodInvoked(methodDesc,
                                               "", "", "", v, p, false);
    jobjectArray result = env->NewObjectArray(findResult.size(), env->FindClass("java/lang/String"),
                                              env->NewStringUTF(""));
    for (int i = 0; i < findResult.size(); i++) {
        env->SetObjectArrayElement(result,i,env->NewStringUTF(findResult[i].c_str()));
    }
    return result;
}
extern "C"
JNIEXPORT void JNICALL
Java_cc_hicore_DexFinder_DexFinderNative_InitPath(JNIEnv *env, jclass clazz, jstring apk_path) {

}