package com.example.vpn;

import android.content.Intent;
import android.net.VpnService;
import android.os.ParcelFileDescriptor;
import android.util.Log;

public class MyVpnService extends VpnService {

    private static final String TAG = "VPN";

    private ParcelFileDescriptor tun;
    private TunnelThread tunnel;
    private Thread tunnelThread;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        // 🔒 НЕ даём запускаться повторно
        if (tunnelThread != null && tunnelThread.isAlive()) {
            Log.i(TAG, "VPN уже запущен — игнор");
            return START_STICKY;
        }

        Builder builder = new Builder();
        builder.setSession("WG-Lite")
                .addAddress("10.0.0.2", 32)
                .addRoute("0.0.0.0", 0);

        tun = builder.establish();
        if (tun == null) {
            Log.e(TAG, "Не удалось создать TUN");
            return START_NOT_STICKY;
        }

        MainActivity.setStatus("🟢 VPN запущен");

        tunnel = new TunnelThread(tun.getFileDescriptor());
        tunnelThread = new Thread(tunnel, "TunnelThread");
        tunnelThread.start();

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        Log.i(TAG, "VPN останавливается");

        if (tunnel != null) {
            tunnel.stop();
            tunnel = null;
        }

        if (tun != null) {
            try {
                tun.close();
            } catch (Exception ignored) {}
            tun = null;
        }

        tunnelThread = null;

        MainActivity.setStatus("🔴 VPN остановлен");

        super.onDestroy();
    }
}
