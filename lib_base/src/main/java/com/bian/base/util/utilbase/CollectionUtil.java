package com.bian.base.util.utilbase;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * author 边凌
 * date 2017/6/19 10:23
 * 类描述：集合工具类
 */

public class CollectionUtil {
    public static <T> List<List<T>> splitByQuantity(List<T> list, int quantity) {
        List<List<T>> results = new ArrayList<>();
        if (list == null || list.size() == 0) {
            return results;
        }

        if (quantity <= 0) {
            throw new IllegalArgumentException("Wrong quantity.");
        }

        int count = 0;
        while (count < list.size()) {
            List<T> ts = list.subList(count, (count + quantity) > list.size() ? list.size() : count + quantity);
            results.add(ts);
            count += quantity;
        }
        return results;
    }

    public static <T> List<T> filterDuplicate(List<T> list) {
        HashSet<T> set = new HashSet<>(list);
        List<T> result = new ArrayList<>();
        result.addAll(set);
        return result;
    }
}
