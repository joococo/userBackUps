package kr.ac.duksung.pongle;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;
import java.io.IOException;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.Locale;

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

public class CheckInfo extends AppCompatActivity {
    TextView stdName;
    // TextView orderedMenu;
    TextView orderedID;
    TextView orderedTime;
    TextView selectedSeat;
    ImageView QRCode;
    ArrayList infoList = new ArrayList();
    Button exitButton;
    Socket mSocket;
    String orderID;



    // Channel에 대한 id 생성
    private static final String PRIMARY_CHANNEL_ID = "FoodNotification";
    // Channel을 생성 및 전달해 줄 수 있는 Manager 생성
    private NotificationManager mNotificationManager;

    // Notification에 대한 ID 생성
    private static final int NOTIFICATION_ID = 0;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_info);

        // button 요소 연결
        stdName = findViewById(R.id.userName);
        // orderedMenu = findViewById(R.id.orderMenu);
        orderedID = findViewById(R.id.orderID);
        orderedTime = findViewById(R.id.orderTime);
        selectedSeat = findViewById(R.id.seatID);
        exitButton = findViewById(R.id.exitButton);

        createNotificationChannel();

        try {
            mSocket = IO.socket("http://10.0.2.2:5000");
            mSocket.connect();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        mSocket.on("pickup_alarm", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                String dataString = (String) args[0];
                try {
                    JSONObject data = new JSONObject(dataString);
                    String result = data.getString("Result");
                    System.out.println(result);
                    if (result.equals("ALARM")) {
                        // orderManager.addOrder(orderID, menuName, "1");
                        sendNotification();
                        runOnUiThread(() -> {
                            sendNotification(); //알람 notification
                            //Intent intent = new Intent(getApplicationContext(), AlarmActivity.class);
                            //startActivity(intent);
                        });
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });


        /*
        try {
            mSocket = IO.socket("http://10.0.2.2:5000");
            mSocket.connect();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        mSocket.on("lastOrderAlarm", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                String dataString = (String) args[0];
                System.out.println(dataString);
                try {
                    JSONObject data = new JSONObject(dataString);
                    String result = data.getString("Result");
                    System.out.println(result);
                    if (result.equals("LASTALARM")) {
                        System.out.println("LAST");
                        // orderManager.addOrder(orderID, menuName, "1");
                        runOnUiThread(() -> {
                            Intent intent = new Intent(getApplicationContext(), AlarmActivity.class);
                            startActivity(intent);
                        });
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
*/



        // 전 액티비티에서 데이터 받아오기
        MyApplication app = (MyApplication) getApplication();
        String stdID = app.getStdID();

        Intent getintent = getIntent();
        if (getintent != null) { // Intent가 비어있지 않은 경우
            orderID = getintent.getStringExtra("orderID"); // "stdNum" 키로 전달된 값을 받아옵니다.
        } else {
            orderID = app.getOrderID();
        }


        System.out.println("=============");
        System.out.println(orderID);


        exitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent2 = new Intent(CheckInfo.this, MainPage.class);
                intent2.putExtra("orderID", orderID);
                intent2.putExtra("stdNum", stdID);
                intent2.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent2);
            }
        });

        // 주문 정보 불러오기
        System.out.println(orderID);
        getOrderInfo(orderID);
        System.out.println("==============");
        System.out.println(infoList);

        // QRCODE 생성
        String QRs = orderID + "_" + stdID;
        System.out.println(QRs);
        QRCode = findViewById(R.id.qrcodeImage);
        QRCode.setImageBitmap(generateQRCode(QRs));
    }
    private Bitmap generateQRCode(String text) {
        int width = 500;
        int height = 500;
        BitMatrix bitMatrix;
        try {
            bitMatrix = new MultiFormatWriter().encode(text, BarcodeFormat.QR_CODE, width, height);
            int[] pixels = new int[width * height];
            for (int y = 0; y < height; y++) {
                int offset = y * width;
                for (int x = 0; x < width; x++) {
                    pixels[offset + x] = bitMatrix.get(x, y) ? Color.BLACK : Color.WHITE;
                }
            }
            Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
            return bitmap;
        } catch (WriterException e) {
            e.printStackTrace();
        }
        return null;
    }




/////////////////////////////////////////////////알람/////////////////////////////////////////////////알람
    public void createNotificationChannel()
    {
        //notification manager 생성
        mNotificationManager = (NotificationManager)
                getSystemService(NOTIFICATION_SERVICE);
        // 기기(device)의 SDK 버전 확인 ( SDK 26 버전 이상인지 - VERSION_CODES.O = 26)
        if(android.os.Build.VERSION.SDK_INT
                >= android.os.Build.VERSION_CODES.O){
            //Channel 정의 생성자( construct 이용 )
            NotificationChannel notificationChannel = new NotificationChannel(PRIMARY_CHANNEL_ID
                    ,"FoodNotification",mNotificationManager.IMPORTANCE_HIGH);
            //Channel에 대한 기본 설정
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.enableVibration(true);
            notificationChannel.setDescription("Notification from Mascot");
            // Manager을 이용하여 Channel 생성
            mNotificationManager.createNotificationChannel(notificationChannel);
        }

    }

    // Notification Builder를 만드는 메소드
    private NotificationCompat.Builder getNotificationBuilder() {
        NotificationCompat.Builder notifyBuilder = new NotificationCompat.Builder(this, PRIMARY_CHANNEL_ID)
                .setContentTitle("음식 준비 완료")
                .setContentText("음식 준비가 완료되었습니다!")
                .setSmallIcon(R.drawable.foodalarm)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);
        return notifyBuilder;
    }

    // Notification을 보내는 메소드
    public void sendNotification(){
        // Builder 생성
        NotificationCompat.Builder notifyBuilder = getNotificationBuilder();
        // Manager를 통해 notification 디바이스로 전달
        mNotificationManager.notify(NOTIFICATION_ID,notifyBuilder.build());
    }

/////////////////////////////////////////////알람/////////////////////////////////////////////////////알람






    OkHttpClient client = new OkHttpClient();
    public void getOrderInfo(String orderID) {
        RequestBody formBody = new FormBody.Builder()
                .add("orderID", orderID)
                .build();
        Request request = new Request.Builder()
                .url("http://10.0.2.2:5000/getOrderInfo")
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
                        JSONArray jsonArray = new JSONArray(responseBody);
                        System.out.println(jsonArray);
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            Iterator<String> keys = jsonObject.keys();
                            while(keys.hasNext()) {
                                String key = keys.next();
                                infoList.add(jsonObject.getString(key));
                                System.out.println(infoList);
                            }
                        }
                        orderedID.setText(orderID);
                        stdName.setText((CharSequence) infoList.get(0));
                        orderedTime.setText((CharSequence) infoList.get(1));
                        selectedSeat.setText((CharSequence) infoList.get(2));
                        // orderedMenu.setText((CharSequence) infoList.get(3));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        exitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MainPage.class);
                startActivity(intent);
            }
        });

    }


    public void basketInit(Intent intent) {
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
                    String responseBody = response.body().string();
                    try {
                        JSONArray jsonArray = new JSONArray(responseBody);
                        System.out.println(jsonArray);

                        StringBuilder stringBuilder = new StringBuilder();
                        for (int i = 0; i < jsonArray.length(); i++) {
                            if (i > 0) {
                                stringBuilder.append(", "); // 값들 사이에 콤마와 공백을 추가
                            }
                            stringBuilder.append(jsonArray.getString(i));
                        }

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