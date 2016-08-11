package com.tencent.tmgp.qixiongwushuang;

import com.unity3d.player.*;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.tencent.android.tpush.XGIOperateCallback;
import com.tencent.android.tpush.XGLocalMessage;
import com.tencent.android.tpush.XGPushManager;
import com.tencent.bugly.crashreport.CrashReport;
import com.tencent.msdk.WeGame;
import com.tencent.msdk.api.LoginRet;
import com.tencent.msdk.api.MsdkBaseInfo;
import com.tencent.msdk.api.WGPlatform;
import com.tencent.msdk.api.WGQZonePermissions;
import com.tencent.msdk.consts.CallbackFlag;
import com.tencent.msdk.consts.EPlatform;
import com.tencent.msdk.consts.TokenType;
import com.tencent.msdk.tools.Logger;
import com.tencent.msdk.u3d.UnityADObserver;
import com.tencent.msdk.u3d.UnityGroupObserver;
import com.tencent.msdk.u3d.UnityPlatformObserver;
import com.tencent.msdk.u3d.UnitySaveUpdateObserver;
import com.unity3d.player.UnityPlayer;


public class UnityPlayerActivity extends Activity{
	private boolean isFirstLogin = false;
	
	protected UnityPlayer mUnityPlayer; // don't change the name of this variable; referenced from native code

	private static String m_log_tag = "UnityPlayer";
	
	public native void WavToMp3(String wav,String mp3,int rate);
	
	static{
		System.loadLibrary("WavToMp3");
	}
	
	private static UnityPlayerActivity m_instance = null;
	
	private static String m_src = "";
	
	private static String m_des = "";
	
	private static int m_rate = 8000;
	
	public static void ConvertWavToMp3( String p_src, String p_des, String p_rate ){
		Log.i( "So.src", p_src );
		
		Log.i( "So.des", p_des );
		
		Log.i( "So.rate", p_rate );
		
		if( m_instance == null ){
			Log.i( "instance is null", "null" );
			return;
		}
		
		{
			m_src = p_src;
			
			m_des = p_des;
			
			m_rate = Integer.parseInt( p_rate );
		}
		
		new Thread(){
			@Override
			public void run() {
				Log.i( "Thread.Process", "Begin" );
				
				m_instance.WavToMp3( m_src, m_des, m_rate );
				
				Log.i( "Thread.Process", "End" );
				
				UnityPlayer.UnitySendMessage( "_Dont_Destroy_On_Load", "ConvertFinish", m_des );
			}
    	}.start();
	}
	
	// Setup activity layout
	@Override protected void onCreate (Bundle savedInstanceState){
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		
		Log.d( m_log_tag, "OnCreate()" );
		
		// TODO GAME 游戏需自行检测自身是否重复, 检测到是重复的Activity则要把自己finish掉
        // 注意：游戏需要加上去重判断finish重复的实例，否则可能发生重复拉起游戏的问题。游戏可自行决定重复的判定。
        if (WGPlatform.IsDifferentActivity(this)) {
            Logger.d("Warning!Reduplicate game activity was detected.Activity will finish immediately.");
            
            Logger.d("Warning!Reduplicate game activity was detected.Activity will finish immediately.");
            
            Logger.d("Warning!Reduplicate game activity was detected.Activity will finish immediately.");
            
            this.finish();
            
            return;
        }

		getWindow().setFormat(PixelFormat.RGBX_8888); // <--- This makes xperia play happy

		mUnityPlayer = new UnityPlayer(this);
		if (mUnityPlayer.getSettings().getBoolean("hide_status_bar", true))
		{
			getWindow ().setFlags (WindowManager.LayoutParams.FLAG_FULLSCREEN,
			                       WindowManager.LayoutParams.FLAG_FULLSCREEN);
		}

		setContentView(mUnityPlayer);
		mUnityPlayer.requestFocus();
		
		{
			Log.d( m_log_tag, "Before InitMSDK" );
			
			InitMSDK();
		}
		
		{
			Log.d( m_log_tag, "Set Instance" );
			
			m_instance = this;
		}
		
		{
			Log.d( m_log_tag, "Before OnRegisterXG" );
			
			OnRegisterXG();
		}
		
		{
			Log.d( m_log_tag, "OnCreate.Done()" );
		}
	}
	
	    
    // TODO GAME 游戏需要集成此方法并调用WGPlatform.onDestory()
    @Override
    protected void onDestroy() {
    	Log.d( m_log_tag, "onDestroy()" );
    	
    	if (mUnityPlayer != null ) {
            Logger.d("Quit Unity");
            mUnityPlayer.quit();
        }
    	
		super.onDestroy();
		
		{
		    WGPlatform.onDestory(this);
		    Logger.d("onDestroy");
		}
        
        Log.d( m_log_tag, "onDestroy.Done()" );
    }
    	

	 // TODO GAME 游戏需要集成此方法并调用WGPlatform.onRestart()
    @Override
    protected void onRestart() {
    	Log.d( m_log_tag, "onRestart()" );
    	
        super.onRestart();
        WGPlatform.onRestart();
        
        Log.d( m_log_tag, "onRestart.Done()" );
    }

	// Resume Unity
	@Override protected void onResume(){
		Log.d( m_log_tag, "onResume()" );
		
		super.onResume();
		mUnityPlayer.resume();
		
		//解决onResume卡顿问题
        new Handler().post(new Runnable(){  
            public void run() {  
                WGPlatform.onResume(); 
            }  
         }); 
        
        // TODO GAME 模拟游戏自动登录，这里需要游戏添加加载动画
        // WGLogin是一个异步接口, 传入ePlatform_None则调用本地票据验证票据是否有效
        // 如果从未登录过，则会立即在onLoginNotify中返回flag为eFlag_Local_Invalid，此时应该拉起授权界面
        // 建议在此时机调用WGLogin,它应该在handlecallback之后进行调用。
        if(isFirstLogin) {
            isFirstLogin = false;
            startWaiting();
            WGPlatform.WGLogin(EPlatform.ePlatform_None);
        }
        
        Log.d( m_log_tag, "onResume.Done()" );
	}

	// Pause Unity
	@Override protected void onPause(){
		Log.d( m_log_tag, "onPause()" );
		
		super.onPause();
		mUnityPlayer.pause();
		
		WGPlatform.onPause();
		
		Log.d( m_log_tag, "onPause.Done()" );
	}
	
    // TODO GAME 游戏需要集成此方法并调用WGPlatform.onStop()
    @Override
    protected void onStop() {
    	Log.d( m_log_tag, "onStop()" );
    	
        super.onStop();
        WGPlatform.onStop();
        
        Log.d( m_log_tag, "onStop.Done()" );
    }

 // TODO GAME 在onActivityResult中需要调用WGPlatform.onActivityResult
    @Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    	Log.d( m_log_tag, "onActivityResult()" );
    	
		super.onActivityResult(requestCode, resultCode, data);
		
		WGPlatform.onActivityResult(requestCode, resultCode, data);
		Logger.d("onActivityResult");
		
		Log.d( m_log_tag, "onActivityResult.Done()" );
	}
    
	// TODO GAME 在onNewIntent中需要调用handleCallback将平台带来的数据交给MSDK处理
    @Override
    protected void onNewIntent(Intent intent) {
    	Log.d( m_log_tag, "onNewIntent()" );
    	
        Logger.d("onNewIntent");
        super.onNewIntent(intent);

        // TODO GAME 处理游戏被拉起的情况
        // launchActivity的onCreat()和onNewIntent()中必须调用
        // WGPlatform.handleCallback()。否则会造成微信登录无回调
        if (WGPlatform.wakeUpFromHall(intent)) {
            Logger.d("LoginPlatform is Hall");
            Logger.d(intent);
        } else {
            Logger.d("LoginPlatform is not Hall");
            Logger.d(intent);
            WGPlatform.handleCallback(intent);
        }
        
        Log.d( m_log_tag, "onNewIntent.Done()" );
    }
 
    public void startWaiting() {
    	Log.d( m_log_tag, "startWaiting()" );
    	
        Logger.d("startWaiting");
        //mAutoLoginWaitingDlg = new ProgressDialog(this);
        //stopWaiting();
        //mAutoLoginWaitingDlg.setTitle("自动登录中...");
        //mAutoLoginWaitingDlg.show();
    }

    public void stopWaiting() {
    	Log.d( m_log_tag, "stopWaiting()" );
    	
        Logger.d("stopWaiting");
        //if (mAutoLoginWaitingDlg != null && mAutoLoginWaitingDlg.isShowing()) {
        //    mAutoLoginWaitingDlg.dismiss();
        //}
    }    
    
    /**
     * 拉起支付Demo,需要先安装支付示例工程AndroidPaySample(位于MSDKzip包中的Tencent AndroidPayRelease.zip中)
     * 才能拉起支付Demo
     */
    public void launchPayDemo() {
    	Log.d( m_log_tag, "launchPayDemo()" );
    	
        Logger.d("called");
        Intent i = new Intent("com.tencent.pay.sdksample.AndroidPaySample");
        LoginRet lr = new LoginRet();
        WGPlatform.WGGetLoginRecord(lr);
        
        // 注意：这里需要判断登录态是否有效
        if (lr.flag != CallbackFlag.eFlag_Succ) {
            if (lr.platform == EPlatform.ePlatform_Weixin.val()) {
                // accesstoken过期，尝试刷新票据
                if (lr.flag == CallbackFlag.eFlag_WX_AccessTokenExpired) {
                    WGPlatform.WGRefreshWXToken();
                    return;
                } else {
                    // 微信登录态失效，引导用户重新登录授权
                    return;
                }
            } else {
                // 手Q登录态失效，引导用户重新登录授权
                return;
            } 
        }
        
        i.putExtra("userId", lr.open_id);
        i.putExtra("offerId", WeGame.getInstance().offerId);
        if (lr.platform == WeGame.WXPLATID) {
            i.putExtra("userKey", lr.getTokenByType(TokenType.eToken_WX_Access));
            i.putExtra("sessionType", "wc_actoken");
            i.putExtra("sessionId", "hy_gameid");
        } else if (lr.platform == WeGame.QQPLATID) {
            i.putExtra("userKey", lr.getTokenByType(TokenType.eToken_QQ_Pay));
            i.putExtra("sessionType", "kp_actoken");
            i.putExtra("sessionId", "openid");
        }

        i.putExtra("pf", WGPlatform.WGGetPf(""));
        i.putExtra("zoneId", "1");
        i.putExtra("pfKey", WGPlatform.WGGetPfKey());
        i.putExtra("acctType", "common");
        i.putExtra("saveValue", "60");
        i.putExtra("msdk", true);
        startActivity(i);
        
        
        Log.d( m_log_tag, "launchPayDemo.Done()" );
    }
    
    /**
     * 游戏崩溃后会将堆栈信息上报到腾讯组件————灯塔中。这时制造native层崩溃测试异常上报
     */
    public static void nativeCrashTest() {
    	Log.d( m_log_tag, "nativeCrashTest()" );
    	
        // Native异常测试
        Logger.d("called");

        CrashReport.testNativeCrash();
        
        Log.d( m_log_tag, "nativeCrashTest.Done()" );
    }
    
    /**
     * 游戏崩溃后会将堆栈信息上报到腾讯组件————灯塔中。这时制造空指针异常测试异常上报
     */
    public static void nullPointerExceptionTest() {
    	Log.d( m_log_tag, "nullPointerExceptionTest()" );
    	
        // 空指针异常测试
        Logger.d("called");
        String aa = null;
        if (aa.equals("aa")) {
        }
        
        Log.d( m_log_tag, "nullPointerExceptionTest.Done()" );
    }

	private void InitMSDK(){
		Log.d( m_log_tag, "InitMSDK()" );
		
		MsdkBaseInfo baseInfo = new MsdkBaseInfo();
		
		{
			// TODO GAME 初始化MSDK
	        /***********************************************************
	         *  TODO GAME 接入必须要看， baseInfo值因游戏而异，填写请注意以下说明:      
	         *  	baseInfo值游戏填写错误将导致 QQ、微信的分享，登录失败 ，切记 ！！！     
	         * 		只接单一平台的游戏请勿随意填写其余平台的信息，否则会导致公告获取失败  
	         *      offerId 为必填，一般为手QAppId
	         ***********************************************************/
			/** Origin
			baseInfo.qqAppId = "100703379";
	        baseInfo.qqAppKey = "4578e54fb3a1bd18e0681bc1c734514e";
	        baseInfo.wxAppId = "wxcde873f99466f74a";
	        baseInfo.msdkKey = "5d1467a4d2866771c3b289965db335f4";
	        //订阅型测试用offerId
	        baseInfo.offerId = "100703379"; 
	        */
	       
			//微信 Appkey:ce804c29c3f6aeddd9d3fe3849372452
			
			baseInfo.qqAppId = "1104965145";
	        baseInfo.qqAppKey = "r2krevcrdBtBsyga";
	        baseInfo.wxAppId = "wxbbd67fda7f55e12d";
	        baseInfo.msdkKey = "b6864e190319c71922d571337b8d6c7d";
	        //订阅型测试用offerId
	        baseInfo.offerId = "1104965145"; 
			
			 // TODO GAME 自2.7.1a开始游戏可在初始化msdk时动态设置版本号，灯塔和bugly的版本号由msdk统一设置
	        // 1、版本号组成 = versionName + versionCode
	        // 2、游戏如果不赋值给appVersionName（或者填为""）和appVersionCode(或者填为-1)，
	        // msdk默认读取AndroidManifest.xml中android:versionCode="51"及android:versionName="2.7.1"
	        // 3、游戏如果在此传入了appVersionName（非空）和appVersionCode（正整数）如下，则灯塔和bugly上获取的版本号为2.7.1.271
	        baseInfo.appVersionName = "1.1.0";
	        baseInfo.appVersionCode = 1;
		}
		
		Log.d( m_log_tag, "InitMSDK.Basic.Info.Done()" );
		
		{
			// 注意：传入Initialized的activity即this，在游戏运行期间不能被销毁，否则会产生Crash
	        WGPlatform.Initialized(this, baseInfo);
	        // 设置拉起QQ时候需要用户授权的项
	        WGPlatform.WGSetPermission(WGQZonePermissions.eOPEN_ALL); 
	        
	        // TODO GAME 此处填写游戏需要回调的脚本绑定的GameObject的名称
	        String ObserverGameObject = "_Dont_Destroy_On_Load";
	        // 全局回调类
	        WGPlatform.WGSetObserver(new UnityPlatformObserver(ObserverGameObject));   
	        // 应用宝更新回调类
	        WGPlatform.WGSetSaveUpdateObserver(new UnitySaveUpdateObserver(ObserverGameObject));
	        // 广告的回调设置
	        WGPlatform.WGSetADObserver(new UnityADObserver(ObserverGameObject));
	        //QQ 加群加好友回调
	        WGPlatform.WGSetGroupObserver(new UnityGroupObserver(ObserverGameObject));
		}
		
		Log.d( m_log_tag, "InitMSDK.Basic.Init.Done()" );
		
		{
			// TODO GAME 处理游戏被拉起的情况
	        // launchActivity的onCreat()和onNewIntent()中必须调用
	        // WGPlatform.handleCallback()。否则会造成微信登录无回调
	        if (WGPlatform.wakeUpFromHall(this.getIntent())) {
	        	Log.d( m_log_tag, "UnityMSDK" + " hall found." );
	        	
	        	// 拉起平台为大厅 
	        	Logger.d("LoginPlatform is Hall");
	            Logger.d(this.getIntent());
	        } else {  
	        	Log.d( m_log_tag, "UnityMSDK" + " not hall." );
	        	
	        	// 拉起平台不是大厅
	            Logger.d("LoginPlatform is not Hall");
	            Logger.d(this.getIntent());
	            WGPlatform.handleCallback(this.getIntent());
	        }
	        
	        isFirstLogin = true;
		}
		
		Log.d( m_log_tag, "InitMSDK.Done()" );
	}
	
	public static void OnLocalPush( String p_key, int p_sec_since_now, String p_push_content ){
		Log.d( m_log_tag, "OnLocalPush()" );
		
		//{
		//	UnityPlayer.UnitySendMessage( "_Dont_Destroy_On_Load", "JavaInvoke", "JavaString" );
		//}
		
		//Log.d( "Push", "p_key: " + p_key );
		
		//Log.d( "Push", "p_sec_since_now: " + p_sec_since_now );
		
		//Log.d( "Push", "p_push_content: " + p_push_content );
		
		if( m_instance == null ){
			Log.e( "Push", "Activity instance = null." );
			return;
		}
		
		XGLocalMessage local_msg = new XGLocalMessage();
		
		local_msg.setType( 1 );
		
		
		local_msg.setTitle( "七雄无双" );
		
		local_msg.setContent( p_push_content );
		
		{
			Date t_target_time = new Date();
			
			t_target_time.setTime( t_target_time.getTime() + p_sec_since_now * 1000 );
			
			{
				SimpleDateFormat t_df = new SimpleDateFormat( "yyyyMMdd" );
				
				String t_target_date = t_df.format( t_target_time );
				
				Log.d( "LocalPush Date", t_target_date );
				
				local_msg.setDate( t_target_date );
			}
			
			{
				SimpleDateFormat t_df = new SimpleDateFormat( "HH" );
				
				String t_target_h = t_df.format( t_target_time );
				
				Log.d( "LocalPush Hour", t_target_h );
				
				local_msg.setHour( t_target_h );
			}
			
			{
				SimpleDateFormat t_df = new SimpleDateFormat( "mm" );
				
				String t_target_m = t_df.format( t_target_time );
				
				Log.d( "LocalPush Min", t_target_m );
				
				local_msg.setMin( t_target_m );
			}
		}
		
		Context context = m_instance.getApplicationContext();
		
		XGPushManager.addLocalNotification(context, local_msg);
		
		Log.d( m_log_tag, "OnLocalPush.Done()" );
	}
	
	protected void OnRegisterXG(){
		Log.d( m_log_tag, "UnityXG.OnRegisterXG()" );
		
		Context context = getApplicationContext();
		
		//XGPushManager.registerPush(context);    
		
		XGPushManager.registerPush(context, new XGIOperateCallback() {
			
			@Override
			public void onSuccess(Object data, int flag) {
				Log.d( "XG Token", "PushXinge register success !  token is " + data);
				
				{
					UnityPlayer.UnitySendMessage( "_Dont_Destroy_On_Load", "XGSetToken", "" + data );
				}
			}
			
			@Override
			public void onFail(Object data, int errCode, String msg) {
				Log.d( "XG Token", "PushXinge register fail !  code:" + errCode + ",msg:" + msg );
				
				{
					UnityPlayer.UnitySendMessage( "_Dont_Destroy_On_Load", "XGSetToken", "" );
				}
			}
		});
		
		Log.d( m_log_tag, "UnityXG.OnRegisterXG.Done()" );
	}
	
	// This ensures the layout will be correct.
	@Override public void onConfigurationChanged(Configuration newConfig)
	{
		super.onConfigurationChanged(newConfig);
		mUnityPlayer.configurationChanged(newConfig);
	}

	// Notify Unity of the focus change.
	@Override public void onWindowFocusChanged(boolean hasFocus)
	{
		super.onWindowFocusChanged(hasFocus);
		mUnityPlayer.windowFocusChanged(hasFocus);
	}

	// For some reason the multiple keyevent type is not supported by the ndk.
	// Force event injection by overriding dispatchKeyEvent().
	@Override public boolean dispatchKeyEvent(KeyEvent event)
	{
		if (event.getAction() == KeyEvent.ACTION_MULTIPLE)
			return mUnityPlayer.injectEvent(event);
		return super.dispatchKeyEvent(event);
	}

	// Pass any events not handled by (unfocused) views straight to UnityPlayer
	@Override public boolean onKeyUp(int keyCode, KeyEvent event)     { return mUnityPlayer.injectEvent(event); }
	@Override public boolean onKeyDown(int keyCode, KeyEvent event)   { return mUnityPlayer.injectEvent(event); }
	@Override public boolean onTouchEvent(MotionEvent event)          { return mUnityPlayer.injectEvent(event); }
	/*API12*/ public boolean onGenericMotionEvent(MotionEvent event)  { return mUnityPlayer.injectEvent(event); }
}
