package com.tencent.tmgp.qixiongwushuang;

import com.unity3d.player.*;
import android.app.Activity;
import android.content.res.Configuration;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;

import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.RecognizerListener;
import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechRecognizer;
import com.iflytek.cloud.SpeechUtility;
import com.unity3d.player.UnityPlayer;

public class UnityPlayerActivity extends Activity {
	static UnityPlayerActivity mContext = null;
	protected UnityPlayer mUnityPlayer; // don't change the name of this
										// variable; referenced from native code

	// Setup activity layout
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);

		getWindow().setFormat(PixelFormat.RGBX_8888); // <--- This makes xperia
														// play happy

		mUnityPlayer = new UnityPlayer(this);
		if (mUnityPlayer.getSettings().getBoolean("hide_status_bar", true)) {
			getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
					WindowManager.LayoutParams.FLAG_FULLSCREEN);
		}

		setContentView(mUnityPlayer);
		mUnityPlayer.requestFocus();

		mContext = this;

		super.onCreate(savedInstanceState);
		SpeechUtility.createUtility(mContext, SpeechConstant.APPID
				+ "=57184db3");
	}

	// Quit Unity
	@Override
	protected void onDestroy() {
		mUnityPlayer.quit();
		super.onDestroy();

		// 退出时释放连接
		mIat.cancel();
		mIat.destroy();
	}

	// Pause Unity
	@Override
	protected void onPause() {
		super.onPause();
		mUnityPlayer.pause();
	}

	// Resume Unity
	@Override
	protected void onResume() {
		super.onResume();
		mUnityPlayer.resume();
	}

	// This ensures the layout will be correct.
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		mUnityPlayer.configurationChanged(newConfig);
	}

	// Notify Unity of the focus change.
	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);
		mUnityPlayer.windowFocusChanged(hasFocus);
	}

	// For some reason the multiple keyevent type is not supported by the ndk.
	// Force event injection by overriding dispatchKeyEvent().
	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		if (event.getAction() == KeyEvent.ACTION_MULTIPLE)
			return mUnityPlayer.injectEvent(event);
		return super.dispatchKeyEvent(event);
	}

	// Pass any events not handled by (unfocused) views straight to UnityPlayer
	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		return mUnityPlayer.injectEvent(event);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		return mUnityPlayer.injectEvent(event);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		return mUnityPlayer.injectEvent(event);
	}

	/* API12 */public boolean onGenericMotionEvent(MotionEvent event) {
		return mUnityPlayer.injectEvent(event);
	}

	private static RecognizerListener mRecoListener = new RecognizerListener() {
		@Override
		public void onBeginOfSpeech() {
			// �˻ص���ʾ��sdk�ڲ�¼�����Ѿ�׼�����ˣ��û����Կ�ʼ��������
			UnityPlayer.UnitySendMessage("_Dont_Destroy_On_Load", "MSCStarted",
					"MSC started");
		}

		@Override
		public void onError(SpeechError error) {
			// Tips：
			// 错误码：10118(您没有说话)，可能是录音机权限被禁，需要提示用户打开应用的录音权限。
			// 如果使用本地功能（语记）需要提示用户开启语记的录音权限。
			UnityPlayer.UnitySendMessage("_Dont_Destroy_On_Load", "MSCError",
					error.getPlainDescription(true));
		}

		@Override
		public void onEndOfSpeech() {
			// �˻ص���ʾ����⵽��������β�˵㣬�Ѿ�����ʶ����̣����ٽ�����������
			UnityPlayer.UnitySendMessage("_Dont_Destroy_On_Load", "MSCEnded",
					"MSC ended");
		}

		@Override
		public void onResult(RecognizerResult results, boolean isLast) {
			PrintResult(results);

			if (isLast) {
				StringBuffer resultBuffer = new StringBuffer();
				for (String key : mIatResults.keySet()) {
					resultBuffer.append(mIatResults.get(key));
				}
				try {
					ConvertWavToMp3(Environment.getExternalStorageDirectory()
							+ "/msc/Bamboo/msc_" + m_mscTag + ".wav",
							Environment.getExternalStorageDirectory()
									+ "/msc/Bamboo/msc_" + m_mscTag + ".mp3",
							"8000");
					UnityPlayer.UnitySendMessage(
							"_Dont_Destroy_On_Load",
							"MSCResult",
							m_mscTag + "&&&"
									+ Environment.getExternalStorageDirectory()
									+ "/msc/Bamboo/msc_" + m_mscTag + ".mp3"
									+ "&&&" + resultBuffer.toString());
				} catch (Exception e) {
					// TODO Auto-generated catch block
					UnityPlayer.UnitySendMessage("_Dont_Destroy_On_Load",
							"MSCError", "error" + e.getMessage());
					e.printStackTrace();
					return;
				}

				mIatResults.clear();
			}
		}

		@Override
		public void onVolumeChanged(int volume, byte[] data) {
			UnityPlayer.UnitySendMessage("_Dont_Destroy_On_Load", "MSCVolume",
					Integer.toString(volume));
		}

		@Override
		public void onEvent(int eventType, int arg1, int arg2, Bundle obj) {

		}
	};

	private static HashMap<String, String> mIatResults = new LinkedHashMap<String, String>();
	private static String m_mscTag;

	private static void PrintResult(RecognizerResult results) {
		String text = JsonParser.parseIatResult(results.getResultString());

		String sn = null;
		// 读取json结果中的sn字段
		try {
			JSONObject resultJson = new JSONObject(results.getResultString());
			sn = resultJson.optString("sn");
		} catch (Exception e) {
			UnityPlayer.UnitySendMessage("_Dont_Destroy_On_Load", "MSCError",
					"error" + e.getMessage());
			e.printStackTrace();
			return;
		}

		mIatResults.put(sn, text);
	}

	private static String m_src = "";
	private static String m_des = "";
	private static int m_rate = 8000;

	public static void ConvertWavToMp3(String p_src, String p_des, String p_rate) {
		if (mContext == null) {
			Log.i("instance is null", "null");
			return;
		}

		{
			m_src = p_src;

			m_des = p_des;

			m_rate = Integer.parseInt(p_rate);
		}

		new Thread() {
			@Override
			public void run() {
				mContext.WavToMp3(m_src, m_des, m_rate);
			}
		}.start();
	}

	public native void WavToMp3(String wav, String mp3, int rate);

	static {
		System.loadLibrary("WavToMp3");
	}

	public static void StopRecognize() {
		mIat.stopListening();
	}

	public static void CancelRecognize() {
		mIat.cancel();
	}

	private static SpeechRecognizer mIat;

	public static void StartRecognize(String mscTag) {
		mIat = SpeechRecognizer.createRecognizer(mContext, null);

		// 清空参数
		mIat.setParameter(SpeechConstant.PARAMS, null);
		// 设置听写引擎
		mIat.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_CLOUD);
		// 设置返回结果格式
		mIat.setParameter(SpeechConstant.RESULT_TYPE, "json");

		mIat.setParameter(SpeechConstant.DOMAIN, "iat");
		mIat.setParameter(SpeechConstant.LANGUAGE, "zh_cn");
		// simplfied chinese
		mIat.setParameter(SpeechConstant.ACCENT, "mandarin");

		// 设置语音前端点:静音超时时间，即用户多长时间不说话则当做超时处理
		mIat.setParameter(SpeechConstant.VAD_BOS, "10000");

		// 设置语音后端点:后端点静音检测时间，即用户停止说话多长时间内即认为不再输入， 自动停止录音
		mIat.setParameter(SpeechConstant.VAD_EOS, "10000");

		// 设置标点符号,设置为"0"返回结果无标点,设置为"1"返回结果有标点
		mIat.setParameter(SpeechConstant.ASR_PTT, "1");

		// 设置音频保存路径，保存音频格式支持pcm、wav，设置路径为sd卡请注意WRITE_EXTERNAL_STORAGE权限
		// 注：AUDIO_FORMAT参数语记需要更新版本才能生效
		mIat.setParameter(SpeechConstant.AUDIO_FORMAT, "wav");
		mIat.setParameter(SpeechConstant.ASR_AUDIO_PATH,
				Environment.getExternalStorageDirectory() + "/msc/Bamboo/msc_"
						+ mscTag + ".wav");

		mIatResults.clear();
		m_mscTag = mscTag;

		mIat.startListening(mRecoListener);

		int ret = mIat.startListening(mRecoListener);
		if (ret != ErrorCode.SUCCESS) {
			UnityPlayer.UnitySendMessage("_Dont_Destroy_On_Load", "MSCError",
					"MSC erorr, code:" + ret);
		}
	}
}
