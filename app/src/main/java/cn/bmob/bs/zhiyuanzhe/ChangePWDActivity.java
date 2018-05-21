package cn.bmob.bs.zhiyuanzhe;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import cn.bmob.bs.zhiyuanzhe.bean.User;
import cn.bmob.bs.zhiyuanzhe.common.BaseActivity;
import cn.bmob.bs.zhiyuanzhe.common.Config;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.UpdateListener;

/**
* author: Mark栋
* date：2018/5/16 21:40
 */
public class ChangePWDActivity extends BaseActivity {

    EditText old;
    EditText new1;
    EditText new2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.change_pwd_activity);
        setTitle("修改密码");

        old = (EditText) findViewById(R.id.old);
        new1 = (EditText) findViewById(R.id.new1);
        new2 = (EditText) findViewById(R.id.new2);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);

        switch (v.getId()) {
            case R.id.change:
                if (!checkET(old, "原密码")
                        && !checkET(new1, "新密码")
                        && !checkET(new2, "新密码")) {
                    return;
                }

                if (!new1.getText().toString().equals(new2.getText().toString())) {
                    toast("新密码不一致！");
                    return;
                }

                User myUser = Config.getInstance().user;
                if (!Config.getInstance().user.password.equals(old.getText().toString())) {
                    toast("旧密码不正确");
                    return;
                }

                showProgress();
                myUser.password = new1.getText().toString();

                myUser.update(myUser.getObjectId(), new UpdateListener() {
                    @Override
                    public void done(BmobException e) {
                        hideProgress();
                        if (e == null) {
                            toast("修改成功");
                            finish();
                        } else {
                            toast("修改失败");
                        }
                    }
                });

                break;
        }
    }
}
