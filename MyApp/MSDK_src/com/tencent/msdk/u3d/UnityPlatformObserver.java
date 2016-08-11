package com.tencent.msdk.u3d;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.tencent.msdk.api.CardRet;
import com.tencent.msdk.api.KVPair;
import com.tencent.msdk.api.LocationRet;
import com.tencent.msdk.api.LoginRet;
import com.tencent.msdk.api.ShareRet;
import com.tencent.msdk.api.TokenRet;
import com.tencent.msdk.api.WGPlatformObserver;
import com.tencent.msdk.api.WakeupRet;
import com.tencent.msdk.remote.api.PersonInfo;
import com.tencent.msdk.remote.api.RelationRet;
import com.unity3d.player.UnityPlayer;

public class UnityPlatformObserver implements WGPlatformObserver {
	private String mUnityGameObject;

	public UnityPlatformObserver (String unityGameObject) {
		mUnityGameObject = unityGameObject;
	}

	@Override
	public void OnLoginNotify(LoginRet ret) {
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
            UnityPlayer.UnitySendMessage(mUnityGameObject, "OnLoginNotify", msg.toString());
        } catch(JSONException ex) {
            UnityPlayer.UnitySendMessage(mUnityGameObject, "OnLoginNotify", "Exception:" + ex.getMessage());
        }
	}

	@Override
	public void OnShareNotify(ShareRet ret) {
        try{
            JSONObject msg = new JSONObject(); 
            msg.put("_flag", ret.flag);
            msg.put("_desc", ret.desc);
            msg.put("_platform", ret.platform);
            msg.put("_extInfo", ret.extInfo);

            UnityPlayer.UnitySendMessage(mUnityGameObject, "OnShareNotify", msg.toString());
        } catch(JSONException ex) {
            UnityPlayer.UnitySendMessage(mUnityGameObject, "OnShareNotify", "Exception:" + ex.getMessage());
        }
	}

	@Override
	public void OnWakeupNotify(WakeupRet ret) {
        try{
            JSONObject msg = new JSONObject(); 
            msg.put("_flag", ret.flag);
            msg.put("_desc", ret.desc);
            msg.put("_platform", ret.platform);
            msg.put("_open_id", ret.open_id);
            msg.put("_media_tag_name", ret.media_tag_name);
            msg.put("_messageExt", ret.messageExt);
            msg.put("_lang", ret.lang);
            msg.put("_country", ret.country);
            JSONArray extInfoArray = new JSONArray();
            for(KVPair pair : ret.extInfo) {
                JSONObject extInfo = new JSONObject();
                extInfo.put("_key", pair.key);
                extInfo.put("_value", pair.value);                       
                extInfoArray.put(extInfo);
            }
            msg.put("_extInfo", extInfoArray);
            UnityPlayer.UnitySendMessage(mUnityGameObject, "OnWakeupNotify", msg.toString());
        } catch(JSONException ex) {
            UnityPlayer.UnitySendMessage(mUnityGameObject, "OnWakeupNotify", "Exception:" + ex.getMessage());
        }
	}

	@Override
	public void OnAddWXCardNotify(CardRet ret) {
        try{
            JSONObject msg = new JSONObject(); 
            msg.put("_flag", ret.flag);
            msg.put("_desc", ret.desc);
            msg.put("_platform", ret.platform);
            msg.put("_open_id", ret.open_id);
            msg.put("_wx_card_list", ret.wx_card_list);
            JSONArray extInfoArray = new JSONArray();
            for(KVPair pair : ret.extInfo) {
                JSONObject extInfo = new JSONObject();
                extInfo.put("_key", pair.key);
                extInfo.put("_value", pair.value);                       
                extInfoArray.put(extInfo);
            }
            msg.put("_extInfo", extInfoArray);
            UnityPlayer.UnitySendMessage(mUnityGameObject, "OnAddWXCardNotify", msg.toString());
        } catch(JSONException ex) {
            UnityPlayer.UnitySendMessage(mUnityGameObject, "OnAddWXCardNotify", "Exception:" + ex.getMessage());
        }
	}

	@Override
	public void OnRelationNotify(RelationRet ret) {
        try{
            JSONObject msg = new JSONObject(); 
            msg.put("_flag", ret.flag);
            msg.put("_desc", ret.desc);
            msg.put("_platform", ret.platform);     
            JSONArray personsArray = new JSONArray();
            for(PersonInfo item : ret.persons) {
                JSONObject person = new JSONObject();
                person.put("_nickName", item.nickName);
                person.put("_openId", item.openId);
                person.put("_gender", item.gender);
                person.put("_pictureSmall", item.pictureSmall);
                person.put("_pictureMiddle", item.pictureMiddle);
                person.put("_pictureLarge", item.pictureLarge);
                person.put("_province", item.province);
                person.put("_city", item.city);
                person.put("_gpsCity", item.gpsCity);
                person.put("_distance", String.valueOf(item.distance));
                person.put("_isFriend", item.isFriend);
                // 如果传的是long，在C#端用LitJson解析会认为是int类型
                person.put("_timestamp", String.valueOf(item.timestamp));
              //  person.put("Timestamp", item.timestamp);
                person.put("_lang", item.lang);
                person.put("_country", item.country);                        
                personsArray.put(person);
            }
            msg.put("_persons", personsArray);

            UnityPlayer.UnitySendMessage(mUnityGameObject, "OnRelationNotify", msg.toString());
        } catch(JSONException ex) {
            UnityPlayer.UnitySendMessage(mUnityGameObject, "OnRelationNotify", "Exception:" + ex.getMessage());
        }
	}

	@Override
	public void OnLocationNotify(RelationRet ret) {
        try{
            JSONObject msg = new JSONObject(); 
            msg.put("_flag", ret.flag);
            msg.put("_desc", ret.desc);
            msg.put("_platform", ret.platform);  
            JSONArray personsArray = new JSONArray();
            for(PersonInfo item : ret.persons) {
                JSONObject person = new JSONObject();
                person.put("_nickName", item.nickName);
                person.put("_openId", item.openId);
                person.put("_gender", item.gender);
                person.put("_pictureSmall", item.pictureSmall);
                person.put("_pictureMiddle", item.pictureMiddle);
                person.put("_pictureLarge", item.pictureLarge);
                person.put("_province", item.province);
                person.put("_city", item.city);
                person.put("_gpsCity", item.gpsCity);
                person.put("_distance", String.valueOf(item.distance));
                person.put("_isFriend", item.isFriend);
                // 如果传的是long，在C#端用LitJson解析会认为是int类型
                person.put("_timestamp", String.valueOf(item.timestamp));
               // person.put("Timestamp", item.timestamp);
                person.put("_lang", item.lang);
                person.put("_country", item.country);
                personsArray.put(person);
            }
            msg.put("_persons", personsArray);
            UnityPlayer.UnitySendMessage(mUnityGameObject, "OnLocationNotify", msg.toString());
        } catch(JSONException ex) {
            UnityPlayer.UnitySendMessage(mUnityGameObject, "OnLocationNotify", "Exception:" + ex.getMessage());
        }
	}

	@Override
	public void OnLocationGotNotify(LocationRet ret) {
	    try{
            JSONObject msg = new JSONObject(); 
            msg.put("_flag", ret.flag);
            msg.put("_desc", ret.desc);
            msg.put("_platform", ret.platform);  
            msg.put("_longitude", ret.longitude);
            msg.put("_latitude", ret.latitude);
            UnityPlayer.UnitySendMessage(mUnityGameObject, "OnLocationGotNotify", msg.toString());
        } catch(JSONException ex) {
            UnityPlayer.UnitySendMessage(mUnityGameObject, "OnLocationGotNotify", "Exception:" + ex.getMessage());
        }
	}

	@Override
	public void OnFeedbackNotify(int flag, String desc) {
	    try{
            JSONObject msg = new JSONObject(); 
            msg.put("_flag", flag);
            msg.put("_desc", desc);
            UnityPlayer.UnitySendMessage(mUnityGameObject, "OnFeedbackNotify", msg.toString());
        } catch(JSONException ex) {
            UnityPlayer.UnitySendMessage(mUnityGameObject, "OnFeedbackNotify", "Exception:" + ex.getMessage());
        }
	}

	@Override
	public String OnCrashExtMessageNotify() {
		return "";
	}

}
