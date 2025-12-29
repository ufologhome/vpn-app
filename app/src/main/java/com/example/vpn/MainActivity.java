package com.example.vpn;

import android.app.Activity;
import android.content.Intent;
import android.net.VpnService;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends Activity {

    private static TextView statusView;

    // Метод для обновления статуса с любого потока
    public static void setStatus(String status) {
        if (statusView != null) {
            statusView.post(() -> statusView.setText("Статус: " + status));
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        statusView = findViewById(R.id.status);
        statusView.setText("Статус: ожидание");
    }

    // Кнопка ВКЛ
    public void startVpn(View v) {
        Intent intent = VpnService.prepare(this);
        if (intent != null) {
            // Показываем экран разрешения VPN
            startActivityForResult(intent, 1);
        } else {
            // Разрешение уже есть
            startService(new Intent(this, MyVpnService.class));
        }
    }

    // Обработка результата разрешения VPN
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) {
            startService(new Intent(this, MyVpnService.class));
        } else {
            setStatus("❌ VPN не разрешён пользователем");
        }
    }

    // Кнопка ВЫКЛ
    public void stopVpn(View v) {
        stopService(new Intent(this, MyVpnService.class));
        setStatus("VPN остановлен");
    }
}
