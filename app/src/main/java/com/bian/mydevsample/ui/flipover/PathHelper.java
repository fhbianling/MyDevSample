package com.bian.mydevsample.ui.flipover;

import android.graphics.Path;
import android.graphics.PathMeasure;

/**
 * author 边凌
 * date 2018/1/17 17:50
 * 类描述：
 */

public class PathHelper {
    private static PathHelper sInstance;
    private PathMeasure pathMeasure = new PathMeasure();

    private PathHelper() {

    }

    static PathHelper getInstance() {
        if (sInstance == null) {
            synchronized (PathHelper.class) {
                if (sInstance == null) {
                    sInstance = new PathHelper();
                }
            }
        }
        return sInstance;
    }

    void startHalfCut(Path path, Path dst, boolean startWithMoveTo) {
        pathMeasure.setPath(path, false);
        float orgLength = pathMeasure.getLength();
        pathMeasure.getSegment(0, orgLength / 2, dst, startWithMoveTo);
    }

    void endHalfCut(Path path, Path dst, boolean startWithMoveTo) {
        pathMeasure.setPath(path, false);
        float orgLength = pathMeasure.getLength();
        pathMeasure.getSegment(orgLength / 2, orgLength, dst, startWithMoveTo);
    }
}
