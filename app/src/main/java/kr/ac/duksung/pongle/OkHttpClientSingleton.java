package kr.ac.duksung.pongle;

import okhttp3.OkHttpClient;

public class OkHttpClientSingleton {
    private static OkHttpClient client;

    private OkHttpClientSingleton() {}

    public static synchronized OkHttpClient getInstance() {
        if (client == null) {
            client = new OkHttpClient();
        }
        return client;
    }
}