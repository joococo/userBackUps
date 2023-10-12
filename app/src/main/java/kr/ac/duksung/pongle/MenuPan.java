package kr.ac.duksung.pongle;

import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MenuPan extends Activity {
    Button btn_pasta;
    Button btn_ttok;
    Button btn_gunsan;
    Button btn_toast;
    Button btn_mara;

    TextView han_waiting;
    TextView joong_waiting;
    TextView il_waiting;
    TextView yang_waiting;
    TextView bun_waiting;
    TextView testing;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_pan);
        btn_pasta = findViewById(R.id.button_pasta);
        btn_ttok = findViewById(R.id.button_ttok);
        btn_gunsan = findViewById(R.id.button_gunsan);
        btn_toast = findViewById(R.id.button_toast);
        btn_mara = findViewById(R.id.button_mara);

        han_waiting = findViewById(R.id.text_han);
        joong_waiting = findViewById(R.id.text_joong);
        il_waiting = findViewById(R.id.text_il);
        yang_waiting = findViewById(R.id.text_yang);
        bun_waiting = findViewById(R.id.text_boon);



        Intent getintent = getIntent();
        Bundle bundle = getintent.getExtras();
        String stdID = bundle.getString("stdNum");
        String seatID = bundle.getString("seatNum");

        //데이터 베이스 연동 해놓은 코드!! 조심해주세욤!!
        OkHttpClient client = new OkHttpClient();

        String url = "http://10.0.2.2:5000/restCount";

        Request request = new Request.Builder()
                .url(url)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                // Handle the error
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String jsonResponse = response.body().string();

                    try {
                        JSONArray jsonArray = new JSONArray(jsonResponse);

                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONArray innerArray = jsonArray.getJSONArray(i);
                            System.out.println(innerArray);

                            String firstValue = innerArray.getString(0);
                            String secondValue = innerArray.getString(1);

                            switch (firstValue) {
                                case "1" :
                                    if (secondValue == "")
                                        secondValue = "0";
                                    han_waiting.setText(secondValue);
                                    break;
                                case "2" :
                                    if (secondValue == "")
                                        secondValue = "0";
                                    il_waiting.setText(secondValue);
                                    break;
                                case "3" :
                                    if (secondValue == "")
                                        secondValue = "0";
                                    joong_waiting.setText(secondValue);
                                    break;
                                case "4" :
                                    if (secondValue == "")
                                        secondValue = "0";
                                    yang_waiting.setText(secondValue);
                                    break;
                                case "5" :
                                    if (secondValue == "")
                                        secondValue = "0";
                                    bun_waiting.setText(secondValue);
                                    break;
                            }
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });


        btn_pasta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Pasta.class);
                intent.putExtra("stdNum", stdID);
                intent.putExtra("seatNum", seatID);
                startActivity(intent);
            }
        });

        btn_ttok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Ttok.class);
                intent.putExtra("stdNum", stdID);
                intent.putExtra("seatNum", seatID);
                startActivity(intent);
            }
        });

        btn_gunsan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Gunsan.class);
                intent.putExtra("stdNum", stdID);
                intent.putExtra("seatNum", seatID);
                startActivity(intent);
            }
        });

        btn_toast.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ToastRest.class);
                intent.putExtra("stdNum", stdID);
                intent.putExtra("seatNum", seatID);
                startActivity(intent);
            }
        });

        btn_mara.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Mara.class);
                intent.putExtra("stdNum", stdID);
                intent.putExtra("seatNum", seatID);
                startActivity(intent);
            }
        });

    }
}