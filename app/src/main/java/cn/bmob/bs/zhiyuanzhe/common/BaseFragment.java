package cn.bmob.bs.zhiyuanzhe.common;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.view.View;

/**
 * Created by dongjin on 2018/4/22.
 */
public class BaseFragment extends Fragment implements View.OnClickListener{

    protected void activity(Class clazz, int... flags) {
        Intent intent = new Intent(getActivity(), clazz);
        for (int f : flags) {
            intent.addFlags(f);
        }
        startActivity(intent);
    }

    protected void activityForResult(Class clazz, int requestCode, int... flags) {
        Intent intent = new Intent(getActivity(), clazz);
        for (int f : flags) {
            intent.addFlags(f);
        }
        startActivityForResult(intent, requestCode);
    }

    protected <T extends View> T v(View v, int resId) {
        return (T) v.findViewById(resId);
    }

    protected <T extends View> T vc(View v, int resId) {
        T t =  (T) v.findViewById(resId);
        t.setOnClickListener(this);
        return t;
    }

    protected void showProgress() {
        ((BaseActivity) getActivity()).showProgress();
    }

    protected void hideProgress() {
        ((BaseActivity) getActivity()).hideProgress();
    }

    protected void toast(String s) {
        ((BaseActivity) getActivity()).toast(s);
    }

    protected void click(View v) {
        v.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

    }
}
