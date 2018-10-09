package ru.magnat.workout;

import android.app.Application;
import io.realm.Realm;
import io.realm.RealmConfiguration;

public class WorkoutApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Realm.init(this);
        RealmConfiguration realmConfig = new RealmConfiguration.Builder()
                .name("workout.realm")
                .schemaVersion(0)
                .build();
        Realm.setDefaultConfiguration(realmConfig);
    }
}
