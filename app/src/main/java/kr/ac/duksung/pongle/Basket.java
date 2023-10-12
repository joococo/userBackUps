package kr.ac.duksung.pongle;

import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URISyntaxException;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Basket extends AppCompatActivity {
    Button button_check;
    Socket mSocket;
    String[] menus = new String[3];
    String stdID, seatID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_basket);

        button_check = findViewById(R.id.button_check);

        Intent getintent = getIntent();
        Bundle bundle = getintent.getExtras();
        String Realtime = bundle.getString("orderTime");
        String menuID = bundle.getString("menuID");
        menus = menuID.split(",");

        System.out.println(menuID);
        MyApplication app = (MyApplication) getApplication();
        stdID = app.getStdID();
        System.out.print(stdID);
        seatID = app.getSeatID();

        Intent intent = new Intent(getApplicationContext(), CheckInfo.class);
        intent.putExtra("stdNum", stdID);


        // System.out.print(stdID);
        System.out.println(seatID);

        orderUpdate(stdID, menuID, Realtime, seatID, intent);
        BasketInit();

        button_check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(intent);
            }
        });

        try {
            mSocket = IO.socket("http://10.0.2.2:5000");
            mSocket.connect();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        mSocket.on("pickup_alarm", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                String data = (String) args[0];  // 문자열 바로 처리
                System.out.println(data);

                if (data.equals("ALARM")) {
                    runOnUiThread(() -> {
                        // Intent intent = new Intent(getApplicationContext(), AlarmActivity.class);
                        // startActivity(intent);
                    });
                }
            }
        });

    }

    private void sendRequest(String url, @Nullable RequestBody body, ResponseCallback callback) {
        Request.Builder requestBuilder = new Request.Builder()
                .url(url);

        if(body != null) {
            requestBuilder.post(body);
        }

        Request request = requestBuilder.build();
        OkHttpClientSingleton.getInstance().newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(() -> {
                    Toast.makeText(getApplicationContext(), "Request failed.", Toast.LENGTH_SHORT).show();
                });
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                callback.onResponse(response);
            }
        });
    }

    public void orderUpdate(String stdID, String menuID, String orderDate, String seatID, Intent intent) {
        RequestBody formBody = new FormBody.Builder()
                .add("stdID", stdID)
                .add("menuID", menuID)
                .add("orderDate", orderDate)
                .add("seatID", seatID)
                .build();

        sendRequest("http://10.0.2.2:5000/orderUpdate", formBody, response -> {
            if (response.isSuccessful()) {
                try {
                    String responseBody = response.body().string();
                    JSONObject jsonResponse = new JSONObject(responseBody);
                    String orderID = jsonResponse.getString("orderID");
                    runOnUiThread(() -> {
                        MyApplication app = (MyApplication) getApplicationContext();
                        app.setOrderID(orderID);
                        intent.putExtra("orderID", orderID);
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void BasketInit() {
        sendRequest("http://10.0.2.2:5000/basketInit", null, response -> {
            if (response.isSuccessful()) {
                runOnUiThread(() -> {
                    Toast.makeText(getApplicationContext(), "Basket Init", Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    // Callback interface for cleaner onResponse handling
    interface ResponseCallback {
        void onResponse(Response response) throws IOException;
    }

    /*
    public void orderUpdate(String stdID, String menuID, String orderDate, String seatID, Intent intent) {
        RequestBody formBody = new FormBody.Builder()
                .add("stdID", stdID)
                .add("menuID", menuID)
                .add("orderDate", orderDate)
                .add("seatID", seatID)
                .build();
        Request request = new Request.Builder()
                .url("http://10.0.2.2:5000/orderUpdate")
                .post(formBody)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    try {
                        String responseBody = response.body().string();
                        JSONObject jsonResponse = new JSONObject(responseBody);
                        String orderID = jsonResponse.getString("orderID");
                        runOnUiThread(() -> {
                            System.out.println("===========");
                            System.out.println(orderID);
                            MyApplication app = (MyApplication) getApplicationContext();
                            app.setOrderID(orderID);
                            intent.putExtra("orderID", orderID);
                        });
                    }
                    catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    public void BasketInit() {
        Request request = new Request.Builder()
                .url("http://10.0.2.2:5000/basketInit")
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    System.out.println("basket INit");
                }
            }
        });
    }
     */
}