package com.sparkrico.share;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.tencent.weibo.api.TAPI;
import com.tencent.weibo.constants.OAuthConstants;
import com.tencent.weibo.oauthv1.OAuthV1Client;
import com.tencent.weibo.oauthv2.OAuthV2;
import com.tencent.weibo.utils.QHttpClient;
import com.tencent.weibo.webview.OAuthV2AuthorizeWebView;

/**
 * 
 * @author sparkrico(sparkrico@qq.com)
 * @since 2013-8-6
 */
public class Share2TencentWeibo implements Share {

	// !!!请根据您的实际情况修改!!! 认证成功后浏览器会被重定向到这个url中 本例子中不需改动
	private String oauthCallback = "http://www.baidu.com";
	// !!!请根据您的实际情况修改!!! 换为您为自己的应用申请到的APP KEY
	private String oauthConsumeKey = "801307187";
	// !!!请根据您的实际情况修改!!! 换为您为自己的应用申请到的APP SECRET
	private String oauthConsumerSecret = "ca5d4e3ad88333a63a7ea57b16b55e5e";

	private OAuthV2 oAuth;
	
	private Context mContext;
	
	public Share2TencentWeibo(Context context) {
		this.mContext = context;
		
		oAuth = new OAuthV2(oauthCallback);
		oAuth.setClientId(oauthConsumeKey);
		oAuth.setClientSecret(oauthConsumerSecret);

		// 关闭OAuthV1Client中的默认开启的QHttpClient。
		OAuthV1Client.getQHttpClient().shutdownConnection();

		// 为OAuthV1Client配置自己定义QHttpClient。
		OAuthV1Client.setQHttpClient(new QHttpClient());
	}
	
	public void setoAuth(OAuthV2 oAuth) {
		this.oAuth = oAuth;
	}

	@Override
	public void auth() {
		Intent intent = new Intent(mContext,
				OAuthV2AuthorizeWebView.class);
		intent.putExtra("oauth", oAuth);
		((Activity)mContext).startActivityForResult(intent, 1);
	}

	@Override
	public void share2(String content) {
		String response;
		TAPI tAPI = new TAPI(OAuthConstants.OAUTH_VERSION_2_A);
		try {
			response = tAPI.add(oAuth, "json", content, "127.0.0.1");
			Log.d("", "t.qq:"+response);
		} catch (Exception e) {
			e.printStackTrace();
		}
		tAPI.shutdownConnection();
	}

	@Override
	public void destory() {
		// 关闭OAuthV1Client中的自定义的QHttpClient。
		OAuthV1Client.getQHttpClient().shutdownConnection();
	}
}
