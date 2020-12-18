package com.shiganablog.vibrationfeedback;

import android.app.Activity;
import android.graphics.Color;
import android.media.AudioAttributes;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.util.Log;
import android.view.WindowManager;
public class MainActivity extends Activity {
    private LightView mLightView;
    private SoundSwitch mSoundSwitch;
    private SoundPool soundPool;
    private int soundOne;
    private Handler mHandler = new Handler();
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mLightView = new LightView(this);//LightViewの呼び出し
        setContentView(mLightView);//画面に表示するviewを指定
// 暗くならないようにする
        getWindow().addFlags(
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        AudioAttributes audioAttributes = new AudioAttributes.Builder()
                // USAGE_MEDIA
                // USAGE_GAME
                .setUsage(AudioAttributes.USAGE_GAME)
                // CONTENT_TYPE_MUSIC
                // CONTENT_TYPE_SPEECH, etc.
                .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                .build();

        soundPool = new SoundPool.Builder()
                .setAudioAttributes(audioAttributes)
                // ストリーム数に応じて
                .setMaxStreams(2)
                .build();

        // one.wav をロードしておく
        soundOne = soundPool.load(this, R.raw.button02a, 1);

        // load が終わったか確認する場合
        soundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
            @Override
            public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
                Log.d("debug","sampleId="+sampleId);
                Log.d("debug","status="+status);
            }
        });
    }
    @Override
    public void onResume() {
        super.onResume();
        mSoundSwitch = new SoundSwitch();
// リスナーを登録して音を感知できるように
        mSoundSwitch.setOnVolumeReachedListener(
                new SoundSwitch.OnReachedVolumeListener() {
                    // 音を感知したら呼び出される
                    public void onReachedVolume(short volume) {
// 別スレッドからUIスレッドに要求するのでHandler.postでエラー回避
                        mHandler.post(new Runnable() {//Runnableに入った要求を順番にLoopでrunを呼び出し処理
                            public void run() {
                                mLightView.randomDraw();//LightViewクラスのrandomDrawを呼び出して描画を依頼
                                soundPool.play(soundOne, 1.0f, 1.0f, 0, 0, 1);
                                Vibrator vib = (Vibrator)getSystemService(VIBRATOR_SERVICE);
                                //100ミリ秒の振動
                                vib.vibrate(100);
                            }
                        });
                    }
                });
// 別スレッドとしてSoundSwitchを開始（録音を開始）
        new Thread(mSoundSwitch).start();
    }
    @Override
    public void onPause() {//Activityの状態がonPauseの時の処理
        super.onPause();//superクラスのonPauseを呼び出す
        mSoundSwitch.stop();// 録音を停止
    }
}
