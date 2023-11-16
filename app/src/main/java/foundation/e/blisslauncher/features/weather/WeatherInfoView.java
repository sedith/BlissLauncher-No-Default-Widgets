package foundation.e.blisslauncher.features.weather;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import foundation.e.blisslauncher.R;
import foundation.e.blisslauncher.core.Preferences;
import timber.log.Timber;

public class WeatherInfoView extends LinearLayout {

    private View mWeatherPanel;
    private View mWeatherSetupTextView;

    private final BroadcastReceiver mWeatherReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (WeatherUpdateService.ACTION_UPDATE_FINISHED.equals(intent.getAction())) {
                updateWeatherPanel();
            }

            if (WeatherUpdateService.ACTION_UPDATE_CITY_FINISHED.equals(intent.getAction())) {
                final TextView textCity = mWeatherPanel.findViewById(R.id.weather_city);
                final String city = intent.getStringExtra(WeatherUpdateService.EXTRA_UPDATE_CITY_KEY);
                if (city != null && !city.trim().isEmpty()) {
                    textCity.setText(city);
                }
            }
        }
    };

    public WeatherInfoView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mWeatherSetupTextView = findViewById(R.id.weather_setup_textview);
        mWeatherPanel = findViewById(R.id.weather_panel);
        mWeatherPanel.setOnClickListener(v -> {
            Intent launchIntent = getContext().getPackageManager().getLaunchIntentForPackage("foundation.e.weather");
            if (launchIntent != null) {
                launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                getContext().startActivity(launchIntent);
            }
        });
        findViewById(R.id.weather_setting_imageview).setOnClickListener(v -> startWeatherPreferences());
        findViewById(R.id.weather_refresh_imageview).setOnClickListener(v -> {
            WeatherUpdater.getInstance(getContext().getApplicationContext()).updateWeather();
        });
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        final LocalBroadcastManager broadcastManager = LocalBroadcastManager.getInstance(getContext());
        final IntentFilter intentFilter = new IntentFilter();

        intentFilter.addAction(WeatherUpdateService.ACTION_UPDATE_FINISHED);
        intentFilter.addAction(WeatherUpdateService.ACTION_UPDATE_CITY_FINISHED);

        broadcastManager.registerReceiver(mWeatherReceiver, intentFilter);
        updateWeatherPanel();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        final LocalBroadcastManager broadcastManager = LocalBroadcastManager.getInstance(getContext());
        broadcastManager.unregisterReceiver(mWeatherReceiver);
    }

    private void updateWeatherPanel() {
        if (Preferences.getCachedWeatherInfo(getContext()) == null) {
            Timber.tag("Weather").i("getCacheWeatherInfo is null");
            mWeatherSetupTextView.setVisibility(VISIBLE);
            mWeatherPanel.setVisibility(GONE);
            mWeatherSetupTextView.setOnClickListener(v -> startWeatherPreferences());
            return;
        }
        mWeatherSetupTextView.setVisibility(GONE);
        mWeatherPanel.setVisibility(VISIBLE);
        ForecastBuilder.buildLargePanel(getContext(), mWeatherPanel, Preferences.getCachedWeatherInfo(getContext()));
    }

    private void startWeatherPreferences() {
        final Intent intent = new Intent(getContext(), WeatherPreferences.class)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        getContext().startActivity(intent);
    }
}
