package cn.bmob.bs.zhiyuanzhe;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import cn.bmob.bs.zhiyuanzhe.common.Config;
import cn.bmob.push.PushConstants;

/**
* author: Mark栋
* date：2018/5/16 21:41
 */
public class MyPushReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.e("dongjinn", "MyPushReceiver");
        if (Config.getInstance().user == null || Config.getInstance().user.type == 0) {
            return;
        }
        if(intent.getAction().equals(PushConstants.ACTION_MESSAGE)){
            String msg = intent.getStringExtra(PushConstants.EXTRA_PUSH_MESSAGE_STRING);

            try {
                JSONObject object = new JSONObject(msg);
                String hdid = object.getString("huodong");

                if (!Config.getInstance().myHuodongs.containsKey(hdid)) {
                    return;
                }

                String hdname = object.getString("huodongname");
                String title = object.getString("title");

                NotificationManager notifyManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                //实例化NotificationCompat.Builde并设置相关属性
                NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                        //设置小图标
                        .setSmallIcon(R.mipmap.ic_launcher)
                                //设置通知标题
                        .setContentTitle("活动 “" + hdname + "” 有新的通知" )
                                //设置通知内容
                        .setContentText(title)
                        .setAutoCancel(true);
                Intent intent1 = new Intent(context, MainActivity.class);
                intent1.putExtra("fromtz", true);
                intent1.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent1, PendingIntent.FLAG_UPDATE_CURRENT);
                builder.setContentIntent(pendingIntent);
                //设置通知时间，默认为系统发出通知的时间，通常不用设置
                //.setWhen(System.currentTimeMillis());
                //通过builder.build()方法生成Notification对象,并发送通知,id=1
                notifyManager.notify(1, builder.build());

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }
}
