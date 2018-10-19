package ru.magnat.workout.Model;

import java.util.Date;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

public class WorkoutResult extends RealmObject {
    @Required
    @PrimaryKey
    private String id;
    @Required
    private Date date;

    private WorkoutType workout;
    @Required
    private Integer counts;
    @Required
    private Integer result;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public WorkoutType getWorkout() {
        return workout;
    }

    public void setWorkout(WorkoutType workout) {
        this.workout = workout;
    }

    public Integer getCounts() {
        return counts;
    }

    public void setCounts(Integer counts) {
        this.counts = counts;
    }

    public Integer getResult() {
        return result;
    }

    public void setResult(Integer result) {
        this.result = result;
    }
}
