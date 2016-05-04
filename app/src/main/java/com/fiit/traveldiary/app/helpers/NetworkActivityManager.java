package com.fiit.traveldiary.app.helpers;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import com.fiit.traveldiary.app.api.ApiRequest;
import com.fiit.traveldiary.app.api.ApiResponse;
import com.fiit.traveldiary.app.api.NetworkSyncOperations;
import com.fiit.traveldiary.app.api.RequestType;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSession;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Created by Jakub Dubec on 26/04/16.
 */
public abstract class NetworkActivityManager {

	private static boolean isNetworkAvailable(Context context) {

		ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

		NetworkInfo info = cm.getActiveNetworkInfo();

		return info != null && info.isConnected();

	}

	public static boolean hasActiveInternetConnection(Context context) {
		if (isNetworkAvailable(context)) {
			NetworkSyncOperations networkSyncOperations = new NetworkSyncOperations();
			List<ApiResponse> responses;
			try {
				responses = networkSyncOperations.execute(new ApiRequest(context, RequestType.STATUS, new String[]{})).get();
				if (responses.size() > 0 && responses.get(0).getStatus() == 200)
					return true;
			} catch (InterruptedException e) {
				return false;
			} catch (ExecutionException e) {
				return false;
			}
		} else {
			Log.d("NetworkActivity", "No network available!");
		}
		return false;
	}

	public static HostnameVerifier createHostnameVerifier() {
		return new HostnameVerifier() {
			@Override
			public boolean verify(String hostname, SSLSession session) {

				List<String> allowedHosts = new ArrayList<String>();
				allowedHosts.add("api.jakubove.zbytocnosti.sk");
				allowedHosts.add("api.traveldiary.dev");

				return allowedHosts.contains(hostname);
			}
		};
	}
}