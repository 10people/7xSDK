package com.tencent.msdk.u3d;

import org.json.JSONException;
import org.json.JSONObject;

import com.tencent.msdk.api.ADRet;
import com.tencent.msdk.api.WGADObserver;
import com.unity3d.player.UnityPlayer;

public class UnityADObserver implements WGADObserver {
	private String mUnityGameObject;

	public UnityADObserver (String unityGameObject) {
		mUnityGameObject = unityGameObject;
	}
	
	@Override
	public void OnADNotify(ADRet ret) {
        try{
            JSONObject msg = new JSONObject(); 
            msg.put("_viewTag", ret.viewTag);
            msg.put("_scene", ret.scene.val());
            UnityPlayer.UnitySendMessage(mUnityGameObject, "OnADNotify", msg.toString());
        } catch(JSONException ex) {
            UnityPlayer.UnitySendMessage(mUnityGameObject, "OnADNotify", "Exception:" + ex.getMessage());
        }
	}

	@Override
	public void OnADBackPressedNotify(ADRet ret) {
        try{
            JSONObject msg = new JSONObject(); 
            msg.put("_viewTag", ret.viewTag);
            msg.put("_scene", ret.scene.val());
            UnityPlayer.UnitySendMessage(mUnityGameObject, "OnADBackPressedNotify", msg.toString());
        } catch(JSONException ex) {
            UnityPlayer.UnitySendMessage(mUnityGameObject, "OnADBackPressedNotify", "Exception:" + ex.getMessage());
        }
	}

}
