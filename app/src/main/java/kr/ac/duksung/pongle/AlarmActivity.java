package kr.ac.duksung.pongle;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class AlarmActivity extends AppCompatActivity {
    Button btn_alarm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        btn_alarm = findViewById(R.id.button_alarm);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm);

/*


        btn_alarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SelectSeat.class);
                startActivity(intent);
            }
        }); */
    }
}