package foundation.e.blisslauncher.features.weather;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

import foundation.e.blisslauncher.features.weather.worker.ForceWeatherRequestWorker;
import foundation.e.blisslauncher.features.weather.worker.OneShotWeatherRequestWorker;

public class WeatherUpdateService extends Service {
    private static final String TAG = "WeatherUpdateService";

    public static final String ACTION_FORCE_UPDATE = "org.indin.blisslauncher.action.FORCE_WEATHER_UPDATE";
    public static final String ACTION_UPDATE_FINISHED = "org.indin.blisslauncher.action.WEATHER_UPDATE_FINISHED";

    private static final long UPDATE_PERIOD_IN_MS = 10L * 1000L;

    private HandlerThread mHandlerThread;
    private Handler mHandler;

    @SuppressLint("MissingPermission")
    @Override
    public void onCreate() {
        Log.d(TAG, "onCreate");

        mHandlerThread = new HandlerThread("WeatherUpdateServiceHandler");
        mHandlerThread.start();
        mHandler = new Handler(mHandlerThread.getLooper());

        executePeriodicRequest();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null && ACTION_FORCE_UPDATE.equals(intent.getAction())) {
            ForceWeatherRequestWorker.start(this);
        }

        return START_STICKY;
    }

    private void executePeriodicRequest() {
        OneShotWeatherRequestWorker.start(this);
        mHandler.removeCallbacksAndMessages(null);
        mHandler.postDelayed(this::executePeriodicRequest, UPDATE_PERIOD_IN_MS);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        mHandler.removeCallbacksAndMessages(null);
        mHandlerThread.quitSafely();
        super.onDestroy();
    }
}
