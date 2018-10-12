package ru.magnat.workout.Model;

import java.util.Comparator;
import java.util.Date;

import java.util.SortedSet;
import java.util.TreeSet;

public class Workout {
    private String name;
    private String description;
    private String imageUri;
    final private SortedSet<Result> records = new TreeSet<>(Result.getComparatorByResult());

    public Workout(String name,String description,String imageUri) {
        setName(name);
        setDescription(description);
        setImageUri(imageUri);
    }
    public Workout(String name) {
        this(name,"","");
    }


    public String getName() {
        return name;
    }

    public Workout setName(String name) {
        this.name = (name==null)?"":name;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public Workout setDescription(String description) {
        this.description = (description==null)?"":description;
        return this;
    }

    public String getImageUri() {
        return imageUri;
    }

    public Workout setImageUri(String imageUri) {
        this.imageUri = (imageUri==null)?"":imageUri;
        return this;
    }

    public boolean addResult(Result result) {
        return records.add(result) && records.first().equals(result);
    }

    public SortedSet<Result> getRecords() {
        return records;
    }
    public SortedSet<Result> getResults() {
        SortedSet<Result> results = new TreeSet<Result>(Result.getComparatorByDate()) ;
        results.addAll(getRecords());
        return records;
    }

    public Result getRecord() {
        if (getRecords().size()==0) return null;
        return getRecords().first();
    }

    public static class Result {
        private int count;
        private int weight;
        private Date date;
        public Result(int count,int weight,Date date) {
            this.setCount(count);
            this.setWeight(weight);
            this.setDate(date);
        }
        public Result(int count,int weight) {
            this(count,weight,new Date());
        }

        public int getCount() {
            return count;
        }

        public void setCount(int count) {
            this.count = count;
        }

        public int getWeight() {
            return weight;
        }

        public void setWeight(int weight) {
            this.weight = weight;
        }

        public Date getDate() {
            return date;
        }

        public void setDate(Date date) {
            this.date = date;
        }

        public static Comparator<Result> getComparatorByResult() {
            return new Comparator<Result>() {
                @Override
                public int compare(Result result1, Result result2) {
                    int result = Long.compare(result2.count*result2.weight,result1.count*result1.weight);
                    if (result==0) return Long.compare(result2.date.getTime(),result1.date.getTime());
                    return result;
                }
            };
        }
        public static Comparator<Result> getComparatorByDate() {
            return new Comparator<Result>() {
                @Override
                public int compare(Result result1, Result result2) {
                    int result = Long.compare(result2.date.getTime(),result1.date.getTime());
                    if (result==0) return Long.compare(result2.count*result2.weight,result1.count*result1.weight);
                    return result;
                }
            };
        }
    }


}
