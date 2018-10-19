package ru.magnat.workout.ui;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import io.realm.OrderedRealmCollection;
import io.realm.RealmRecyclerViewAdapter;
import ru.magnat.workout.Model.WorkoutType;
import ru.magnat.workout.R;

class WorkoutListAdapter extends RealmRecyclerViewAdapter<WorkoutType,WorkoutListAdapter.ViewHolder> {


    private int selectedPosition;
    private OnSelectPositionEventListener onSelectPositionEventListener;

    public WorkoutListAdapter(@Nullable OrderedRealmCollection<WorkoutType> data, int selectedPosition) {
        super(data, true);
        this.selectedPosition = selectedPosition;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.workout_list_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        holder.bindView(getData().get(position), isSelected(position),new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setSelectedPosition(position,true);
            }
        });
    }

    private boolean isSelected(int position) {
        return position==selectedPosition;
    }
    void setSelectedPosition(int position) {
        setSelectedPosition(position,false);
    }
    void setSelectedPosition(int position,boolean fromUser){
        if (selectedPosition!=position) {
            if (selectedPosition!=RecyclerView.NO_POSITION) notifyItemChanged(selectedPosition);
            selectedPosition = position;
            notifyItemChanged(selectedPosition);
        }
        if (selectedPosition!=RecyclerView.NO_POSITION && fromUser) {
            fireSelectPositionEvent(selectedPosition,getData().get(selectedPosition));
        }
    }

    public int getSelectedPosition() {
        return selectedPosition;
    }

    public void setOnSelectPositionEventListener(OnSelectPositionEventListener listener) {
        this.onSelectPositionEventListener = listener;
    }
    void fireSelectPositionEvent(int position,WorkoutType object) {
        if (onSelectPositionEventListener!=null) onSelectPositionEventListener.onSelectPosition(position,object);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        final View holder;
        final TextView workoutName;
        final ImageView workoutImage;
        final TextView workoutDescription;
        final int selectedColor;
        final int normalColor;

        public ViewHolder(View itemView) {
            super(itemView);
            workoutName = itemView.findViewById(R.id.workout_name);
            workoutImage = itemView.findViewById(R.id.workout_image);
            workoutDescription = itemView.findViewById(R.id.workout_description_value);
            holder = itemView.findViewById(R.id.view_holder);
            Context context = itemView.getContext();
            selectedColor = context.getResources().getColor(R.color.colorAccent );
            normalColor = workoutName.getCurrentTextColor();
        }
        public void bindView(WorkoutType workoutType, boolean selected, View.OnClickListener onClickListener) {
            workoutName.setText(workoutType.getName());
            workoutDescription.setText(workoutType.getDescription());
            holder.setOnClickListener(onClickListener);
            workoutName.setTextColor(selected?selectedColor:normalColor);

        }
    }
}
