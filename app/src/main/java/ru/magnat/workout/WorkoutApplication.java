package ru.magnat.workout;

import android.app.Application;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import ru.magnat.workout.Model.WorkoutRealmMigration;

public class WorkoutApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Realm.init(this);
        RealmConfiguration realmConfig = new RealmConfiguration.Builder()
                .name("workout.realm")
                .schemaVersion(1)
                .migration(new WorkoutRealmMigration())
                .build();
        Realm.setDefaultConfiguration(realmConfig);
    }
}
