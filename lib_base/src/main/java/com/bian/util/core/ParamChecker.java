package com.bian.util.core;

import java.util.List;

/**
 * author 边凌
 * date 2017/7/27 17:31
 * 类描述：参数检查工具类
 */

public class ParamChecker {
    private ParamChecker(){
      throw new UnsupportedOperationException();
    }
    public static boolean hasNullObj(Object...objects){
        if (objects != null) {
            for (Object object : objects) {
                if (object==null)return true;
            }
            return false;
        }
        return true;
    }

    public static <T> boolean isEmpty(List<T> list) {
        return list == null || list.size() == 0;
    }
}
