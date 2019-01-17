package registration.zhiyihealth.com.lib_ime.hcicloud;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Lihao on 2019-1-7.
 * Email heaolihao@163.com
 */
public class MySurfaceView extends SurfaceView implements SurfaceHolder.Callback, Runnable {
    //建立手写输入对象
    private short sShortData[];
    private ArrayList<Short> list = new ArrayList<Short>();

    //是否显示结果
    private boolean resultDisplay = false;

    private Thread mThread;
    private SurfaceHolder mSurfaceHolder = null;
    private Canvas mCanvas = null;
    private Paint mPaint = null;
    private Path mPath = null;
    //文字画笔
    private Paint mTextPaint = null;
    //画布更新帧数
    public static final int FRAME = 60;
    //控制是否更新
    private boolean mIsRunning = false;
    //触摸点当前座标
    private float posX, posY;
    //触发定时识别任务
    private Timer tExit;
    private TimerTask task;

    private Context mContext;

    public MySurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;

        //设置拥有焦点
        this.setFocusable(true);
        //设置触摸时拥有焦点
        this.setFocusableInTouchMode(true);
        //获取holder
        mSurfaceHolder = this.getHolder();
        //添加holder到callback函数之中
        mSurfaceHolder.addCallback(this);
        //创建画布
        mCanvas = new Canvas();
        //创建画笔
        mPaint = new Paint();
        //颜色
        mPaint.setColor(Color.BLUE);
        //抗锯齿
        mPaint.setAntiAlias(true);
        //Paint.Style.STROKE 、Paint.Style.FILL、Paint.Style.FILL_AND_STROKE
        //意思分别为 空心 、实心、实心与空心
        mPaint.setStyle(Paint.Style.STROKE);
        //设置画笔为圆滑状
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        //设置线的宽度
        mPaint.setStrokeWidth(5);
        //创建路径轨迹
        mPath = new Path();
        //创建文字画笔
        mTextPaint = new Paint();
        mTextPaint.setColor(Color.BLACK);
        mTextPaint.setTextSize(15);
    }


    public MySurfaceView(Context context) {
        super(context);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {

        //获取触摸动作以及座标
        int action = event.getAction();
        float x = event.getX();
        float y = event.getY();

        //按触摸动作分发执行内容
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                if (tExit != null) {
                    tExit.cancel();
                    tExit = null;
                    task = null;
                }
                resultDisplay = false;
                //设定轨迹的起始点
                mPath.moveTo(x, y);
                break;

            case MotionEvent.ACTION_MOVE:
                //随触摸移动设置轨迹
                mPath.quadTo(posX, posY, x, y);
                list.add((short) posX);
                list.add((short) posY);
                break;

            case MotionEvent.ACTION_UP:
                list.add((short) -1);
                list.add((short) 0);
                tExit = new Timer();
                task = new TimerTask() {

                    @Override
                    public void run() {
                        resultDisplay = true;

                        list.add((short) -1);
                        list.add((short) -1);
                        Short ss[] = new Short[list.size()];
                        list.toArray(ss);
                        sShortData = new short[list.size()];
                        for (int i = 0; i < ss.length; i++) {
                            sShortData[i] = ss[i];
                        }

                        HciCloudFuncHelper.Funcs(mContext, sShortData);
                    }
                };
                tExit.schedule(task, 1000);
                break;
            default:
                break;
        }

        //记录当前座标
        posX = x;
        posY = y;

        return true;
    }

    private void Draw() {
        //防止canvas为null导致出现null pointer问题
        if (mCanvas != null) {
            //清空画布
            mCanvas.drawColor(Color.WHITE);
            //画出轨迹
            mCanvas.drawPath(mPath, mPaint);
            //数据记录
            /*mCanvas.drawText("触点X的座标 : " + posX, 5, 40, mTextPaint);
            mCanvas.drawText("触点Y的座标 : " + posY, 5, 60, mTextPaint);*/
        }

        // 清除轨迹
        if (resultDisplay) {
            if (tExit != null) {
                tExit.cancel();
                tExit = null;
                task = null;
            }
            list.clear();
            mPath.reset();//触摸结束即清除轨迹
            resultDisplay = false;
        }
    }


    @Override
    public void run() {

        while (mIsRunning) {
            //更新前的时间
            long startTime = System.currentTimeMillis();

            //线程安全锁
            synchronized (mSurfaceHolder) {
                mCanvas = mSurfaceHolder.lockCanvas();
                Draw();
                mSurfaceHolder.unlockCanvasAndPost(mCanvas);
            }
            //获取更新后的时间
            long endTime = System.currentTimeMillis();
            //获取更新时间差
            int diffTime = (int) (endTime - startTime);
            //确保每次更新都为FRAME
            while (diffTime <= FRAME) {
                diffTime = (int) (System.currentTimeMillis() - startTime);
                //Thread.yield(): 与Thread.sleep(long millis):的区别，
                //Thread.yield(): 是暂停当前正在执行的线程对象 ，并去执行其他线程。
                //Thread.sleep(long millis):则是使当前线程暂停参数中所指定的毫秒数然后在继续执行线程
                Thread.yield();
            }
        }

    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        mIsRunning = true;
        mThread = new Thread(this);
        mThread.start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        mThread = null;
    }
}
