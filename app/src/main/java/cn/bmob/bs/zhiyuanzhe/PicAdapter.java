package cn.bmob.bs.zhiyuanzhe;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import me.iwf.photopicker.PhotoPicker;
import me.iwf.photopicker.PhotoPreview;

/**
* author: Mark栋
* date：2018/5/16 21:41
 */
public class PicAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Activity context;
    private ArrayList<String> mList = new ArrayList<>();
    private final LayoutInflater inflater;
    private static final int ITEM_TYPE_ONE = 0x00001;
    boolean hideAdd;
    private static final int ITEM_TYPE_TWO = 0x00002;
    /**
     *这里之所以用多行视图，因为我们默认的有一张图片的（那个带+的图片，用户点击它才会才会让你去选择图片）
     *集合url为空的时候，默认显示它，当它达到集合9时，这个图片会自动隐藏。
     */
    public PicAdapter(Activity context, ArrayList<String> mList) {
        this.context = context;
        this.mList.addAll(mList);
        inflater = LayoutInflater.from(context);
    }
    public PicAdapter(Activity context) {
        this.context = context;
        inflater = LayoutInflater.from(context);
    }

    public void hideAdd() {
        hideAdd = true;
    }

    public void addPic(ArrayList<String> pics) {
        mList.addAll(pics);
        notifyItemRangeInserted(mList.size() - pics.size(), mList.size());
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        parent.setPadding(20, 0, 20, 0);
        switch (viewType) {
            case ITEM_TYPE_ONE:
                return new MyHolder(inflater.inflate(R.layout.pic, parent, false));
            case ITEM_TYPE_TWO:
                return new MyTWOHolder(inflater.inflate(R.layout.pic_add, parent, false));
            default:
                return null;
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof MyHolder) {
            bindItemMyHolder((MyHolder) holder, position);
        } else if (holder instanceof MyTWOHolder) {
            bindItemTWOMyHolder((MyTWOHolder) holder, position);
        }
    }

    private void bindItemTWOMyHolder(final MyTWOHolder holder, int position) {
        if (listSize() >= 9 || hideAdd) {//集合长度大于等于9张时，隐藏 图片
            holder.add.setVisibility(View.GONE);
        }
    }

    private void bindItemMyHolder(MyHolder holder, int position) {
        Glide.with(context)
                .load(mList.get(position))
                .centerCrop()
                .into(holder.imageview);
    }

    @Override
    public int getItemViewType(int position) {
        if (position + 1 == getItemCount()) {
            return ITEM_TYPE_TWO;
        } else {
            return ITEM_TYPE_ONE;
        }
    }

    @Override
    public int getItemCount() {
        return mList.size() + (hideAdd ? 0 : 1);
    }

    class MyHolder extends RecyclerView.ViewHolder {
        private final ImageView imageview;
        public MyHolder(View itemView) {
            super(itemView);
            imageview = (ImageView) itemView.findViewById(R.id.pic);
        }
    }

    class MyTWOHolder extends RecyclerView.ViewHolder {
        private final RelativeLayout add;
        public MyTWOHolder(View itemView) {
            super(itemView);
            add = (RelativeLayout) itemView.findViewById(R.id.pic);
            add.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //选择图片
                    PhotoPicker.builder()
                            .setPhotoCount(9 - mList.size())
                            .setShowCamera(true)
                            .setShowGif(true)
                            .setPreviewEnabled(false)
                            .start(context, PhotoPicker.REQUEST_CODE);
                }
            });
        }
    }
    //对外暴露方法  。点击添加图片（类似于上啦加载数据）
    public void addMoreItem(ArrayList<String> loarMoreDatas) {
        mList.addAll(loarMoreDatas);
        notifyDataSetChanged();
    }
    //得到集合长度
    public int listSize() {
        int size = mList.size();
        return size;
    }
}
