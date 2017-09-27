package com.example.lsm.firesensorproject;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;

import static com.example.lsm.firesensorproject.SettingFragment.push_flag;

public class PushService extends Service {
    public static NotificationManager noti_manager;
    public static Notification notifi;
    public static PushThread thread;
    public static PushServiceHandler handler;
    public static int temp, humid;
    public static int noti_id = 1;

    @Override
    public IBinder onBind(Intent intent) {
        // Service 객체와 (화면단 Activity 사이에서)
        // 통신(데이터를 주고받을) 때 사용하는 메서드
        // 데이터를 전달할 필요가 없으면 return null;
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        // 서비스에서 가장 먼저 호출됨(최초에 한번만)

        temp = 27;
        humid = 10;

        //알림(Notification)을 관리하는 NotificationManager 얻어오기
        noti_manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        handler = new PushServiceHandler();
        thread = new PushThread(handler);
        thread.start();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // 서비스가 호출될 때마다 실행

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // 서비스가 종료될 때 실행

        //알림 제거할 때
        //noti_manager.cancel(id);
    }

    class PushServiceHandler extends Handler {
        @Override
        public void handleMessage(android.os.Message msg) {
            if (push_flag) {
                Intent intent = new Intent(PushService.this, MainActivity.class);
                PendingIntent pendingIntent = PendingIntent.getActivity(PushService.this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

                //알림(Notification)을 만들어내는 Builder 객체 생성
                NotificationCompat.Builder builder = new NotificationCompat.Builder(PushService.this)
                        .setSmallIcon(R.drawable.app_icon)  //상태표시줄에 보이는 아이콘 모양
                        .setContentTitle("화재 감지 중입니다.")                        //알림창에서의 제목
                        .setContentText("현재 상태: 정상    온도: " + String.valueOf(temp) + "℃    습도: " + String.valueOf(humid) + "%");   //알림창에서의 글씨

                //알림을 확인했을 때(알림창 클릭) MainActivity 실행
                builder.setContentIntent(pendingIntent);   //PendingIntent 설정
                builder.setAutoCancel(false);         //클릭하면 자동으로 알림 삭제 X

                notifi = builder.build();    //Notification 객체 생성
                notifi.flags |= Notification.FLAG_ONGOING_EVENT;  //노티피케이션이 알림에 뜨지 않고 진행중에 뜨게 되는 플래그
                noti_manager.notify(noti_id, notifi);    //NotificationManager가 알림(Notification)을 표시, id는 알림구분용

                temp++;
                humid++;

                if (temp == 100) {
                    temp = 27;
                    humid = 10;
                }
            } else
                noti_manager.cancel(noti_id);
        }
    }
}