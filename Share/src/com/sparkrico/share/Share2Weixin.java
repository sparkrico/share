package com.sparkrico.share;

import android.content.Context;

import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.SendMessageToWX;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.tencent.mm.sdk.openapi.WXMediaMessage;
import com.tencent.mm.sdk.openapi.WXTextObject;

/**
 * 
 * @author sparkrico(sparkrico@qq.com)
 * @since 2013-8-6
 */
public class Share2Weixin implements Share {
	
	private static final String APP_ID = "wxd1278d2aeddf72a8";
	
	private IWXAPI api;
	
	private Context mContext;
	
	public Share2Weixin(Context context) {
		this.mContext = context;
	}

	@Override
	public void auth() {
		api = WXAPIFactory.createWXAPI(mContext, APP_ID, true);
		api.registerApp(APP_ID);
	}

	@Override
	public void share2(String content) {
		//1
		WXTextObject wxTextObject = new WXTextObject(content);
		//2
		WXMediaMessage msg = new WXMediaMessage();
		msg.mediaObject = wxTextObject;
		msg.description = content;
		//3
		SendMessageToWX.Req req = new SendMessageToWX.Req();
		req.transaction = String.valueOf(System.currentTimeMillis());
		req.message = msg;
		//
		api.sendReq(req);
	}
	
	public boolean supportShare2WeixinFriends(){
		return api.getWXAppSupportAPI() >= 0x21020001;
	}
	
	public void share2WeixinFriends(String content) {
		//1
		WXTextObject wxTextObject = new WXTextObject(content);
		//2
		WXMediaMessage msg = new WXMediaMessage();
		msg.mediaObject = wxTextObject;
		msg.description = content;
		//3
		SendMessageToWX.Req req = new SendMessageToWX.Req();
		req.scene = SendMessageToWX.Req.WXSceneTimeline;
		req.transaction = String.valueOf(System.currentTimeMillis());
		req.message = msg;
		//
		api.sendReq(req);
	}

	@Override
	public void destory() {
		// TODO Auto-generated method stub
		
	}
}
