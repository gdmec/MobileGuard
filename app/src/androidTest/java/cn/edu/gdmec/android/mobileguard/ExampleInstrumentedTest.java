package cn.edu.gdmec.android.mobileguard;

import android.content.Context;
import android.content.Intent;
import android.support.test.InstrumentationRegistry;
import android.support.test.filters.SdkSuppress;
import android.support.test.runner.AndroidJUnit4;
import android.support.test.uiautomator.By;
import android.support.test.uiautomator.UiDevice;
import android.support.test.uiautomator.UiObject;
import android.support.test.uiautomator.UiObject2;
import android.support.test.uiautomator.UiSelector;
import android.support.test.uiautomator.Until;

import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;

import java.util.Date;

import static java.lang.Thread.sleep;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)
@SdkSuppress(minSdkVersion = 18)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ExampleInstrumentedTest {
    //包名
    private static final String BASIC_SAMPLE_PACKAGE
            = "cn.edu.gdmec.android.mobileguard";
    //超时时间
    private static final int LAUNCH_TIMEOUT = 5000;
    //设备实例
    private UiDevice mDevice;
    @Before
    public void startMainActivityFromHomeScreen() {
        // 初始化 UiDevice 实例
        mDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());

        // 按home键，返回到主界面
        mDevice.pressHome();

        // 等待应用装载运行
        final String launcherPackage = mDevice.getLauncherPackageName();
        assertThat(launcherPackage, notNullValue());
        mDevice.wait(Until.hasObject(By.pkg(launcherPackage).depth(0)),
                LAUNCH_TIMEOUT);

        // 启动应用
        Context context = InstrumentationRegistry.getContext();
        final Intent intent = context.getPackageManager()
                .getLaunchIntentForPackage(BASIC_SAMPLE_PACKAGE);
        // 开始新的acivity，移除以前的所有实例
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(intent);

        // 等待应用启动
        mDevice.wait(Until.hasObject(By.pkg(BASIC_SAMPLE_PACKAGE).depth(0)),
                LAUNCH_TIMEOUT);
    }

    public boolean waitForUiObject(UiSelector selector,double timeOut) {//等待对象出现
        Date start = new Date();
        boolean result = false;
        while(!result){
            try {
                sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            UiObject it = mDevice.findObject(selector);
            if (it.exists()) {
                result = true;
                break;
            }
            Date end = new Date();
            long time = end.getTime() - start.getTime();
            if (time>timeOut) {
                break;
            }
        }
        return result;
    }
    @Test
    public void t1ShowVersion() throws Exception {
        UiObject result = mDevice.findObject(new UiSelector().textStartsWith("版本号"));
        assertNotNull("出现版本号",result);
    }

    @Test
    public void t2ShowUpdateDialog() throws Exception {
//        UiSelector us = new UiSelector().className("android.app.AlertDialog");
//        if(waitForUiObject(us,10000)){
//            UiObject object2 ;
//            object2 = mDevice.findObject(us);
//            assertNotNull("弹出升级对话框",object2);
//        }
        UiObject result = mDevice.findObject(new UiSelector().textStartsWith("2.0"));
        assertNotNull("检查到新版本",result);
    }

    @Test
    public void t3ShowMainActivity() throws Exception {
        // 使用UIselector找到包含『版本号』文字的UI组件
        UiObject result = mDevice.findObject(new UiSelector().textStartsWith("暂不升级"));
        result.clickAndWaitForNewWindow();
        result = mDevice.findObject(new UiSelector().textStartsWith("手机防盗"));
        assertNotNull("出现主界面手机防盗",result);
    }


}