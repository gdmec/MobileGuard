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
import android.support.test.uiautomator.UiObjectNotFoundException;
import android.support.test.uiautomator.UiSelector;
import android.support.test.uiautomator.Until;
import android.widget.EditText;

import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
    public void t1ShowUpdateDialog() throws UiObjectNotFoundException {
        UiObject result = mDevice.findObject(new UiSelector().textContains("2.0"));
        String str = null;
        str = result.getText();
        assertNotNull("检查到新版本",result);
    }

    @Test
    public void t2ShowMainActivity() throws UiObjectNotFoundException {
        // 使用UIselector找到包含『版本号』文字的UI组件
        UiObject result = mDevice.findObject(new UiSelector().textStartsWith("暂不升级"));
        result.clickAndWaitForNewWindow();
        result = mDevice.findObject(new UiSelector().textStartsWith("手机防盗"));
        assertNotNull("出现主界面手机防盗",result);
    }

    @Test
    public void t3SetupPwd() throws Exception{
        UiObject result = mDevice.findObject(new UiSelector().textStartsWith("暂不升级"));
        result.clickAndWaitForNewWindow();
        result = mDevice.findObject(new UiSelector().textStartsWith("手机防盗"));
        result.clickAndWaitForNewWindow();
        List<UiObject2> results;
        results = mDevice.findObjects(By.clazz(EditText.class));
        UiObject2 pwd1 = results.get(0);
        pwd1.setText("1");
        UiObject2 pwd2 = results.get(1);
        pwd2.setText("1");
        result = mDevice.findObject(new UiSelector().textStartsWith("确认"));
        result.clickAndWaitForNewWindow();
    }

    @Test
    public void t4EnterPwd() throws Exception{
        UiObject result = mDevice.findObject(new UiSelector().textStartsWith("暂不升级"));
        result.clickAndWaitForNewWindow();
        result = mDevice.findObject(new UiSelector().textStartsWith("手机防盗"));
        result.clickAndWaitForNewWindow();
        List<UiObject2> results;
        results = mDevice.findObjects(By.clazz(EditText.class));
        UiObject2 pwd1 = results.get(0);
        pwd1.setText("1");
        result = mDevice.findObject(new UiSelector().textStartsWith("确认"));
        result.clickAndWaitForNewWindow();
    }

    @Test
    public void t5SetupFling() throws Exception{
        System.out.println("begin SetupFling");
        UiObject result = mDevice.findObject(new UiSelector().textStartsWith("暂不升级"));
        result.clickAndWaitForNewWindow();
        result = mDevice.findObject(new UiSelector().textStartsWith("手机防盗"));
        result.clickAndWaitForNewWindow();
        List<UiObject2> results;
        results = mDevice.findObjects(By.clazz(EditText.class));
        UiObject2 pwd1 = results.get(0);
        pwd1.setText("1");
        result = mDevice.findObject(new UiSelector().textStartsWith("确认"));
        result.clickAndWaitForNewWindow();
        mDevice.wait(Until.hasObject(By.textStartsWith("手机防盗向导")),LAUNCH_TIMEOUT);
        mDevice.swipe(400,300,0,300,100);
        mDevice.wait(Until.hasObject(By.textStartsWith("SIM卡绑定")),LAUNCH_TIMEOUT);
        mDevice.swipe(400,300,0,300,100);
        mDevice.wait(Until.hasObject(By.textStartsWith("选择安全联系人")),LAUNCH_TIMEOUT);
        mDevice.swipe(400,300,0,300,100);
        result = mDevice.findObject(new UiSelector().textStartsWith("恭喜"));

        assertNotNull("手机防盗向导",result);
    }
}