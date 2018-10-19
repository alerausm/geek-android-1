package ru.magnat.workout.ui;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.util.UUID;

import io.realm.Realm;
import io.realm.RealmResults;
import ru.magnat.workout.Model.WorkoutResult;
import ru.magnat.workout.Model.WorkoutType;
import ru.magnat.workout.R;

public class WorkoutListActivity extends AppCompatActivity implements OnSelectPositionEventListener {
    private static final String KEY_SELECTED_POSITION = "selectedPosition";
    private Realm realm;
    private WorkoutListAdapter workoutListAdapter;
    private int selectedPosition = RecyclerView.NO_POSITION;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.workout_list_layout);
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        if (savedInstanceState!=null) selectedPosition = savedInstanceState.getInt(KEY_SELECTED_POSITION,RecyclerView.NO_POSITION);
    }

    @Override
    protected void onStart() {
        super.onStart();
        realm = Realm.getDefaultInstance();
    }

    @Override
    protected void onResume() {
        super.onResume();
        initUi();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        selectedPosition = getSelectedPosition();
        outState.putInt(KEY_SELECTED_POSITION,selectedPosition);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        setSelectedPosition(savedInstanceState.getInt(KEY_SELECTED_POSITION,RecyclerView.NO_POSITION));
    }

    @Override
    protected void onStop() {
        super.onStop();
        realm.close();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_workout_list,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_exit:
                finish();
                return false;
            default:
            return super.onOptionsItemSelected(item);
        }

    }

    private void initUi() {
        RealmResults<WorkoutType> workouts = realm.where(WorkoutType.class).findAll();
        workoutListAdapter = new WorkoutListAdapter(this,workouts,selectedPosition);
        workoutListAdapter.setOnSelectPositionEventListener(this);
        RecyclerView listView = findViewById(R.id.workout_list);
        listView.setLayoutManager(new LinearLayoutManager(this));
        listView.setAdapter(workoutListAdapter);


        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createWorkoutType();
            }
        });

        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
                int position = viewHolder.getAdapterPosition();
                final String id = workoutListAdapter.getItem(position).getId();
                realm.executeTransactionAsync(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        WorkoutType item = realm.where(WorkoutType.class)
                                .equalTo("id", id)
                                .findFirst();
                        if (item != null) {
                            RealmResults<WorkoutResult> results = realm.where(WorkoutResult.class)
                                    .equalTo("workout.id", id)
                                    .findAll();
                            if (results!=null && !results.isEmpty()) {
                                results.deleteAllFromRealm();
                            }
                            item.deleteFromRealm();
                        }

                    }
                });
            }
        };

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(listView);
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



    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

    public int getSelectedPosition() {
        if (workoutListAdapter!=null) return workoutListAdapter.getSelectedPosition();
        return RecyclerView.NO_POSITION;
    }

    public void setSelectedPosition(int selectedPosition) {
        if (workoutListAdapter!=null) workoutListAdapter.setSelectedPosition(selectedPosition);
    }

    @Override
    public void onSelectPosition(int position, Object object) {
        selectedPosition = position;
        if (selectedPosition!=RecyclerView.NO_POSITION && object!=null && object instanceof WorkoutType) {
            WorkoutTypeActivity.open(this,(WorkoutType) object);
        }
    }
}
