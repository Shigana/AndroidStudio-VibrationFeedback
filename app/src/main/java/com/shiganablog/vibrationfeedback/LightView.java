package com.shiganablog.vibrationfeedback;
import android.content.Context;
import android.view.View;
import android.graphics.Canvas;
import android.graphics.Color;
import android.view.animation.AlphaAnimation;

import java.util.Random;

public class LightView extends View {
    private AlphaAnimation mAnimation;
    private Random random = new Random();
    // 描画する色
    private int mColor =Color.rgb(255,  255,  255);// 白
    private int[] mColors = new int[] {
            Color.rgb(237,  26,  61), // 赤
};
public LightView(Context context) {
        super(context);
// だんだん透明になるAlphaAnimationを生成
        mAnimation = new AlphaAnimation(1, 0);
// 1秒で動くように
        mAnimation.setDuration(100);
// 終了後、元に戻らないように
        mAnimation.setFillAfter(true);
        }
public void randomDraw() {
// アニメーション中に次のアニメーションが動いて
// 欲しいのでキャンセルする
        clearAnimation();
    // 色をランダムに選んで
    mColor = mColors[random.nextInt(mColors.length)];
// アニメーション開始
        startAnimation(mAnimation);
        }
@Override
protected void onDraw(Canvas canvas) {
// 画面を指定された色で塗りつぶす
        canvas.drawColor(mColor);
        }
        }