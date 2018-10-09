package ru.magnat.workout.ui;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;

import java.util.UUID;

import io.realm.Realm;
import io.realm.RealmResults;
import ru.magnat.workout.Model.WorkoutType;
import ru.magnat.workout.R;

public class WorkoutListActivity extends AppCompatActivity {
    private Realm realm;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.workout_list_layout);
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        realm = Realm.getDefaultInstance();
        initUi();
    }

    private void initUi() {
        RealmResults<WorkoutType> workouts = realm.where(WorkoutType.class).findAll();
        final WorkoutListAdapter adapter = new WorkoutListAdapter(this,workouts);
        ListView listView = findViewById(R.id.workout_list);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                openWorkoutType((WorkoutType) adapterView.getAdapter().getItem(position));
            }
        });


        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createWorkoutType();
            }
        });
    }

    private void createWorkoutType() {
        final EditText workoutEditText = new EditText(WorkoutListActivity.this);
        AlertDialog dialog = new AlertDialog.Builder(WorkoutListActivity.this)
                .setTitle(getString(R.string.lbl_add_workout_type))
                .setView(workoutEditText)
                .setPositiveButton(getString(R.string.lbl_add), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        realm.executeTransactionAsync(new Realm.Transaction() {
                            @Override
                            public void execute(Realm realm) {
                                realm.createObject(WorkoutType.class, UUID.randomUUID().toString())
                                        .setName(String.valueOf(workoutEditText.getText()));
                            }
                        });
                    }
                })
                .setNegativeButton(getString(R.string.lbl_cancel), null)
                .create();
        dialog.show();
    }

    private void openWorkoutType(WorkoutType item) {
       WorkoutTypeActivity.open(this,item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        realm.close();
    }
}
