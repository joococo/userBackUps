package kr.ac.duksung.pongle;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Ttok extends AppCompatActivity {

    ImageView masungttok;
    ImageView chalsundae;
    ImageView busan;
    ImageView modum;
    ImageView kimbob;
    ImageView chickenmayo;
    String menuID;
    Button goback;
    Button gofront;
    Button basket;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ttok);

        masungttok = findViewById(R.id.masungttok);
        chalsundae = findViewById(R.id.chalsundae);
        busan = findViewById(R.id.busan);
        modum = findViewById(R.id.modum);
        kimbob = findViewById(R.id.kimbob);
        chickenmayo = findViewById(R.id.chickenmayo);
        goback = findViewById(R.id.goback);
        gofront = findViewById(R.id.gofront);
        basket = findViewById(R.id.basket);

        Intent getintent = getIntent();
        Bundle bundle = getintent.getExtras();
        String stdID = bundle.getString("stdNum");
        String seatID = bundle.getString("seatNum");

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());

        basket.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Baguni.class);
                startActivity(intent);
            }
        });

        goback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Gunsan.class);
                startActivity(intent);
            }
        });

        gofront.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ToastRest.class);
                startActivity(intent);
            }
        });

        masungttok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "장바구니에 담겼습니다!", Toast.LENGTH_SHORT).show();
                menuID = "1";
                basketInput(menuID);
                String Realtime = sdf.format(calendar.getTime());
                Intent intent = new Intent(getApplicationContext(), Baguni.class);
                intent.putExtra("menuNum", menuID);
                intent.putExtra("seatNum", seatID);
                intent.putExtra("stdNum", stdID);
                intent.putExtra("orderTime", Realtime);
                //startActivity(intent);
            }
        });

        chalsundae.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "장바구니에 담겼습니다!", Toast.LENGTH_SHORT).show();
                menuID = "21";
                basketInput(menuID);
                String Realtime = sdf.format(calendar.getTime());
                Intent intent = new Intent(getApplicationContext(), Baguni.class);
                intent.putExtra("menuNum", menuID);
                intent.putExtra("seatNum", seatID);
                intent.putExtra("stdNum", stdID);
                intent.putExtra("orderTime", Realtime);
                //startActivity(intent);
            }
        });

        busan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "장바구니에 담겼습니다!", Toast.LENGTH_SHORT).show();
                menuID = "19";
                basketInput(menuID);
                String Realtime = sdf.format(calendar.getTime());
                Intent intent = new Intent(getApplicationContext(), Baguni.class);
                intent.putExtra("menuNum", menuID);
                intent.putExtra("seatNum", seatID);
                intent.putExtra("stdNum", stdID);
                intent.putExtra("orderTime", Realtime);
                //startActivity(intent);
            }
        });

        modum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "장바구니에 담겼습니다!", Toast.LENGTH_SHORT).show();
                menuID = "27";
                basketInput(menuID);
                String Realtime = sdf.format(calendar.getTime());
                Intent intent = new Intent(getApplicationContext(), Baguni.class);
                intent.putExtra("menuNum", menuID);
                intent.putExtra("seatNum", seatID);
                intent.putExtra("stdNum", stdID);
                intent.putExtra("orderTime", Realtime);
                //startActivity(intent);
            }
        });

        kimbob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "장바구니에 담겼습니다!", Toast.LENGTH_SHORT).show();
                menuID = "23";
                basketInput(menuID);
                String Realtime = sdf.format(calendar.getTime());
                Intent intent = new Intent(getApplicationContext(), Baguni.class);
                intent.putExtra("menuNum", menuID);
                intent.putExtra("seatNum", seatID);
                intent.putExtra("stdNum", stdID);
                intent.putExtra("orderTime", Realtime);
                //startActivity(intent);
            }
        });

        chickenmayo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "장바구니에 담겼습니다!", Toast.LENGTH_SHORT).show();                menuID = "6";
                menuID = "22";
                basketInput(menuID);
                String Realtime = sdf.format(calendar.getTime());
                Intent intent = new Intent(getApplicationContext(), Baguni.class);
                intent.putExtra("menuNum", menuID);
                intent.putExtra("seatNum", seatID);
                intent.putExtra("stdNum", stdID);
                intent.putExtra("orderTime", Realtime);
                //startActivity(intent);
            }
        });
    }

    OkHttpClient client = new OkHttpClient();
    public void basketInput(String menuID) {
        RequestBody formBody = new FormBody.Builder()
                .add("menuID", menuID)
                .build();
        Request request = new Request.Builder()
                .url("http://10.0.2.2:5000/basketUpdate")
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
                    String responseBody = response.body().string();
                    try {
                        JSONObject jsonObject = new JSONObject(responseBody);
                        if (jsonObject.has("Result")) {
                            String result = jsonObject.getString("Result");
                            runOnUiThread(() -> {
                                System.out.println("===========");
                                System.out.println(result);
                            });
                        } else if (jsonObject.has("error")) {
                            String error = jsonObject.getString("error");
                            runOnUiThread(() -> {
                                System.out.println("error");
                            });
                        }
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