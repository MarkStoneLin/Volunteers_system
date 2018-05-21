package cn.bmob.bs.zhiyuanzhe;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;

import com.bumptech.glide.Glide;
import com.smile.filechoose.api.ChooserType;
import com.smile.filechoose.api.ChosenFile;
import com.smile.filechoose.api.FileChooserListener;
import com.smile.filechoose.api.FileChooserManager;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.bs.zhiyuanzhe.bean.User;
import cn.bmob.bs.zhiyuanzhe.common.BaseActivity;
import cn.bmob.bs.zhiyuanzhe.common.Config;
import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UploadBatchListener;

/**
* author: Mark栋
* date：2018/5/16 21:41
 */
public class RegistActivity extends BaseActivity implements FileChooserListener {
    String touxiang;
    String url;
    private FileChooserManager fm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_regist);

        fm = new FileChooserManager(this);
        fm.setFileChooserListener(this);

        setTitle("注册");
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.submit:
                String username = ((EditText) findViewById(R.id.username)).getText().toString();
                if (TextUtils.isEmpty(username)) {
                    toast("用户名不能为空");
                    return;
                }
                String name = ((EditText) findViewById(R.id.name)).getText().toString();
                if (TextUtils.isEmpty(name)) {
                    toast("姓名不能为空");
                    return;
                }
                String nianling = ((EditText) findViewById(R.id.nianlin)).getText().toString();
                if (TextUtils.isEmpty(nianling)) {
                    toast("请输入年龄信息");
                    return;
                }

                String password = ((EditText) findViewById(R.id.password)).getText().toString();
                if (TextUtils.isEmpty(password)) {
                    toast("密码不能为空");
                    return;
                }

                String passwordConfirm = ((EditText) findViewById(R.id.passwordConfirm)).getText().toString();
                if (TextUtils.isEmpty(passwordConfirm)) {
                    toast("请再次输入密码");
                    return;
                }
                if (!TextUtils.equals(password, passwordConfirm)) {
                    toast("两次密码不一致");
                    return;
                }
                if (url == null || touxiang == null) {
                    uploadFile();
                    return;
                }

                int xingbie = -1;
                if (((RadioGroup) findViewById(R.id.xingbie)).getCheckedRadioButtonId() == R.id.nan) {
                    xingbie = 0;
                } else {
                    xingbie = 1;
                }

                signUp(username, password, name, Integer.parseInt(nianling), xingbie, url);

                break;

            case R.id.touxiang:
                try {
                    fm.choose();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;

        }
    }

    @SuppressLint("UseValueOf")
    private void signUp(String username, String password,
                        String name, int nianling, int xingbie, String touxiang) {
        showProgress();
        final User myUser = new User();
        myUser.username = username;
        myUser.password = password;
        myUser.name = name;
        myUser.nianling = nianling;
        myUser.xingbie = xingbie;
        myUser.touxiang = touxiang;
        myUser.type = 1;
        myUser.save(new SaveListener<String>() {
            @Override
            public void done(String s, BmobException e) {
                hideProgress();
                if (e == null) {
                    myUser.setObjectId(s);
                    Config.getInstance().user = myUser;
                    toast("注册成功");
                    setResult(RESULT_OK);
                    finish();
                } else {
                    toast("注册失败");
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ChooserType.REQUEST_PICK_FILE && resultCode == RESULT_OK) {
            if (fm == null) {
                fm = new FileChooserManager(this);
                fm.setFileChooserListener(this);
            }
            Log.i(TAG, "Probable file size: " + fm.queryProbableFileSize(data.getData(), this));
            fm.submit(requestCode, data);
        }
    }

    @Override
    public void onFileChosen(ChosenFile chosenFile) {
        touxiang = chosenFile.getFilePath();
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Glide.with(RegistActivity.this).load(touxiang).into((ImageView) findViewById(R.id.touxiang));
            }
        });
    }


    private void uploadFile() {
        if (touxiang == null) {
            toast("请选择头像");
            return;
        }
        showProgress();
        String[] filePaths = {touxiang};
        final List<BmobObject> picss = new ArrayList<>();
        //批量上传是会依次上传文件夹里面的文件
        BmobFile.uploadBatch(filePaths, new UploadBatchListener() {

            @Override
            public void onSuccess(List<BmobFile> files, List<String> urls) {
                url = files.get(0).getUrl();
                onClick(findViewById(R.id.submit));
            }

            @Override
            public void onError(int statuscode, String errormsg) {
                showToast("错误码：" + statuscode + ",错误描述：" + errormsg);
                hideProgress();
            }

            @Override
            public void onProgress(int curIndex, int curPercent, int total, int totalPercent) {
            }
        });
    }

    @Override
    public void onError(String s) {

    }
}
