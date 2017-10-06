package cn.edu.gdmec.android.mobileguard.m1home.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.File;
import java.io.IOException;
import cn.edu.gdmec.android.mobileguard.R;
import cn.edu.gdmec.android.mobileguard.m1home.HomeActivity;
import cn.edu.gdmec.android.mobileguard.m1home.entity.VersionEntity;

/**
 * 更新提醒工具类
 * Created by apple on 2017/9/11.
 */

public class VersionUpdateUtils {
    //声明常量
    private static final int MESSAGE_IO_ERROR = 102;
    private static final int MESSAGE_JSON_ERROR = 103;
    private static final int MESSAGE_SHOW_DIALOG = 104;
    private static final int MESSAGE_ENTERHOME = 105;
    //声明类属性
    private String mVersion;
    private Activity context;
    private ProgressDialog mProgressDialog;
    private VersionEntity versionEntity;

    //构造方法
    public VersionUpdateUtils(String mVersion, Activity context) {
        this.mVersion = mVersion;
        this.context = context;
    }

    //发送进入主界面消息
    private void enterHome(){
        handler.sendEmptyMessageDelayed(MESSAGE_ENTERHOME,2000);
    }

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
           switch(msg.what){
               case MESSAGE_IO_ERROR:
                   Toast.makeText(context,"IO异常",Toast.LENGTH_LONG).show();
                   enterHome();
                   break;
               case MESSAGE_JSON_ERROR:
                   Toast.makeText(context,"JSON解析异常",Toast.LENGTH_LONG).show();
                   enterHome();
                   break;
               case MESSAGE_SHOW_DIALOG:
                   showUpdataDialog(versionEntity);
                   break;
               case MESSAGE_ENTERHOME:
                   Intent intent = new Intent(context,HomeActivity.class);
                   context.startActivity(intent);
                   context.finish();
                   break;
           }
        }
    };


    /**
     * 获取服务器版本号*/
    public void getCloudVersion(){

        try {
            HttpClient client = new DefaultHttpClient();
		  /*连接超时*/
            HttpConnectionParams.setConnectionTimeout(client.getParams(), 5000);
        /*请求超时*/
            HttpConnectionParams.setSoTimeout(client.getParams(), 5000);
            HttpGet httpGet = new HttpGet(
                    "http://android2017.duapp.com/updateinfo.html");
            HttpResponse execute = client.execute(httpGet);
            if (execute.getStatusLine().getStatusCode() == 200) {
                // 请求和响应都成功了
                HttpEntity entity = execute.getEntity();
                String result = EntityUtils.toString(entity, "utf-8");
                // 创建jsonObject对象
                JSONObject jsonObject = new JSONObject(result);
                versionEntity = new VersionEntity();
                String code = jsonObject.getString("code");
                versionEntity.versioncode = code;
                String des = jsonObject.getString("des");
                versionEntity.description = des;
                String apkurl = jsonObject.getString("apkurl");
                versionEntity.apkurl = apkurl;
                if (!mVersion.equals(versionEntity.versioncode)) {
                    // 版本号不一致
                    handler.sendEmptyMessage(MESSAGE_SHOW_DIALOG);
                }
            }
        } catch (IOException e) {
            handler.sendEmptyMessage(MESSAGE_IO_ERROR);
            e.printStackTrace();
        } catch (JSONException e) {
            handler.sendEmptyMessage(MESSAGE_JSON_ERROR);
            e.printStackTrace();
        }
    }
    private void showUpdataDialog(final VersionEntity versionEntity){
        //创建dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("检查到有新版本:"+versionEntity.versioncode);
        builder.setMessage(versionEntity.description);
        builder.setCancelable(false);
        builder.setIcon(R.mipmap.ic_launcher_round);
        builder.setPositiveButton("立刻升级", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //下载apk
                downloadNewApk(versionEntity.apkurl);
                enterHome();
            }
        });
        builder.setNegativeButton("暂不升级", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                enterHome();
            }
        });
        builder.show();

    }


    protected void downloadNewApk(String apkurl){
        DownLoadUtils downLoadUtils = new DownLoadUtils();
        downLoadUtils.downapk(apkurl, "mobilesafenew.apk", context);
    }
}
