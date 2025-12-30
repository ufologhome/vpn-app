package com.example.vpn;

import android.content.Intent;
import android.net.VpnService;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    static TextView status;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        status = findViewById(R.id.status);

        Button btn = findViewById(R.id.startVpn);
        btn.setOnClickListener(v -> {
            Intent intent = VpnService.prepare(this);
            if (intent != null) {
                startActivityForResult(intent, 1);
            } else {
                startService(new Intent(this, MyVpnService.class));
            }
        });
    }

    public static void setStatus(String s) {
        if (status != null) {
            status.post(() -> status.setText(s));
        }
    }
}
