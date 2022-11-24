package com.example.gamestorm;

import android.content.Context;

import org.chromium.net.CronetEngine;
import org.chromium.net.UploadDataProvider;
import org.chromium.net.UploadDataProviders;
import org.chromium.net.UrlRequest;
import org.json.JSONException;

import java.nio.ByteBuffer;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class API {
    private final Context context;
    private String response;
    public API(Context context) {
        this.context = context;
    }

    public String getResponse() {
        return response;
    }

    public void callAPI(String query) throws JSONException {
        String url = context.getString(R.string.urlAPI);

        CronetEngine.Builder myBuilder = new CronetEngine.Builder(context);
        CronetEngine cronetEngine = myBuilder.build();
        Executor executor = Executors.newSingleThreadExecutor();
        MyUrlRequestCallback myUrlRequestCallback = new MyUrlRequestCallback();
        UrlRequest.Builder requestBuilder = cronetEngine.newUrlRequestBuilder(
                url, myUrlRequestCallback, executor);

        setRequest(requestBuilder);

        byte[] bytes = convertStringToBytes(query);
        UploadDataProvider myUploadDataProvider = UploadDataProviders.create(bytes);

        requestBuilder.setUploadDataProvider(myUploadDataProvider, executor);
        UrlRequest request = requestBuilder.build();

        request.start();
        while (!request.isDone()){
            response = myUrlRequestCallback.getResponse();
        }
    }

    private void setRequest(UrlRequest.Builder requestBuilder) {
        requestBuilder.setHttpMethod("POST");
        requestBuilder.addHeader("Content-Type",context.getString(R.string.contentType));
        requestBuilder.addHeader("Client-ID", context.getString(R.string.clientID));
        requestBuilder.addHeader("Authorization", context.getString(R.string.tokenAPI));
    }

    public static byte[] convertStringToBytes(String payload) {
        byte[] bytes;
        ByteBuffer byteBuffer = ByteBuffer.wrap(payload.getBytes());
        if (byteBuffer.hasArray()) {
            bytes = byteBuffer.array();
        } else {
            bytes = new byte[byteBuffer.remaining()];
            byteBuffer.get(bytes);
        }

        return bytes;
    }
}
