package cn.bmob.bs.zhiyuanzhe;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import cn.bmob.bs.zhiyuanzhe.bean.Huodong;
import cn.bmob.bs.zhiyuanzhe.bean.MyHuodong;
import cn.bmob.bs.zhiyuanzhe.common.BaseActivity;
import cn.bmob.bs.zhiyuanzhe.common.Config;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;

/**
* author: Mark栋
* date：2018/5/16 18:49
 * 这个类用于用户点击了活动后，转到不同的Fragment
 */
public class HuodongActivity extends BaseActivity {

    public static void huodong(Context context, Huodong huodong) {
        context.startActivity(new Intent(context, HuodongActivity.class).putExtra("dongtai", huodong));
    }

    Huodong huodong;

    TextView name;
    TextView neirong;
    TextView shijian;
    TextView didian;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_huodong);
        setTitle("活动详情");

        huodong = (Huodong) getIntent().getSerializableExtra("dongtai");

        name = v(R.id.name);
        neirong = v(R.id.neirong);
        shijian = v(R.id.shijian);
        didian = v(R.id.didian);

        name.setText(huodong.name);
        neirong.setText(huodong.content);
        shijian.setText(huodong.shijian);
        didian.setText(huodong.didian);

        Button canjia = v(R.id.canjia);
        if (Config.getInstance().myHuodongs.containsKey(huodong.getObjectId())) {
            canjia.setText("您已参加该活动");
            canjia.setEnabled(false);
            canjia.setClickable(false);
            canjia.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));
        }
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);

        switch (v.getId()) {
            case R.id.canjia:
                showProgress();
                final MyHuodong myHuodong = new MyHuodong();
                myHuodong.huodong = huodong.getObjectId();
                myHuodong.name = huodong.name;
                myHuodong.neirong = huodong.content;
                myHuodong.user = Config.getInstance().user.getObjectId();
                myHuodong.save(new SaveListener<String>() {
                    @Override
                    public void done(String s, BmobException e) {
                        hideProgress();
                        if (e == null) {
                            myHuodong.setObjectId(s);
                            Config.getInstance().myHuodongs.put(myHuodong.huodong, myHuodong);
                            Button canjia = v(R.id.canjia);
                            canjia.setText("您已参加该活动");
                            canjia.setEnabled(false);
                            canjia.setClickable(false);
                            canjia.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));
                        } else {
                            toast("参加活动失败");
                        }
                    }
                });
                break;
        }
    }
}
