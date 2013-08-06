package com.sparkrico.share;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;

import org.apache.http.conn.ConnectTimeoutException;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.tencent.open.HttpStatusException;
import com.tencent.open.NetworkUnavailableException;
import com.tencent.tauth.Constants;
import com.tencent.tauth.IRequestListener;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

/**
 * 
 * @author sparkrico(sparkrico@qq.com)
 * @since 2013-8-6
 */
public class Share2QZone implements Share {
	
	private static final String APP_ID = "100368509";
	private static final String SCOPE = "all";
	
	private Tencent mTencent;
	
	private Context mContext;
	
	public Share2QZone(Context context) {
		this.mContext = context;
		mTencent = Tencent.createInstance(APP_ID, mContext);
	}
	
	public Tencent getmTencent() {
		return mTencent;
	}

	@Override
	public void auth() {
		if (!mTencent.isSessionValid()) {
//          IUiListener listener = new BaseUiListener() {
//              @Override
//              protected void doComplete(JSONObject values) {
//                  updateLoginButton();
//              }
//          };
          mTencent.login((Activity)mContext, SCOPE, new BaseUiListener());
      } else {
          mTencent.logout(mContext);
//          updateLoginButton();
      }
	}

	@Override
	public void share2(String content) {
		if(ready()){
			Bundle params = new Bundle();
	        params = new Bundle();
	        params.putString("title", "¥Ûª“¿«");
	        params.putString("url", "http://www.baidu.com");
	        params.putString("site", "–°∫Ï√±");
	        params.putString("fromurl", "http://www.bing.com");
	        params.putString("nswb", "0");
	        
	        params.putString("format", "json");
	        
			mTencent.requestAsync(Constants.GRAPH_ADD_SHARE, 
					params, Constants.HTTP_POST, new RequsetListener(), null);
		}
	}
	
	@Override
	public void destory() {
		// TODO Auto-generated method stub
		
	}
	
	private boolean ready() {
        boolean ready = mTencent.isSessionValid()
                && mTencent.getOpenId() != null;
        if (!ready)
            Toast.makeText(mContext, "login and get openId first, please!",
                    Toast.LENGTH_SHORT).show();
        return ready;
    }

	private class BaseUiListener implements IUiListener {

        @Override
        public void onComplete(JSONObject response) {
            Log.d("", "qzone onComplete:"+response.toString());
            doComplete(response);
        }

        protected void doComplete(JSONObject values) {
        	Log.d("", values.toString());
        	//TODO test
        	share2("–°∫Ï√±¥Ûª“¿«");
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
