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
	private static final String CONSUMER_KEY = "849800804";// 替换为开发者的appkey，例如"1646212860";
	private static final String REDIRECT_URL = "http://www.baidu.com";
	public static Oauth2AccessToken accessToken;
	/**
	 * SsoHandler 仅当sdk支持sso时有效，
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
					Log.d(TAG, "认证成功: \r\n access_token: " + token + "\r\n"
							+ "expires_in: " + expires_in + "\r\n有效期：" + date);
				try {
					Class sso = Class
							.forName("com.weibo.sdk.android.api.WeiboAPI");// 如果支持weiboapi的话，显示api功能演示入口按钮
				} catch (ClassNotFoundException e) {
					// e.printStackTrace();
					if(DEBUG)
						Log.i(TAG, "com.weibo.sdk.android.api.WeiboAPI not found");

				}
				AccessTokenKeeper.keepAccessToken(mContext,
						accessToken);
				Toast.makeText(mContext, "认证成功", Toast.LENGTH_SHORT)
						.show();
				//TODO test
				share2("小红帽大灰狼");
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
