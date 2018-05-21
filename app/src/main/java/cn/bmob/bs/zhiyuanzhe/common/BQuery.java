package cn.bmob.bs.zhiyuanzhe.common;

import java.util.ArrayList;
import java.util.Objects;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;

/**
 * Created by dongjin on 18-4-24.
 */

public class BQuery<T> extends BmobQuery<T> {

    public void and(FindListener<T> findListener, Object... params) {
        BmobQuery<T> query = buildQuery(true, params);
        query.findObjects(findListener);
    }

    public void orderAnd(FindListener<T> findListener, String order, Object... params) {
        BmobQuery<T> query = buildQuery(true, params);
        query.order(order);
        query.findObjects(findListener);
    }

    public void or(FindListener<T> findListener, Object... params) {
        BmobQuery<T> query = buildQuery(false, params);
        query.findObjects(findListener);
    }

    public void orderOr(FindListener<T> findListener, String order, Object... params) {
        BmobQuery<T> query = buildQuery(false, params);
        query.order(order);
        query.findObjects(findListener);
    }

    private BmobQuery<T> buildQuery(boolean and, Object... params) {
        BmobQuery<T> query = null;
        ArrayList<BmobQuery<T>> queries = new ArrayList<>();

        if (params != null) {
            for (int i = 0, length = params.length / 2; i < length; i++) {
                query = new BmobQuery<>();
                query.addWhereEqualTo(String.valueOf(params[i * 2]), params[i * 2 + 1]);
                queries.add(query);
            }
        } else if (params.length % 2 != 0) {
            throw new IllegalArgumentException("params not right, it's length % 2 must be 0");
        }

        query = new BmobQuery<>();
        if (!queries.isEmpty()) {
            if (and) {
                query.and(queries);
            } else {
                query.or(queries);
            }
        }
        return query;
    }
}
