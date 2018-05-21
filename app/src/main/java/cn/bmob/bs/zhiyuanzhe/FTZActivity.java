package cn.bmob.bs.zhiyuanzhe;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cn.bmob.bs.zhiyuanzhe.bean.Huodong;
import cn.bmob.bs.zhiyuanzhe.bean.Tongzhi;
import cn.bmob.bs.zhiyuanzhe.common.BaseActivity;
import cn.bmob.push.BmobPush;
import cn.bmob.v3.BmobPushManager;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.PushListener;
import cn.bmob.v3.listener.SaveListener;

/**
* author: Mark栋
* date：2018/5/16 21:41
 */
public class FTZActivity extends BaseActivity {

    EditText title;
    EditText neirong;
    TextView huodong;

    String hdid;
    String hdname;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_ftz);
        setTitle("发布活动通知");

        title = v(R.id.title);
        neirong = v(R.id.neirong);
        huodong = v(R.id.huodong);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == 0) {
                hdid = data.getStringExtra("huodong");
                hdname = data.getStringExtra("name");
                huodong.setText(hdname);
            }
        }
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.submit:
                if (!checkET(title, "标题")
                        || !checkET(neirong, "内容")) {
                    return;
                }

                String sj = huodong.getText().toString();
                if (TextUtils.equals(sj, "点击选择活动")) {
                    toast("请选择活动");
                    return;
                }

                showProgress();
                final Tongzhi tongzhi = new Tongzhi();
                tongzhi.title = title.getText().toString();
                tongzhi.content = neirong.getText().toString();
                tongzhi.huodong = hdid;
                tongzhi.huodongname = hdname;
                tongzhi.save(new SaveListener<String>() {
                    @Override
                    public void done(String s, BmobException e) {
                        hideProgress();
                        if (e == null) {
                            toast("发布成功");

                            BmobPushManager bmobPushManager = new BmobPushManager();
                            JSONObject jsonObject = new JSONObject();
                            try {
                                jsonObject.put("huodong", hdid);
                                jsonObject.put("huodongname", hdname);
                                jsonObject.put("title", tongzhi.title);
                            } catch (JSONException e1) {
                                e1.printStackTrace();
                            }

                            bmobPushManager.pushMessageAll(jsonObject, new PushListener() {
                                @Override
                                public void done(BmobException e) {
                                    if (e == null) {
                                        Log.e("dongjinn", "推送成功");
                                    } else {
                                        Log.e("dongjinn", "推送失败");
                                    }
                                }
                            });

                            finish();
                        } else {
                            toast("发布失败");
                        }
                    }
                });
                break;
            case R.id.huodong:
                startActivityForResult(new Intent(this, AllHuodongActivity.class), 0);
                break;
        }
    }
}
