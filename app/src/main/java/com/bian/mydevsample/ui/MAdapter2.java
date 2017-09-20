package com.bian.mydevsample.ui;

import android.app.Activity;
import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.bian.base.baseclass.baseadapter.AbsRecycleViewAdapter;
import com.bian.base.baseclass.baseadapter.DataLoader;
import com.bian.base.baseclass.baseadapter.LoadType;
import com.bian.base.baseclass.baseadapter.RetrofitDataLoader;
import com.bian.base.component.net.Api;
import com.bian.mydevsample.R;
import com.bian.mydevsample.bean.BookBean;
import com.bian.mydevsample.bean.BookRequest;
import com.bian.mydevsample.databinding.ItemBookBinding;
import com.bian.mydevsample.net.BookService;

import java.util.List;

import retrofit2.Call;

/**
 * author 边凌
 * date 2017/9/20 10:27
 * 类描述：
 */

class MAdapter2 extends AbsRecycleViewAdapter<BookBean, MAdapter2.Holder> {

    public MAdapter2(Activity mActivity) {
        super(mActivity);
    }

    @Override
    protected DataLoader<BookBean> getDataLoader() {
        return new RetrofitDataLoader<BookRequest, BookBean>() {

            @Override
            public Call<BookRequest> getCall(int pageIndex, int pageSize,
                                             LoadType loadType) {
                int start = 0;
                int count = 0;
                switch (loadType) {
                    case LoadMore:
                        start = getItemCount();
                        count = 10;
                        break;
                    case Refresh:
                    case Reload:
                    case FirstLoad:
                        start = 0;
                        count = getItemCount() == 0 ? 10 : getItemCount();
                        break;
                }
                return Api.getService(BookService.class).getBookList("量子力学", start, count);
            }

            @Override
            public List<BookBean> convertData(BookRequest callData) {
                return callData.books;
            }
        };
    }

    @Override
    protected Holder onCreateHolder(LayoutInflater inflater, ViewGroup parent, int viewType) {
        return new Holder((ItemBookBinding) DataBindingUtil.inflate(inflater, R.layout.item_book, parent, false));
    }

    @Override
    protected void bindView(Holder holder, int realPosition, BookBean item) {
        holder.root.setBook(item);
    }

    class Holder extends RecyclerView.ViewHolder {

        private ItemBookBinding root;

        Holder(ItemBookBinding root) {
            super(root.getRoot());
            this.root = root;
        }
    }
}
