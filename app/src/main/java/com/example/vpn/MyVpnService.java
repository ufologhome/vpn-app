package com.example.vpn;

import android.net.VpnService;
import android.content.Intent;
import android.os.ParcelFileDescriptor;

public class MyVpnService extends VpnService {

    private ParcelFileDescriptor tun;
    private Thread thread;
    private TunnelThread tunnelRunnable;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Builder builder = new Builder();
        builder.setSession("WG-Lite")
                .addAddress("10.0.0.2", 32)
                .addRoute("0.0.0.0", 0);

        tun = builder.establish();
        if (tun == null) return START_NOT_STICKY;

        MainActivity.setStatus("VPN запущен");

        // создаём Runnable и запускаем Thread
        tunnelRunnable = new TunnelThread(tun.getFileDescriptor());
        thread = new Thread(tunnelRunnable);
        thread.start();

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        // останавливаем TunnelThread
        if (tunnelRunnable != null) {
            tunnelRunnable.stop();
        }

        // закрываем туннель
        try {
            if (tun != null) tun.close();
        } catch (Exception ignored) {}

        MainActivity.setStatus("VPN остановлен");
    }
}
