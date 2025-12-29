package com.example.vpn;

import android.app.Service;
import android.content.Intent;
import android.net.VpnService;
import android.os.ParcelFileDescriptor;

public class MyVpnService extends VpnService {

    private ParcelFileDescriptor tun;
    private Thread tunnelThread;
    private TunnelThread tunnelRunnable;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        try {
            // Создание TUN
            VpnService.Builder builder = new VpnService.Builder();
            builder.setSession("UDP-Tunnel")
                    .addAddress("10.0.0.2", 32)
                    .addRoute("0.0.0.0", 0);

            tun = builder.establish();
            if (tun == null) {
                MainActivity.setStatus("❌ Не удалось создать туннель");
                stopSelf();
                return START_NOT_STICKY;
            }

            tunnelRunnable = new TunnelThread();
            tunnelThread = new Thread(tunnelRunnable);
            tunnelThread.start();

            MainActivity.setStatus("VPN запущен");

        } catch (Exception e) {
            MainActivity.setStatus("❌ Ошибка запуска VPN");
            e.printStackTrace();
            stopSelf();
        }

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        if (tunnelRunnable != null) tunnelRunnable.stop();
        tunnelRunnable = null;
        tunnelThread = null;

        try { if (tun != null) tun.close(); } catch (Exception ignored) {}

        MainActivity.setStatus("VPN остановлен");
        super.onDestroy();
    }
}
