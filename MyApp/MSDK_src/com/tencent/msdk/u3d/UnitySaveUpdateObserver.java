package com.tencent.msdk.u3d;

import org.json.JSONException;
import org.json.JSONObject;

import com.tencent.msdk.myapp.autoupdate.WGSaveUpdateObserver;
import com.unity3d.player.UnityPlayer;

public class UnitySaveUpdateObserver extends WGSaveUpdateObserver {
	private String mUnityGameObject;

	public UnitySaveUpdateObserver (String unityGameObject) {
		mUnityGameObject = unityGameObject;
	}
	
	@Override
	public void OnCheckNeedUpdateInfo(long newApkSize, String newFeature,
			long patchSize, int status, String updateDownloadUrl,
			int updateMethod) {
        try{
            JSONObject msg = new JSONObject(); 
            msg.put("newApkSize", newApkSize);
            msg.put("newFeature", newFeature);
            msg.put("patchSize", patchSize);
            msg.put("status", status);
            msg.put("updateDownloadUrl", updateDownloadUrl);
            msg.put("updateMethod", updateMethod);                   
            UnityPlayer.UnitySendMessage(mUnityGameObject, "OnCheckNeedUpdateInfo", msg.toString());
        } catch(JSONException ex) {
            UnityPlayer.UnitySendMessage(mUnityGameObject, "OnCheckNeedUpdateInfo", "Exception:" + ex.getMessage());
        }
	}

	@Override
	public void OnDownloadAppProgressChanged(long receiveDataLen,
			long totalDataLen) {
        try{
            JSONObject msg = new JSONObject(); 
            msg.put("receiveDataLen", receiveDataLen);
            msg.put("totalDataLen", totalDataLen);
            UnityPlayer.UnitySendMessage(mUnityGameObject, "OnDownloadAppProgressChanged", msg.toString());
        } catch(JSONException ex) {
            UnityPlayer.UnitySendMessage(mUnityGameObject, "OnDownloadAppProgressChanged", "Exception:" + ex.getMessage());
        }
	}

	@Override
	public void OnDownloadAppStateChanged(int state, int errorCode,
			String errorMsg) {
        try{
            JSONObject msg = new JSONObject(); 
            msg.put("state", state);
            msg.put("errorCode", errorCode);
            msg.put("errorMsg", errorMsg);
            UnityPlayer.UnitySendMessage(mUnityGameObject, "OnDownloadAppStateChanged", msg.toString());
        } catch(JSONException ex) {
            UnityPlayer.UnitySendMessage(mUnityGameObject, "OnDownloadAppStateChanged", "Exception:" + ex.getMessage());
        }
	}

	@Override
	public void OnDownloadYYBProgressChanged(String url, long receiveDataLen,
			long totalDataLen) {
        try{
            JSONObject msg = new JSONObject(); 
            msg.put("url", url);
            msg.put("receiveDataLen", receiveDataLen);
            msg.put("totalDataLen", totalDataLen);
            UnityPlayer.UnitySendMessage(mUnityGameObject, "OnDownloadYYBProgressChanged", msg.toString());
        } catch(JSONException ex) {
            UnityPlayer.UnitySendMessage(mUnityGameObject, "OnDownloadYYBProgressChanged", "Exception:" + ex.getMessage());
        }
	}

	@Override
	public void OnDownloadYYBStateChanged(String url, int state, int errorCode,
			String errorMsg) {
        try{
            JSONObject msg = new JSONObject(); 
            msg.put("url", url);
            msg.put("state", state);
            msg.put("errorCode", errorCode);
            msg.put("errorMsg", errorMsg);
            UnityPlayer.UnitySendMessage(mUnityGameObject, "OnDownloadYYBStateChanged", msg.toString());
        } catch(JSONException ex) {
            UnityPlayer.UnitySendMessage(mUnityGameObject, "OnDownloadYYBStateChanged", "Exception:" + ex.getMessage());
        }
	}

}
