package cn.bmob.bs.zhiyuanzhe;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cn.bmob.bs.zhiyuanzhe.bean.Dongtai;
import cn.bmob.bs.zhiyuanzhe.bean.Pic;
import cn.bmob.bs.zhiyuanzhe.bean.Pinglun;
import cn.bmob.bs.zhiyuanzhe.bean.Zan;
import cn.bmob.bs.zhiyuanzhe.common.BQuery;
import cn.bmob.bs.zhiyuanzhe.common.BaseActivity;
import cn.bmob.bs.zhiyuanzhe.common.CircleImageView;
import cn.bmob.bs.zhiyuanzhe.common.Config;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import me.iwf.photopicker.PhotoPicker;

/**
* author: Mark栋
* date：2018/5/16 21:40
 */
public class DongtaiActivity extends BaseActivity {

    public static void dongtai(Context context, Dongtai dongtai) {
        context.startActivity(new Intent(context, DongtaiActivity.class).putExtra("dongtai", dongtai));
    }

    Dongtai dongtai;

    CircleImageView touxiang;
    TextView name;
    TextView neirong;
    Button reply;
    EditText pl;
    TextView shijian;
    LinearLayout pinglunLayout;
    TextView zans;
    LinearLayout pinglun;
    RecyclerView picsV;

    PicAdapter mAdapter;

    HashMap<String, Zan> zansMap = new HashMap<>();
    ArrayList<Pinglun> pingluns = new ArrayList<>();
    ArrayList<Pic>  pics = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView1(R.layout.activity_dongtai);
        setTitle("动态");

        dongtai = (Dongtai) getIntent().getSerializableExtra("dongtai");

        touxiang = v(R.id.touxiang);
        name = v(R.id.name);
        neirong = v(R.id.neirong);
        reply = v(R.id.reply);
        pl = v(R.id.pl);
        shijian = v(R.id.shijian);
        pinglunLayout = v(R.id.pinglunLayout);
        zans = v(R.id.zans);
        pinglun = v(R.id.pinglun);
        picsV = v(R.id.pics);
        picsV.setLayoutManager(new GridLayoutManager(this, 4));
        mAdapter = new PicAdapter(this);
        mAdapter.hideAdd();
        picsV.setAdapter(mAdapter);

        name.setText(dongtai.username);
        neirong.setText(dongtai.content);
        shijian.setText(dongtai.getCreatedAt());
        Glide.with(this).load(dongtai.touxiang).into(touxiang);

        loadPic();
        loadZan();
        loadPinglun();
    }

    private void loadPic() {
        new BQuery<Pic>().orderAnd(new FindListener<Pic>() {
            @Override
            public void done(List<Pic> list, BmobException e) {
                if (e == null) {
                    pics.addAll(list);
                    renderPic();
                }
            }
        }, "createAt", "key", dongtai.getObjectId());
    }

    private void renderPic() {
        ArrayList<String> ps = new ArrayList<>();
        for (Pic p : pics) {
            ps.add(p.file.getFileUrl());
        }
        mAdapter.addPic(ps);
    }

    private void loadZan() {
        new BQuery<Zan>().orderAnd(new FindListener<Zan>() {
            @Override
            public void done(List<Zan> list, BmobException e) {
                if (e == null) {
                    StringBuilder sb = new StringBuilder();
                    for (Zan z : list) {
                        sb.append(z.username).append(" ");
                        zansMap.put(z.user, z);
                    }
                    if (list.size() > 0) {
                        pinglunLayout.setVisibility(View.VISIBLE);
                    }
                    zans.setText(sb.toString());
                }
            }
        }, "createAt", "dongtai", dongtai.getObjectId());
    }

    private void loadPinglun() {
        new BQuery<Pinglun>().orderAnd(new FindListener<Pinglun>() {
            @Override
            public void done(List<Pinglun> list, BmobException e) {
                if (e == null) {
                    pingluns.addAll(list);
                    if (list.size() > 0) {
                        pinglunLayout.setVisibility(View.VISIBLE);
                    }
                    renderPinglun();
                }
            }
        }, "createAt", "dongtai", dongtai.getObjectId());
    }

    private void renderPinglun() {
        pinglun.removeAllViews();
        for (final Pinglun pl : pingluns) {
            View view = getLayoutInflater().inflate(R.layout.pinglun_item, null);
            TextView name = (TextView) view.findViewById(R.id.name);
            TextView content = (TextView) view.findViewById(R.id.content);
            name.setText(pl.username + "：" + pl.neirong);
            content.setText(pl.neirong);

            pinglun.addView(view);
        }
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);

        switch (v.getId()) {
            case R.id.zan:
                if (!zansMap.containsKey(Config.getInstance().user.getObjectId())) {
                    Zan zan = new Zan();
                    zan.dongtai = dongtai.getObjectId();
                    zan.user = Config.getInstance().user.getObjectId();
                    zan.username = Config.getInstance().user.name;
                    zan.save();
                    zansMap.put(zan.user, zan);
                    zans.setText(zans.getText() + zan.username + " ");
                }
                break;

            case R.id.reply:

                String content = pl.getText().toString();
                if (TextUtils.isEmpty(content)) {
                    return;
                }

                showProgress();
                final Pinglun pinglun = new Pinglun();
                pinglun.dongtai = dongtai.getObjectId();
                pinglun.neirong = content;
                pinglun.user = Config.getInstance().user.getObjectId();
                pinglun.username = Config.getInstance().user.name;

                pinglun.save(new SaveListener<String>() {
                    @Override
                    public void done(String s, BmobException e) {
                        hideProgress();

                        if (e != null) {
                            toast("评论失败");
                        } else {
                            pl.setText("");
                            pinglun.setObjectId(s);
                            pingluns.add(pinglun);
                            renderPinglun();
                        }
                    }
                });
                break;
        }
    }
}
