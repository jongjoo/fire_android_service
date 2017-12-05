package com.example.lsm.firesensorproject;

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

/**
 * 사용자 기기별 token을 생성하는 클래스 입니다.
 * 나중에 push 알림을 특정 타겟에게 보낼 때 사용되는 고유 키 값 입니다.
 * 이 토큰값을 용도에 맞게 사용하시면 됩니다.
 */


public class FirebaseInstanceIDService extends FirebaseInstanceIdService {

    private static final String TAG = "MyFirebaseIIDService";

    // [START refresh_token]
    @Override
    public void onTokenRefresh() {
        // Get updated InstanceID token.
        String token = FirebaseInstanceId.getInstance().getToken();
        Log.d("TOKEN","TOKEN: "+token);
    }
}