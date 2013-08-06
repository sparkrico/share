package com.sparkrico.share;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

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
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case android.R.id.button1:
			share2Weibo.auth();
			break;
		case android.R.id.button2:
			share2TencentWeibo.auth();
			break;
		case android.R.id.button3:
			share2qZone.auth();
			break;
		case R.id.button4:
			share2Weixin.auth();
			share2Weixin.share2("С��ñ�����");
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
		 * ��������ע�͵��Ĵ��룬����sdk֧��ssoʱ��Ч��
		 */
		if (share2Weibo.getmSsoHandler() != null) {
			share2Weibo.getmSsoHandler().authorizeCallBack(requestCode,
					resultCode, data);
		}
		// tencent weibo
		if (requestCode == 1) { // ��Ӧ֮ǰ���õĵ�myRequsetCode
			if (resultCode == OAuthV2AuthorizeWebView.RESULT_CODE) {
				// ȡ�÷��ص�OAuthV2��ʵ��oAuth
				share2TencentWeibo.setoAuth((OAuthV2) data.getExtras()
						.getSerializable("oauth"));
				//
				new Thread() {
					public void run() {
						share2TencentWeibo.share2("С��ñ�����");
					};
				}.start();
			}
		}
		// QZone
		share2qZone.getmTencent().onActivityResult(requestCode, resultCode,
				data);

	}

}
