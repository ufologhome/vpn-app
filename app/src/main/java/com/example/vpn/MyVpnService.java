package com.example.vpn;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class MyVpnService extends Service {

    private Thread tunnelThread;
    private TunnelThread tunnelRunnable;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        tunnelRunnable = new TunnelThread();
        tunnelThread = new Thread(tunnelRunnable);
        tunnelThread.start();

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        if (tunnelRunnable != null) tunnelRunnable.stop();
        tunnelRunnable = null;
        tunnelThread = null;

        MainActivity.setStatus("VPN остановлен");

        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
