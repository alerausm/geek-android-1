package ru.magnat.workout.ui;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Surface;
import android.view.View;
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
import io.realm.RealmObjectSchema;
import ru.magnat.workout.Model.WorkoutResult;
import ru.magnat.workout.Model.WorkoutType;
import ru.magnat.workout.R;

public class WorkoutTypeActivity extends AppCompatActivity implements RealmChangeListener<Realm> {
    private static final String KEY_WORKOUT_TYPE_ID = "workoutTypeId";
    private static final String KEY_WEIGHT_PROGRESS = "weightProgress";
    private static final String KEY_COUNT_TEXT = "countText";
    private static final String LOG_TAG = "WorkoutApp";
    private Realm realm;
    private WorkoutType workoutType;
    private ViewHolder viewHolder;
    private String workoutTypeId;

    public static void open(Activity parentActivity, WorkoutType workoutType) {
        Intent intent = new Intent(parentActivity,WorkoutTypeActivity.class);
        intent.putExtra(KEY_WORKOUT_TYPE_ID,workoutType.getId());
        parentActivity.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        Log.d(LOG_TAG,"onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.workout_type_layout);
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        workoutTypeId = getIntent().getStringExtra(KEY_WORKOUT_TYPE_ID);
        initUi();
    }

    @Override
    protected void onStart() {
        Log.d(LOG_TAG,"onStart");
        super.onStart();
        realm = Realm.getDefaultInstance();
        realm.addChangeListener(this);

    }

    @Override
    protected void onResume() {
        Log.d(LOG_TAG,"onResume");
        super.onResume();
        onChange(realm);
    }

    @Override
    protected void onStop() {
        Log.d(LOG_TAG,"onStop");
        super.onStop();
        realm.removeChangeListener(this);
        realm.close();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        Log.d(LOG_TAG,"onSaveInstanceState");
        super.onSaveInstanceState(outState);
        outState.putInt(KEY_WEIGHT_PROGRESS,getViewHolder().getWorkoutWeightValue().getProgress());
        outState.putString(KEY_COUNT_TEXT,getViewHolder().getWorkoutCountValue().getEditableText().toString());
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        Log.d(LOG_TAG,"onRestoreInstanceState");
        super.onRestoreInstanceState(savedInstanceState);

        getViewHolder().getWorkoutWeightValue().setProgress(savedInstanceState.getInt(KEY_WEIGHT_PROGRESS,0));
        getViewHolder().getWorkoutCountValue().setText(savedInstanceState.getString(KEY_COUNT_TEXT,"0"));
    }

    @Override
    protected void onDestroy() {
        Log.d(LOG_TAG,"onDestroy");
        super.onDestroy();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_workout_type,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_fix_orientation:
                item.setChecked(!item.isChecked());
                lockUnlockOrientation(item.isChecked());
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    item.setIcon(getDrawable( (int) ((item.isChecked())?R.drawable.ic_screen_rotation_lock:R.drawable.ic_screen_rotation)));
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
            int rotation = ((WindowManager) getSystemService(
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
        setRequestedOrientation(orientation);
    }
    private void changeProperties() {
        changeName();
    }

    private void changeName() {
        final EditText workoutEditText = new EditText(this);
        final Handler handler = new Handler();
        WorkoutType workoutType = getWorkoutType(realm);
        workoutEditText.setText(workoutType.getName());
        workoutEditText.selectAll();
        AlertDialog dialog = new AlertDialog.Builder(this)
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
        final EditText workoutEditText = new EditText(this);
        WorkoutType workoutType = getWorkoutType(realm);
        workoutEditText.setText(workoutType.getDescription());
        workoutEditText.selectAll();
        workoutEditText.setMinLines(2);
        workoutEditText.setScroller(new Scroller(this));
        workoutEditText.setVerticalScrollBarEnabled(true);
        AlertDialog dialog = new AlertDialog.Builder(this)
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
            Toast.makeText(this,getString(R.string.nothing_to_share),Toast.LENGTH_SHORT).show();
            return;
        }
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMMM",Locale.getDefault());
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_TEXT, String.format(getString(R.string.record_message_pattern),sdf.format(record.getDate()),workoutType.getName(),record.getCounts(),record.getResult()));
        intent.setType("text/plain");
        startActivity(Intent.createChooser(intent, getString(R.string.share)));
    }
    private void initUi() {
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setViewHolder(new ViewHolder(findViewById(R.id.view_holder)));
        getViewHolder().getWorkoutCountValue().setText("0");
        getViewHolder().getWorkoutWeightValue().setProgress(0);
        getViewHolder().getWorkoutWeightLabel().setText("");
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
        getSupportActionBar().setTitle(workoutType.getName());
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

        public ViewHolder(View itemView) {
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



        public ImageView getWorkoutImage() {
            return workoutImage;
        }

        public TextView getRecordLabel() {
            return recordLabel;
        }

        public TextView getRecordDateLabel() {
            return recordDateLabel;
        }

        public TextView getRecordDateValue() {
            return recordDateValue;
        }

        public TextView getRecordCountLabel() {
            return recordCountLabel;
        }

        public TextView getRecordCountValue() {
            return recordCountValue;
        }

        public TextView getRecordWeightLabel() {
            return recordWeightLabel;
        }

        public TextView getRecordWeightValue() {
            return recordWeightValue;
        }

        public TextView getWorkoutWeightLabel() {
            return workoutWeightLabel;
        }

        public SeekBar getWorkoutWeightValue() {
            return workoutWeightValue;
        }

        public EditText getWorkoutCountValue() {
            return workoutCountValue;
        }

        public Button getWorkoutSaveButton() {
            return workoutSaveButton;
        }

        public TextView getWorkoutDescriptionLabel() {
            return workoutDescriptionLabel;
        }

        public TextView getWorkoutDescriptionValue() {
            return workoutDescriptionValue;
        }
    }
}
