package kr.ac.duksung.pongle;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class Baguni extends AppCompatActivity {
    Button button_order;
    TextView Baguni;
    Calendar calendar = Calendar.getInstance();
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
    TextView[] Menu = new TextView[3];
    TextView[] Price = new TextView[3];
    TextView[] Rest = new TextView[3];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_baguni);

        button_order = findViewById(R.id.button_order);
        Baguni = findViewById(R.id.text_baguni);
        Menu[0] = findViewById(R.id.orderMenu1);
        Menu[1] = findViewById(R.id.orderMenu2);
        Menu[2] = findViewById(R.id.orderMenu3);
        Price[0] = findViewById(R.id.price_menu1);
        Price[1] = findViewById(R.id.price_menu2);
        Price[2] = findViewById(R.id.price_menu3);
        Rest[0] = findViewById(R.id.restaurant1);
        Rest[1] = findViewById(R.id.restaurant2);
        Rest[2] = findViewById(R.id.restaurant3);

        Intent intent = new Intent(getApplicationContext(), Basket.class);

        getBasket(intent);

        button_order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String Realtime = sdf.format(calendar.getTime());
                intent.putExtra("orderTime", Realtime);
                startActivity(intent);
            }
        });

    }

    OkHttpClient client = new OkHttpClient();

    public void getBasket(Intent intent) {
        Request request = new Request.Builder()
                .url("http://10.0.2.2:5000/getBasket")
                // .url("http://192.168.35.88:5000/getBasket")
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseBody = response.body().string();
                    try {
                        JSONArray jsonArray = new JSONArray(responseBody);
                        System.out.println(jsonArray);
                        StringBuilder stringBuilder = new StringBuilder();

                        if (jsonArray.length() > 0) {
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONArray subArray = jsonArray.getJSONArray(i);
                                JSONArray menuInfo = subArray.getJSONArray(0);
                                System.out.println(menuInfo);
                                System.out.println(menuInfo.getString(0));
                                int finalI = i;
                                String menu = menuInfo.getString(0);
                                String price = menuInfo.getString(1);
                                String rest = menuInfo.getString(2);
                                stringBuilder.append(menuInfo.getString(3));
                                stringBuilder.append(",");
                                runOnUiThread(() -> {
                                    Menu[finalI].setText(menu);
                                    // System.out.println(menuInfo.getString(1));
                                    Price[finalI].setText(price + "원");
                                    // System.out.println(menuInfo.getString(2));
                                    Rest[finalI].setText(rest);
                                    // System.out.println(menuInfo.getString(3));
                                });
                            }
                        }

                        else {
                            Baguni.setText("장바구니가 비어있습니다!");
                        }

                        System.out.println(stringBuilder);
                        intent.putExtra("menuID", stringBuilder.toString());

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    finally {
                        response.close();
                    }
                }
            }
        });
    }
}