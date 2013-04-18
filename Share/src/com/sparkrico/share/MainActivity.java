package com.sparkrico.share;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.text.SimpleDateFormat;

import org.apache.http.conn.ConnectTimeoutException;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

import com.tencent.open.HttpStatusException;
import com.tencent.open.NetworkUnavailableException;
import com.tencent.tauth.Constants;
import com.tencent.tauth.IRequestListener;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;
import com.tencent.weibo.api.TAPI;
import com.tencent.weibo.constants.OAuthConstants;
import com.tencent.weibo.oauthv1.OAuthV1Client;
import com.tencent.weibo.oauthv2.OAuthV2;
import com.tencent.weibo.utils.QHttpClient;
import com.tencent.weibo.webview.OAuthV2AuthorizeWebView;
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
 * @author sparkrico(sparkrico@yahoo.com.cn)
 * @since 2013-4-18
 */
public class MainActivity extends Activity implements OnClickListener,
		RequestListener {

	// weibo

	private Weibo mWeibo;
	private static final String CONSUMER_KEY = "849800804";// 替换为开发者的appkey，例如"1646212860";
	private static final String REDIRECT_URL = "http://www.baidu.com";
	public static Oauth2AccessToken accessToken;
	public static final String TAG = "MainActivity";
	/**
	 * SsoHandler 仅当sdk支持sso时有效，
	 */
	SsoHandler mSsoHandler;

	// t.qq
	// !!!请根据您的实际情况修改!!! 认证成功后浏览器会被重定向到这个url中 本例子中不需改动
	private String oauthCallback = "http://www.baidu.com";
	// !!!请根据您的实际情况修改!!! 换为您为自己的应用申请到的APP KEY
	private String oauthConsumeKey = "801307187";
	// !!!请根据您的实际情况修改!!! 换为您为自己的应用申请到的APP SECRET
	private String oauthConsumerSecret = "ca5d4e3ad88333a63a7ea57b16b55e5e";

	private OAuthV2 oAuth;
	
	//qzone
	private Tencent mTencent;
	
	private static final String APP_ID = "100368509";
	private static final String SCOPE = "all";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		setupWeibo();
		setupQQ();
		setupQZone();

		findViewById(android.R.id.button1).setOnClickListener(this);
		findViewById(android.R.id.button2).setOnClickListener(this);
		findViewById(android.R.id.button3).setOnClickListener(this);
	}

	private void setupQQ() {
		oAuth = new OAuthV2(oauthCallback);
		oAuth.setClientId(oauthConsumeKey);
		oAuth.setClientSecret(oauthConsumerSecret);

		// 关闭OAuthV1Client中的默认开启的QHttpClient。
		OAuthV1Client.getQHttpClient().shutdownConnection();

		// 为OAuthV1Client配置自己定义QHttpClient。
		OAuthV1Client.setQHttpClient(new QHttpClient());
	}
	
	private void setupQZone(){
		mTencent = Tencent.createInstance(APP_ID, getApplicationContext());
	}

	private void setupWeibo() {
		mWeibo = Weibo.getInstance(CONSUMER_KEY, REDIRECT_URL);
	}

	@Override
	public void onClick(View v) {
		Intent intent;
		switch (v.getId()) {
		case android.R.id.button1:
//			mWeibo.authorize(MainActivity.this, new AuthDialogListener());
			
			mSsoHandler = new SsoHandler(MainActivity.this, mWeibo);
            mSsoHandler.authorize(new AuthDialogListener());
			break;
		case android.R.id.button2:
			intent = new Intent(MainActivity.this,
					OAuthV2AuthorizeWebView.class);
			intent.putExtra("oauth", oAuth);
			startActivityForResult(intent, 1);
			break;
		case android.R.id.button3:
			onClickLogin();
			break;

		default:
			break;
		}
	}

	class AuthDialogListener implements WeiboAuthListener {

		@Override
		public void onComplete(Bundle values) {
			String token = values.getString("access_token");
			String expires_in = values.getString("expires_in");
			MainActivity.accessToken = new Oauth2AccessToken(token, expires_in);
			if (MainActivity.accessToken.isSessionValid()) {
				String date = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss")
						.format(new java.util.Date(MainActivity.accessToken
								.getExpiresTime()));
				Log.d(TAG, "认证成功: \r\n access_token: " + token + "\r\n"
						+ "expires_in: " + expires_in + "\r\n有效期：" + date);
				try {
					Class sso = Class
							.forName("com.weibo.sdk.android.api.WeiboAPI");// 如果支持weiboapi的话，显示api功能演示入口按钮
				} catch (ClassNotFoundException e) {
					// e.printStackTrace();
					Log.i(TAG, "com.weibo.sdk.android.api.WeiboAPI not found");

				}
				AccessTokenKeeper.keepAccessToken(MainActivity.this,
						accessToken);
				Toast.makeText(MainActivity.this, "认证成功", Toast.LENGTH_SHORT)
						.show();
				//
				share2Weibo();
			}
		}

		@Override
		public void onError(WeiboDialogError e) {
			Toast.makeText(getApplicationContext(),
					"Auth error : " + e.getMessage(), Toast.LENGTH_LONG).show();
		}

		@Override
		public void onCancel() {
			Toast.makeText(getApplicationContext(), "Auth cancel",
					Toast.LENGTH_LONG).show();
		}

		@Override
		public void onWeiboException(WeiboException e) {
			Toast.makeText(getApplicationContext(),
					"Auth exception : " + e.getMessage(), Toast.LENGTH_LONG)
					.show();
		}

	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		// 关闭OAuthV1Client中的自定义的QHttpClient。
		OAuthV1Client.getQHttpClient().shutdownConnection();
		finish();
		System.exit(0);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		/**
		 * 下面两个注释掉的代码，仅当sdk支持sso时有效，
		 */
		if (mSsoHandler != null) {
			mSsoHandler.authorizeCallBack(requestCode, resultCode, data);
		}

		if (requestCode==1) {  //对应之前设置的的myRequsetCode
		     if (resultCode==OAuthV2AuthorizeWebView.RESULT_CODE) {
		         //取得返回的OAuthV2类实例oAuth
		         oAuth=(OAuthV2) data.getExtras().getSerializable("oauth");
		         //
		         new Thread(){
		        	 public void run() {
		        		 share2QQWeibo();
		        	 };
		         }.start();
		     }
		 }
		
		mTencent.onActivityResult(requestCode, resultCode, data);
		
	}

	private void share2Weibo() {
		StatusesAPI statusesAPI = new StatusesAPI(accessToken);
		statusesAPI.update("大灰狼", "", "", MainActivity.this);
	}

	private void share2QQWeibo() {
		String response;
		TAPI tAPI = new TAPI(OAuthConstants.OAUTH_VERSION_2_A);
		try {
			response = tAPI.add(oAuth, "json", "大灰狼", "127.0.0.1");
			Log.d("", "t.qq:"+response);
		} catch (Exception e) {
			e.printStackTrace();
		}
		tAPI.shutdownConnection();
	}
	
	private void share2QQZone(){
		if(ready()){
			Bundle params = new Bundle();
	        params = new Bundle();
	        params.putString("title", "大灰狼");
	        params.putString("url", "http://www.baidu.com");
	        params.putString("site", "小红帽");
	        params.putString("fromurl", "http://www.bing.com");
	        params.putString("nswb", "0");
	        
	        params.putString("format", "json");
	        
			mTencent.requestAsync(Constants.GRAPH_ADD_SHARE, 
					params, Constants.HTTP_POST, new RequsetListener(), null);
		}
	}
	
	private void onClickLogin() {
        if (!mTencent.isSessionValid()) {
//            IUiListener listener = new BaseUiListener() {
//                @Override
//                protected void doComplete(JSONObject values) {
//                    updateLoginButton();
//                }
//            };
            mTencent.login(this, SCOPE, new BaseUiListener());
        } else {
            mTencent.logout(this);
//            updateLoginButton();
        }
    }
	
	private boolean ready() {
        boolean ready = mTencent.isSessionValid()
                && mTencent.getOpenId() != null;
        if (!ready)
            Toast.makeText(this, "login and get openId first, please!",
                    Toast.LENGTH_SHORT).show();
        return ready;
    }

	@Override
	public void onComplete(String arg0) {
		Log.d(TAG, arg0);
	}

	@Override
	public void onError(WeiboException e) {
		Log.d(TAG, e.getMessage());
	}

	@Override
	public void onIOException(IOException e) {
		Log.d(TAG, e.getMessage());
	}
	
	private class BaseUiListener implements IUiListener {

        @Override
        public void onComplete(JSONObject response) {
            Log.d("", "qzone onComplete:"+response.toString());
            doComplete(response);
        }

        protected void doComplete(JSONObject values) {
        	Log.d("", values.toString());
        	share2QQZone();
        }

        @Override
        public void onError(UiError e) {
        	Log.d("onError:", "code:" + e.errorCode + ", msg:"
                    + e.errorMessage + ", detail:" + e.errorDetail);
        }

        @Override
        public void onCancel() {
        	Log.d("", "onCancel");
        }
    }
	
	private class RequsetListener implements IRequestListener {

		@Override
		public void onComplete(JSONObject arg0, Object arg1) {
			Log.d("", arg0.toString());
		}

		@Override
		public void onConnectTimeoutException(ConnectTimeoutException arg0,
				Object arg1) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onHttpStatusException(HttpStatusException arg0, Object arg1) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onIOException(IOException arg0, Object arg1) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onJSONException(JSONException arg0, Object arg1) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onMalformedURLException(MalformedURLException arg0,
				Object arg1) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onNetworkUnavailableException(
				NetworkUnavailableException arg0, Object arg1) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onSocketTimeoutException(SocketTimeoutException arg0,
				Object arg1) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onUnknowException(Exception arg0, Object arg1) {
			// TODO Auto-generated method stub
			
		}
		
	}
}
