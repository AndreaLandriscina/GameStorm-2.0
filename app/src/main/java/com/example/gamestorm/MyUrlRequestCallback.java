package com.example.gamestorm;

import android.util.Log;

import org.chromium.net.CronetException;
import org.chromium.net.UrlRequest;
import org.chromium.net.UrlResponseInfo;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Objects;

class MyUrlRequestCallback extends UrlRequest.Callback {
    private static final String TAG = "MyUrlRequestCallback";
    private String response = "null";

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    @Override
    public void onRedirectReceived(UrlRequest request, UrlResponseInfo info, String newLocationUrl) {
        Log.i(TAG, "onRedirectReceived method called.");
        request.followRedirect();
    }

    @Override
    public void onResponseStarted(UrlRequest request, UrlResponseInfo info) {
        Log.i(TAG, "onResponseStarted method called.");

        int httpStatusCode = info.getHttpStatusCode();
        Log.i("STATUS", String.valueOf(httpStatusCode));
        if (httpStatusCode == 200) {
            request.read(ByteBuffer.allocateDirect(getSizeBody(info)));
        } else if (httpStatusCode == 503) {
            // The service is unavailable. You should still check if the request
            // contains some data.
            //request.read(myBuffer);
        }
    }

    private int getSizeBody(UrlResponseInfo info) {
        Map<String, List<String>> list= info.getAllHeaders();
        int size = 0;
        for (int i = 0; i < list.size(); i++) {
            if (list.containsKey("x-amzn-remapped-content-length"))
                size = Integer.parseInt(Objects.requireNonNull(list.get("x-amzn-remapped-content-length")).get(0));
        }
        return size;
    }

    @Override
    public void onReadCompleted(UrlRequest request, UrlResponseInfo info, ByteBuffer byteBuffer) throws JSONException {
        Log.i("RESPONSE", "onReadCompleted method called.");
        // You should keep reading the request until there's no more data.
        byteBuffer.clear();
        //the response is in the buffer
        request.read(byteBuffer);
        //fix the response
        setResponse(StandardCharsets.UTF_8.decode(byteBuffer).toString());

        Log.i("RESPONSE", getResponse());
        //get the info from the response
        JSONArray data = new JSONArray(getResponse());
        String[] names = new String[data.length()];
        String[] ids = new String[data.length()];
        for (int i = 0; i < data.length(); i++){
            JSONObject jsonObject = data.getJSONObject(i);

            ids[i] = jsonObject.getString("id");
            names[i] = jsonObject.getString("name");
            Log.i("id",ids[i]);
            Log.i("name", names[i]);

        }
        request.cancel();
    }

    @Override
    public void onSucceeded(UrlRequest request, UrlResponseInfo info) {
        Log.i(TAG, "onSucceeded method called.");
    }

    @Override
    public void onFailed(UrlRequest request, UrlResponseInfo info, CronetException error) {
        Log.i(TAG, "onFailed method called.");
    }

    @Override
    public void onCanceled(UrlRequest request, UrlResponseInfo info) {
        Log.i(TAG, "onCanceled method called.");
    }
}
