package com.bian.mydevsample.ui.flipover;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import androidx.annotation.FloatRange;

import com.bian.util.core.L;

/**
 * author 边凌
 * date 2018/1/17 12:11
 * 类描述：
 */

class PathComputer {
    //已知条件---------------->
    /**
     * 页面右下顶点，在求解过程中需为已知条件
     */
    private PointF Pprb = new PointF();
    /**
     * 页面右上顶点
     */
    private PointF Pprt = new PointF();
    /**
     * 页面右边界中点
     */
    private PointF Perc = new PointF();
    /**
     * (可能经历过对称变换)的手指位置，在求解过程中需为已知条件
     */
    private PointF PfMatrix = new PointF();

    /**
     * 真实的手指位置,主要是用于计算恢复动画时会用到
     */
    private PointF PfReal = new PointF();

    //<----------------已知条件

    //待求解值--------------->
    /**
     * 待求解值，页面左下折页开始处点
     */
    private PointF Plb = new PointF();
    /**
     * 待求解值，页面左下折页路径弧形部分斜率与 手指与右下顶点的连线的中垂线的斜率 相等的点
     */
    private PointF Pal = new PointF();
    /**
     * 待求解值，从手指位置出发，路径左下直线部分的终点
     */
    private PointF Plc = new PointF();
    /**
     * 待求解值，从手指位置触发，路径右上直线部分的终点
     */
    private PointF Prc = new PointF();

    /**
     * 待求解值，页面右上折页路径弧形部分斜率与 手指与右下顶点的连线的中垂线的斜率 相等的点
     */
    private PointF Par = new PointF();

    /**
     * 待求解值，页面右上折页开始处点
     */
    private PointF Prt = new PointF();


    /**
     * 待求解值，当斜率为无穷大或负值时使用，左上点
     */
    private PointF Pilt = new PointF();
    /**
     * 待求解值，当斜率为无穷大或负值时使用，右上点
     */
    private PointF Pirt = new PointF();

    /**
     * 待求解值，当斜率为无穷大或负值时使用，左下点
     */
    private PointF Pilb = new PointF();
    /**
     * 待求解值，当斜率为无穷大或负值时使用，右下点
     */
    private PointF Pirb = new PointF();

    /**
     * 包含计算好的路径信息的封装类
     * <p>
     * 折页上路径为 弧线段(Plb,Pal,Plc)，直线段(Plc,PfMatrix),直线段(PfMatrix,Prc),弧线段(Prc,Par,Prt)连线
     * 折页下路径为 弧线段(Plb,Pal)，直线段(Pal,Par),弧线段(Par,Prt)
     */
    private PathInfo pathInfo;
    //<---------------待求解值


    //求解需要用到的中间值------------->
    /**
     * Pf与Prb中垂线与下边界的交点
     */
    private PointF Plce = new PointF();

    /**
     * Pf与Prb中垂线与右边界的交点
     */
    private PointF Prce = new PointF();

    /**
     * Pf与Prb中垂线中点
     */
    private PointF Pcfrb = new PointF();

    /**
     * Pf与Prb连线上的四等分点，靠近Pf这一侧
     */
    private PointF Pcfrb4 = new PointF();

    //<-------------求解需要用到的中间值


    //调试
    private PointF[] pointFS = new PointF[]{PfReal, Plc, Pal, Plb, Plce, Pprb, Prce, Prt, Par,
                                            Prc, Pcfrb, Pcfrb4};

    PathComputer(float width, float height) {
        Pprb.set(width, height);
        Pprt.set(width, 0);
        Perc.set(width, height / 2);
        pathInfo = new PathInfo();
    }

    /**
     * 该方法用于调试
     */
    void drawPoint(Canvas canvas, Paint mPaint) {
        mPaint.setStrokeWidth(0);
        mPaint.setTextSize(32);
        mPaint.setColor(Color.BLACK);
        for (int i = 0; i < pointFS.length; i++) {
            PointF pointF = pointFS[i];
            if (pointF != null) {
                canvas.drawText(String.valueOf(i), pointF.x, pointF.y, mPaint);
            } else {
                L.e("null point,index:" + i);
            }
        }
    }

    void computePathInfo(float startX, float startY, float fingerX, float fingerY) {
        if (pathInfo == null) {
            pathInfo = new PathInfo();
            pathInfo.mPath1 = new Path();
            pathInfo.mPath2 = new Path();
        }
        float Pfy = fingerY;
        Matrix transformMatrix = null;
        boolean isTouchFromTopPage = startY < Pprb.y / 2;
        boolean isFromLeftToRight = startX < Pprb.x / 2;
        pathInfo.downAtLeft = isFromLeftToRight;
        pathInfo.nowAtLeft = fingerX < Pprb.x / 2;
        pathInfo.downAtTop = isTouchFromTopPage;
        pathInfo.startX = startX;
        pathInfo.startY = startY;

        if (isTouchFromTopPage) {
            Pfy = Pprb.y - Pfy;
            transformMatrix = new Matrix();
            transformMatrix.setValues(new float[]{1f, 0, 0, 0, -1f, 0, 0, 0, 1f});
            transformMatrix.postTranslate(0, Pprb.y);
        }

        PfMatrix.set(fingerX, Pfy);
        PfReal.set(fingerX, fingerY);

        realCompute(isFromLeftToRight);

        if (transformMatrix != null) {
            pathInfo.transform(transformMatrix);
        }
    }

    void computeRevert(@FloatRange(from = 0f, to = 1f) float processRate) {
        if (processRate >= 1f) {
            pathInfo.mPath1.reset();
            pathInfo.mPath2.reset();
            return;
        }
        PointF startPoint = PfReal;
        PointF endPoint;
        if (pathInfo.downAtLeft) {
            endPoint = Perc;
        } else if (pathInfo.downAtTop) {
            endPoint = Pprt;
        } else {
            endPoint = Pprb;
        }
        float mockFingerX = startPoint.x + (endPoint.x - startPoint.x) * processRate;
        float mockFingerY = startPoint.y + (endPoint.y - startPoint.y) * processRate;
        computePathInfo(pathInfo.startX, pathInfo.startY, mockFingerX, mockFingerY);
    }

    private void realCompute(boolean isFromLeftToRight) {
        if (isFromLeftToRight) {
            computeStraight();
        } else {
            //手指和右下定点连线斜率
            float Kfrb = (Pprb.y - PfMatrix.y) / (Pprb.x - PfMatrix.x);

            //连线斜率大于0时可以正常表现折页效果，其他情况需要换公式，这里把这种情况简化
            //注意由于手机坐标系和平常使用的平面坐标系y轴方向，所以这里不要把方向和斜率正负关系搞错
            //考虑到斜率在0.02以下时，原公式计算路径已经误差较大，此时直接采用斜率为0时的公式
            if (Kfrb > 0.02) {
                //中垂线斜率
                pathInfo.flipState = FlipState.Normal;
                computeFold(-1 / Kfrb);
            } else {
                pathInfo.flipState = FlipState.Infinity;
                computeStraight();
            }
        }

        pathInfo.computePath();
    }

    Path getBackPath() {
        return pathInfo.mBackPath;
    }

    Path getNextPagePath() {
        return pathInfo.mNextPagePath;
    }

    private void computeStraight() {
        pathInfo.computeStraight = true;
        Pilt.set(PfMatrix.x, 0);
        Pilb.set(PfMatrix.x, Pprb.y);
        Pirt.set((PfMatrix.x + Pprb.x) / 2f, 0);
        Pirb.set((PfMatrix.x + Pprb.x) / 2f, Pprb.y);
    }

    /**
     * @param Kclr 手指与右下顶点的连线的中垂线的斜率
     */
    private void computeFold(float Kclr) {
        pathInfo.computeStraight = false;
        //中垂线中点
        Pcfrb.set((Pprb.x + PfMatrix.x) / 2f, (Pprb.y + PfMatrix.y) / 2f);

        //中垂线y轴截距
        float Bclr = Pcfrb.y - Kclr * Pcfrb.x;
        //Pf与Prb连线四等分点，既是Pcfrb与Pf的中点
        Pcfrb4.set((PfMatrix.x + Pcfrb.x) / 2f, (PfMatrix.y + Pcfrb.y) / 2f);

        //四等分线y轴截距
        float Bclr4 = Pcfrb4.y - Kclr * Pcfrb4.x;
        //该点用于边界条件检测

        Plb.set((Pprb.y - Bclr4) / Kclr, Pprb.y);

        //中垂线与下边界交点
        Plce.set((Pprb.y - Bclr) / Kclr, Pprb.y);
        //中垂线与右边界交点
        Prce.set(Pprb.x, Kclr * Pprb.x + Bclr);


        //求解剩余5个点
        Prt.set(Pprb.x, Kclr * Pprb.x + Bclr4);
        //Plc实际上也是Pf与Plce的中点
        Plc.set((PfMatrix.x + Plce.x) / 2f, (PfMatrix.y + Plce.y) / 2f);
        //Prc实际上也是Pf与Prce的中点
        Prc.set((PfMatrix.x + Prce.x) / 2f, (PfMatrix.y + Prce.y) / 2f);

        //Plb,Plc的中点与Plce中点连线的中点即为Pal,Prt与Prc的中点与Prce的中点的连线的中点即为Par
        Pal.set(((Plb.x + Plc.x) / 2f + Plce.x) / 2f, ((Plb.y + Plc.y) / 2f + Plce.y) / 2f);
        Par.set(((Prt.x + Prc.x) / 2f + Prce.x) / 2f, ((Prt.y + Prc.y) / 2f + Prce.y) / 2f);
    }

    enum FlipState {
        Infinity,
        Normal
    }

    private class PathInfo {
        /**
         * 该页与背面的分割线
         */
        private Path mPath1 = new Path();
        /**
         * 背面与下一页的分割线
         */
        private Path mPath2 = new Path();

        private boolean downAtLeft;
        private boolean downAtTop;
        private boolean nowAtLeft;
        private FlipState flipState;
        private float startX;
        private float startY;
        private boolean computeStraight;

        private Path arc1 = new Path();
        private Path arc2 = new Path();
        //绘制中需要的重要参数，需要返回
        /**
         * 背景图对应的闭合path
         */
        private Path mBackPath = new Path();

        /**
         * 该path的闭合区域内应该显示下一页的内容
         */
        private Path mNextPagePath = new Path();

        /**
         * 分页线
         */
        private Path mFlipPath = new Path();

        /**
         * 缓存上一次的矩阵变换后的手指点位置
         */
        private PointF mLastPfMatrix = new PointF();

        private void transform(Matrix matrix) {
            mBackPath.transform(matrix);
            mNextPagePath.transform(matrix);
        }

        private void computePath() {
            arc1.reset();
            arc2.reset();
            mBackPath.reset();
            mNextPagePath.reset();

            if (!computeStraight) {
                computeArc();
                computeBackPath();
                computeNextPagePath();
            } else {
                computeStraight();
            }
            mLastPfMatrix.set(PfMatrix);
        }

        private void computeStraight() {
            mBackPath.moveTo(Pilt.x, Pilt.y);
            mBackPath.lineTo(Pilb.x, Pilb.y);
            mBackPath.lineTo(Pirb.x, Pirb.y);
            mBackPath.lineTo(Pirt.x, Pirt.y);
            mBackPath.lineTo(Pilt.x, Pilt.y);
            mBackPath.close();

            mNextPagePath.moveTo(Pirt.x, Pirt.y);
            mNextPagePath.lineTo(Pprt.x, Pprt.y);
            mNextPagePath.lineTo(Pprb.x, Pprb.y);
            mNextPagePath.lineTo(Pirb.x, Pirb.y);
            mNextPagePath.lineTo(Pirt.x, Pirt.y);
            mNextPagePath.close();
        }

        private void computeNextPagePath() {
            PathHelper.getInstance().startHalfCut(arc1, mNextPagePath, true);
            mNextPagePath.lineTo(Par.x, Par.y);
            PathHelper.getInstance().endHalfCut(arc2, mNextPagePath, false);
            mNextPagePath.lineTo(Pprb.x, Pprb.y);
            mNextPagePath.lineTo(Plb.x, Plb.y);
            mNextPagePath.close();
        }

        private void computeBackPath() {
            PathHelper.getInstance().endHalfCut(arc1, mBackPath, true);
            mBackPath.lineTo(PfMatrix.x, PfMatrix.y);
            mBackPath.lineTo(Prc.x, Prc.y);
            PathHelper.getInstance().startHalfCut(arc2, mBackPath, false);
            mBackPath.lineTo(Pal.x, Pal.y);
            mBackPath.close();
        }

        private void computeArc() {
            arc1.moveTo(Plb.x, Plb.y);
            arc1.quadTo(Plce.x, Plce.y, Plc.x, Plc.y);
            arc2.moveTo(Prc.x, Prc.y);
            arc2.quadTo(Prce.x, Prce.y, Prt.x, Prt.y);
        }
    }

}
