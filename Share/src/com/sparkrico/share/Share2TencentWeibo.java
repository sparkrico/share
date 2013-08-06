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

	// !!!���������ʵ������޸�!!! ��֤�ɹ���������ᱻ�ض������url�� �������в���Ķ�
	private String oauthCallback = "http://www.baidu.com";
	// !!!���������ʵ������޸�!!! ��Ϊ��Ϊ�Լ���Ӧ�����뵽��APP KEY
	private String oauthConsumeKey = "801307187";
	// !!!���������ʵ������޸�!!! ��Ϊ��Ϊ�Լ���Ӧ�����뵽��APP SECRET
	private String oauthConsumerSecret = "ca5d4e3ad88333a63a7ea57b16b55e5e";

	private OAuthV2 oAuth;
	
	private Context mContext;
	
	public Share2TencentWeibo(Context context) {
		this.mContext = context;
		
		oAuth = new OAuthV2(oauthCallback);
		oAuth.setClientId(oauthConsumeKey);
		oAuth.setClientSecret(oauthConsumerSecret);

		// �ر�OAuthV1Client�е�Ĭ�Ͽ�����QHttpClient��
		OAuthV1Client.getQHttpClient().shutdownConnection();

		// ΪOAuthV1Client�����Լ�����QHttpClient��
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
		// �ر�OAuthV1Client�е��Զ����QHttpClient��
		OAuthV1Client.getQHttpClient().shutdownConnection();
	}
}
