package cn.bmob.bs.zhiyuanzhe;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.bs.zhiyuanzhe.bean.Dongtai;
import cn.bmob.bs.zhiyuanzhe.bean.Pic;
import cn.bmob.bs.zhiyuanzhe.bean.User;
import cn.bmob.bs.zhiyuanzhe.common.BaseActivity;
import cn.bmob.bs.zhiyuanzhe.common.Config;
import cn.bmob.v3.BmobBatch;
import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BatchResult;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.QueryListListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UploadBatchListener;
import me.iwf.photopicker.PhotoPicker;

/**
* author: Mark栋
* date：2018/5/16 21:41
 */
public class FDTActivity extends BaseActivity {

    EditText neirong;
    RecyclerView pics;
    PicAdapter mAdapter;

    Dongtai dongtai;

    ArrayList<String> picsA = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView1(R.layout.activity_fdt);
        setTitle("发表动态");

        neirong = v(R.id.neirong);
        pics = v(R.id.pics);
        mAdapter = new PicAdapter(this);
        pics.setAdapter(mAdapter);
        pics.setLayoutManager(new GridLayoutManager(this, 4));
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);

        switch (v.getId()) {
            case R.id.submit:
                fabiao();
                break;
        }
    }

    private void fabiao() {
        if (!checkET(neirong, "内容")) {
            return;
        }

        showProgress();
        User user = Config.getInstance().user;
        if (dongtai == null) {
            dongtai = new Dongtai();
        }
        dongtai.content = neirong.getText().toString();
        dongtai.touxiang = user.touxiang;
        dongtai.user = user.getObjectId();
        dongtai.username = user.username;
        dongtai.save(new SaveListener<String>() {
            @Override
            public void done(String s, BmobException e) {
                if (e == null) {
                    dongtai.setObjectId(s);
                    if (!picsA.isEmpty()) {
                        uploadFile();
                    } else {
                        hideProgress();
                        toast("发表成功");
                        finish();
                    }
                } else {
                    hideProgress();
                    toast("发表失败");
                }
            }
        });
    }

    private void uploadFile() {
        if (picsA.isEmpty()) {
            return;
        }
        String[] filePaths = new String[picsA.size()];
        for (int i = 0; i < filePaths.length; i++) {
            filePaths[i] = picsA.get(i);
        }
        final List<BmobObject> picss = new ArrayList<>();
        //批量上传是会依次上传文件夹里面的文件
        BmobFile.uploadBatch(filePaths, new UploadBatchListener() {

            @Override
            public void onSuccess(List<BmobFile> files, List<String> urls) {
                if (files.size() > picss.size()) {
                    for (int i = picss.size(); i < files.size(); i++) {
                        Pic pic = new Pic();
                        pic.file = files.get(i);
                        pic.key = dongtai.getObjectId();
                        picss.add(pic);
                    }
                }
                if (files.size() == picsA.size()) {
                    insertBatch(picss);
                }
            }

            @Override
            public void onError(int statuscode, String errormsg) {
                showToast("错误码：" + statuscode + ",错误描述：" + errormsg);
            }

            @Override
            public void onProgress(int curIndex, int curPercent, int total, int totalPercent) {
                log("insertBatchDatasWithOne -onProgress :" + curIndex + "---" + curPercent + "---" + total + "----" + totalPercent);
            }
        });
    }

    public void insertBatch(final List<BmobObject> files){
        new BmobBatch().insertBatch(files).doBatch(new QueryListListener<BatchResult>() {
            @Override
            public void done(List<BatchResult> o, BmobException e) {
                if (e == null) {
                    List<BmobObject> faileds = new ArrayList<BmobObject>();
                    for (int i = 0; i < o.size(); i++) {
                        BatchResult result = o.get(i);
                        BmobException ex = result.getError();
                        if (ex == null) {
                            log("第" + i + "个数据批量添加成功：" + result.getCreatedAt() + "," + result.getObjectId() + "," + result.getUpdatedAt());
                        } else {
                            log("第" + i + "个数据批量添加失败：" + ex.getMessage() + "," + ex.getErrorCode());
                            faileds.add(files.get(i));
                        }
                    }
                    if (faileds.isEmpty()) {
                        hideProgress();
                        toast("发表成功");
                        finish();
                    } else {
                        insertBatch(faileds);
                    }
                } else {
                    loge(e);
                }
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == PhotoPicker.REQUEST_CODE) {
            if (data != null) {
                ArrayList<String> photos =
                        data.getStringArrayListExtra(PhotoPicker.KEY_SELECTED_PHOTOS);
                picsA.addAll(photos);
                mAdapter.addPic(photos);
            }
        }
    }
}
