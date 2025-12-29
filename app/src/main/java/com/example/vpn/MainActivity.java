package com.example.vpn;

import android.app.Activity;
import android.content.Intent;
import android.net.VpnService;
import android.os.Bundle;
import android.widget.TextView;
import android.view.View;

public class MainActivity extends Activity {

    private static TextView statusView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        statusView = findViewById(R.id.status);
    }

    public static void setStatus(final String status) {
        if (statusView != null) {
            statusView.post(() -> statusView.setText("Статус: " + status));
        }
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
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1 && resultCode == RESULT_OK) {
            startService(new Intent(this, MyVpnService.class));
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
