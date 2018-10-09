package ru.magnat.workout.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;

import io.realm.OrderedRealmCollection;
import io.realm.RealmBaseAdapter;
import io.realm.RealmResults;
import ru.magnat.workout.Model.WorkoutType;
import ru.magnat.workout.R;

class WorkoutListAdapter extends RealmBaseAdapter<WorkoutType> implements ListAdapter {
    private WorkoutListActivity activity;
    WorkoutListAdapter(WorkoutListActivity activity, OrderedRealmCollection<WorkoutType> data) {
        super(data);
        this.activity = activity;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder viewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.workout_list_row, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.workoutName = convertView.findViewById(R.id.workout_name);
            viewHolder.workoutImage = convertView.findViewById(R.id.workout_image);
            viewHolder.holder = convertView.findViewById(R.id.view_holder);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        if (adapterData != null) {
            WorkoutType workout = adapterData.get(position);
            viewHolder.workoutName.setText(workout.getName());

        }

        return convertView;
    }


    private static class ViewHolder {
        View holder;
        TextView workoutName;
        ImageView workoutImage;
    }
}
