package com.bian.adapter;

import android.content.Context;
import android.view.LayoutInflater;

import java.util.List;

/**
 * author 边凌
 * date 2018/6/20 10:56
 * 类描述：
 */
interface IAdapter<T> {
    /**
     * 重置数据，会在触发预处理后，再进行Adapter刷新
     */
    void resetData(List<T> data);

    /**
     * 添加数据（集合），会在触发预处理后，再进行Adapter刷新
     */
    void addData(List<T> data);

    /**
     * 添加数据（单个对象），会在触发预处理后，再进行Adapter刷新
     */
    void addData(T data);

    /**
     * 移除数据，会在触发预处理后，再进行Adapter刷新
     */
    void removeData(T data);

    /**
     * 移除数据（指定位置），会在触发预处理后，再进行Adapter刷新
     *
     * @param position 对应Adapter的item真实position,即经过预处理后的真实position，非原始数据集中的position
     */
    void removeData(int position);

    LayoutInflater getInflater();

    Context getContext();

    /**
     * 数据预处理
     *
     * @param data 原始数据,也即{@link #getData()}返回的数据
     * @return 实际被显示的数据，也即{@link #getPretreatmentData()}返回的数据
     */
    List<T> dataAssignment(List<T> data);

    /**
     * 获得原始数据
     */
    List<T> getData();

    /**
     * 获取经过预处理的数据
     */
    List<T> getPretreatmentData();

}
