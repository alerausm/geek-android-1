package ru.magnat.workout.ui;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import java.util.UUID;

import io.realm.Realm;
import io.realm.RealmResults;
import ru.magnat.workout.Model.WorkoutResult;
import ru.magnat.workout.Model.WorkoutType;
import ru.magnat.workout.R;

public class WorkoutListFragment extends Fragment {
    private static final String KEY_SELECTED_POSITION = "selectedPosition";
    private static final String LOG_TAG = "WorkoutListFragment";
    private Realm realm;
    private WorkoutListAdapter workoutListAdapter;
    private int selectedPosition = RecyclerView.NO_POSITION;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        realm = Realm.getDefaultInstance();
        if (savedInstanceState!=null) {
            selectedPosition = savedInstanceState.getInt(KEY_SELECTED_POSITION,RecyclerView.NO_POSITION);

        }
        View root = inflater.inflate(R.layout.workout_list_fragment,container,false);
        setHasOptionsMenu(true);
        initUi(root);
        return root;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putInt(KEY_SELECTED_POSITION,selectedPosition);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onDestroyView() {
        realm.close();
         super.onDestroyView();

    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.menu_workout_list,menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_exit:
                getActivity().finish();
                return false;
            default:
                return super.onOptionsItemSelected(item);
        }

    }



    private void initUi(View root) {
        RealmResults<WorkoutType> workouts = realm.where(WorkoutType.class).findAll();
        workoutListAdapter = new WorkoutListAdapter(workouts,selectedPosition);
        if (getContext() instanceof OnSelectWorkoutEventListener) {
            workoutListAdapter.setOnSelectPositionEventListener(new OnSelectWorkoutEventListener() {

                @Override
                public void onSelectWorkout(int position, Object object) {
                    selectedPosition = position;
                    if ( getContext() instanceof  OnSelectWorkoutEventListener) {
                        ((OnSelectWorkoutEventListener) getContext()).onSelectWorkout(position,object);
                    }
                }
            });
        }
        RecyclerView listView = root.findViewById(R.id.workout_list);
        listView.setLayoutManager(new LinearLayoutManager(getActivity()));
        listView.setAdapter(workoutListAdapter);


        FloatingActionButton fab = root.findViewById(R.id.fab);
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
        final EditText workoutEditText = new EditText(getContext());
        AlertDialog dialog = new AlertDialog.Builder(getContext())
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

}
