package com.tencent.tmgp.qixiongwushuang;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.tencent.android.tpush.XGIOperateCallback;
import com.tencent.android.tpush.XGLocalMessage;
import com.tencent.android.tpush.XGPushManager;
import com.tencent.bugly.opengame.crashreport.CrashReport;
import com.tencent.unipay.plugsdk.IUnipayServiceCallBackPro;
import com.tencent.unipay.plugsdk.UnipayPlugAPI;
import com.tencent.unipay.plugsdk.UnipayResponse;
import com.tencent.unipay.request.UnipayGameRequest;
import com.tencent.unipay.request.UnipayUserInfo;
import com.tencent.ysdk.api.YSDKApi;
import com.tencent.ysdk.framework.common.eFlag;
import com.tencent.ysdk.framework.common.ePlatform;
import com.tencent.ysdk.module.bugly.BuglyListener;
import com.tencent.ysdk.module.user.PersonInfo;
import com.tencent.ysdk.module.user.UserListener;
import com.tencent.ysdk.module.user.UserLoginRet;
import com.tencent.ysdk.module.user.UserRelationRet;
import com.tencent.ysdk.module.user.WakeupRet;
import com.unity3d.player.UnityPlayer;
//import com.tencent.unipay.plugsdk.sample.AndroidPaySample;

////////////////////////////////////////////////////////////
// MSC
// //////////////////////////////////////////////////////////

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

	// test 支付沙箱环境，release支付现网环境
	private static String PAYMENT_ENV = "test";
	//private static String PAYMENT_ENV = "release";

	private boolean isFirstLogin = false;

	protected UnityPlayer mUnityPlayer; // don't change the name of this
										// variable; referenced from native code

	private static String m_log_tag = "UnityPlayer";

	private static UnityPlayerActivity m_instance = null;

	// Setup activity layout
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);

		Log.d(m_log_tag, "OnCreate()");

		/*
		 * // TODO GAME 游戏需自行检测自身是否重复, 检测到是重复的Activity则要把自己finish掉 //
		 * 注意：游戏需要加上去重判断finish重复的实例，否则可能发生重复拉起游戏的问题。游戏可自行决定重复的判定。 if
		 * (WGPlatform.IsDifferentActivity(this)) { Log.d( m_log_tag,
		 * "Warning!Reduplicate game activity was detected.Activity will finish immediately."
		 * );
		 * 
		 * Log.d( m_log_tag,
		 * "Warning!Reduplicate game activity was detected.Activity will finish immediately."
		 * );
		 * 
		 * Log.d( m_log_tag,
		 * "Warning!Reduplicate game activity was detected.Activity will finish immediately."
		 * );
		 * 
		 * this.finish();
		 * 
		 * return; }
		 */

		getWindow().setFormat(PixelFormat.RGBX_8888); // <--- This makes xperia
														// play happy

		mUnityPlayer = new UnityPlayer(this);
		if (mUnityPlayer.getSettings().getBoolean("hide_status_bar", true)) {
			getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
					WindowManager.LayoutParams.FLAG_FULLSCREEN);
		}

		setContentView(mUnityPlayer);
		mUnityPlayer.requestFocus();

		/*
		 * { Log.d( m_log_tag, "Before InitMSDK" );
		 * 
		 * InitMSDK(); }
		 */

		{
			// YSDK 的初始化只需要游戏launcherActivity的onCreate调用
			YSDKApi.onCreate(this);

			YSDKApi.handleIntent(this.getIntent());
		}

		{
			YSDKApi.setUserListener(new YSDKCallback(this));

			YSDKApi.setBuglyListener(new YSDKCallback(this));
		}

		{
			Log.d(m_log_tag, "Set Instance");

			m_instance = this;
		}

		{
			Log.d(m_log_tag, "Before OnRegisterXG");

			OnRegisterXG();
		}

		{
			Log.d(m_log_tag, "OnCreate.Done()");
		}

		// //////////////////////////////////////////////////////////
		// MSC
		// //////////////////////////////////////////////////////////
		SpeechUtility.createUtility(m_instance, SpeechConstant.APPID
				+ "=57184db3");
	}

	// TODO GAME 游戏需要集成此方法并调用WGPlatform.onDestory()
	@Override
	protected void onDestroy() {
		Log.d(m_log_tag, "onDestroy()");

		if (mUnityPlayer != null) {
			Log.d(m_log_tag, "Quit Unity");

			mUnityPlayer.quit();
		}

		super.onDestroy();

		YSDKApi.onDestroy(this);

		// WGPlatform.onDestory(this);

		Log.d(m_log_tag, "onDestroy");

		Log.d(m_log_tag, "onDestroy.Done()");

		// //////////////////////////////////////////////////////////
		// MSC
		// //////////////////////////////////////////////////////////
		// 退出时释放连接
		mIat.cancel();
		mIat.destroy();
	}

	// TODO GAME 游戏需要集成此方法并调用WGPlatform.onRestart()
	@Override
	protected void onRestart() {
		Log.d(m_log_tag, "onRestart()");

		super.onRestart();

		YSDKApi.onRestart(this);

		// WGPlatform.onRestart();

		Log.d(m_log_tag, "onRestart.Done()");
	}

	// Resume Unity
	@Override
	protected void onResume() {
		Log.d(m_log_tag, "onResume()");

		super.onResume();
		mUnityPlayer.resume();

		YSDKApi.onResume(this);

		/**
		 * //解决onResume卡顿问题 new Handler().post(new Runnable(){ public void run()
		 * { WGPlatform.onResume(); } });
		 * 
		 * // TODO GAME 模拟游戏自动登录，这里需要游戏添加加载动画 // WGLogin是一个异步接口,
		 * 传入ePlatform_None则调用本地票据验证票据是否有效 //
		 * 如果从未登录过，则会立即在onLoginNotify中返回flag为eFlag_Local_Invalid，此时应该拉起授权界面 //
		 * 建议在此时机调用WGLogin,它应该在handlecallback之后进行调用。 if(isFirstLogin) {
		 * isFirstLogin = false; startWaiting();
		 * WGPlatform.WGLogin(EPlatform.ePlatform_None); }
		 */

		Log.d(m_log_tag, "onResume.Done()");
	}

	// Pause Unity
	@Override
	protected void onPause() {
		Log.d(m_log_tag, "onPause()");

		super.onPause();
		mUnityPlayer.pause();

		YSDKApi.onPause(this);

		// WGPlatform.onPause();

		Log.d(m_log_tag, "onPause.Done()");
	}

	// TODO GAME 游戏需要集成此方法并调用WGPlatform.onStop()
	@Override
	protected void onStop() {
		Log.d(m_log_tag, "onStop()");

		super.onStop();

		YSDKApi.onStop(this);

		// WGPlatform.onStop();

		Log.d(m_log_tag, "onStop.Done()");
	}

	// TODO GAME 在onActivityResult中需要调用WGPlatform.onActivityResult
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		Log.d(m_log_tag, "onActivityResult()");

		super.onActivityResult(requestCode, resultCode, data);

		YSDKApi.onActivityResult(requestCode, resultCode, data);

		// WGPlatform.onActivityResult(requestCode, resultCode, data);

		Log.d(m_log_tag, "onActivityResult");

		Log.d(m_log_tag, "onActivityResult.Done()");
	}

	// TODO GAME 在onNewIntent中需要调用handleCallback将平台带来的数据交给MSDK处理
	@Override
	protected void onNewIntent(Intent intent) {
		Log.d(m_log_tag, "onNewIntent()");

		super.onNewIntent(intent);

		YSDKApi.handleIntent(intent);

		/**
		 * // TODO GAME 处理游戏被拉起的情况 //
		 * launchActivity的onCreat()和onNewIntent()中必须调用 //
		 * WGPlatform.handleCallback()。否则会造成微信登录无回调 if
		 * (WGPlatform.wakeUpFromHall(intent)) { Log.d( m_log_tag,
		 * "LoginPlatform is Hall"); Log.d( m_log_tag, intent); } else { Log.d(
		 * m_log_tag, "LoginPlatform is not Hall"); Log.d( m_log_tag, intent);
		 * WGPlatform.handleCallback(intent); }
		 */

		Log.d(m_log_tag, "onNewIntent.Done()");
	}

	public void startWaiting() {
		Log.d(m_log_tag, "startWaiting()");

		Log.d(m_log_tag, "startWaiting");
		// mAutoLoginWaitingDlg = new ProgressDialog(this);
		// stopWaiting();
		// mAutoLoginWaitingDlg.setTitle("自动登录中...");
		// mAutoLoginWaitingDlg.show();
	}

	public void stopWaiting() {
		Log.d(m_log_tag, "stopWaiting()");

		Log.d(m_log_tag, "stopWaiting");
		// if (mAutoLoginWaitingDlg != null && mAutoLoginWaitingDlg.isShowing())
		// {
		// mAutoLoginWaitingDlg.dismiss();
		// }
	}

	/**
	 * 拉起支付Demo,需要先安装支付示例工程AndroidPaySample(位于MSDKzip包中的Tencent
	 * AndroidPayRelease.zip中) 才能拉起支付Demo
	 */
	/**
	 * public void launchPayDemo() { Log.d( m_log_tag, "launchPayDemo()" );
	 * 
	 * Log.d( m_log_tag, "called"); Intent i = new
	 * Intent("com.tencent.pay.sdksample.AndroidPaySample"); LoginRet lr = new
	 * LoginRet(); WGPlatform.WGGetLoginRecord(lr);
	 * 
	 * // 注意：这里需要判断登录态是否有效 if (lr.flag != CallbackFlag.eFlag_Succ) { if
	 * (lr.platform == EPlatform.ePlatform_Weixin.val()) { //
	 * accesstoken过期，尝试刷新票据 if (lr.flag ==
	 * CallbackFlag.eFlag_WX_AccessTokenExpired) {
	 * WGPlatform.WGRefreshWXToken(); return; } else { // 微信登录态失效，引导用户重新登录授权
	 * return; } } else { // 手Q登录态失效，引导用户重新登录授权 return; } }
	 * 
	 * i.putExtra("userId", lr.open_id); i.putExtra("offerId",
	 * WeGame.getInstance().offerId); if (lr.platform == WeGame.WXPLATID) {
	 * i.putExtra("userKey", lr.getTokenByType(TokenType.eToken_WX_Access));
	 * i.putExtra("sessionType", "wc_actoken"); i.putExtra("sessionId",
	 * "hy_gameid"); } else if (lr.platform == WeGame.QQPLATID) {
	 * i.putExtra("userKey", lr.getTokenByType(TokenType.eToken_QQ_Pay));
	 * i.putExtra("sessionType", "kp_actoken"); i.putExtra("sessionId",
	 * "openid"); }
	 * 
	 * i.putExtra("pf", WGPlatform.WGGetPf("")); i.putExtra("zoneId", "1");
	 * i.putExtra("pfKey", WGPlatform.WGGetPfKey()); i.putExtra("acctType",
	 * "common"); i.putExtra("saveValue", "60"); i.putExtra("msdk", true);
	 * startActivity(i);
	 * 
	 * 
	 * Log.d( m_log_tag, "launchPayDemo.Done()" ); }
	 */

	/**
	 * 游戏崩溃后会将堆栈信息上报到腾讯组件————灯塔中。这时制造native层崩溃测试异常上报
	 */
	public static void nativeCrashTest() {
		Log.d(m_log_tag, "nativeCrashTest()");

		// Native异常测试
		Log.d(m_log_tag, "called");

		CrashReport.testNativeCrash();

		Log.d(m_log_tag, "nativeCrashTest.Done()");
	}

	/**
	 * 游戏崩溃后会将堆栈信息上报到腾讯组件————灯塔中。这时制造空指针异常测试异常上报
	 */
	public static void nullPointerExceptionTest() {
		Log.d(m_log_tag, "nullPointerExceptionTest()");

		// 空指针异常测试
		Log.d(m_log_tag, "called");
		String aa = null;
		if (aa.equals("aa")) {
		}

		Log.d(m_log_tag, "nullPointerExceptionTest.Done()");
	}

	private void InitMSDK() {
		Log.d(m_log_tag, "InitMSDK()");

		/**
		 * MsdkBaseInfo baseInfo = new MsdkBaseInfo();
		 * 
		 * { //微信 Appkey:ce804c29c3f6aeddd9d3fe3849372452
		 * 
		 * baseInfo.qqAppId = "1104965145"; baseInfo.qqAppKey =
		 * baseInfo.qqAppKey = "r2krevcrdBtBsyga"; baseInfo.wxAppId =
		 * "wxbbd67fda7f55e12d"; baseInfo.msdkKey =
		 * "b6864e190319c71922d571337b8d6c7d"; //订阅型测试用offerId baseInfo.offerId
		 * = "1105332189";
		 * 
		 * // TODO GAME 自2.7.1a开始游戏可在初始化msdk时动态设置版本号，灯塔和bugly的版本号由msdk统一设置 //
		 * 1、版本号组成 = versionName + versionCode //
		 * 2、游戏如果不赋值给appVersionName（或者填为""）和appVersionCode(或者填为-1)， //
		 * msdk默认读取AndroidManifest
		 * .xml中android:versionCode="51"及android:versionName="2.7.1" //
		 * 3、游戏如果在此传入了appVersionName
		 * （非空）和appVersionCode（正整数）如下，则灯塔和bugly上获取的版本号为2.7.1.271
		 * baseInfo.appVersionName = "1.1.0"; baseInfo.appVersionCode = 1; }
		 * 
		 * Log.d( m_log_tag, "InitMSDK.Basic.Info.Done()" );
		 * 
		 * { // 注意：传入Initialized的activity即this，在游戏运行期间不能被销毁，否则会产生Crash
		 * WGPlatform.Initialized(this, baseInfo); // 设置拉起QQ时候需要用户授权的项
		 * WGPlatform.WGSetPermission(WGQZonePermissions.eOPEN_ALL);
		 * 
		 * // TODO GAME 此处填写游戏需要回调的脚本绑定的GameObject的名称 String ObserverGameObject
		 * = "_Dont_Destroy_On_Load"; // 全局回调类 WGPlatform.WGSetObserver(new
		 * UnityPlatformObserver(ObserverGameObject)); // 应用宝更新回调类
		 * WGPlatform.WGSetSaveUpdateObserver(new
		 * UnitySaveUpdateObserver(ObserverGameObject)); // 广告的回调设置
		 * WGPlatform.WGSetADObserver(new UnityADObserver(ObserverGameObject));
		 * //QQ 加群加好友回调 WGPlatform.WGSetGroupObserver(new
		 * UnityGroupObserver(ObserverGameObject)); }
		 * 
		 * Log.d( m_log_tag, "InitMSDK.Basic.Init.Done()" );
		 * 
		 * { // TODO GAME 处理游戏被拉起的情况 //
		 * launchActivity的onCreat()和onNewIntent()中必须调用 //
		 * WGPlatform.handleCallback()。否则会造成微信登录无回调 if
		 * (WGPlatform.wakeUpFromHall(this.getIntent())) { Log.d( m_log_tag,
		 * "UnityMSDK" + " hall found." );
		 * 
		 * // 拉起平台为大厅 Log.d( m_log_tag, "LoginPlatform is Hall"); Log.d(
		 * m_log_tag, this.getIntent()); } else { Log.d( m_log_tag, "UnityMSDK"
		 * + " not hall." );
		 * 
		 * // 拉起平台不是大厅 Log.d( m_log_tag, "LoginPlatform is not Hall"); Log.d(
		 * m_log_tag, this.getIntent());
		 * WGPlatform.handleCallback(this.getIntent()); }
		 * 
		 * isFirstLogin = true; }
		 */

		Log.d(m_log_tag, "InitMSDK.Done()");

	}

	public static void OnLocalPush(String p_key, int p_sec_since_now,
			String p_push_content) {
		Log.d(m_log_tag, "OnLocalPush()");

		// {
		// UnityPlayer.UnitySendMessage( "_Dont_Destroy_On_Load", "JavaInvoke",
		// "JavaString" );
		// }

		// Log.d( "Push", "p_key: " + p_key );

		// Log.d( "Push", "p_sec_since_now: " + p_sec_since_now );

		// Log.d( "Push", "p_push_content: " + p_push_content );

		if (m_instance == null) {
			Log.e("Push", "Activity instance = null.");
			return;
		}

		XGLocalMessage local_msg = new XGLocalMessage();

		local_msg.setType(1);

		local_msg.setTitle("七雄无双");

		local_msg.setContent(p_push_content);

		{
			Date t_target_time = new Date();

			t_target_time.setTime(t_target_time.getTime() + p_sec_since_now
					* 1000);

			{
				SimpleDateFormat t_df = new SimpleDateFormat("yyyyMMdd");

				String t_target_date = t_df.format(t_target_time);

				Log.d("LocalPush Date", t_target_date);

				local_msg.setDate(t_target_date);
			}

			{
				SimpleDateFormat t_df = new SimpleDateFormat("HH");

				String t_target_h = t_df.format(t_target_time);

				Log.d("LocalPush Hour", t_target_h);

				local_msg.setHour(t_target_h);
			}

			{
				SimpleDateFormat t_df = new SimpleDateFormat("mm");

				String t_target_m = t_df.format(t_target_time);

				Log.d("LocalPush Min", t_target_m);

				local_msg.setMin(t_target_m);
			}
		}

		Context context = m_instance.getApplicationContext();

		XGPushManager.clearLocalNotifications(context);

		XGPushManager.addLocalNotification(context, local_msg);

		Log.d(m_log_tag, "OnLocalPush.Done()");
	}

	protected void OnRegisterXG() {
		Log.d(m_log_tag, "UnityXG.OnRegisterXG()");

		Context context = getApplicationContext();

		// XGPushManager.registerPush(context);

		XGPushManager.registerPush(context, new XGIOperateCallback() {

			@Override
			public void onSuccess(Object data, int flag) {
				Log.d("XG Token", "PushXinge register success !  token is "
						+ data);

				{
					UnityPlayer.UnitySendMessage("_Dont_Destroy_On_Load",
							"XGSetToken", "" + data);
				}
			}

			@Override
			public void onFail(Object data, int errCode, String msg) {
				Log.d("XG Token", "PushXinge register fail !  code:" + errCode
						+ ",msg:" + msg);

				{
					UnityPlayer.UnitySendMessage("_Dont_Destroy_On_Load",
							"XGSetToken", "");
				}
			}
		});

		Log.d(m_log_tag, "UnityXG.OnRegisterXG.Done()");
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

	private String userId = "";
	private String userKey = "";
	private String sessionId = ""; // ok
	private String sessionType = ""; // ok
	private String zoneId = ""; // default, only 1 with payment
	private String saveValue = "";
	private String pf = ""; // ok
	private String pfKey = ""; // ok
	private String acctType = "";
	private String tokenUrl = "";
	private int resId = 0;
	private byte[] appResData = null;

	private static int retCode = -100;
	private static String retMessage = "";

	private UnipayPlugAPI unipayAPI = null;

	public static void SetOpenIdAndOpenKey(String p_open_id, String p_open_key) {
		Log.d(m_log_tag, "SetOpenIdAndOpenKey()");

		Log.d(m_log_tag, "p_open_id: " + p_open_id);

		Log.d(m_log_tag, "p_open_key: " + p_open_key);

		m_instance.userId = p_open_id;

		m_instance.userKey = p_open_key;

		UnityPlayer.UnitySendMessage("_Dont_Destroy_On_Load", "SetPayToken",
				p_open_key);
	}

	public static void SetSessionIdAndType(String p_session_id,
			String p_session_type) {
		Log.d(m_log_tag, "SetSessionIdAndType()");

		if (m_login_platform == ePlatform.QQ) {
			m_instance.sessionId = "openid";

			m_instance.sessionType = "kp_actoken";
		} else if (m_login_platform == ePlatform.WX) {
			m_instance.sessionId = "hy_gameid";

			m_instance.sessionType = "wc_actoken";
		} else {
			Log.e(m_log_tag, "Platform Error.");
		}

		Log.d(m_log_tag, "p_session_id: " + m_instance.sessionId);

		Log.d(m_log_tag, "p_session_type: " + m_instance.sessionType);
	}

	public static void SetPfAndPfKey(String p_pf, String p_pf_key) {
		Log.d(m_log_tag, "SetPfAndPfKey()");

		m_instance.pf = m_login_ret.pf;

		m_instance.pfKey = m_login_ret.pf_key;

		Log.d(m_log_tag, "p_pf: " + m_instance.pf);

		Log.d(m_log_tag, "p_pf_key: " + m_instance.pfKey);

		{
			UnityPlayer.UnitySendMessage("_Dont_Destroy_On_Load", "SetPf",
					m_instance.pf);
		}

		{
			UnityPlayer.UnitySendMessage("_Dont_Destroy_On_Load", "SetPfKey",
					m_instance.pfKey);
		}
	}

	// 设置支付参数
	private void setParams() {
		Bundle bundle = this.getIntent().getExtras();
		// 用户ID,demo中调用即通登录模块获取，应用根据不同平台传递相应的登录id
		// userId = bundle.getString("uin");

		// 用户登录态,demo中从即通登录模块获取，应用根据不同平台传递相应的登录票据
		// userKey = bundle.getString("skey");

		// 用户ID类型,应用根据自己的登录类型传递,如uin、openid、hy_gameid、uin
		// sessionId = "uin";
		// 用户登录态类型,应用根据自己的登录类型传递,如seky、kp_actoken、wc_actoken、sid
		// sessionType = "skey";

		// 游戏分区id（若无分区， 默认传1）
		zoneId = "1";

		// 平台信息,格式平台-渠道-系统-自定义（详细见说明文档）
		// pf = "huyu_m-2001-android-xxxx";
		// 由平台下发，游戏传递给支付sdk,自研应用不校验，可以设置为pfKey
		// pfKey = "877047063A36265C24233774959A0D34";

		acctType = UnipayPlugAPI.ACCOUNT_TYPE_COMMON;
		// 货币类型 ACCOUNT_TYPE_COMMON:基础货币； ACCOUNT_TYPE_SECURITY:安全货币

		// 用户的充值数额（可选，调用相应充值接口即可）
		saveValue = "60";
	}

	public static void InitPayment() {
		Log.d(m_log_tag, "InitPayment()");

		// 设置支付参数
		m_instance.setParams();

		// 初始化
		m_instance.unipayAPI = new UnipayPlugAPI(m_instance);

		UnipayUserInfo userInfo = new UnipayUserInfo();

		userInfo.userId = m_instance.userId; // ok
		userInfo.userKey = m_instance.userKey; // ok

		userInfo.sessionId = m_instance.sessionId; // ok
		userInfo.sessionType = m_instance.sessionType; // ok

		userInfo.pf = m_instance.pf; // ok
		userInfo.pfKey = m_instance.pfKey; // ok

		userInfo.offerid = "1105332189"; // ok

		// test 支付沙箱环境，release支付现网环境
		// 调用初始化接口
		m_instance.unipayAPI.init(userInfo, PAYMENT_ENV);

		m_instance.unipayAPI.setLogEnable(true);
	}

	public static void BuyMonthCard() {
		{
			Log.d(m_log_tag, "BuyMonthCard()");

			Log.d(m_log_tag, "BuyMonthCard()");

			Log.d(m_log_tag, "BuyMonthCard()");
		}

		/**
		 * { unipayAPI.setLogEnable(true);
		 * 
		 * //充值游戏币接口，充值默认值由支付SDK设置; // unipayAPI.setEnv("test");
		 * 
		 * unipayAPI.setLogEnable(true);
		 * 
		 * UnipaySubscribeRequest request = new UnipaySubscribeRequest();
		 * 
		 * request.offerId = "1105332189";
		 * 
		 * request.openId = userId; request.openKey = userKey;
		 * 
		 * request.sessionId = sessionId; request.sessionType = sessionType;
		 * 
		 * request.serviceCode = "1450000593-1001"; // TODO request.serviceName
		 * = "绿钻贵族"; // TODO
		 * 
		 * request.remark = "七雄无双月卡";
		 * 
		 * request.zoneId = zoneId;
		 * 
		 * request.pf = pf; request.pfKey = pfKey;
		 * 
		 * request.acctType = acctType;
		 * 
		 * request.resId = R.drawable.yuanbao; request.resData = GetResData();
		 * 
		 * request.isCanChange = false;
		 * 
		 * request.saveValue = "1";
		 * 
		 * request.extendInfo.unit="月";
		 * 
		 * request.productId = "1450000593-1001"; // TODO
		 * 
		 * Log.i("TencentPay",
		 * "userId, userKey, sessionId, sessionType, zoneId, pf, pfKey, acctType"
		 * + "====" + userId + "," + userKey + "," + sessionId + "," +
		 * sessionType + "," + zoneId + "," + pf + "," + pfKey + "," +
		 * acctType);
		 * 
		 * unipayAPI.LaunchPay(request, unipayStubCallBack); }
		 */
	}

	public static void BuyEverCard() {
		{
			Log.d(m_log_tag, "BuyEverCard()");

			Log.d(m_log_tag, "BuyEverCard()");

			Log.d(m_log_tag, "BuyEverCard()");
		}
	}

	private static int m_last_buy_yuan_bao_count = 0;

	public static void BuyYuanBao(int p_yuan_bao_count) {
		{
			Log.d(m_log_tag, "BuyYuanBao( " + p_yuan_bao_count + " )");

			Log.d(m_log_tag, "BuyYuanBao( " + p_yuan_bao_count + " )");

			Log.d(m_log_tag, "BuyYuanBao( " + p_yuan_bao_count + " )");
		}

		m_last_buy_yuan_bao_count = p_yuan_bao_count;

		m_instance.runOnUiThread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub

				// 充值游戏币接口，充值默认值由支付SDK设置;
				// unipayAPI.setEnv("test");

				m_instance.unipayAPI.setLogEnable(true);

				UnipayGameRequest request = new UnipayGameRequest();

				request.offerId = "1105332189";

				request.openId = m_instance.userId;
				request.openKey = m_instance.userKey;

				request.sessionId = m_instance.sessionId;
				request.sessionType = m_instance.sessionType;

				request.zoneId = m_instance.zoneId;

				request.pf = m_instance.pf;
				request.pfKey = m_instance.pfKey;

				request.acctType = m_instance.acctType;

				request.resId = R.drawable.yuanbao;

				request.resData = GetResData();

				request.isCanChange = false;

				// request.saveValue = "1";

				request.saveValue = m_last_buy_yuan_bao_count + "";

				request.extendInfo.unit = "个";

				Log.i("TencentPay", "userId: " + m_instance.userId );
				
				Log.i("TencentPay", "userKey: " + m_instance.userKey );
				
				Log.i("TencentPay", "sessionId: " + m_instance.sessionId );
				
				Log.i("TencentPay", "sessionType: " + m_instance.sessionType );
				
				Log.i("TencentPay", "zoneId: " + m_instance.zoneId );
				
				Log.i("TencentPay", "pf: " + m_instance.pf );
				
				Log.i("TencentPay", "pfKey: " + m_instance.pfKey );
				
				Log.i("TencentPay", "acctType: " + m_instance.acctType );
				
				m_instance.unipayAPI.LaunchPay(request,
						m_instance.unipayStubCallBack);
			}
		});

		{
			Log.d(m_log_tag, "BuyYuanBao( " + p_yuan_bao_count + " ) --- Done.");
		}
	}

	private static byte[] GetResData() {
		Bitmap bmp = BitmapFactory.decodeResource(m_instance.getResources(),
				R.drawable.yuanbao);

		ByteArrayOutputStream baos = new ByteArrayOutputStream();

		bmp.compress(Bitmap.CompressFormat.PNG, 100, baos);

		byte[] appResData = baos.toByteArray();

		return appResData;
	}

	// 回调接口
	IUnipayServiceCallBackPro.Stub unipayStubCallBack = new IUnipayServiceCallBackPro.Stub() {
		@Override
		public void UnipayNeedLogin() {
			Log.i("UnipayPlugAPI", "UnipayNeedLogin");

		}

		@Override
		public void UnipayCallBack(UnipayResponse response) {
			// TODO Auto-generated method stub
			Log.i("UnipayPlugAPI", "UnipayCallBack \n" + "\nresultCode = "
					+ response.resultCode + "\npayChannel = "
					+ response.payChannel + "\npayState = " + response.payState
					+ "\nproviderState = " + response.provideState
					+ "\nsavetype = " + response.extendInfo);

			retCode = response.resultCode;
			retMessage = response.resultMsg;

			// handler.sendEmptyMessage( 0 );

			UnityPlayer.UnitySendMessage("_Dont_Destroy_On_Load",
					"SetPayResultCode", retCode + "");
		}
	};

	Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			Log.i(m_log_tag, "Handler.Handle.Message");

			Toast.makeText(
					m_instance,
					"call back retCode=" + String.valueOf(retCode)
							+ " retMessage=" + retMessage, Toast.LENGTH_SHORT)
					.show();
		}
	};

	private static UserLoginRet m_login_ret;

	public class YSDKCallback implements UserListener, BuglyListener {
		// public UnityPlayerActivity mainActivity;

		public YSDKCallback(Activity activity) {
			// mainActivity = (UnityPlayerActivity) activity;
		}

		public void OnLoginNotify(UserLoginRet ret) {
			Log.d(m_log_tag, "OnLoginNotify: " + ret.toString());

			Log.d(m_log_tag, "called");
			Log.d(m_log_tag, "ret.flag" + ret.flag);

			m_login_ret = null;

			// mainActivity.stopWaiting();
			switch (ret.flag) {
			case eFlag.Succ:
				m_login_ret = ret;

				if (ret.platform == 1) {
					m_login_platform = ePlatform.QQ;
				} else if (ret.platform == 2) {
					m_login_platform = ePlatform.WX;
				} else {
					Log.e(m_log_tag, "Platform Error.");
				}

				// SetPlaform
				{
					UnityPlayer.UnitySendMessage("_Dont_Destroy_On_Load",
							"SetPlatform", ret.platform + "");
				}

				// openId
				{
					UnityPlayer.UnitySendMessage("_Dont_Destroy_On_Load",
							"SetOpenId", m_login_ret.open_id);
				}

				// accessToken
				{
					if (m_login_platform == ePlatform.QQ) {
						// TOKEN_TYPE_QQ_ACCESS = 1 //手Q accesstoken

						UnityPlayer.UnitySendMessage("_Dont_Destroy_On_Load",
								"SetAccessToken",
								m_login_ret.getTokenByType(1).value);
					} else if (m_login_platform == ePlatform.WX) {
						// TOKEN_TYPE_WX_ACCESS = 3 //微信 accesstoken

						UnityPlayer.UnitySendMessage("_Dont_Destroy_On_Load",
								"SetAccessToken",
								m_login_ret.getTokenByType(3).value);
					}
				}

				// openId openKey
				{
					if (m_login_platform == ePlatform.QQ) {
						// public const int eToken_QQ_Pay = 2;
						SetOpenIdAndOpenKey(m_login_ret.open_id,
								m_login_ret.getTokenByType(2).value);
					} else if (m_login_platform == ePlatform.WX) {
						// public const int eToken_WX_Access = 3;
						SetOpenIdAndOpenKey(m_login_ret.open_id,
								m_login_ret.getTokenByType(3).value);
					}
				}

				// sessionId SessionType
				{
					SetSessionIdAndType("", "");
				}

				// pf pfKey
				{
					SetPfAndPfKey("", "");
				}

				{
					YSDKApi.queryUserInfo(m_login_platform);
				}

				// mainActivity.letUserLogin();
				break;

			// 游戏逻辑，对登陆失败情况分别进行处理
			//case eFlag.User_QQ_NetworkErr:
			//case eFlag.User_WX_UserCancel:
			//case eFlag.User_WX_NotInstall:
			//case eFlag.User_WX_NotSupportApi:
			//case eFlag.User_WX_LoginFail:
			//case eFlag.User_QQ_LoginFail:
			//case eFlag.User_LocalTokenInvalid:
				/*
				 * Toast.makeText(mainActivity.getApplicationContext(),
				 * "login error : ret=" + ret.flag + " msg=" + ret.msg,
				 * Toast.LENGTH_LONG).show(); Log.d( m_log_tag,ret.toString() );
				 * // 显示登陆界面 mainActivity.letUserLogout();
				 */
			//	break;

			default:
				// 显示登陆界面
				// mainActivity.letUserLogout();
				break;
			}

			{
				UnityPlayer.UnitySendMessage("_Dont_Destroy_On_Load",
						"OnLoginNotify", ret.flag + "");
			}
		}

		public void OnWakeupNotify(WakeupRet ret) {
			Log.d(m_log_tag, "called");
			Log.d(m_log_tag, ret.toString() + ":flag:" + ret.flag);
			Log.d(m_log_tag, ret.toString() + "msg:" + ret.msg);
			Log.d(m_log_tag, ret.toString() + "platform:" + ret.platform);

			/**
			 * MainActivity.platform = ret.platform; // TODO GAME
			 * 游戏需要在这里增加处理异账号的逻辑 if (eFlag.Succ == ret.flag ||
			 * eFlag.User_NeedSelectAccount == ret.flag) {
			 * //eFlag_AccountRefresh代表 刷新微信票据成功
			 * Log.d(MainActivity.LOG_TAG,"login success flag:" + ret.flag);
			 * mainActivity.letUserLogin(); } else if (eFlag.User_UrlLogin ==
			 * ret.flag) { // 用拉起的账号登录，登录结果在OnLoginNotify()中回调 } else if
			 * (ret.flag == eFlag.User_NeedSelectAccount) { //
			 * 异账号时，游戏需要弹出提示框让用户选择需要登陆的账号
			 * Log.d(MainActivity.LOG_TAG,"diff account");
			 * mainActivity.showDiffLogin(); } else if (ret.flag ==
			 * eFlag.User_NeedLogin) { // 没有有效的票据，登出游戏让用户重新登录
			 * Log.d(MainActivity.LOG_TAG,"need login");
			 * mainActivity.letUserLogout(); } else {
			 * Log.d(MainActivity.LOG_TAG,"logout");
			 * mainActivity.letUserLogout(); }
			 */
		}

		@Override
		public void OnRelationNotify(UserRelationRet relationRet) {
			Log.d(m_log_tag, "OnRelationNotify: " + relationRet.toString());

			PersonInfo t_person_info = (PersonInfo) relationRet.persons
					.firstElement();

			UnityPlayer.UnitySendMessage("_Dont_Destroy_On_Load",
					"SetNickName", t_person_info.nickName);

			String result = "";

			result = result + "flag:" + relationRet.flag + "\n";
			result = result + "msg:" + relationRet.msg + "\n";
			result = result + "platform:" + relationRet.platform + "\n";

			if (relationRet.persons != null && relationRet.persons.size() > 0) {
				PersonInfo personInfo = (PersonInfo) relationRet.persons
						.firstElement();
				StringBuilder builder = new StringBuilder();
				builder.append("UserInfoResponse json: \n");
				builder.append("nick_name: " + personInfo.nickName + "\n");
				builder.append("open_id: " + personInfo.openId + "\n");
				builder.append("userId: " + personInfo.userId + "\n");
				builder.append("gender: " + personInfo.gender + "\n");
				builder.append("picture_small: " + personInfo.pictureSmall
						+ "\n");
				builder.append("picture_middle: " + personInfo.pictureMiddle
						+ "\n");
				builder.append("picture_large: " + personInfo.pictureLarge
						+ "\n");
				builder.append("provice: " + personInfo.province + "\n");
				builder.append("city: " + personInfo.city + "\n");
				builder.append("country: " + personInfo.country + "\n");
				builder.append("is_yellow_vip: " + personInfo.is_yellow_vip
						+ "\n");
				builder.append("is_yellow_year_vip: "
						+ personInfo.is_yellow_year_vip + "\n");
				builder.append("yellow_vip_level: "
						+ personInfo.yellow_vip_level + "\n");
				builder.append("is_yellow_high_vip: "
						+ personInfo.is_yellow_high_vip + "\n");
				result = result + builder.toString();
			} else {
				result = result + "relationRet.persons is bad";
			}

			Log.d(m_log_tag, "OnRelationNotify" + result);

			// 发送结果到结果展示界面
			// mainActivity.sendResult(result);
		}

		@Override
		public String OnCrashExtMessageNotify() {
			// 此处游戏补充crash时上报的额外信息
			Log.d(m_log_tag, String.format(Locale.CHINA,
					"OnCrashExtMessageNotify called"));

			Date nowTime = new Date();

			SimpleDateFormat time = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

			return "new Upload extra crashing message for bugly on "
					+ time.format(nowTime);
		}

		@Override
		public byte[] OnCrashExtDataNotify() {
			// TODO Auto-generated method stub
			return null;
		}
	}

	private static ePlatform m_login_platform;

	public static void OnQQLogin() {
		Log.d(m_log_tag, "OnQQLogin()");

		m_login_platform = ePlatform.QQ;

		YSDKApi.login(m_login_platform);
	}

	public static void OnWXLogin() {
		Log.d(m_log_tag, "OnWXLogin()");

		m_login_platform = ePlatform.WX;

		YSDKApi.login(m_login_platform);
	}

	public static void OnLogOut() {
		Log.d(m_log_tag, "OnLogOut()");

		YSDKApi.logout();
	}

	// //////////////////////////////////////////////////////////
	// MSC
	// //////////////////////////////////////////////////////////
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
					Integer.toString(error.getErrorCode()));
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
							"8000", resultBuffer);
					/*
					UnityPlayer.UnitySendMessage(
							"_Dont_Destroy_On_Load",
							"MSCResult",
							m_mscTag + "&&&"
									+ Environment.getExternalStorageDirectory()
									+ "/msc/Bamboo/msc_" + m_mscTag + ".wav"
									+ "&&&" + resultBuffer.toString());
									*/
				} catch (Exception e) {
					// TODO Auto-generated catch block
					Log.i("MSCError",  e.getMessage());
					//UnityPlayer.UnitySendMessage("_Dont_Destroy_On_Load",
							//"MSCError", "error" + e.getMessage());
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
			Log.i("MSCError",  e.getMessage());
			//UnityPlayer.UnitySendMessage("_Dont_Destroy_On_Load", "MSCError",
					//"error" + e.getMessage());
			e.printStackTrace();
			return;
		}

		mIatResults.put(sn, text);
	}

	private static String m_src = "";
	private static String m_des = "";
	private static int m_rate = 8000;
	private static StringBuffer m_resultBuffer;

	/*
	public static void ConvertWavToMp3(String p_src, String p_des, String p_rate) {
		if (m_instance == null) {
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
				m_instance.WavToMp3(m_src, m_des, m_rate);
			}
		}.start();
	}
	*/
	
	public static void ConvertWavToMp3(String p_src, String p_des,
			String p_rate, StringBuffer resultBuffer) {
		if (m_instance == null) {
			Log.i("instance is null", "null");
			return;
		}

		{
			m_src = p_src;

			m_des = p_des;

			m_rate = Integer.parseInt(p_rate);

			m_resultBuffer = resultBuffer;
		}

		new Thread() {
			@Override
			public void run() {
				m_instance.WavToMp3(m_src, m_des, m_rate);

				UnityPlayer.UnitySendMessage(
						"_Dont_Destroy_On_Load",
						"MSCResult",
						m_mscTag + "&&&"
								+ Environment.getExternalStorageDirectory()
								+ "/msc/Bamboo/msc_" + m_mscTag + ".mp3"
								+ "&&&" + m_resultBuffer.toString());
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
		mIat = SpeechRecognizer.createRecognizer(m_instance, null);

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
					Integer.toString(ret));
		}
	}
}