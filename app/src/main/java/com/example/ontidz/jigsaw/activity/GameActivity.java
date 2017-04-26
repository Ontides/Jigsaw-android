package com.example.ontidz.jigsaw.activity;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ontidz.jigsaw.R;
import com.example.ontidz.jigsaw.common.Grade;
import com.example.ontidz.jigsaw.common.Login;

/**
 * Created by ontidz on 2017/4/12.
 */

public class GameActivity extends AppCompatActivity implements View.OnClickListener{

    private static final String LEVEL = "column_num";
    private int n = 3;

    //利用二维数组创建若干个游戏小方块
    private ImageView[][] mGameImages;

    //游戏主界面
    private GridLayout mMainGrid;

    //当前空方块实例的保存
    private ImageView mNullImage;

    //当前手势
    private GestureDetector mDetector;

    //判断游戏是否开始
    private boolean isGameStart = false;

    //是否过关
    private boolean isWin = false;

    //当前动画是否正在执行
    private boolean isAnimRun = false;

    //倒计时
    private TextView mDownTimerText;

    //时间
    private double spentTime;

    //计时器
    private CountDownTimer timer;

    //限定时间
    private int limitedTime;
    private Button mBeginGameBtn;
    private int mLevel;

    //最佳时间
    private TextView mPersonalBest;


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return mDetector.onTouchEvent(event);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        mDetector.onTouchEvent(ev);
        return super.dispatchTouchEvent(ev);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
    }


    private void init() {
        initView();
        initDetector();

        mLevel = getIntent().getIntExtra(LEVEL,1);
        n = mLevel +2;
        mGameImages = new ImageView[n][n];
        setTitle("第"+ mLevel +"关");
        mMainGrid.setColumnCount(n);
        mMainGrid.setRowCount(n);

        mPersonalBest = (TextView) findViewById(R.id.personal_best);
        mPersonalBest.setText("个人记录：" + Grade.grade.getLevel(mLevel).grade + "秒");

        //初始化游戏的若干个小方块
        //获取一张大图
        Bitmap bigMp = ((BitmapDrawable)getResources().getDrawable(R.drawable.game_pic)).getBitmap();

        //获取屏幕大小
        WindowManager wm = this.getWindowManager();
        int bmWidth = wm.getDefaultDisplay().getWidth() - (int)(getResources().getDisplayMetrics().density*(32+2*(n-1))+0.5f);

        //自适应大小
        bigMp = resizeBitmap(bigMp, bmWidth, bmWidth);

        int width = bigMp.getWidth()/n;//每个游戏小方块的宽和高

        for (int i = 0; i < mGameImages.length; i++){
            for (int j = 0; j < mGameImages[0].length; j++){
                //根据行和列切成若干个游戏小图片
                Bitmap bm = Bitmap.createBitmap(bigMp, j*width, i*width, width, width);
                mGameImages[i][j] = new ImageView(this);
                mGameImages[i][j].setImageBitmap(bm);
                //设置方块之间的间距
                mGameImages[i][j].setPadding(2,2,2,2);

                //绑定自定义的数据
                mGameImages[i][j].setTag(new Gamedata(i,j,bm));

                //监听当前方块
                mGameImages[i][j].setOnClickListener(this);
            }
        }
        //初始化游戏主界面添加若干个小方块
        for (int i = 0; i < mGameImages.length; i++){
            for (int j = 0; j < mGameImages[0].length; j++){
                mMainGrid.addView(mGameImages[i][j]);
            }
        }
        //设置最后一个方块为空方块
        setNullImageView(mGameImages[mGameImages.length-1][mGameImages[0].length-1]);

        //初始化定时器
        limitedTime = 400000;
        timer = new CountDownTimer(limitedTime,1) {
            private Activity mActivity = GameActivity.this;
            @Override
            public void onTick(long millisUntilFinished) {
                spentTime = (double)millisUntilFinished/1000;
                mDownTimerText.setText("剩余时间：" + spentTime + "s");
            }

            @Override
            public void onFinish() {
                if(!isWin){
                    this.cancel();
                    mDownTimerText.setText("剩余时间：0s");
                    isGameStart = false;
                    if (!mActivity.isFinishing())
                        showFail();
                }else{
                    this.cancel();
                    isGameStart = false;
                    showWin();
                }
            }

        };

        mBeginGameBtn.setOnClickListener(this);
    }

    private void showFail() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("闯关失败");
        builder.setMessage("闯关失败！是否重新开始？");
        builder.setNegativeButton("否",null);
        builder.setPositiveButton("是", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                GameActivity.startActivity(GameActivity.this,mLevel);
                finish();
            }
        });

        builder.show();
    }

    private void showWin() {
        String score = String.format("%.3f", (limitedTime/1000-spentTime));
        if (Float.parseFloat(score) < Float.parseFloat(Grade.grade.getLevel(mLevel).grade) || Grade.grade.getLevel(mLevel).rank == "max"){
            Log.e("level", String.valueOf(mLevel));
            mPersonalBest.setText("个人纪录：" + score + "秒");
            Grade.modifyGrade(Login.getUserName(), mLevel, score, this);
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("闯关成功");
        if(mLevel != 6){
            builder.setMessage("闯关成功！闯关时间为：" + score + "秒！，是否进入下一关？");
            builder.setNegativeButton("否",null);
            builder.setPositiveButton("是", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    GameActivity.startActivity(GameActivity.this,mLevel+1);
                    finish();
                }
            });
        }else{
            Toast.makeText(this,"闯关成功！闯关时间为：" + score + "秒！恭喜，您已通关!", Toast.LENGTH_LONG);
        }
        builder.show();
    }

    private void initView() {
        setContentView(R.layout.activity_game);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mDownTimerText = (TextView) findViewById(R.id.time);
        mMainGrid = (GridLayout) findViewById(R.id.main_game);
        mBeginGameBtn = (Button) findViewById(R.id.begin_game);
    }

    private void initDetector() {
        mDetector = new GestureDetector(this, new GestureDetector.OnGestureListener() {
            @Override
            public boolean onDown(MotionEvent e) {
                return false;
            }

            @Override
            public void onShowPress(MotionEvent e) {

            }

            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                return false;
            }

            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                return false;
            }

            @Override
            public void onLongPress(MotionEvent e) {

            }

            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                if (!isGameStart){
                    return false;
                }
                int type = getDirByGes(e1.getX(),e1.getY(),e2.getX(),e2.getY());
                changeByDir(type);
                return false;
            }
        });
    }

    //判断游戏结束
    public void isGameOver(){
        //遍历每个游戏小方块
        isWin = true;
        for (int i = 0; i < mGameImages.length; i++){
            for (int j = 0; j < mGameImages[0].length; j++){
                if (mGameImages[i][j] == mNullImage){
                    continue;
                }
                Gamedata mGameData = (Gamedata) mGameImages[i][j].getTag();
                if (!mGameData.isTrue()){
                    isWin = false;
                    break;
                }
            }
        }
        if (isWin){
            timer.onFinish();

        }
    }



    /**
     * 根据手势的方向，获取空方块相应的香菱位置，如果存在方块，那么进行数据交换
     * @param type
     */
    public void changeByDir(int type){
        changeByDir(type, true);
    }

    /**
     * 根据手势的方向，获取空方块相应的香菱位置，如果存在方块，那么进行数据交换
     * @param type
     * @param isAnim true:有动画,false:无动画
     */
    public void changeByDir(int type, boolean isAnim){
        //获取当前空方块的位置
        Gamedata mNullGameData = (Gamedata) mNullImage.getTag();
        //根据方向设置相应的相邻的位置的坐标
        int new_x = mNullGameData.x;
        int new_y = mNullGameData.y;
        if (type == 1){//要移动的方块在当前方块的下边
            new_x++;
        }else if (type == 2){
            new_x--;
        }
        else if (type == 3){
            new_y++;
        }
        else if (type == 4){
            new_y--;
        }
        //判断这个新坐标是否存在,存在的话开始移动
        if (new_x>=0 && new_x< mGameImages.length && new_y>=0 && new_y< mGameImages[0].length){
            if (isAnim){
                changeDataByImageView(mGameImages[new_x][new_y]);
            }else{
                changeDataByImageView(mGameImages[new_x][new_y], isAnim);
            }
        }
    }

    /**
     * 手势判断
     * @param start_x 手势的起始点x
     * @param start_y 手势的起始点y
     * @param end_x 手势的终止点x
     * @param end_y 手势的终止点y
     * @return 1: 上, 2: 下, 3: 左,4: 右
     */
    public int getDirByGes(float start_x, float start_y, float end_x, float end_y){
        boolean isLeftOrRight = (Math.abs(start_x-end_x) > Math.abs(start_y-end_y));//是否是左右
        if(isLeftOrRight){//左右
            boolean isLeft = (start_x > end_x);
            if (isLeft){
                return 3;
            }else{
                return 4;
            }
        }else{//上下
            boolean isUp = (start_y > end_y);
            if (isUp){
                return 1;
            }else{
                return 2;
            }
        }
    }


    //随机打乱顺序
    public void randomMove(){
        //打乱的次数
        for (int i = 0; i <1000; i++){
            int type = (int) (Math.random()*4) + 1;
            //根据手势开始交换，无动画
            changeByDir(type,false);
        }
    }



    /**
     * 利用方块结束之后，交换两个方块的数据
     * @param mImageView 点击的方块,默认有动画
     */
    public void changeDataByImageView(final ImageView mImageView){
        changeDataByImageView(mImageView, true);//默认有动画
    }
    /**
     * 利用方块结束之后，交换两个方块的数据
     * @param mImageView 点击的方块
     * @param isAnim true:有动画，false:无动画
     */
    public void changeDataByImageView(final ImageView mImageView, final boolean isAnim){
        if (isAnimRun){
            return;
        }
        if (!isAnim){
            Gamedata mGamedata = (Gamedata) mImageView.getTag();
            mNullImage.setImageBitmap(mGamedata.bm);
            Gamedata mNullGamedata = (Gamedata) mNullImage.getTag();
            mNullGamedata.bm = mGamedata.bm;
            mNullGamedata.p_x = mGamedata.p_x;
            mNullGamedata.p_y = mGamedata.p_y;
            //设置当前点击的方块为空方块
            setNullImageView(mImageView);
            if (isGameStart) {
                isGameOver();//成功时会弹出toast
            }
            return;
        }
        //创建一个动画，设置好方向
        TranslateAnimation translateAnimation = null;
        if (mImageView.getX() > mNullImage.getX()){//当前点击的方块在空方块的下面
            //向上移动
            translateAnimation = new TranslateAnimation(0.1f, -mImageView.getWidth(), 0.1f, 0.1f);
        }else if (mImageView.getX() < mNullImage.getX()){
            //向下移动
            translateAnimation = new TranslateAnimation(0.1f, mImageView.getWidth(), 0.1f, 0.1f);
        }else if (mImageView.getY() > mNullImage.getY()){
            //向左移动
            translateAnimation = new TranslateAnimation(0.1f, 0.1f, 0.1f, -mImageView.getWidth());
        }else if (mImageView.getY() < mNullImage.getY()){
            //向右移动
            translateAnimation = new TranslateAnimation(0.1f, 0.1f,  0.1f, mImageView.getWidth());
        }
        //设置动画时长
        translateAnimation.setDuration(70);
        //设置动画结束后是否停留
        translateAnimation.setFillAfter(true);
        //设置动画结束后要把真正的数据交换了
        translateAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                isAnimRun = true;
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                isAnimRun = false;
                mImageView.clearAnimation();
                Gamedata mGamedata = (Gamedata) mImageView.getTag();
                mNullImage.setImageBitmap(mGamedata.bm);
                Gamedata mNullGamedata = (Gamedata) mNullImage.getTag();
                mNullGamedata.bm = mGamedata.bm;
                mNullGamedata.p_x = mGamedata.p_x;
                mNullGamedata.p_y = mGamedata.p_y;
                //设置当前点击的方块为空方块
                setNullImageView(mImageView);
                if (isGameStart) {
                    isGameOver();//成功时会弹出toast
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        //执行动画
        mImageView.startAnimation(translateAnimation);
    }

    /**
     * 设置某个方块为空方块
     * @param mImageView 当前要设置为空的方块
     */
    private void setNullImageView(ImageView mImageView){
        mImageView.setImageBitmap(null);
        mNullImage = mImageView;
    }

    /**
     * 判断当前点击的方块是否与空方块的位置关系是相邻关系
     * @param mImageView 所点击的方块
     * @return true：相邻，false：不相邻
     */
    private boolean isHasByNullImageView(ImageView mImageView){
        //分别获取当前空方块的位置与被点击方块的位置，通过x，y两边都差1的方式获取
        Gamedata mNullGamedata = (Gamedata) mNullImage.getTag();
        Gamedata mGamedata = (Gamedata) mImageView.getTag();
        mImageView.getTag();
        if (mNullGamedata.y == mGamedata.y && mGamedata.x+1 == mNullGamedata.x){//当前点击的方块在空方块的上边
            return true;
        }else if (mNullGamedata.y == mGamedata.y && mGamedata.x-1 == mNullGamedata.x){//当前点击的方块在空方块的下边
            return true;
        }else if (mNullGamedata.y == mGamedata.y+1 && mGamedata.x == mNullGamedata.x){//当前点击的方块在空方块的左边
            return true;
        }else if (mNullGamedata.y == mGamedata.y-1 && mGamedata.x == mNullGamedata.x){//当前点击的方块在空方块的右边
            return true;
        }
        return false;
    }

    //自适应大小
    private Bitmap resizeBitmap(Bitmap bitmap,int w,int h)
    {
        if(bitmap!=null)
        {
            int width = bitmap.getWidth();
            int height = bitmap.getHeight();
            int newWidth = w;
            int newHeight = h;
            float scaleWight = ((float)newWidth)/width;
            float scaleHeight = ((float)newHeight)/height;
            Matrix matrix = new Matrix();
            matrix.postScale(scaleWight, scaleHeight);
            Bitmap res = Bitmap.createBitmap(bitmap, 0,0,width, height, matrix, true);
            return res;
        }
        else{
            return null;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        switch (itemId){
            case android.R.id.home:
                finish();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.begin_game:
                if (!isGameStart){
                    //随机打乱
                    randomMove();
                    isGameStart = true;
                    timer.start();
                }

                break;
            default:
                if (!isGameStart){
                    return;
                }
                boolean flag = isHasByNullImageView((ImageView)v);
                if (flag){
                    changeDataByImageView((ImageView) v);
                }
                break;
        }
    }

    public static void startActivity(Context context,int level){

        Intent intent = new Intent(context,GameActivity.class);

        intent.putExtra(LEVEL,level);
        context.startActivity(intent);

    }

    //每个小方块上要绑定的数据
    class Gamedata{
        //每个小方块的实际位置x
        public int x = 0;
        //每个小方块的实际位置y
        public int y = 0;
        //每个小方块的图片
        public Bitmap bm;
        //每个小方块的图片的位置
        public int p_x = 0;
        public int p_y = 0;

        public Gamedata(int x, int y, Bitmap bm) {
            super();
            this.x = x;
            this.y = y;
            this.bm = bm;
            this.p_x = x;
            this.p_y = y;
        }

        //每个小方块的位置是否正确
        public boolean isTrue() {
            if (x == p_x && y == p_y){
                return true;
            }else{
                return false;
            }
        }
    }


}
