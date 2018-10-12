package ru.magnat.workout.Model;

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.Sort;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

public class WorkoutType extends RealmObject {
    @Required
    @PrimaryKey
    private String id;
    @Required
    private String name;
    private String description;
    private String imageUri;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        if (description==null) return "";
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImageUri() {
        return imageUri;
    }

    public void setImageUri(String imageUri) {
        this.imageUri = imageUri;
    }

    public WorkoutResult getRecord(Realm realm) {
        return realm.where(WorkoutResult.class).equalTo("workout.id",getId() ).sort(new String[]{"result","counts"}, new Sort[]{Sort.DESCENDING,Sort.ASCENDING}).findFirst();
    }
}
