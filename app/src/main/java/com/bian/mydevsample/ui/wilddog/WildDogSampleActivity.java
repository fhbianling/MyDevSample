package com.bian.mydevsample.ui.wilddog;

import android.app.Activity;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioGroup;

import com.bian.base.baseclass.baseadapter.AbsAdapter;
import com.bian.base.util.utilbase.ToastUtil;
import com.bian.mydevsample.R;
import com.bian.mydevsample.base.BaseActivity;
import com.bian.mydevsample.databinding.ItemWilddotBinding;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.wilddog.client.DataSnapshot;
import com.wilddog.client.SyncError;
import com.wilddog.client.SyncReference;
import com.wilddog.client.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * author 边凌
 * date 2017/12/13 14:22
 * 类描述：野狗 SDK的sample，野狗和firebase的功能类似，
 * 但是firebase在国内的使用因为墙的原因受限
 */

public class WildDogSampleActivity extends BaseActivity
        implements ValueEventListener, SyncReference.CompletionListener {

    private RadioGroup rg;
    private EditText et;
    private Adapter adapter;
    private SyncReference usersSyncRef;
    private List<User> currentUsers = new ArrayList<>();
    private Gson gson = new Gson();

    @Override
    protected int bindLayoutId() {
        return R.layout.activity_wilddog;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        rg = findViewById(R.id.sexRg);
        et = findViewById(R.id.nameEt);
        ListView mList = findViewById(R.id.userList);
        adapter = new Adapter(this);
        mList.setAdapter(adapter);
    }

    @Override
    protected void initData() {
        SyncReference ref = WildDogHelper.getInstance(this).getRef();
        usersSyncRef = ref.child("users");
        usersSyncRef.addValueEventListener(this);
    }

    public void save(View view) {
        Editable text = et.getText();
        if (TextUtils.isEmpty(text)) {
            ToastUtil.showToastShort("请输入姓名");
            return;
        }
        int sex = rg.getCheckedRadioButtonId() == R.id.boy ? User.BOY : User.GIRL;
        User user = new User();
        user.setSex(sex);
        user.setName(text.toString());
        user.setUserId(System.currentTimeMillis());

        usersSyncRef.child(currentUsers.size() + "").setValue(user, this);
    }

    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {
        currentUsers.clear();
        Iterable children = dataSnapshot.getChildren();
        if (children != null) {
            for (Object child : children) {
                if (child instanceof DataSnapshot) {
                    Object value = ((DataSnapshot) child).getValue();
                    currentUsers.add((User) gson.fromJson(value.toString(), new TypeToken<User>() {
                    }.getType()));
                }
            }
        }
        adapter.resetData(currentUsers);
    }

    @Override
    public void onCancelled(SyncError syncError) {

    }

    @Override
    public void onComplete(SyncError syncError, SyncReference syncReference) {
        et.setText("");
        ToastUtil.showToastShort("添加成功");
    }

    private class Adapter extends AbsAdapter<User, Holder> {

        Adapter(Activity mActivity) {
            super(mActivity);
        }

        @NonNull
        @Override
        protected Holder onCreateHolder(LayoutInflater inflater, ViewGroup parent,
                                        int viewType) {
            return new Holder((ItemWilddotBinding) DataBindingUtil.inflate(inflater,
                                                                           R.layout.item_wilddot,
                                                                           parent,
                                                                           false));
        }

        @Override
        protected void bindView(int position, int viewType,
                                @NonNull Holder holder, @NonNull User user,
                                boolean isLast) {
            holder.binding.setUser(user);
        }
    }

    private class Holder extends AbsAdapter.ViewHolder
            implements View.OnClickListener, SyncReference.CompletionListener {

        private ItemWilddotBinding binding;

        Holder(ItemWilddotBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            binding.delete.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int indexOf = currentUsers.indexOf(binding.getUser());
            usersSyncRef.child(indexOf + "").removeValue(this);
        }

        @Override
        public void onComplete(SyncError syncError, SyncReference syncReference) {
            ToastUtil.showToastShort("删除成功");
        }
    }
}
