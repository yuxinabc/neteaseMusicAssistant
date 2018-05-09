package com.synertone.neteasemusicassiant;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.othershe.nicedialog.BaseNiceDialog;
import com.othershe.nicedialog.NiceDialog;
import com.othershe.nicedialog.ViewConvertListener;
import com.othershe.nicedialog.ViewHolder;
import com.synertone.neteasemusicassiant.util.DensityUtils;
import com.synertone.neteasemusicassiant.util.MobileUtils;


public class MainActivity extends AppCompatActivity {
    private BaseNiceDialog baseNiceDialog;
    private BaseNiceDialog baseNiceDialog1;
    private BaseNiceDialog baseNiceDialog2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        checkAdmin();
        checkBatteryOptimizations();
        checkAccessibilitySetting();
    }

    private void checkAccessibilitySetting() {
        if (!MobileUtils.isAccessibilitySettingsOn(this)) {
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

    private void checkBatteryOptimizations() {
        if(!MobileUtils.isIgnoringBatteryOptimizations(this)){
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
                                    MobileUtils.jumpStartInterface(MainActivity.this);
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
    }

    private void checkAdmin() {
        if(!MobileUtils.isAdmin(this)){
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
    }
}
