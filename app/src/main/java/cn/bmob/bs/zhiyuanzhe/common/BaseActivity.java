package cn.bmob.bs.zhiyuanzhe.common;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ScrollView;
import android.widget.Toast;

import cn.bmob.bs.zhiyuanzhe.R;
import cn.bmob.v3.exception.BmobException;
import rx.Subscription;
import rx.subscriptions.CompositeSubscription;

public class BaseActivity extends AppCompatActivity implements View.OnClickListener{
	
	public static String TAG = "bmob";
    protected View mProgressBar;

	private CompositeSubscription mCompositeSubscription;

	/**
	 * 解决Subscription内存泄露问题
	 * @param s
	 */
	protected void addSubscription(Subscription s) {
		if (this.mCompositeSubscription == null) {
			this.mCompositeSubscription = new CompositeSubscription();
		}
		this.mCompositeSubscription.add(s);
	}

    protected void activity(Class clazz, int... flags) {
        Intent intent = new Intent(this, clazz);
        for (int f : flags) {
            intent.addFlags(f);
        }
        startActivity(intent);
    }

    protected void activityForResult(Class clazz, int requestCode, int... flags) {
        Intent intent = new Intent(this, clazz);
        for (int f : flags) {
            intent.addFlags(f);
        }
        startActivityForResult(intent, requestCode);
    }

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	
	public void toast(String msg){
		Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
	}

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(R.layout.activity_base);

        ScrollView content = (ScrollView) findViewById(R.id.content);

        getLayoutInflater().inflate(layoutResID, content);

        mProgressBar = findViewById(R.id.progressbar);
    }

    public void setContentView1(int layoutResID) {
        super.setContentView(R.layout.activity_base1);

        FrameLayout content = (FrameLayout) findViewById(R.id.content);

        getLayoutInflater().inflate(layoutResID, content);

        mProgressBar = findViewById(R.id.progressbar);
    }

    protected <T extends View> T v(int resId) {
		return (T) findViewById(resId);
	}

    protected <T extends View> T vc(View v, int resId) {
        T t =  (T) v.findViewById(resId);
        t.setOnClickListener(this);
        return t;
    }

    protected void showProgress() {
        if (mProgressBar != null) {
            mProgressBar.setVisibility(View.VISIBLE);
        }
    }

    protected void hideProgress() {
        if (mProgressBar != null) {
            mProgressBar.setVisibility(View.GONE);
        }

    }

	protected void hideTitle() {
		getSupportActionBar().hide();
	}

    Toast mToast;

    protected void setTitle(String title) {
        getSupportActionBar().setTitle(title);
    }

	public void showToast(String text) {
		if (!TextUtils.isEmpty(text)) {
			if (mToast == null) {
				mToast = Toast.makeText(getApplicationContext(), text,
						Toast.LENGTH_SHORT);
			} else {
				mToast.setText(text);
			}
			mToast.show();
		}
	}
	
	public void showToast(int resId) {
		if (mToast == null) {
			mToast = Toast.makeText(getApplicationContext(), resId,
					Toast.LENGTH_SHORT);
		} else {
			mToast.setText(resId);
		}
		mToast.show();
	}
	
	public static void log(String msg) {
		Log.i(TAG,"===============================================================================");
		Log.i(TAG, msg);
	}

	public static void loge(Throwable e) {
		Log.i(TAG,"===============================================================================");
		if(e instanceof BmobException){
			Log.e(TAG, "错误码："+((BmobException)e).getErrorCode()+",错误描述："+((BmobException)e).getMessage());
		}else{
			Log.e(TAG, "错误描述："+e.getMessage());
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (this.mCompositeSubscription != null) {
			this.mCompositeSubscription.unsubscribe();
		}
	}

    @Override
    public void onClick(View v) {
    }

    protected boolean checkET(EditText et, String msg) {
        if (TextUtils.isEmpty(et.getText().toString())) {
            toast("请输入" + msg);
            return false;
        }
        return true;
    }
}
