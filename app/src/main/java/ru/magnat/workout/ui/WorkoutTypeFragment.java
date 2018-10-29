package ru.magnat.workout.ui;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.Surface;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Scroller;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

import io.realm.Realm;
import io.realm.RealmChangeListener;
import ru.magnat.workout.Model.WorkoutResult;
import ru.magnat.workout.Model.WorkoutType;
import ru.magnat.workout.R;

public class WorkoutTypeFragment extends Fragment implements RealmChangeListener<Realm> {
    private static final String KEY_WORKOUT_TYPE_ID = "workoutTypeId";
    private static final String KEY_WEIGHT_PROGRESS = "weightProgress";
    private static final String KEY_COUNT_TEXT = "countText";
    private static final String LOG_TAG = "WorkoutApp";
    private Realm realm;
    private WorkoutType workoutType;
    private ViewHolder viewHolder;
    private String workoutTypeId;

    public static Fragment create(WorkoutType workoutType) {
        Fragment fragment = new WorkoutTypeFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        args.putString(KEY_WORKOUT_TYPE_ID,workoutType.getId());
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (savedInstanceState==null) {
            savedInstanceState = new Bundle();

        }
        setHasOptionsMenu(true);
        View root = inflater.inflate(R.layout.workout_type_layout,container,false);
        workoutTypeId = getArguments().getString(KEY_WORKOUT_TYPE_ID);
        initUi(root,savedInstanceState.getInt(KEY_WEIGHT_PROGRESS,0),savedInstanceState.getString(KEY_COUNT_TEXT,"0"));
        return root;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putInt(KEY_WEIGHT_PROGRESS,getViewHolder().getWorkoutWeightValue().getProgress());
        outState.putString(KEY_COUNT_TEXT,getViewHolder().getWorkoutCountValue().getEditableText().toString());
        super.onSaveInstanceState(outState);
    }



    @Override
    public void onStart() {
        Log.d(LOG_TAG,"onStart");
        super.onStart();
        realm = Realm.getDefaultInstance();
        realm.addChangeListener(this);
        onChange(realm);
    }


    @Override
    public void onStop() {
        Log.d(LOG_TAG,"onStop");
        super.onStop();
        realm.removeChangeListener(this);
        realm.close();
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_workout_type,menu);
        super.onCreateOptionsMenu(menu, inflater);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_fix_orientation:
                item.setChecked(!item.isChecked());
                lockUnlockOrientation(item.isChecked());
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    item.setIcon(getContext().getDrawable( (int) ((item.isChecked())?R.drawable.ic_screen_rotation_lock:R.drawable.ic_screen_rotation)));
                }
                return true;
            case R.id.menu_item_share:
                shareRecord();
                return true;
            case R.id.menu_item_properties:
                changeProperties();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    void lockUnlockOrientation(boolean lock) {
        int orientation;
        if (lock) {
            int rotation = ((WindowManager) getContext().getSystemService(
                    Context.WINDOW_SERVICE)).getDefaultDisplay().getRotation();
            switch (rotation) {
                case Surface.ROTATION_0:
                    orientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
                    break;
                case Surface.ROTATION_90:
                    orientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
                    break;
                case Surface.ROTATION_180:
                    orientation = ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT;
                    break;
                default:
                    orientation = ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE;
                    break;
            }
        }
        else {
            orientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED;
        }
        getActivity().setRequestedOrientation(orientation);
    }
    private void changeProperties() {
        changeName();
    }

    private void changeName() {
        final EditText workoutEditText = new EditText(getContext());
        final Handler handler = new Handler();
        WorkoutType workoutType = getWorkoutType(realm);
        workoutEditText.setText(workoutType.getName());
        workoutEditText.selectAll();
        AlertDialog dialog = new AlertDialog.Builder(getContext())
                .setTitle(getString(R.string.lbl_name))
                .setView(workoutEditText)
                .setPositiveButton(getString(R.string.lbl_next), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        realm.executeTransactionAsync(new Realm.Transaction() {
                            @Override
                            public void execute(Realm realm) {
                                WorkoutType workoutType = getWorkoutType(realm);
                                workoutType.setName(workoutEditText.getEditableText().toString());
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        changeDescription();
                                    }
                                });
                            }
                        });
                    }
                })
                .setNegativeButton(getString(R.string.lbl_cancel), null)
                .create();
        dialog.show();
    }

    private void changeDescription() {
        final EditText workoutEditText = new EditText(getContext());
        WorkoutType workoutType = getWorkoutType(realm);
        workoutEditText.setText(workoutType.getDescription());
        workoutEditText.selectAll();
        workoutEditText.setMinLines(2);
        workoutEditText.setScroller(new Scroller(getContext()));
        workoutEditText.setVerticalScrollBarEnabled(true);
        AlertDialog dialog = new AlertDialog.Builder(getContext())
                .setTitle(getString(R.string.lbl_description))
                .setView(workoutEditText)
                .setPositiveButton(getString(R.string.lbl_save), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        realm.executeTransactionAsync(new Realm.Transaction() {
                            @Override
                            public void execute(Realm realm) {
                                WorkoutType workoutType = getWorkoutType(realm);
                                workoutType.setDescription(workoutEditText.getEditableText().toString());

                            }
                        });
                    }
                })
                .setNegativeButton(getString(R.string.lbl_cancel), null)
                .create();
        dialog.show();
    }


    private void shareRecord() {
        WorkoutType workoutType = getWorkoutType(realm);
        WorkoutResult record = workoutType.getRecord(realm);
        if (record==null) {
            Toast.makeText(getContext(),getString(R.string.nothing_to_share),Toast.LENGTH_SHORT).show();
            return;
        }
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMMM",Locale.getDefault());
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_TEXT, String.format(getString(R.string.record_message_pattern),sdf.format(record.getDate()),workoutType.getName(),record.getCounts(),record.getResult()));
        intent.setType("text/plain");
        startActivity(Intent.createChooser(intent, getString(R.string.share)));
    }

    private void initUi(View root, int weightProgress, String workoutCount) {
        if (getActivity() instanceof AppCompatActivity) {
            ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        setViewHolder(new ViewHolder(root));
        getViewHolder().getWorkoutCountValue().setText(workoutCount);
        getViewHolder().getWorkoutWeightValue().setProgress(weightProgress);
        getViewHolder().getWorkoutWeightLabel().setText(String.format(Locale.getDefault(),getString(R.string.mask_weight),weightProgress));
        getViewHolder().getWorkoutWeightValue().setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                getViewHolder().getWorkoutWeightLabel().setText(String.format(Locale.getDefault(),getString(R.string.mask_weight),seekBar.getProgress()));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        getViewHolder().getWorkoutSaveButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addResult();
            }

        });



    }

    private void addResult() {
        final int counts;
        final int weight;
        int value;
        try {
            value = Integer.parseInt(getViewHolder().getWorkoutCountValue().getEditableText().toString());
        }
        catch (NumberFormatException e) {
            value = 0;
        }
        counts = value;
        weight = getViewHolder().getWorkoutWeightValue().getProgress();
        if (counts==0 || weight==0) return;

        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                WorkoutResult result = realm.createObject(WorkoutResult.class, UUID.randomUUID().toString());
                result.setDate(new Date());
                result.setWorkout(getWorkoutType(realm));
                result.setCounts(counts);
                result.setResult(weight);
            }

        });
    }




    public ViewHolder getViewHolder() {
        return viewHolder;
    }

    public void setViewHolder(ViewHolder viewHolder) {
        this.viewHolder = viewHolder;
    }
    private String getWorkoutTypeId() {
        return workoutTypeId;
    }
    public WorkoutType getWorkoutType(Realm realm) {
        return realm.where(WorkoutType.class).equalTo("id",getWorkoutTypeId() ).findFirst();
    }

    @Override
    public void onChange(Realm realm) {
        DateFormat sdf = new SimpleDateFormat("dd.MM", Locale.getDefault());
        WorkoutType workoutType = getWorkoutType(realm);
        if (getActivity() instanceof AppCompatActivity) {
            ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle(workoutType.getName());
        }

        if (workoutType.getImageUri()!=null) {
            getViewHolder().getWorkoutImage().setImageURI(Uri.parse(workoutType.getImageUri()));
            getViewHolder().getWorkoutImage().setVisibility(View.VISIBLE);
        }
        else {
            getViewHolder().getWorkoutImage().setVisibility(View.GONE);
        }
        getViewHolder().getWorkoutDescriptionValue().setText(workoutType.getDescription());
        WorkoutResult record = workoutType.getRecord(realm);
        if (record!=null) {
            getViewHolder().getRecordCountValue().setText(String.format(Locale.getDefault(),"%d",record.getCounts()));
            getViewHolder().getRecordWeightValue().setText(String.format(Locale.getDefault(),getString(R.string.mask_weight),record.getResult()));
            getViewHolder().getRecordDateValue().setText(sdf.format(record.getDate()));
        }
        else {
            getViewHolder().getRecordCountValue().setText("");
            getViewHolder().getRecordWeightValue().setText("");
            getViewHolder().getRecordDateValue().setText("");
        }
    }


    static class ViewHolder {

        private final ImageView workoutImage;
        private final TextView  recordLabel;
        private final TextView recordDateLabel;
        private final TextView recordDateValue;
        private final TextView recordCountLabel;
        private final TextView recordCountValue;
        private final TextView recordWeightLabel;
        private final TextView recordWeightValue;
        private final TextView workoutWeightLabel;
        private final SeekBar workoutWeightValue;
        private final EditText workoutCountValue;
        private final Button workoutSaveButton;
        private final TextView workoutDescriptionLabel;
        private final TextView workoutDescriptionValue;

        ViewHolder(View itemView) {
            this.workoutImage  = itemView.findViewById(R.id.workout_image);
            this.recordLabel  = itemView.findViewById(R.id.record_label);
            this.recordDateLabel = itemView.findViewById(R.id.record_date_label);
            this.recordDateValue = itemView.findViewById(R.id.record_date_value);
            this.recordCountLabel = itemView.findViewById(R.id.record_count_label);
            this.recordCountValue = itemView.findViewById(R.id.record_count_value);
            this.recordWeightLabel = itemView.findViewById(R.id.record_weight_label);
            this.recordWeightValue = itemView.findViewById(R.id.record_weight_value);
            this.workoutWeightLabel = itemView.findViewById(R.id.workout_weight_label);
            this.workoutWeightValue = itemView.findViewById(R.id.workout_weight_value);
            this.workoutCountValue = itemView.findViewById(R.id.workout_count_value);
            this.workoutSaveButton = itemView.findViewById(R.id.workout_save_button);
            this.workoutDescriptionLabel = itemView.findViewById(R.id.workout_description_label);
            this.workoutDescriptionValue = itemView.findViewById(R.id.workout_description_value);

        }



        ImageView getWorkoutImage() {
            return workoutImage;
        }

        public TextView getRecordLabel() {
            return recordLabel;
        }

        public TextView getRecordDateLabel() {
            return recordDateLabel;
        }

        TextView getRecordDateValue() {
            return recordDateValue;
        }

        public TextView getRecordCountLabel() {
            return recordCountLabel;
        }

        TextView getRecordCountValue() {
            return recordCountValue;
        }

        public TextView getRecordWeightLabel() {
            return recordWeightLabel;
        }

        TextView getRecordWeightValue() {
            return recordWeightValue;
        }

        public TextView getWorkoutWeightLabel() {
            return workoutWeightLabel;
        }

        SeekBar getWorkoutWeightValue() {
            return workoutWeightValue;
        }

        EditText getWorkoutCountValue() {
            return workoutCountValue;
        }

        Button getWorkoutSaveButton() {
            return workoutSaveButton;
        }

        public TextView getWorkoutDescriptionLabel() {
            return workoutDescriptionLabel;
        }

        TextView getWorkoutDescriptionValue() {
            return workoutDescriptionValue;
        }
    }
}
