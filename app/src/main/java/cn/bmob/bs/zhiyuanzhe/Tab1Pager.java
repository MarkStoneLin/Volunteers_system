package cn.bmob.bs.zhiyuanzhe;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import cn.bmob.bs.zhiyuanzhe.bean.Huodong;
import cn.bmob.bs.zhiyuanzhe.bean.MyHuodong;
import cn.bmob.bs.zhiyuanzhe.bean.Tongzhi;
import cn.bmob.bs.zhiyuanzhe.common.BQuery;
import cn.bmob.bs.zhiyuanzhe.common.BaseFragment;
import cn.bmob.bs.zhiyuanzhe.common.Config;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

/**
* author: Mark栋
* date：2018/5/16 20:13
 *  加载通知Activity
 */
public class Tab1Pager extends BaseFragment {
    ArrayList<Tongzhi> tongzhis = new ArrayList<>();

    ListView listView;
    MAdapter mAdapter;

    boolean isLoading;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.activity_list, container, false);

        listView = v(v, R.id.list);
        mAdapter = new MAdapter();
        listView.setAdapter(mAdapter);
//        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//
//            }
//        });

        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        new BQuery<MyHuodong>().and(new FindListener<MyHuodong>() {
            @Override
            public void done(List<MyHuodong> list, BmobException e) {
                if (e == null && !list.isEmpty()) {
                    HashMap<String, MyHuodong> myHuodongHashMap = Config.getInstance().myHuodongs;
                    for (MyHuodong myHuodong : list) {
                        myHuodongHashMap.put(myHuodong.huodong, myHuodong);
                    }
                    loadTongzhi();
                } else {
//                    toast("还未参加活动");
                }
            }
        }, "user", Config.getInstance().user.getObjectId());

    }

    @Override
    public void onResume() {
        super.onResume();
        if (!Config.getInstance().myHuodongs.isEmpty() && !isLoading) {
            loadTongzhi();
        }
    }

    /**
    * purpose:过滤掉未参加的活动通知
    * Note:
    */
    private void loadTongzhi() {
        isLoading = true;
        new Thread(){
            int done;
            @Override
            public void run() {
                super.run();

                BQuery<Tongzhi> bQuery = new BQuery<>();
                tongzhis.clear();

                //做一个迭代器，遍历所有的活动列表，只有获取(跟用户选择了的活动)的通知
                final HashMap<String, MyHuodong> myHuodongs = Config.getInstance().myHuodongs;
                Set<String> hds = myHuodongs.keySet();
                Iterator<String> ids = hds.iterator();
                while (ids.hasNext()) {
                    bQuery.and(new FindListener<Tongzhi>() {
                        @Override
                        public void done(List<Tongzhi> list, BmobException e) {
                            done++;
                            if (e == null) {
                                tongzhis.addAll(list);
                            }
                            if (done == myHuodongs.size()) {
                                final DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                Collections.sort(tongzhis, new Comparator<Tongzhi>() {
                                    @Override
                                    public int compare(Tongzhi o1, Tongzhi o2) {
                                        try {
                                            return (int) (df.parse(o2.getCreatedAt()).getTime() - df.parse(o1.getCreatedAt()).getTime());
                                        } catch (ParseException e1) {
                                            e1.printStackTrace();
                                        }
                                        return 0;
                                    }
                                });
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        mAdapter.notifyDataSetChanged();
                                    }
                                });
                                isLoading = false;
                            }
                        }
                    }, "huodong", ids.next());
                }
            }
        }.start();
    }

    /**
    * purpose:BaseAdapter是一个基础数据适配器，给xml配置数据的
    * Note:
    */
    class MAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return tongzhis.size();
        }

        @Override
        public Tongzhi getItem(int position) {
            return tongzhis.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            if (convertView == null) {
                convertView = getActivity().getLayoutInflater().inflate(R.layout.tongzhi_item, null);

                holder = new ViewHolder();
                holder.title = (TextView) convertView.findViewById(R.id.title);
                holder.content = (TextView) convertView.findViewById(R.id.cont);
                holder.riqi = (TextView) convertView.findViewById(R.id.riqi);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            Tongzhi tongzhi = getItem(position);


            holder.riqi.setText(tongzhi.getCreatedAt());
            holder.title.setText(tongzhi.title);
            holder.content.setText(tongzhi.content);
            return convertView;
        }
    }

    static class ViewHolder {
        TextView title;
        TextView content;
        TextView riqi;
    }
}
