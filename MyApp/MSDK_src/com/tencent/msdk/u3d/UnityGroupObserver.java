package com.tencent.msdk.u3d;

import org.json.JSONException;
import org.json.JSONObject;

import com.tencent.msdk.api.GroupRet;
import com.tencent.msdk.api.WGGroupObserver;
import com.unity3d.player.UnityPlayer;

public class UnityGroupObserver implements WGGroupObserver {
	private String mUnityGameObject;

	public UnityGroupObserver(String unityGameObject) {
		mUnityGameObject = unityGameObject;
	}

	private String groupRet2String(GroupRet ret) {
		String retStr = "";

		if (ret != null) {
			JSONObject msg = new JSONObject();
			try {
				msg.put("_flag", ret.flag);
				msg.put("_desc", ret.desc);
				msg.put("_platform", ret.platform);
				msg.put("_errorCode", ret.errorCode);
				if (ret.mGroupInfo != null) {
					JSONObject groupInfo = new JSONObject();
					groupInfo.put("_groupName", ret.mGroupInfo.groupName);
					groupInfo.put("_fingerMemo", ret.mGroupInfo.fingerMemo);
					groupInfo.put("_memberNum", ret.mGroupInfo.memberNum);
					groupInfo.put("_maxNum", ret.mGroupInfo.maxNum);
					groupInfo.put("_ownerOpenid", ret.mGroupInfo.ownerOpenid);
					groupInfo.put("_unionid", ret.mGroupInfo.unionid);
					groupInfo.put("_zoneid", ret.mGroupInfo.zoneid);
					groupInfo.put("_adminOpenids", ret.mGroupInfo.adminOpenids);
					groupInfo.put("_groupOpenid", ret.mGroupInfo.groupOpenid);
					groupInfo.put("_groupKey", ret.mGroupInfo.groupKey);

					msg.put("_mGroupInfo", groupInfo);
				}

				retStr = msg.toString();
			} catch (JSONException ex) {
				retStr = "Exception:" + ex.getMessage();
			}
		}

		return retStr;
	}

	@Override
	public void OnQueryGroupInfoNotify(GroupRet ret) {
		UnityPlayer.UnitySendMessage(mUnityGameObject, "OnQueryGroupInfoNotify", groupRet2String(ret));
	}

	@Override
	public void OnBindGroupNotify(GroupRet ret) {
		UnityPlayer.UnitySendMessage(mUnityGameObject, "OnBindGroupNotify", groupRet2String(ret));
	}

	@Override
	public void OnUnbindGroupNotify(GroupRet ret) {
		UnityPlayer.UnitySendMessage(mUnityGameObject, "OnUnbindGroupNotify", groupRet2String(ret));
	}

	@Override
	public void OnQueryQQGroupKeyNotify(GroupRet ret) {
		UnityPlayer.UnitySendMessage(mUnityGameObject, "OnQueryQQGroupKeyNotify", groupRet2String(ret));
	}

}
