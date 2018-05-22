package cn.bmob.bs.zhiyuanzhe;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.List;

/**
* author: Mark栋
* date：2018/5/16 21:40
 */
public class Adapter extends FragmentPagerAdapter{
    private List<Fragment> list;

    public Adapter(FragmentManager fm, List<Fragment> list) {
        super(fm);
        this.list = list;
    }

    @Override
    public Fragment getItem(int position) {
        int i = 0;
        i++;
        return list.get(position);
    }


    @Override
    public int getCount() {
        return list.size();
    }
}
