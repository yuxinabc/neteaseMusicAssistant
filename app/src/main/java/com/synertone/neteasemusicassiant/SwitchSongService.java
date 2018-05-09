package com.synertone.neteasemusicassiant;

import android.accessibilityservice.AccessibilityService;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;
import android.os.PowerManager;
import android.os.Vibrator;
import android.support.v4.app.NotificationCompat;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import java.util.List;

public class SwitchSongService extends AccessibilityService {
    public AccessibilityNodeInfo delButton;
    private SensorManager sensorManager;
    private Vibrator vibrator;
    private ShakeListener shakeListener;
    private PowerManager.WakeLock mWakelock;
    private PowerManager pm;

    @Override
    public void onCreate() {
        super.onCreate();
        initPowerManger();
        initNotification();
        initShake();
    }

    private void initShake() {
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        vibrator = (Vibrator) getSystemService(Service.VIBRATOR_SERVICE);
        shakeListener = new ShakeListener();
        sensorManager.registerListener(shakeListener, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), sensorManager.SENSOR_DELAY_NORMAL);
    }

    private void initNotification() {
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this,"default");
        notification(mBuilder, new Intent(this, MainActivity.class));
        Notification notification = mBuilder.build();
        notification.flags = Notification.FLAG_ONGOING_EVENT;
        notification.flags |= Notification.FLAG_NO_CLEAR;
        notification.flags |= Notification.FLAG_FOREGROUND_SERVICE;
        startForeground(1,notification);
    }

    private void initPowerManger() {
        pm = (PowerManager)getSystemService(POWER_SERVICE);// init powerManager
        mWakelock = pm.newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP|
                PowerManager.SCREEN_DIM_WAKE_LOCK,"com.netease.cloudmusic");
    }


    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        int eventType = event.getEventType();
        switch (eventType) {
            case AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED:
                break;
            case AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED:
            case AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED:
                String className = event.getClassName().toString();
                System.out.println("className="+className);
               if (className.contains("PlayerRadioActivity")) {
                   //FM模式
                   getViewById("com.netease.cloudmusic:id/qw");
                }else if(className.contains("LockScreenActivity")){
                   //锁屏状态且为FM模式
                   getViewById("com.netease.cloudmusic:id/mi");
               }else if(className.contains("PlayerActivity")) {
                   //歌单模式
                   getViewById("com.netease.cloudmusic:id/q7");
               }
                break;
        }
    }

    private void getViewById(String id) {
        AccessibilityNodeInfo nodeInfo = getRootInActiveWindow();
        if (nodeInfo != null) {
            List<AccessibilityNodeInfo> list = nodeInfo.findAccessibilityNodeInfosByViewId(id);
            if(list!=null&&list.size()>0){
                delButton=list.get(0);
            }else{
                //锁屏状态且为歌单模式
                List<AccessibilityNodeInfo> list1 = nodeInfo.findAccessibilityNodeInfosByViewId("com.netease.cloudmusic:id/ml");
                if(list1!=null&&list1.size()>0){
                    delButton=list1.get(0);
                }
            }
            nodeInfo.recycle();
        }
    }
    private boolean isDarkScreen(){
        PowerManager pm = (PowerManager)getSystemService(Context.POWER_SERVICE);
        boolean isScreenOn = pm.isInteractive();
        return !isScreenOn;
    }
    private void screenOff(){
        DevicePolicyManager policyManager = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
        ComponentName adminReceiver = new ComponentName(this, ScreenOffAdminReceiver.class);
        boolean admin = policyManager.isAdminActive(adminReceiver);
        if (admin) {
            policyManager.lockNow();
        }
    }
    @Override
    public void onInterrupt() {

    }



    //摇一摇监听器
    public class ShakeListener implements SensorEventListener {
        /**
         * 检测的时间间隔
         */
        static final int UPDATE_INTERVAL = 100;
        /**
         * 上一次检测的时间
         */
        long mLastUpdateTime;
        /**
         * 上一次检测时，加速度在x、y、z方向上的分量，用于和当前加速度比较求差。
         */
        float mLastX, mLastY, mLastZ;

        /**
         * 摇晃检测阈值，决定了对摇晃的敏感程度，越小越敏感。
         */
        public int shakeThreshold = 2800;
        private long currentDelTime;
        private long preDelTime;


        @Override
        public void onSensorChanged(SensorEvent event) {
            long currentTime = System.currentTimeMillis();
            long diffTime = currentTime - mLastUpdateTime;
            if (diffTime < UPDATE_INTERVAL) {
                return;
            }
            mLastUpdateTime = currentTime;
            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];
            float deltaX = x - mLastX;
            float deltaY = y - mLastY;
            float deltaZ = z - mLastZ;
            mLastX = x;
            mLastY = y;
            mLastZ = z;
            float delta = (float) (Math.sqrt(deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ) / diffTime * 10000);
            // 当加速度的差值大于指定的阈值，认为这是一个摇晃
            if (delta > shakeThreshold) {
                vibrator.vibrate(200);
                currentDelTime=System.currentTimeMillis();
                if(currentDelTime-preDelTime>2000){
                    doSwitchSong();
                }
                  preDelTime=currentDelTime;
            }
        }

        private void doSwitchSong() {
            if(isDarkScreen()){
                mWakelock.acquire();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        delButton.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                        mWakelock.release();
                        screenOff();
                    }
                }, 400);

            }else{
                delButton.performAction(AccessibilityNodeInfo.ACTION_CLICK);
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }
    private void notification(NotificationCompat.Builder mBuilder, Intent intent) {
        mBuilder.setDefaults(NotificationCompat.DEFAULT_SOUND);
        //设置震动方式，延迟零秒，震动一秒，延迟一秒、震动一秒
        mBuilder.setContentTitle("换歌服务启动中");
        mBuilder.setSmallIcon(R.mipmap.ic_app);
        mBuilder.setWhen(System.currentTimeMillis());
        mBuilder.setContentInfo("Info");
        mBuilder.setTicker("换歌服务需要启动");
        PendingIntent pintent = PendingIntent.getActivity(this, 0, intent, 0);
        mBuilder.setContentIntent(pintent);
    }
}
