package ru.magnat.workout.ui;

import java.util.EventListener;

interface OnSelectWorkoutEventListener extends EventListener {
    void onSelectWorkout(int position,Object object);
}
