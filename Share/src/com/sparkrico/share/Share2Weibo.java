package com.sparkrico.share;

import java.io.IOException;
import java.text.SimpleDateFormat;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.weibo.sdk.android.Oauth2AccessToken;
import com.weibo.sdk.android.Weibo;
import com.weibo.sdk.android.WeiboAuthListener;
import com.weibo.sdk.android.WeiboDialogError;
import com.weibo.sdk.android.WeiboException;
import com.weibo.sdk.android.api.StatusesAPI;
import com.weibo.sdk.android.net.RequestListener;
import com.weibo.sdk.android.sso.SsoHandler;

/**
 * 
 * @author sparkrico(sparkrico@qq.com)
 * @since 2013-8-6
 */
public class Share2Weibo implements Share, RequestListener{
	
	private static final boolean DEBUG = false;
	
	private static final String TAG = "Share2Weibo";
	
	private Weibo mWeibo;
	private static final String CONSUMER_KEY = "849800804";// �滻Ϊ�����ߵ�appkey������"1646212860";
	private static final String REDIRECT_URL = "http://www.baidu.com";
	public static Oauth2AccessToken accessToken;
	/**
	 * SsoHandler ����sdk֧��ssoʱ��Ч��
	 */
	SsoHandler mSsoHandler;
	
	public SsoHandler getmSsoHandler() {
		return mSsoHandler;
	}
	
	private StatusesAPI statusesAPI;
	
	Context mContext;
	
	public Share2Weibo(Context context) {
		this.mContext = context;
		
		mWeibo = Weibo.getInstance(CONSUMER_KEY, REDIRECT_URL);
	}
	
	@Override
	public void auth() {
//		mWeibo.authorize(MainActivity.this, new AuthDialogListener());
		mSsoHandler = new SsoHandler((Activity) mContext, mWeibo);
        mSsoHandler.authorize(new AuthDialogListener());
	}

	@Override
	public void share2(String content) {
		statusesAPI = new StatusesAPI(accessToken);
		statusesAPI.update(content, "", "", this);
	}
	
	@Override
	public void destory() {
		// TODO Auto-generated method stub
		
	}
	
	class AuthDialogListener implements WeiboAuthListener {

		@Override
		public void onComplete(Bundle values) {
			String token = values.getString("access_token");
			String expires_in = values.getString("expires_in");
			accessToken = new Oauth2AccessToken(token, expires_in);
			if (accessToken.isSessionValid()) {
				String date = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss")
						.format(new java.util.Date(accessToken
								.getExpiresTime()));
				if(DEBUG)
					Log.d(TAG, "��֤�ɹ�: \r\n access_token: " + token + "\r\n"
							+ "expires_in: " + expires_in + "\r\n��Ч�ڣ�" + date);
				try {
					Class sso = Class
							.forName("com.weibo.sdk.android.api.WeiboAPI");// ���֧��weiboapi�Ļ�����ʾapi������ʾ��ڰ�ť
				} catch (ClassNotFoundException e) {
					// e.printStackTrace();
					if(DEBUG)
						Log.i(TAG, "com.weibo.sdk.android.api.WeiboAPI not found");

				}
				AccessTokenKeeper.keepAccessToken(mContext,
						accessToken);
				Toast.makeText(mContext, "��֤�ɹ�", Toast.LENGTH_SHORT)
						.show();
				//TODO test
				share2("С��ñ�����");
			}
		}

		@Override
		public void onError(WeiboDialogError e) {
			Toast.makeText(mContext,
					"Auth error : " + e.getMessage(), Toast.LENGTH_LONG).show();
		}

		@Override
		public void onCancel() {
			Toast.makeText(mContext, "Auth cancel",
					Toast.LENGTH_LONG).show();
		}

		@Override
		public void onWeiboException(WeiboException e) {
			Toast.makeText(mContext,
					"Auth exception : " + e.getMessage(), Toast.LENGTH_LONG)
					.show();
		}

	}

	@Override
	public void onComplete(String arg0) {
		if(DEBUG)
			Log.d(TAG, arg0);
	}

	@Override
	public void onError(WeiboException e) {
		if(DEBUG)
			Log.d(TAG, e.getMessage());
	}

	@Override
	public void onIOException(IOException e) {
		if(DEBUG)
			Log.d(TAG, e.getMessage());
	}

}
