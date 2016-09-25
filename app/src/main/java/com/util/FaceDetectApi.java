package com.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import android.util.Base64;
import android.util.Log;

public class FaceDetectApi {

	// API key �� API ����
	static private String sAPI_KEY = "2ulEo5pW5xA4TKmI";
	static private String sAPI_SECRET = "dLkbDsfc6UHqEIOj";

	public interface APICallback {
		public void gotResponse(String sResponse);
	}

	/**
	 * 
	 * @param b
	 *            ����ʶ���ͼƬ���ֽ�����
	 * @param callbackFunc
	 *            ʶ��ɹ���Ļص����
	 */
	public static void face_detect(byte[] b, final APICallback callbackFunc) {
		List<NameValuePair> params = getBasicParameters();
		// Call this API with the image data
		addImageDataValuePair(params, b);
		// ͨ��ReKogntion.com���Ի�ȡ������������
		params.add(new BasicNameValuePair("jobs",
				"face_aggressive_part_gender_age_glass_smile_emotion_beauty"));
		callAPICallInAnotherThread(params, callbackFunc);
	}

	private static void addImageDataValuePair(List<NameValuePair> params,
			byte[] b) {
		if (b != null) {
			final String encodedImage = Base64
					.encodeToString(b, Base64.DEFAULT);
			params.add(new BasicNameValuePair("base64", encodedImage));
		}
	}

	/**
	 * ����APIKEY��APISECRET
	 * 
	 * @return
	 */
	private static List<NameValuePair> getBasicParameters() {
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		nameValuePairs.add(new BasicNameValuePair("api_key", sAPI_KEY));
		nameValuePairs.add(new BasicNameValuePair("api_secret", sAPI_SECRET));
		return nameValuePairs;
	}

	private static void callAPICallInAnotherThread(
			final List<NameValuePair> params, final APICallback callbackFunc) {
		Thread trd = new Thread(new Runnable() {
			@Override
			public void run() {
				String sResponse = getAPIResponse(params);
				callbackFunc.gotResponse(sResponse);
			}
		});
		trd.start();
	}

	private static String getAPIResponse(List<NameValuePair> params) {
		String sResponse = null;
		HttpClient httpclient = new DefaultHttpClient();

		HttpPost httppost = new HttpPost(
				"http://rekognition.com/func/api/index.php");
		try {
			// HTTP����Ĳ���
			httppost.setEntity(new UrlEncodedFormEntity(params));
			// ��ʼAPI����
			HttpResponse response = httpclient.execute(httppost);
			HttpEntity responseEntity = response.getEntity();
			if (responseEntity != null) {
				sResponse = EntityUtils.toString(responseEntity);
				Log.v("json_result", sResponse);
			}
		} catch (ClientProtocolException e) {
			Log.v("ClientProtocolException", e.getMessage());
		} catch (IOException e) {
			Log.v("IOException", e.getMessage());
		}
		return sResponse;
	}

}
