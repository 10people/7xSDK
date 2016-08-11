package com.tencent.msdk.u3d;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.tencent.msdk.api.LoginRet;
import com.tencent.msdk.api.TokenRet;
import com.tencent.msdk.api.WGPlatform;
import com.tencent.msdk.api.eADType;
import com.tencent.msdk.api.eQQScene;
import com.tencent.msdk.api.eWechatScene;
import com.tencent.msdk.consts.EPlatform;
import com.tencent.msdk.notice.NoticeInfo;
import com.tencent.msdk.notice.NoticePic;
import com.tencent.msdk.qq.ApiName;

public class UnityWGPlatform {
	public static void Login(int platform) {
		WGPlatform.WGLogin(EPlatform.getEnum(platform));
	}
	
	public static String WGGetLoginRecord() {
		LoginRet ret = new LoginRet();
		int platform = WGPlatform.WGGetLoginRecord(ret);
		
        try{
            JSONObject msg = new JSONObject(); 
            msg.put("_flag", ret.flag);
            msg.put("_desc", ret.desc);
            msg.put("_platform", ret.platform);  
            msg.put("_open_id", ret.open_id);
            msg.put("_user_id", ret.user_id);
            msg.put("_pf", ret.pf);
            msg.put("_pf_key", ret.pf_key);   
            JSONArray tokenArray = new JSONArray();
            for(TokenRet token : ret.token) {
                JSONObject tk = new JSONObject();
                tk.put("_type", token.type);
                tk.put("_value", token.value);
                // 如果传的是long，在C#端用LitJson解析会认为是int类型
                tk.put("_expiration", String.valueOf(token.expiration));
                tokenArray.put(tk);
            }
            msg.put("_token", tokenArray);
            
			String result = msg.toString();
			return result;
        } catch(JSONException ex) {
			Log.e("MSDK", ex.getMessage());
			return null;
        }
	}
	// --------- QQ相关API begin ---------

	public static void LoginQQ() {
		WGPlatform.WGLogin(EPlatform.ePlatform_QQ);
	}

	public static boolean QueryMyQQInfo() {
		return WGPlatform.WGQueryQQMyInfo();
	}

	public static boolean QueryQQGameFriendsInfo() {
		return WGPlatform.WGQueryQQGameFriendsInfo();
	}

	/**
	 * 结构化消息分享到QQ空间
	 */
	public static void SendToQQ_Qzone(String title, String desc, String url,
			String imgUrl, int imgUrlLen) {
		WGPlatform.WGSendToQQ(eQQScene.QQScene_QZone, title, desc, url, imgUrl,
				imgUrl.length());
	}

	/**
	 * 结构化消息分享到QQ会话
	 */
	public static void SendToQQ_Session(String title, String desc, String url,
			String imgUrl, int imgUrlLen) {
		WGPlatform.WGSendToQQ(eQQScene.QQScene_Session, title, desc, url,
				imgUrl, imgUrl.length());
	}

	/**
	 * 把音乐消息分享到手Q会话
	 */
	public static void SendToQQWithMusic_Session(String title, String desc,
			String musicUrl, String musicDataUrl, String imgUrl) {
		WGPlatform.WGSendToQQWithMusic(eQQScene.QQScene_Session, title, desc,
				musicUrl, musicDataUrl, imgUrl);
	}

	public static void SendToQQWithMusic_Qzone(String title, String desc,
			String musicUrl, String musicDataUrl, String imgUrl) {
		WGPlatform.WGSendToQQWithMusic(eQQScene.QQScene_QZone, title, desc,
				musicUrl, musicDataUrl, imgUrl);
	}

	/**
	 * 后端分享
	 */
	public static boolean SendToQQGameFriend(int act, String friendOpenId,
			String title, String summary, String targetUrl, String imageUrl,
			String previewText, String gameTag) {
		return WGPlatform.WGSendToQQGameFriend(act, friendOpenId, title,
				summary, targetUrl, imageUrl, previewText, gameTag);
	}

	/**
	 * 纯图分享到手Q好友
	 */
	public static void SendToQQWithPhoto_Session(String imgFilePath) {
		WGPlatform.WGSendToQQWithPhoto(eQQScene.QQScene_Session, imgFilePath);
	}

	public static void SendToQQWithPhoto_Qzone(String imgFilePath) {
		WGPlatform.WGSendToQQWithPhoto(eQQScene.QQScene_QZone, imgFilePath);
	}

	/**
	 * 绑定QQ群
	 */
	public static void BindQQGroup(final String unionid,
			final String union_name, final String zoneid, final String signature) {
		WGPlatform.WGBindQQGroup(unionid, union_name, zoneid, signature);
	}

	public static void JoinQQGroup(final String qqGroupKey) {
		WGPlatform.WGJoinQQGroup(qqGroupKey);
	}
	
	public static void WGUnbindQQGroup(final String groupOpenid, final String unionid) {
		WGPlatform.WGUnbindQQGroup(groupOpenid, unionid);
	}
	
    public static void WGQueryQQGroupInfo(final String unionid, final String zoneid){
    	WGPlatform.WGQueryQQGroupInfo(unionid, zoneid);
    }
    
    public static void WGQueryQQGroupKey(final String groupOpenid){
    	WGPlatform.WGQueryQQGroupKey(groupOpenid);
    }

	/**
	 * 添加QQ好友
	 */
	public static void AddGameFriendToQQ(final String fopenid,
			final String desc, final String message) {
		WGPlatform.WGAddGameFriendToQQ(fopenid, desc, message);
	}

	// --------- QQ相关API end ------------

	// --------- 微信相关API start ----------

	public static void LoginWX() {
		WGPlatform.WGLogin(EPlatform.ePlatform_Weixin);
	}

	public static boolean QueryMyWXInfo() {
		return WGPlatform.WGQueryWXMyInfo();
	}

	public static boolean QueryWXGameFriendsInfo() {
		return WGPlatform.WGQueryWXGameFriendsInfo();
	}

	public static void SendToWeixin_Session(String title, String desc,
			String mediaTagName, byte[] thumbData, int thumbDataLen,
			String messageExt) {
		WGPlatform.WGSendToWeixin(title, desc, mediaTagName, thumbData,
				thumbDataLen, messageExt);
	}

	public static void SendToWeixinWithPhoto_Session(String mediaTagName,
			byte[] imgData, int imgDataLen, String messageExt,
			String mediaAction) {
		WGPlatform.WGSendToWeixinWithPhoto(eWechatScene.WechatScene_Session,
				mediaTagName, imgData, imgData.length, messageExt, mediaAction);
	}

	public static void SendToWeixinWithPhoto_Moment(String mediaTagName,
			byte[] imgData, int imgDataLen, String messageExt,
			String mediaAction) {
		WGPlatform.WGSendToWeixinWithPhoto(eWechatScene.WechatScene_Timeline,
				mediaTagName, imgData, imgData.length, messageExt, mediaAction);
	}

	public static void SendToWeixinWithMusic_Session(String title, String desc,
			String musicUrl, String musicDataUrl, String mediaTagName,
			byte[] imgData, int imgDataLen, String messageExt,
			String messageAction) {
		WGPlatform.WGSendToWeixinWithMusic(eWechatScene.WechatScene_Session,
				title, desc, musicUrl, musicDataUrl, mediaTagName, imgData,
				imgData.length, messageExt, messageAction);
	}

	public static void SendToWeixinWithMusic_Moment(String title, String desc,
			String musicUrl, String musicDataUrl, String mediaTagName,
			byte[] imgData, int imgDataLen, String messageExt,
			String messageAction) {
		WGPlatform.WGSendToWeixinWithMusic(eWechatScene.WechatScene_Timeline,
				title, desc, musicUrl, musicDataUrl, mediaTagName, imgData,
				imgData.length, messageExt, messageAction);
	}

	public static boolean SendToWXGameFriend(String fopenid, String title,
			String description, String messageExt, String mediaTagName,
			String thumbMediaId, String msdkExtInfo) {
		return WGPlatform.WGSendToWXGameFriend(fopenid, title, description,
				messageExt, mediaTagName, thumbMediaId, msdkExtInfo);
	}
	
	public static void WGSendToWeixinWithPhotoPath(int scene, String mediaTagName,
            String imgPath, String messageExt, String mediaAction) {
		WGPlatform.WGSendToWeixinWithPhotoPath(eWechatScene.getEnum(scene), mediaTagName, imgPath, messageExt, mediaAction);
	}

	public static void RefreshWXToken() {
		WGPlatform.WGRefreshWXToken();
	}
	
    public static void WGOpenWeiXinDeeplink(String link) {
    	WGPlatform.WGOpenWeiXinDeeplink(link);
    }   

    public static void WGAddCardToWXCardPackage(String cardId, String timestamp, String sign){
    	WGPlatform.WGAddCardToWXCardPackage(cardId, timestamp, sign);
    }

	// --------- 微信相关API end ----------

	/**
	 * 展示公告
	 * 
	 * @param scene
	 */
	public static void ShowNotice(String scene) {
		WGPlatform.WGShowNotice(scene);
	}

	public static void HideScrollNotice() {
		WGPlatform.WGHideScrollNotice();
	}

	/**
	 * 获取公告数据,返回json字符串给调用的C#接口
	 * 
	 * @param scene
	 * @return
	 */
	public static String GetNoticeData(String scene) {
		Vector<NoticeInfo> noticeInfos = WGPlatform.WGGetNoticeData(scene);
		try {
			JSONObject msg = new JSONObject();
			JSONArray noticeArray = new JSONArray();
			for (NoticeInfo notice : noticeInfos) {
				JSONArray noticePicArray = new JSONArray();
				JSONObject item = new JSONObject();
				item.put("_msg_id", notice.mNoticeId);
				//item.put(JsonKeyConst.APP_ID, notice.mAppId);
				item.put("_open_id", notice.mOpenId);
				item.put("_msg_url", notice.mNoticeUrl);
				item.put("_msg_type", notice.mNoticeType.val());
				item.put("_msg_scene", notice.mNoticeScene);
				item.put("_start_time", notice.mNoticeStartTime);
				item.put("_end_time", notice.mNoticeEndTime);
				item.put("_update_time", notice.mNoticeUpdateTime);
				item.put("_content_type", notice.mNoticeContentType.val());
				item.put("_msg_title", notice.mNoticeTitle);
				item.put("_msg_content", notice.mNoticeContent);
				item.put("_content_url", notice.mNoticeContentWebUrl);
				for (NoticePic pic : notice.mNoticePics) {
					JSONObject subItem = new JSONObject();
					//subItem.put(JsonKeyConst.NOTICE_ID, pic.mNoticeId);
					subItem.put("_picPath", pic.mPicUrl);
					subItem.put("_hashValue", pic.mPicHash);
					subItem.put("_screenDir", pic.mScreenDir.val());
					// item.accumulate(JsonKeyConst.NOTICE_PIC_LIST, subItem);
					noticePicArray.put(subItem);
				}
				item.put("_picArray", noticePicArray);
				noticeArray.put(item);
			}
			msg.put("_notice_list", noticeArray);
			String result = msg.toString();
			Log.d("MSDK", "len:" + result.length() + " json:" + result);
			return result;
		} catch (JSONException ex) {
			Log.e("MSDK", ex.getMessage());
			return null;
		}
	}

	public static void OpenUrl(String url) {
		WGPlatform.WGOpenUrl(url);
	}

	public static void ReportEvent(String name, String jsonParams,
			boolean isRealTime) {
		HashMap<String, String> params = new HashMap<String, String>();
		try {
			JSONObject jsonObj = new JSONObject(jsonParams);
			@SuppressWarnings("rawtypes")
			Iterator it = jsonObj.keys();
			while (it.hasNext()) {
				String key = (String) it.next();
				String value = jsonObj.getString(key);
				params.put(key, value);
			}

		} catch (JSONException e) {
			Log.e("MSDK", e.getMessage());
		}

		WGPlatform.WGReportEvent(name, params, true);
	}

	public static void GetNearbyPersonInfo() {
		WGPlatform.WGGetNearbyPersonInfo();
	}

	public static boolean GetLocationInfo() {
		return WGPlatform.WGGetLocationInfo();
	}

	public static boolean CleanLocation() {
		return WGPlatform.WGCleanLocation();
	}

	/**
	 * 通过外部拉起的URL登陆。该接口用于异帐号场景发生时，用户选择使用外部拉起帐号时调用。 登陆成功后通过onLoginNotify回调
	 * 
	 * @param switchToLaunchUser
	 *            为true时表示用户需要切换到拉起帐号，此时该接口会使用上一次保存的拉起帐号登陆数据登陆。
	 *            登陆成功后通过onLoginNotify回调；
	 *            如果没有票据，或票据无效函数将会返回NO，不会发生onLoginNotify回调。
	 *            为false时表示用户继续使用原帐号，此时删除保存的拉起帐号数据，避免产生混淆。
	 * @return 如果没有票据，或票据无效将会返回false；其它情况返回true
	 */
	public static boolean SwitchUser(boolean switchToLaunchUser) {
		return WGPlatform.WGSwitchUser(switchToLaunchUser);
	}

	public static boolean Logout() {
		return WGPlatform.WGLogout();
	}

	public static void EnableCrashReport(boolean bRdmEnable, boolean bMtaEnable) {
		WGPlatform.WGEnableCrashReport(bRdmEnable, bMtaEnable);
	}

	/* 工具接口 start */

	public static boolean IsPlatformInstalled(int platform) {
		return WGPlatform.WGIsPlatformInstalled(EPlatform.getEnum(platform));
	}

	public static String GetPlatformAPPVersion(int platform) {
		return WGPlatform.WGGetPlatformAPPVersion(EPlatform.getEnum(platform));
	}

	public static boolean CheckApiSupport(int api) {
		return WGPlatform.WGCheckApiSupport(ApiName.getEnum(api));
	}

	public static String GetVersion() {
		return WGPlatform.WGGetVersion();
	}

	public static String GetChannelId() {
		return WGPlatform.WGGetChannelId();
	}

	public static String GetRegisterChannelId() {
		return WGPlatform.WGGetRegisterChannelId();
	}

	/* 工具接口 end */

	public static String GetPf(String gameCustomInfo) {
		return WGPlatform.WGGetPf(gameCustomInfo);
	}

	public static String GetPfKey() {
		return WGPlatform.WGGetPfKey();
	}

	public static void LoginWithLocalInfo() {
		WGPlatform.WGLoginWithLocalInfo();
	}

	public static boolean OpenAmsCenter(String params) {
		return WGPlatform.WGOpenAmsCenter(params);
	}

	public static boolean Feedback(String game, String txt) {
		return WGPlatform.WGFeedback(game, txt);
	}

	public static void Feedback(String body) {
		WGPlatform.WGFeedback(body);
	}

	/* 应用宝 start */
	/**
	 * 检查应用宝是否安装
	 */
	public static int CheckYYBInstalled() {
		return WGPlatform.WGCheckYYBInstalled();
	}

	/**
	 * 查询结果回调到由SetSaveUpdateObserver接口设置的回调对象的OnCheckNeedUpdateInfo方法
	 */
	public static void CheckNeedUpdate() {
		WGPlatform.WGCheckNeedUpdate();
	}

	/*
	 * 如果手机上已经安装应用宝则此接口会根据参数选择是否拉起应用宝
	 * 下载进度和状态变化会通过OnDownloadAppProgressChanged和OnDownloadAppStateChanged回调给游戏
	 * 
	 * @para
	 * isUseYYB:是否拉起应用宝更新游戏.如果选false，会直接在游戏内完成更新;如果选true,会拉起应用宝更新(未安装则会先下载应用宝);
	 */
	public static void StartSaveUpdate(boolean isUseYYB) {
		WGPlatform.WGStartSaveUpdate(isUseYYB);
	}

	public static void ShowAD(int adType) {
		WGPlatform.WGShowAD(eADType.getEnum(adType));
	}
}
