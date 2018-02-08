package com.xiangzi.screenelf;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.util.FloatMath;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by Administrator on 2018/2/2.
 */

public class SampleView extends View {
    //定义常量,指定该图片横向被划分为20格
    private static final int WIDTH = 20;
    //定义常量,指定该图片纵向上被划分为20格
    private static final int HEIGHT = 20;
    //记录该图像上包含441个顶点
    private static final int COUNT = (WIDTH + 1) * (HEIGHT + 1);
    //位图
    private Bitmap mBitmap;
    //数组,记录Bitmap上的21*21个点的坐标
    private final float[] mVerts = new float[COUNT * 2];
    //记录Bitmap上的21*21个点经过扭曲后的坐标
    private final float[] mOrig = new float[COUNT * 2];

    private final Matrix mMatrix = new Matrix();
    private final Matrix mInverse = new Matrix();

    private static void setXY(float[] array, int index, float x, float y) {
        array[index * 2 + 0] = x;
        array[index * 2 + 1] = y;
    }

    public SampleView(Context context) {
        super(context);
        setFocusable(true);
        //加载图片
        mBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.beach);
        //获取图像的宽度和高度
        float w = mBitmap.getWidth();
        float h = mBitmap.getHeight();
        //构建扭曲数据
        int index = 0;
        for (int y = 0; y <= HEIGHT; y++) {
            float fy = h * y / HEIGHT;
            for (int x = 0; x <= WIDTH; x++) {
                float fx = w * x / WIDTH;
                //初始化orig,verts数组
                //初始化,orig,verts两个数组均匀地保存了21 * 21个点的x,y坐标　
                setXY(mVerts, index, fx, fy);
                setXY(mOrig, index, fx, fy);
                index += 1;
            }
        }
        //设置平移效果
        mMatrix.setTranslate(10, 10);
        //实现乱矩阵逆向坐标映射
        mMatrix.invert(mInverse);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawColor(0xFFCCCCCC);
        //对matrix的变换应用到canvas上的所有对象.
        canvas.concat(mMatrix);
        /**
         * bitmap　　　　    　需要扭曲的源位图
         * meshWidth 　　控制在横向上把该源位图划成成多少格
         * meshHeight　  　 控制在纵向上把该源位图划成成多少格
         * verts 　　　　　    长度为(meshWidth + 1) * (meshHeight + 1) * 2的数组，它记录了扭曲后的位图各顶点位置
         * vertOffset    控制verts数组中从第几个数组元素开始才对bitmap进行扭曲
         */
        canvas.drawBitmapMesh(mBitmap, WIDTH, HEIGHT, mVerts, 0, null, 0,
                null);
    }
    //根据触摸事件的位置计算verts数组里各元素的值
    private void warp(float cx, float cy) {
        final float K = 10000;
        float[] src = mOrig;
        float[] dst = mVerts;
        for (int i = 0; i < COUNT * 2; i += 2) {
            float x = src[i + 0];
            float y = src[i + 1];
            float dx = cx - x;
            float dy = cy - y;
            float dd = dx * dx + dy * dy;
            //计算每个坐标点与当前点(cx,cy)之间的距离
            float d = (float) Math.sqrt(dd);
            //扭曲度，距离当前点(cx,cy)越远，扭曲度越小
            float pull = K / (dd + 0.000001f);
            pull /= (d + 0.000001f);
            //对dst数组(保存bitmap　上21 * 21个点经过扭曲后的坐标)赋值
            if (pull >= 1) {
                dst[i + 0] = cx;
                dst[i + 1] = cy;
            } else {
                //控制各顶点向触摸事件发生点偏移
                dst[i + 0] = x + dx * pull;
                dst[i + 1] = y + dy * pull;
            }
        }
    }

    private int mLastWarpX = -9999; // don't match a touch coordinate
    private int mLastWarpY;

    @SuppressLint("ClickableViewAccessibility") @Override
    public boolean onTouchEvent(MotionEvent event) {
        float[] pt = { event.getX(), event.getY() };
        //用当前矩阵改变pts中的值，然后存储在pts中，同上，pts也是存储点的坐标的数组
        mInverse.mapPoints(pt);

        int x = (int) pt[0];
        int y = (int) pt[1];
        if (mLastWarpX != x || mLastWarpY != y) {
            mLastWarpX = x;
            mLastWarpY = y;
            warp(pt[0], pt[1]);
            invalidate();
        }
        return true;
    }
}
