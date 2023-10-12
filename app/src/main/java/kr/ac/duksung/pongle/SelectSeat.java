package kr.ac.duksung.pongle;

import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Objects;
import java.util.concurrent.CountDownLatch;

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

public class SelectSeat extends AppCompatActivity {
    Button selected_button2, selected_button1, select_seat, choice;
    // 각각의 seatButton과 choiceButton를 배열로 관리하기 위한 배열 선언
    ImageView[] seatButtons = new ImageView[5];
    ImageView[] choiceButtons = new ImageView[5];
    String seatID, stdID;
    ArrayList<String> SeatInfo = new ArrayList<>();
    Socket mSocket;
    // 각 choiceButton의 상태를 나타내는 변수
    boolean[] choiceButtonStates = new boolean[5];
    Button exitButton;
    TextView seatName;
    String selectedSeat;

    //hello
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_seat);

        selected_button1 = findViewById(R.id.selected_button1);
        selected_button2 = findViewById(R.id.selected_button2);
        seatName = findViewById(R.id.seatName);


        Intent getintent = getIntent();
        Bundle bundle = getintent.getExtras();
        if (bundle != null) {
            stdID = bundle.getString("stdNum");
        }

        SeatInfo = SeatInit();

        // XML 레이아웃에서 ImageView들을 배열에 할당
        seatButtons[0] = findViewById(R.id.seat_button_1);
        seatButtons[1] = findViewById(R.id.seat_button_2);
        seatButtons[2] = findViewById(R.id.seat_button_3);
        seatButtons[3] = findViewById(R.id.seat_button_4);
        seatButtons[4] = findViewById(R.id.seat_button_5);


        choiceButtons[0] = findViewById(R.id.choice_button_1);
        choiceButtons[1] = findViewById(R.id.choice_button_2);
        choiceButtons[2] = findViewById(R.id.choice_button_3);
        choiceButtons[3] = findViewById(R.id.choice_button_4);
        choiceButtons[4] = findViewById(R.id.choice_button_5);

        exitButton = findViewById(R.id.exitButton);

        exitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SelectSeat.this, MainPage.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });


        // seatButtons 배열에 대한 클릭 이벤트 리스너 설정
        for (int i = 0; i < seatButtons.length; i++) {
            final int index = i;

            seatButtons[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // 해당 seatButton이 클릭되었을 때 해당 choiceButton을 화면에 보이도록 설정
                    choiceButtons[index].setVisibility(View.VISIBLE);
                    // 상태를 토글하고 바로 색상을 변경
                    choiceButtonStates[index] = !choiceButtonStates[index];
                    if (choiceButtonStates[index]) {
                        choiceButtons[index].setBackgroundResource(R.drawable.red_seat_sero); // 빨간색으로 변경
                        String Alpha = null;

                        switch (index) {
                            case 0:
                                Alpha = "A";
                            case 1:
                                Alpha = "A";
                            case 2:
                                Alpha = "B";
                            case 3:
                                Alpha = "C";
                            case 4:
                                Alpha = "E";
                        }

                        String seat = Alpha.concat(String.valueOf(index+1));
                        selectedSeat = seat;
                        seatName.setText(seat);
                        seatID = String.valueOf(index);
                        seatON(seatID);
                        System.out.println(seatID);

                    } else {
                        choiceButtons[index].setBackgroundResource(R.drawable.green_seat_sero); // 초록색으로 변경
                        seatName.setText("");
                        seatID = String.valueOf(index);
                        seatOFF(seatID);
                        System.out.println(seatID);
                    }
                }
            });
        }



        choice = findViewById(R.id.select_seat);
        choice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // MenuPan 액티비티로 데이터 전달
                Intent intent = new Intent(getApplicationContext(), MenuPan.class);
                intent.putExtra("seatNum", seatID);
                System.out.println(seatID);
                intent.putExtra("stdNum", stdID);
                System.out.println(stdID);
                startActivity(intent);
                MyApplication app = (MyApplication) getApplication();
                app.setSeatID(selectedSeat);
            }
        });


        //이미 선택된 자리 클릭하면 팝업

        Button button1 = findViewById(R.id.selected_button1); //c구역 1번
        Button button2 = findViewById(R.id.selected_button2); //c구역 4번
        Button button3 = findViewById(R.id.selected_button3); //f구역 1번
        Button button4 = findViewById(R.id.selected_button4); //a구역 4번
        Button button = findViewById(R.id.selected_button); //e구역 1번


        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                android.widget.Toast.makeText(getApplicationContext(), "이미 선택된 좌석입니다.", Toast.LENGTH_SHORT).show();
            }
        });
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                android.widget.Toast.makeText(getApplicationContext(), "이미 선택된 좌석입니다.", Toast.LENGTH_SHORT).show();
            }
        });
        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                android.widget.Toast.makeText(getApplicationContext(), "이미 선택된 좌석입니다.", Toast.LENGTH_SHORT).show();
            }
        });
        button4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                android.widget.Toast.makeText(getApplicationContext(), "이미 선택된 좌석입니다.", Toast.LENGTH_SHORT).show();
            }
        });
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                android.widget.Toast.makeText(getApplicationContext(), "이미 선택된 좌석입니다.", Toast.LENGTH_SHORT).show();
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
                        //Intent intent = new Intent(getApplicationContext(), AlarmActivity.class);
                        //startActivity(intent);
                    });
                }
            }
        });
    }

    OkHttpClient client = new OkHttpClient();
    public void seatOFF(String seatID) {
        RequestBody formBody = new FormBody.Builder()
                .add("seatID", seatID)
                .build();
        Request request = new Request.Builder()
                .url("http://10.0.2.2:5000/seatOFF")
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
                }
            }
        });
    }

    public void seatON(String seatID) {
        RequestBody formBody = new FormBody.Builder()
                .add("seatID", seatID)
                .build();
        Request request = new Request.Builder()
                .url("http://10.0.2.2:5000/seatON")
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
                }
            }
        });
    }


    public void getSeatInfo(String seatID) {
        RequestBody formBody = new FormBody.Builder()
                .add("seatID", seatID)
                .build();
        Request request = new Request.Builder()
                .url("http://10.0.2.2:5000/getSeatInfo")
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
                }
            }
        });
    }


    public ArrayList<String> SeatInit() {
        final ArrayList<String> info = new ArrayList<>();

        Request request = new Request.Builder()
                .url("http://10.0.2.2:5000/seatInfo")
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final ArrayList<String> SeatInfo = new ArrayList<>();

                if (response.isSuccessful()) {
                    String responseBody = response.body().string();
                    System.out.println(responseBody);
                    try {
                        JSONArray jsonArray = new JSONArray(responseBody);

                        runOnUiThread(() -> {
                            try {
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONArray innerArray = jsonArray.getJSONArray(i);
                                    System.out.println(innerArray);
                                    //int id = innerArray.getInt(0);
                                    String value = innerArray.getString(1);
                                    System.out.println(value);
                                    if (Objects.equals(value, "YES")) {
                                        info.add(String.valueOf(i));
                                        choiceButtons[i].setVisibility(View.VISIBLE);
                                        choiceButtons[i].setBackgroundResource(R.drawable.grey_seat);
                                        choiceButtons[i].setEnabled(false);
                                    }
                                }
                            } catch (JSONException e) {
                                throw new RuntimeException(e);
                            }
                        });

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        return info;
    }
}