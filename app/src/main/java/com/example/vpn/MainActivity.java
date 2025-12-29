package com.example.vpn;

import android.app.Activity;
import android.content.Intent;
import android.net.VpnService;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends Activity {

    private static TextView statusView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // исправленный id
        statusView = findViewById(R.id.status);
    }

    public void startVpn(View v) {
        Intent intent = VpnService.prepare(this);
        if (intent != null) {
            startActivityForResult(intent, 1);
        } else {
            startService(new Intent(this, MyVpnService.class));
        }
    }

    public void stopVpn(View v) {
        stopService(new Intent(this, MyVpnService.class));
        setStatus("🔴 VPN остановлен");
    }

    public static void setStatus(String s) {
        if (statusView != null) {
            statusView.setText(s);
        }
    }
}

