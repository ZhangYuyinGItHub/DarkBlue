#include <jni.h>
#include <stdio.h>

//com.zhangyy.bluetooth.le
JNIEXPORT void JNICALL Java_com_zhangyy_bluetooth_le_RcuVoiceActivity_DeCode(
		JNIEnv * env, jobject obj, jbooleanArray arrin, jint len,
		jshortArray arrout) {
	//拿到整型数组的长度以及第0个元素的地址
	//jsize       (*GetArrayLength)(JNIEnv*, jarray);
	int length = (*env)->GetArrayLength(env, arrin);
	unsigned char* arrp = (*env)->GetBooleanArrayElements(env, arrin, 0);

	int i;
	for (i = 0; i < length; i++) {
		*(arrp + i) = 55; //将数组中的每个元素加10
	}

}

JNIEXPORT jstring JNICALL Java_com_zhangyy_bluetooth_le_RcuVoiceActivity_GetString(
		JNIEnv * env, jobject obj, jbyteArray datain) {
	char cstr[] = "hello form c";

	int len = (*env)->GetArrayLength(env, datain);
	unsigned char* arrin = (*env)->GetByteArrayElements(env, datain, 0);
	cstr[0] = arrin[0];

	(*env)->SetByteArrayRegion(env, arrin, 0, 1, 51);
	jstring jstr2 = (*env)->NewStringUTF(env, cstr);
	return jstr2;
}

JNIEXPORT jintArray JNICALL Java_com_zhangyy_bluetooth_le_RcuVoiceActivity_encodeArray
  (JNIEnv * env, jobject obj, jintArray arr){
     //拿到整型数组的长度以及第0个元素的地址
     //jsize       (*GetArrayLength)(JNIEnv*, jarray);
    int length = (*env)->GetArrayLength(env, arr);
    // jint*       (*GetIntArrayElements)(JNIEnv*, jintArray, jboolean*);
    int* arrp = (*env)->GetIntArrayElements(env, arr, 0);
    int i;
    for(i = 0;i<length;i++){
        *(arrp + i) += 10; //将数组中的每个元素加10
    }

    printf("***%d", arrp[0]);

    return arr;
}
