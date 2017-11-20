package com.bian.mydevsample.ui.fragment.adaptertest;

import android.app.Activity;
import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.bian.base.baseclass.baseadapter.AbsBaseAdapter;
import com.bian.base.baseclass.baseadapter.BasePTRAdapter;
import com.bian.base.baseclass.baseadapter.DataLoader;
import com.bian.base.baseclass.baseadapter.LoadType;
import com.bian.base.baseclass.baseadapter.RetrofitDataLoader;
import com.bian.mydevsample.R;
import com.bian.mydevsample.bean.BookBean;
import com.bian.mydevsample.bean.BookRequest;
import com.bian.mydevsample.databinding.ItemBookBinding;
import com.bian.mydevsample.net.BookService;
import com.bian.net.Api;

import java.util.List;

import retrofit2.Call;

/**
 * author 边凌
 * date 2017/9/13 20:34
 * 类描述：
 */

class BookListAdapter extends BasePTRAdapter<BookBean, BookListAdapter.Holder> {

    BookListAdapter(Activity mActivity, boolean loadData) {
        super(mActivity, loadData);
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
                        start = getCount();
                        count = 10;
                        break;
                    case Refresh:
                    case Reload:
                    case FirstLoad:
                        start = 0;
                        count = getCount() == 0 ? 10 : getCount();
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

    @NonNull
    @Override
    protected Holder onCreateHolder(LayoutInflater inflater, ViewGroup parent, int viewType) {
        ItemBookBinding bookBinding = DataBindingUtil.inflate(inflater, R.layout.item_book, parent,
                                                              false);
        return new Holder(bookBinding);
    }

    @Override
    protected void bindView(int position, int viewType, @NonNull Holder holder,
                            @NonNull BookBean bookBean, boolean isLast) {
        holder.root.setBook(bookBean);
    }


    class Holder extends AbsBaseAdapter.ViewHolder {

        private ItemBookBinding root;

        Holder(ItemBookBinding root) {
            super(root.getRoot());
            this.root = root;
        }
    }
}
