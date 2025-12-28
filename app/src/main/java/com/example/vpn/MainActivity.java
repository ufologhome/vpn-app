package com.example.vpn;

import android.app.Activity;
import android.content.Intent;
import android.net.VpnService;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends Activity {

    private static final int VPN_REQUEST_CODE = 0x0F;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btnOn = findViewById(R.id.btn_on);
        Button btnOff = findViewById(R.id.btn_off);

        btnOn.setOnClickListener(v -> {
            Intent intent = VpnService.prepare(MainActivity.this);
            if (intent != null) {
                startActivityForResult(intent, VPN_REQUEST_CODE);
            } else {
                startVpnService();
            }
        });

        btnOff.setOnClickListener(v -> stopService(new Intent(MainActivity.this, MyVpnService.class)));
    }

    private void startVpnService() {
        Intent intent = new Intent(this, MyVpnService.class);
        startService(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == VPN_REQUEST_CODE && resultCode == RESULT_OK) {
            startVpnService();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
