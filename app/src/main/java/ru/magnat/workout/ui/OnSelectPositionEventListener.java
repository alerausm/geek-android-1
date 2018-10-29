package ru.magnat.workout.ui;

import java.util.EventListener;

interface OnSelectPositionEventListener extends EventListener {
    void onSelectPosition(int position,Object object);
}
