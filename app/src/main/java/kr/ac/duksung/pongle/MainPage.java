package kr.ac.duksung.pongle;

import static android.graphics.Color.BLACK;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Random;

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

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.IDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.listener.ChartTouchListener;
import com.github.mikephil.charting.listener.OnChartGestureListener;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.MPPointF;

public class MainPage extends Activity {

    Button btn_menu, btn_info, btn_seat;
    TextView Date, Name, leftSeat, waiting;
    String stdNum, stdName, orderID;
    Socket mSocket;
    LineChart lineChart;

    TextView maxPassengerText;

    private Spinner daySpinner;
    private final String[] daysOfWeek = {"월", "화", "수", "목", "금", "토", "일"};

    private List<List<Entry>> dataByDay; // 요일별 데이터 저장


    int currentDay; // 현재 요일로 초기화

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_page);


        Intent getintent = getIntent();
        Bundle bundle = getintent.getExtras();
        if (bundle != null) {
            orderID = bundle.getString("orderID");
            stdNum = bundle.getString("stdNum");
        }

        btn_seat = findViewById(R.id.button_seat);
        btn_menu = findViewById(R.id.button_menu);
        btn_info = findViewById(R.id.button_info);
        Date = findViewById(R.id.page_date);
        Name = findViewById(R.id.Name);
        leftSeat = findViewById(R.id.leftseat);
        waiting = findViewById(R.id.waitingOrder);


        MyApplication app = (MyApplication) getApplication();
        stdName = app.getStdName();

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        String Realtime = sdf.format(calendar.getTime());

        LeftSeat();
        Waiting();
        BasketInit();

        String[] onlyDate = Realtime.split(" ");
        onlyDate = onlyDate[0].split("-");
        String finalDate = onlyDate[0] + "년 " + onlyDate[1] + "월 " + onlyDate[2] + "일";
        Date.setText(finalDate);
        Name.setText(stdName);



        //그래프

        maxPassengerText = findViewById(R.id.maxPassengerText); // TextView 초기화
        lineChart = findViewById(R.id.chart);
        daySpinner = findViewById(R.id.daySpinner);

        generateData();

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, daysOfWeek);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        daySpinner.setAdapter(adapter);

        // 오늘의 요일을 기본 선택으로 설정
        int todayIndex = getTodayIndex();
        daySpinner.setSelection(todayIndex);

        daySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                // 선택한 요일에 따라 그래프 업데이트
                updateChart(dataByDay.get(position));

                // 선택한 요일의 최대 이용객 수와 시간 표시
                int maxPassenger = getMaxPassenger(dataByDay.get(position));
                String maxPassengerTime = getMaxPassengerTime(dataByDay.get(position));
                maxPassengerText.setText("혼잡도 가장 높은 시간: " + maxPassengerTime +"\n최대 이용객 수: " + maxPassenger + "명 ");
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // 아무것도 선택되지 않았을 때의 처리
            }
        });

        //그래프


        try {
            //mSocket = IO.socket("http://10.0.2.2:5000");
            mSocket = IO.socket("http://192.168.35.88:5000");
            //mSocket = IO.socket("http://172.20.10.5:5000");
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
                        Intent intent = new Intent(getApplicationContext(), AlarmActivity.class);
                        startActivity(intent);
                    });
                }
            }
        });



        //PackageManager pm = getPackageManager();
        //pm.setComponentEnabledSetting(new ComponentName(getApplicationContext(), MainActivity.class),
                //PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);


        btn_seat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SelectSeat.class);
                intent.putExtra("stdNum", stdNum);
                System.out.println(stdNum);
                startActivity(intent);
            }
        });

        btn_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MenuPan.class);
                intent.putExtra("stdNum", stdNum);
                startActivity(intent);
            }
        });

        btn_info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),CheckInfo.class);
                intent.putExtra("stdNum", stdNum);
                System.out.println(stdNum);
                intent.putExtra("orderID", orderID);
                System.out.println(orderID);

                startActivity(intent);
            }
        });

    }
//////////////////////////////////////////그래프//////////////////////////////////////
    // 요일별 데이터 생성
    private void generateData() {
        dataByDay = new ArrayList<>();

        for (int i = 0; i < daysOfWeek.length; i++) {
            List<Entry> entries = new ArrayList<>();
            Random random = new Random();

            // 요일별 랜덤 데이터 생성
            for (int j = 0; j < 18; j++) {
                int passengers = random.nextInt(50);
                entries.add(new Entry(j, passengers));
            }

            dataByDay.add(entries);
        }
    }

    // 그래프 업데이트
    private void updateChart(List<Entry> entries) {
        LineDataSet dataSet = new LineDataSet(entries, "이용객 수");
        dataSet.setAxisDependency(YAxis.AxisDependency.LEFT);
        dataSet.setColor(ColorTemplate.getHoloBlue());
        dataSet.setValueTextColor(Color.BLACK);
        dataSet.setValueTextSize(10f);
        dataSet.setLineWidth(2f);
        dataSet.setDrawCircles(true);
        dataSet.setDrawValues(false);

        ArrayList<ILineDataSet> dataSets = new ArrayList<>();
        dataSets.add(dataSet);

        LineData lineData = new LineData(dataSets);
        lineChart.setData(lineData);
        lineChart.getAxisRight().setEnabled(false);

        lineChart.getDescription().setEnabled(false);
        lineChart.invalidate();
        XAxis xAxis = lineChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f);

        YAxis leftAxis = lineChart.getAxisLeft();
        leftAxis.removeAllLimitLines();
        leftAxis.setAxisMinimum(0f);

        lineChart.getAxisRight().setEnabled(false);

        lineChart.getDescription().setEnabled(false);

        Legend legend = lineChart.getLegend();
        legend.setForm(Legend.LegendForm.LINE);

        xAxis.setValueFormatter(new IndexAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                // 해당 시간을 텍스트로 변환하여 반환
                int hour = 11 + (int) (value / 6); // 10분 간격이므로 6으로 나눔
                int minute = (int) (value % 6) * 10;
                return String.format("%02d:%02d", hour, minute);
            }
        });
        lineChart.invalidate();
    }

    // 선택한 요일의 최대 이용객 수 반환
    private int getMaxPassenger(List<Entry> entries) {
        int maxPassenger = Integer.MIN_VALUE;

        for (Entry entry : entries) {
            int passengers = (int) entry.getY();
            if (passengers > maxPassenger) {
                maxPassenger = passengers;
            }
        }

        return maxPassenger;
    }

    // 선택한 요일의 최대 이용객 시간 반환
    private String getMaxPassengerTime(List<Entry> entries) {
        int maxPassenger = Integer.MIN_VALUE;
        int maxPassengerHour = 0;
        int maxPassengerMinute = 0;

        for (int i = 0; i < entries.size(); i++) {
            Entry entry = entries.get(i);
            int passengers = (int) entry.getY();

            if (passengers > maxPassenger) {
                maxPassenger = passengers;
                maxPassengerHour = 11 + (i / 6);
                maxPassengerMinute = (i % 6) * 10;
            }
        }

        return String.format(Locale.getDefault(), "%02d:%02d", maxPassengerHour, maxPassengerMinute);
    }

    private int getTodayIndex() {
        Calendar calendar = Calendar.getInstance();
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK); // 일요일(1)부터 토요일(7)까지

        // 일요일을 배열의 첫 번째로 설정하고, 현재 요일에 맞게 인덱스를 계산
        int todayIndex = dayOfWeek - Calendar.MONDAY;
        if (todayIndex < 0) {
            todayIndex += 7; // 음수가 나올 경우 처리
        }

        return todayIndex;
    }

/////////////////////////////////////그래프 ////////////////////////////////////////////////
    OkHttpClient client = new OkHttpClient();
    public void LeftSeat() {
        Request request = new Request.Builder()
                //.url("http://10.0.2.2:5000/countSeat")
                .url("http://192.168.35.88:5000/countSeat")
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
                        System.out.println(jsonObject);
                        String leftseats = jsonObject.getString("leftseat");
                        runOnUiThread(() -> {
                            leftSeat.setText("현재 남은 좌석 : " + leftseats + "석");
                        });
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }


    public void Waiting() {
        Request request = new Request.Builder()
                //.url("http://10.0.2.2:5000/countWaiting")
                .url("http://192.168.35.88:5000/countWaiting")
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
                        System.out.println(jsonObject);
                        String waits = jsonObject.getString("waiting");
                        runOnUiThread(() -> {
                            waiting.setText("주문 대기 인원 : " + waits + "석");
                        });
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }


    public void BasketInit() {
        Request request = new Request.Builder()
                //.url("http://10.0.2.2:5000/basketInit")
                .url("http://192.168.35.88:5000/basketInit")
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    Log.i("BasketInit" , "basket Init");
                }
            }
        });
    }
}


