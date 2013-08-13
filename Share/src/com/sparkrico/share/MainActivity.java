package com.sparkrico.share;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

import com.tencent.weibo.oauthv2.OAuthV2;
import com.tencent.weibo.webview.OAuthV2AuthorizeWebView;

/**
 * 
 * @author sparkrico(sparkrico@qq.com)
 * @since 2013-4-18
 */
public class MainActivity extends Activity implements OnClickListener {

	// weibo
	Share2Weibo share2Weibo;

	// t.qq
	Share2TencentWeibo share2TencentWeibo;

	// qzone
	Share2QZone share2qZone;
	// weixin
	private Share2Weixin share2Weixin;
	
	Handler mHandler = new Handler(){
		public void dispatchMessage(android.os.Message msg) {
			String message = ((String)msg.obj);
			if(!TextUtils.isEmpty(message))
				Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		share2Weibo = new Share2Weibo(this);
		share2TencentWeibo = new Share2TencentWeibo(this);
		share2qZone = new Share2QZone(this);
		share2Weixin = new Share2Weixin(this);

		findViewById(android.R.id.button1).setOnClickListener(this);
		findViewById(android.R.id.button2).setOnClickListener(this);
		findViewById(android.R.id.button3).setOnClickListener(this);
		findViewById(R.id.button4).setOnClickListener(this);
		findViewById(R.id.button5).setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case android.R.id.button1:
			share2Weibo.auth();
//			share2Weibo.share2("小红帽");
			break;
		case android.R.id.button2:
			share2TencentWeibo.auth();
			break;
		case android.R.id.button3:
			share2qZone.auth();
			break;
		case R.id.button4:
			share2Weixin.auth();
			share2Weixin.share2("小红帽大灰狼");
			break;
		case R.id.button5:
			share2Weixin.auth();
			if(share2Weixin.supportShare2WeixinFriends())
				share2Weixin.share2WeixinFriends("小红帽大灰狼");
			else
				Toast.makeText(this, "当前微信版本不支持分享到朋友圈！", Toast.LENGTH_SHORT).show();
			break;

		default:
			break;
		}
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		if (share2TencentWeibo != null)
			share2TencentWeibo.destory();
		finish();
		System.exit(0);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		// weibo
		/**
		 * 下面两个注释掉的代码，仅当sdk支持sso时有效，
		 */
		if (share2Weibo.getmSsoHandler() != null) {
			share2Weibo.getmSsoHandler().authorizeCallBack(requestCode,
					resultCode, data);
		}
		// tencent weibo
		if (requestCode == 1) { // 对应之前设置的的myRequsetCode
			if (resultCode == OAuthV2AuthorizeWebView.RESULT_CODE) {
				// 取得返回的OAuthV2类实例oAuth
				share2TencentWeibo.setoAuth((OAuthV2) data.getExtras()
						.getSerializable("oauth"));
				//
				new Thread() {
					public void run() {
						share2TencentWeibo.share2("小红帽大灰狼");
					};
				}.start();
			}
		}
		// QZone
		share2qZone.getmTencent().onActivityResult(requestCode, resultCode,
				data);

	}

}
