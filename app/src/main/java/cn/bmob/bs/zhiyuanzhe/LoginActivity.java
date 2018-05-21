package cn.bmob.bs.zhiyuanzhe;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.bs.zhiyuanzhe.bean.User;
import cn.bmob.bs.zhiyuanzhe.common.BaseActivity;
import cn.bmob.bs.zhiyuanzhe.common.Config;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

/**
* author: Mark栋
* date：2018/5/16 21:41
 */
public class LoginActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        setTitle("登录");

        if (Config.getInstance().user != null) {
            activity(MainActivity.class);
            finish();
        }
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.login:
                String userName = ((TextView) findViewById(R.id.username)).getText().toString();
                String password = ((TextView) findViewById(R.id.password)).getText().toString();
                login(userName, password);
                break;
            case R.id.register:
                startActivityForResult(new Intent(this, RegistActivity.class), 0);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case 0:
                    activity(MainActivity.class);
                    finish();
                    break;
            }
        }
    }

    /**
     * 注意下如果返回206错误 一般是多设备登录导致
     */
    private void login(final String username, final String password) {
        showProgress();


        List<BmobQuery<User>> queries = new ArrayList<>();
        BmobQuery<User> query = new BmobQuery();
        query.addWhereEqualTo("username", username);
        queries.add(query);
        query = new BmobQuery();
        query.addWhereEqualTo("password", password);
        queries.add(query);

        query = new BmobQuery();
        query.and(queries);
        query.findObjects(new FindListener<User>() {
            @Override
            public void done(List<User> list, BmobException e) {
                hideProgress();
                if (e == null && list != null && !list.isEmpty()) {
                    User user = list.get(0);
                    toast(user.username + "登录成功");
                    Config.getInstance().user = user;
                    startActivity(new Intent(LoginActivity.this, MainActivity.class));

                    finish();
                } else {
                    toast("登录失败");
                }
            }
        });
    }

}
