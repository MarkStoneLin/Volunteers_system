package cn.bmob.bs.zhiyuanzhe;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.bumptech.glide.Glide;
import com.smile.filechoose.api.ChooserType;
import com.smile.filechoose.api.ChosenFile;
import com.smile.filechoose.api.FileChooserListener;
import com.smile.filechoose.api.FileChooserManager;

import java.util.List;

import cn.bmob.bs.zhiyuanzhe.bean.User;
import cn.bmob.bs.zhiyuanzhe.common.BaseActivity;
import cn.bmob.bs.zhiyuanzhe.common.CircleImageView;
import cn.bmob.bs.zhiyuanzhe.common.Config;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.UpdateListener;
import cn.bmob.v3.listener.UploadBatchListener;

/**
* author: Mark栋
* date：2018/5/16 21:40
 */
public class ChangeInfoActivity extends BaseActivity implements FileChooserListener {
    String touxiang;
    String url;
    private FileChooserManager fm;

    EditText name;
    ImageView touxiangV;
    EditText nianlin;
    RadioGroup xingbie;
    RadioButton nan;
    RadioButton nv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.change_info_activity);

        fm = new FileChooserManager(this);
        fm.setFileChooserListener(this);

        setTitle("修改信息");

        name = v(R.id.name);
        touxiangV = v(R.id.touxiang);
        nianlin = v(R.id.nianlin);
        xingbie = v(R.id.xingbie);
        nan = v(R.id.nan);
        nv = v(R.id.nv);

        User user = Config.getInstance().user;

        name.setText(user.name);
        url = user.touxiang;
        Glide.with(this).load(user.touxiang).into(touxiangV);
        nianlin.setText(String.valueOf(user.nianling));
        nan.setChecked(user.xingbie == 0);
        nv.setChecked(user.xingbie == 1);
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.submit:
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
                if (touxiang != null) {
                    uploadFile();
                    return;
                }

                int xb = -1;
                if (xingbie.getCheckedRadioButtonId() == R.id.nan) {
                    xb = 0;
                } else {
                    xb = 1;
                }

                signUp(name, Integer.parseInt(nianling), xb, url);

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
    private void signUp(String name, int nianling, int xingbie, String touxiang) {
        showProgress();
        final User myUser = Config.getInstance().user;
        myUser.name = name;
        myUser.nianling = nianling;
        myUser.xingbie = xingbie;
        myUser.touxiang = touxiang;
        myUser.update(new UpdateListener(){
            @Override
            public void done(BmobException e) {
                hideProgress();
                if (e == null) {
                    toast("修改成功");
                    setResult(RESULT_OK);
                    finish();
                } else {
                    toast("修改失败");
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
                Glide.with(ChangeInfoActivity.this).load(touxiang).into((CircleImageView) findViewById(R.id.touxiang));
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
        //批量上传是会依次上传文件夹里面的文件
        BmobFile.uploadBatch(filePaths, new UploadBatchListener() {

            @Override
            public void onSuccess(List<BmobFile> files, List<String> urls) {
                url = files.get(0).getUrl();
                touxiang = null;
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
