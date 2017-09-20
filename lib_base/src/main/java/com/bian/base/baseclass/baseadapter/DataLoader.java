package com.bian.base.baseclass.baseadapter;

/**
 * author 边凌
 * date 2017/9/20 9:54
 * 类描述：
 */
public interface DataLoader<DataType> {
    void loadData(int pageNum, int pageSize, final DataSetter<DataType> setter,
                  LoadType loadType);
}
