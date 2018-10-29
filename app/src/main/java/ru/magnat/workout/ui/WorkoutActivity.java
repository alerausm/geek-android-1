package ru.magnat.workout.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import ru.magnat.workout.Model.WorkoutType;
import ru.magnat.workout.R;

public class WorkoutActivity extends AppCompatActivity implements OnSelectWorkoutEventListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workout);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (savedInstanceState == null)
        {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment,new WorkoutListFragment())
                    .commit();
        }


    }

    @Override
    public void onSelectWorkout(int position, Object object) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment,WorkoutTypeFragment.create((WorkoutType) object))
                .addToBackStack("details")
                .commit();

    }


}
