package ru.magnat.workout.ui;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;

import io.realm.Realm;
import ru.magnat.workout.Model.WorkoutType;
import ru.magnat.workout.R;

public class WorkoutTypeActivity extends AppCompatActivity {
    private static final String KEY_WORKOUT_TYPE_ID = "workoutTypeId";
    private Realm realm;
    WorkoutType workoutType;
    private ViewHolder viewHolder;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.workout_type_layout);
        realm = Realm.getDefaultInstance();
        workoutType = realm.where(WorkoutType.class).equalTo("id",getIntent().getStringExtra(KEY_WORKOUT_TYPE_ID)).findFirst();
        initUi();
    }

    private void initUi() {
        setViewHolder(new ViewHolder(findViewById(R.id.view_holder)));
        DateFormat sdf = new SimpleDateFormat("dd.MM", Locale.getDefault());
        getViewHolder().getWorkoutName().setText(workoutType.getName());
        if (workoutType.getImageUri()!=null) {
            getViewHolder().getWorkoutImage().setImageURI(Uri.parse(workoutType.getImageUri()));
            getViewHolder().getWorkoutImage().setVisibility(View.VISIBLE);
        }
        else {
            getViewHolder().getWorkoutImage().setVisibility(View.GONE);
        }
        getViewHolder().getWorkoutDescriptionValue().setText(workoutType.getDescription());
      //  if (getWorkount().getRecord()!=null) {
      //      getViewHolder().getRecordCountValue().setText(String.format(Locale.getDefault(),"%d",getWorkount().getRecord().getCount()));
      //      getViewHolder().getRecordWeightValue().setText(String.format(Locale.getDefault(),getString(R.string.mask_weight),getWorkount().getRecord().getWeight()));
      //      getViewHolder().getRecordDateValue().setText(sdf.format(getWorkount().getRecord().getDate()));
      //  }
      //  else {
            getViewHolder().getRecordCountValue().setText("");
            getViewHolder().getRecordWeightValue().setText("");
            getViewHolder().getRecordDateValue().setText("");
        //}


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

            }

        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        realm.close();
    }

    public static void open(Activity parentActivity, WorkoutType workoutType) {
        Intent intent = new Intent(parentActivity,WorkoutTypeActivity.class);
        intent.putExtra(KEY_WORKOUT_TYPE_ID,workoutType.getId());
        parentActivity.startActivity(intent);

    }

    public ViewHolder getViewHolder() {
        return viewHolder;
    }

    public void setViewHolder(ViewHolder viewHolder) {
        this.viewHolder = viewHolder;
    }

    static class ViewHolder {
        private final TextView workoutName;
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
            this.workoutName = itemView.findViewById(R.id.workout_name);
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

        public TextView getWorkoutName() {
            return workoutName;
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
