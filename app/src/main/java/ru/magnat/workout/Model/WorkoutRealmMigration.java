package ru.magnat.workout.Model;

import java.util.Date;

import io.realm.DynamicRealm;
import io.realm.RealmMigration;
import io.realm.RealmSchema;

public class WorkoutRealmMigration implements RealmMigration {

    @Override
    public void migrate(DynamicRealm realm, long oldVersion, long newVersion) {
        RealmSchema schema = realm.getSchema();
        if (oldVersion == 0) {
            schema.create("WorkoutResult")
                    .addField("id", String.class)
                    .setRequired("id",true)
                    .addPrimaryKey("id")
                    .addField("date", Date.class)
                    .setRequired("date",true)
                    .addRealmObjectField("workout",schema.get("WorkoutType"))
                    .addField("counts", Integer.class)
                    .setRequired("counts",true)
                    .addField("result", Integer.class)
                    .setRequired("result",true)
            ;
            oldVersion++;
        }
        if (oldVersion == 1) {

            oldVersion++;
        }
    }
}
