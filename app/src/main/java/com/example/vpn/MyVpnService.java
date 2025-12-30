package com.example.vpn;

import android.content.Intent;
import android.net.VpnService;
import android.os.ParcelFileDescriptor;

public class MyVpnService extends VpnService {

    ParcelFileDescriptor tun;
    Thread tunnelThread;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Builder b = new Builder();
        b.setSession("WG-Lite")
         .addAddress("10.0.0.2", 32)
         .addRoute("0.0.0.0", 0);

        tun = b.establish();

        MainActivity.setStatus("VPN запущен");

        tunnelThread = new Thread(new TunnelThread(tun.getFileDescriptor()));
        tunnelThread.start();

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        if (tunnelThread != null) tunnelThread.interrupt();
        try { if (tun != null) tun.close(); } catch (Exception ignored) {}
        super.onDestroy();
    }
}
