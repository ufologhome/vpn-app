package com.example.vpn;

import android.content.Intent;
import android.net.VpnService;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    TextView status;
    static TextView staticStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        status = findViewById(R.id.status);
        staticStatus = status;

        Button start = findViewById(R.id.startVpn);

        start.setOnClickListener(v -> {
            Intent intent = VpnService.prepare(this);
            if (intent != null) {
                startActivityForResult(intent, 1);
            } else {
                startService(new Intent(this, MyVpnService.class));
            }
        });
    }

    public static void setStatus(String text) {
        if (staticStatus != null) {
            staticStatus.post(() -> staticStatus.setText(text));
        }
    }
}
