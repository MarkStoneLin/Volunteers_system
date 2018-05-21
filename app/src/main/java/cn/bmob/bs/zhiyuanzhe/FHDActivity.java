package cn.bmob.bs.zhiyuanzhe;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import cn.bmob.bs.zhiyuanzhe.bean.Huodong;
import cn.bmob.bs.zhiyuanzhe.common.BaseActivity;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;

/**
* author: Mark栋
* date：2018/5/16 21:41
 */
public class FHDActivity extends BaseActivity {

    EditText title;
    EditText neirong;
    EditText didian;
    TextView shijian;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_fhd);
        setTitle("发布活动");

        title = v(R.id.title);
        neirong = v(R.id.neirong);
        didian = v(R.id.didian);
        shijian = v(R.id.shijian);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.submit:
                if (!checkET(title, "标题")
                        || !checkET(neirong, "内容")
                        || !checkET(didian, "地点")) {
                    return;
                }

                String sj = shijian.getText().toString();
                if (TextUtils.equals(sj, "点击选择活动时间")) {
                    toast("请选择活动时间");
                    return;
                }

                showProgress();
                Huodong huodong = new Huodong();
                huodong.name = title.getText().toString();
                huodong.content = neirong.getText().toString();
                huodong.didian = didian.getText().toString();
                huodong.shijian = shijian.getText().toString();
                huodong.save(new SaveListener<String>() {
                    @Override
                    public void done(String s, BmobException e) {
                        if (e == null) {
                            toast("发布成功");
                            finish();
                        } else {
                            toast("发布失败");
                        }
                    }
                });
                break;
            case R.id.shijian:
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(System.currentTimeMillis());
                new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        shijian.setText(year + "-" + (month + 1) + "-" + dayOfMonth);
                    }
                }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
                break;
        }
    }
}
