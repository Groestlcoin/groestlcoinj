/**
 * Created by Hash Engineering on 4/24/14 for the X11 algorithm
 */
#include "hashblock.h"
#include <inttypes.h>

#include <jni.h>



jbyteArray JNICALL groestld_native(JNIEnv *env, jclass cls, jbyteArray header, jint offset, jint length)
{
    jint Plen = (env)->GetArrayLength(header);
    jbyte *P = (env)->GetByteArrayElements(header, NULL);
    //uint8_t *buf = malloc(sizeof(uint8_t) * dkLen);
    jbyteArray DK = NULL;

    if (P)
	{
	    char unsigned result[HASH256_SIZE];
        HashGroestl(reinterpret_cast<const unsigned char*>(P+offset), reinterpret_cast<const unsigned char*>(P+length+offset), result);

        DK = (env)->NewByteArray(32);
        if (DK)
        {
            (env)->SetByteArrayRegion(DK, 0, 32, (jbyte *) result);
        }


        if (P) (env)->ReleaseByteArrayElements(header, P, JNI_ABORT);

	}
    return DK;
}

static const JNINativeMethod methods[] = {
    { (char*)"groestld_native", (char*)"([BII)[B", (void *) groestld_native }
};

jint JNI_OnLoad(JavaVM *vm, void *reserved) {
    JNIEnv *env;

    if ((vm)->GetEnv((void **) &env, JNI_VERSION_1_6) != JNI_OK) {
        return -1;
    }

    jclass cls = (env)->FindClass("com/hashengineering/crypto/Groestl");
    int r = (env)->RegisterNatives(cls, methods, 1);

    return (r == JNI_OK) ? JNI_VERSION_1_6 : -1;
}