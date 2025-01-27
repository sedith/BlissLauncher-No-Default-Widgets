package foundation.e.blisslauncher.features.weather;

import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import androidx.annotation.NonNull;
import foundation.e.blisslauncher.BuildConfig;

public class WeatherAppWidgetProvider extends AppWidgetProvider {

    @NonNull
    public static final ComponentName COMPONENT_NAME = new ComponentName(BuildConfig.APPLICATION_ID,
            WeatherAppWidgetProvider.class.getName());
}
