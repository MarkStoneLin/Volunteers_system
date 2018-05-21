package cn.bmob.bs.zhiyuanzhe.common;

import java.util.HashMap;

import cn.bmob.bs.zhiyuanzhe.bean.Huodong;
import cn.bmob.bs.zhiyuanzhe.bean.MyHuodong;
import cn.bmob.bs.zhiyuanzhe.bean.User;

/**
 * Created by dongjin on 2018/3/24.
 */
public class Config {

    private static Config sInstance;

    private Config() {}

    public static Config getInstance() {
        if (sInstance == null) {
            sInstance = new Config();
        }

        return sInstance;
    }

    public User user;

    public HashMap<String, MyHuodong> myHuodongs = new HashMap<>();

}
