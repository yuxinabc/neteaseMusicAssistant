package com.synertone.neteasemusicassiant;

import android.annotation.TargetApi;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;

import com.othershe.nicedialog.BaseNiceDialog;
import com.othershe.nicedialog.NiceDialog;
import com.othershe.nicedialog.ViewConvertListener;
import com.othershe.nicedialog.ViewHolder;
import com.synertone.neteasemusicassiant.util.DensityUtils;
import com.synertone.neteasemusicassiant.util.MobileInfoUtils;


public class MainActivity extends AppCompatActivity {
    private BaseNiceDialog baseNiceDialog;
    private BaseNiceDialog baseNiceDialog1;
    private BaseNiceDialog baseNiceDialog2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if(!isAdmin()){
            baseNiceDialog1 = NiceDialog.init().setLayoutId(R.layout.dialog)
                    .setConvertListener(new ViewConvertListener() {     //进行相关View操作的回调
                        @Override
                        public void convertView(ViewHolder holder, final BaseNiceDialog dialog) {
                            holder.setText(R.id.tv_content, getString(R.string.tip_security_settings));
                            holder.setOnClickListener(R.id.btb_cancel, new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    dialog.dismiss();
                                }
                            });
                            holder.setOnClickListener(R.id.btb_ok, new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent intent = new Intent(Settings.ACTION_SECURITY_SETTINGS);
                                    startActivity(intent);
                                    dialog.dismiss();
                                }
                            });
                        }
                    })
                    .setMargin(DensityUtils.dip2px(this, 15));
            if(!baseNiceDialog1.isVisible()){
                baseNiceDialog1.show(getSupportFragmentManager());
            }

        }
        if(!isIgnoringBatteryOptimizations()){
            baseNiceDialog2 = NiceDialog.init().setLayoutId(R.layout.dialog)
                    .setConvertListener(new ViewConvertListener() {     //进行相关View操作的回调
                        @Override
                        public void convertView(ViewHolder holder, final BaseNiceDialog dialog) {
                            holder.setText(R.id.tv_content, getString(R.string.tip_battery_optimzation));
                            holder.setOnClickListener(R.id.btb_cancel, new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    dialog.dismiss();
                                }
                            });
                            holder.setOnClickListener(R.id.btb_ok, new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    MobileInfoUtils.jumpStartInterface(MainActivity.this);
                                    dialog.dismiss();
                                }
                            });
                        }
                    })
                    .setMargin(DensityUtils.dip2px(this, 15));
            if(!baseNiceDialog2.isVisible()){
                baseNiceDialog2.show(getSupportFragmentManager());
            }
        }
        if (!isAccessibilitySettingsOn(this)) {
            baseNiceDialog = NiceDialog.init().setLayoutId(R.layout.dialog)
                    .setConvertListener(new ViewConvertListener() {     //进行相关View操作的回调
                        @Override
                        public void convertView(ViewHolder holder, final BaseNiceDialog dialog) {
                            holder.setText(R.id.tv_content, getString(R.string.tip_accessibility));
                            holder.setOnClickListener(R.id.btb_cancel, new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    dialog.dismiss();
                                }
                            });
                            holder.setOnClickListener(R.id.btb_ok, new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
                                    startActivity(intent);
                                    dialog.dismiss();
                                }
                            });
                        }
                    })
                    .setMargin(DensityUtils.dip2px(this, 15));
            if(!baseNiceDialog.isVisible()){
                baseNiceDialog.show(getSupportFragmentManager());
            }


        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @TargetApi(Build.VERSION_CODES.M)
    private boolean isIgnoringBatteryOptimizations() {
        PowerManager pm = (PowerManager)getSystemService(Context.POWER_SERVICE);
        return pm.isIgnoringBatteryOptimizations(getPackageName());
    }

    private boolean isAdmin() {
        DevicePolicyManager policyManager = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
        ComponentName adminReceiver = new ComponentName(this, ScreenOffAdminReceiver.class);
        return  policyManager.isAdminActive(adminReceiver);
    }

    /**
     * 检测辅助功能是否开启<br>
     * 方 法 名：isAccessibilitySettingsOn <br>
     * 创 建 人 <br>
     * 创建时间：2016-6-22 下午2:29:24 <br>
     * 修 改 人： <br>
     * 修改日期： <br>
     * @param mContext
     * @return boolean
     */
    private boolean isAccessibilitySettingsOn(Context mContext) {
        int accessibilityEnabled = 0;
        // TestService为对应的服务
        final String service = getPackageName() + "/" + SwitchSongService.class.getCanonicalName();
        try {
            accessibilityEnabled = Settings.Secure.getInt(mContext.getApplicationContext().getContentResolver(),
                    android.provider.Settings.Secure.ACCESSIBILITY_ENABLED);
        } catch (Settings.SettingNotFoundException e) {
           e.printStackTrace();
        }
        TextUtils.SimpleStringSplitter mStringColonSplitter = new TextUtils.SimpleStringSplitter(':');

        if (accessibilityEnabled == 1) {
            String settingValue = Settings.Secure.getString(mContext.getApplicationContext().getContentResolver(),
                    Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES);
            if (settingValue != null) {
                mStringColonSplitter.setString(settingValue);
                while (mStringColonSplitter.hasNext()) {
                    String accessibilityService = mStringColonSplitter.next();
                    if (accessibilityService.equalsIgnoreCase(service)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
}
