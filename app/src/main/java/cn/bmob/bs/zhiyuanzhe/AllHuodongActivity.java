package cn.bmob.bs.zhiyuanzhe;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.bs.zhiyuanzhe.bean.Huodong;
import cn.bmob.bs.zhiyuanzhe.common.BQuery;
import cn.bmob.bs.zhiyuanzhe.common.BaseActivity;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

/**
* author: Mark栋
* date：2018/5/16 21:40
 */
public class AllHuodongActivity extends BaseActivity {
    ArrayList<Huodong> huodongs = new ArrayList<>();

    ListView listView;
    MAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView1(R.layout.activity_list);
        setTitle("请选择活动");

        listView = v(R.id.list);
        mAdapter = new MAdapter();
        listView.setAdapter(mAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Huodong huodong = mAdapter.getItem(position);
                Intent intent = new Intent();
                intent.putExtra("huodong", huodong.getObjectId());
                intent.putExtra("name", huodong.name);
                setResult(RESULT_OK, intent);
                finish();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();


        new BQuery<Huodong>().and(new FindListener<Huodong>() {
            @Override
            public void done(List<Huodong> list, BmobException e) {
                if (e == null && !list.isEmpty()) {
                    huodongs.addAll(list);
                    mAdapter.notifyDataSetChanged();
                } else {
//                    toast("还未参加活动");
                }
            }
        });
    }

    class MAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return huodongs.size();
        }

        @Override
        public Huodong getItem(int position) {
            return huodongs.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            if (convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.tongzhi_item, null);

                holder = new ViewHolder();
                holder.title = (TextView) convertView.findViewById(R.id.title);
                holder.content = (TextView) convertView.findViewById(R.id.cont);
                holder.riqi = (TextView) convertView.findViewById(R.id.riqi);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            Huodong huodong = getItem(position);

            holder.riqi.setText(huodong.getCreatedAt());
            holder.title.setText(huodong.name);
            holder.content.setText(huodong.content);

            return convertView;
        }
    }

    static class ViewHolder {
        TextView title;
        TextView content;
        TextView riqi;
    }
}
