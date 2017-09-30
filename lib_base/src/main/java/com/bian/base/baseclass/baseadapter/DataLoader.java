package com.bian.base.baseclass.baseadapter;

import java.util.List;

/**
 * author 边凌
 * date 2017/9/20 9:54
 * 类描述：数据加载器，在方法{@link #loadData(int, int, DataSetter, LoadType)}中加载数据，
 * 加载成功后,方法参数{@link DataSetter}是一个Adapter的内部实现类对象的引用，不应对这个setter引用作任何重新赋值的操作
 * <p>
 * 调用其方法{@link DataSetter#setLoadedData(List)}或{@link DataSetter#setFailed(int, String)}设置数据
 */
public interface DataLoader<DataType> {
    void loadData(int pageNum, int pageSize, final DataSetter<DataType> setter,
                  LoadType loadType);
}
