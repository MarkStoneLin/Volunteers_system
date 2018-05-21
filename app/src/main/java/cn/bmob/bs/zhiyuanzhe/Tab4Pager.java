package cn.bmob.bs.zhiyuanzhe;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.smile.filechoose.api.ChooserType;
import com.smile.filechoose.api.ChosenFile;
import com.smile.filechoose.api.FileChooserListener;
import com.smile.filechoose.api.FileChooserManager;

import java.util.List;

import cn.bmob.bs.zhiyuanzhe.common.BaseFragment;
import cn.bmob.bs.zhiyuanzhe.common.CircleImageView;
import cn.bmob.bs.zhiyuanzhe.common.Config;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.listener.UploadBatchListener;
import jp.wasabeef.glide.transformations.BlurTransformation;

/**
* author: Mark栋
* date：2018/5/16 18:19
 * 个人信息页面，能够选择图片  对应页面：activity_wode.xml
 */
public class Tab4Pager extends BaseFragment implements FileChooserListener {
    private FileChooserManager fm;

    String tx;
    String url;
    CircleImageView touxiang;
    ImageView touxiangBG;
    TextView name;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.activity_wode, container, false);


        touxiang = v(v, R.id.touxiang);
        touxiangBG = v(v, R.id.touxiangBG);
        name = v(v, R.id.name);
        vc(v, R.id.changeInfo);
        vc(v, R.id.changePwd);
        vc(v, R.id.logout);

        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        fm = new FileChooserManager(this);
        fm.setFileChooserListener(this);

        Glide.with(getActivity()).load(Config.getInstance().user.touxiang).into(touxiang);
        Glide.with(getActivity()).load(Config.getInstance().user.touxiang).bitmapTransform(new BlurTransformation(getActivity(), 25), new CenterCrop(getActivity())).into(touxiangBG);
        name.setText(Config.getInstance().user.name);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ChooserType.REQUEST_PICK_FILE && resultCode == Activity.RESULT_OK) {
            if (fm == null) {
                fm = new FileChooserManager(this);
                fm.setFileChooserListener(this);
            }
            fm.submit(requestCode, data);
        }
    }

    @Override
    public void onFileChosen(final ChosenFile chosenFile) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                showProgress();
                String[] filePaths = {chosenFile.getFilePath()};
                //批量上传是会依次上传文件夹里面的文件
                BmobFile.uploadBatch(filePaths, new UploadBatchListener() {

                    @Override
                    public void onSuccess(List<BmobFile> files, List<String> urls) {
                        hideProgress();
                        url = files.get(0).getUrl();
                        Config.getInstance().user.touxiang = url;
                        Glide.with(getActivity()).load(Config.getInstance().user.touxiang).into(touxiang);
                        Config.getInstance().user.update();
                    }

                    @Override
                    public void onError(int statuscode, String errormsg) {
                        toast("错误码：" + statuscode + ",错误描述：" + errormsg);
                        hideProgress();
                    }

                    @Override
                    public void onProgress(int curIndex, int curPercent, int total, int totalPercent) {
                    }
                });
            }
        });
    }

    @Override
    public void onError(String s) {

    }

    /**
    * purpose:各个按钮的响应事件
    * Note:
    */
    @Override
    public void onClick(View v) {
        super.onClick(v);

        switch (v.getId()) {
            case R.id.changeInfo:
                activity(ChangeInfoActivity.class);
                break;
            case R.id.changePwd:
                activity(ChangePWDActivity.class);
                break;
            case R.id.logout:
                activity(LoginActivity.class);
                Config.getInstance().user = null;
                getActivity().finish();
                break;
        }
    }
}
