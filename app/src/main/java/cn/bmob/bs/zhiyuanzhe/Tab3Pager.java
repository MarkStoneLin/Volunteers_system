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

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.bs.zhiyuanzhe.bean.Dongtai;
import cn.bmob.bs.zhiyuanzhe.common.BQuery;
import cn.bmob.bs.zhiyuanzhe.common.BaseFragment;
import cn.bmob.bs.zhiyuanzhe.common.CircleImageView;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

/**
* author: Mark栋
* date：2018/5/16 18:21
 * 对应页面：dongtai_item.xml
 */
public class Tab3Pager extends BaseFragment {
    ArrayList<Dongtai> dongtais = new ArrayList<>();

    Dongtai dongtai;
    ListView listView;
    MAdapter mAdapter;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.activity_list, container, false);

        listView = v(v, R.id.list);

        mAdapter = new MAdapter();
        listView.setAdapter(mAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                DongtaiActivity.dongtai(getActivity(), mAdapter.getItem(position));
            }
        });

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        new BQuery<Dongtai>().orderAnd(new FindListener<Dongtai>() {
            @Override
            public void done(List<Dongtai> list, BmobException e) {
                if (e == null && !list.isEmpty()) {
                    dongtais.clear();
                    dongtais.addAll(list);
                    mAdapter.notifyDataSetChanged();
                } else {
//                    toast("还未参加活动");
                }
            }
        }, "createAt");
    }

    /**
     * purpose:BaseAdapter是一个基础数据适配器，给xml配置数据的
     * Note:
     */
    class MAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return dongtais.size();
        }

        @Override
        public Dongtai getItem(int position) {
            return dongtais.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        /**
        * purpose:在此处贴头像、用户名和动态信息
        * Note:
        */
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            if (convertView == null) {
                convertView = getActivity().getLayoutInflater().inflate(R.layout.dongtai_item, null);

                holder = new ViewHolder();
                holder.touxiang = (CircleImageView) convertView.findViewById(R.id.touxiang);
                holder.title = (TextView) convertView.findViewById(R.id.title);
                holder.riqi = (TextView) convertView.findViewById(R.id.riqi);
                holder.name = (TextView) convertView.findViewById(R.id.name);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            Dongtai huodong = getItem(position);

            //上动态
            holder.riqi.setText(huodong.getCreatedAt());
            holder.title.setText(huodong.content);
            holder.name.setText(huodong.username);
            Glide.with(Tab3Pager.this).load(huodong.touxiang).into(holder.touxiang);



            return convertView;
        }
    }

    static class ViewHolder {
        CircleImageView touxiang;
        TextView title;
        TextView riqi;
        TextView name;
    }
}
