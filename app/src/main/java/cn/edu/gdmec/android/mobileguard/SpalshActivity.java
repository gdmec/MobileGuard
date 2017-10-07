package cn.edu.gdmec.android.mobileguard;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import junit.runner.Version;

import cn.edu.gdmec.android.mobileguard.m1home.utils.MyUtils;
import cn.edu.gdmec.android.mobileguard.m1home.utils.VersionUpdateUtils;

public class SpalshActivity extends AppCompatActivity {
    private String mVersion;
    private TextView mVersionTV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spalsh);
        getSupportActionBar().hide();
        mVersion = MyUtils.getVersion(getApplicationContext());
        mVersionTV = (TextView) findViewById(R.id.tv_splash_version);
        mVersionTV.setText("版本号:"+mVersion);
        final VersionUpdateUtils versionUpdateUtils = new VersionUpdateUtils(mVersion,SpalshActivity.this);
        new Thread(){

            @Override
            public void run() {
                versionUpdateUtils.getCloudVersion();
            }
        }.start();
    }
}
