package io.aceisnotmycard.yono.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Task implements Parcelable {
    private int position;
    private String text;
    private boolean important;
    private boolean isChanged;

    public Task(int position, String text, boolean important) {
        this.position = position;
        this.text = text;
        this.important = important;
        this.isChanged = false;
    }

    public Task() {}

    public Task(String text, boolean important) {
        this.text = text;
        this.important = important;
        this.isChanged = false;
    }

    public int getPosition() {
        return position;
    }

    public String getText() {
        return text;
    }

    public boolean isImportant() {
        return important;
    }

    public boolean isChanged() {
        return isChanged;
    }

    public void setPosition(int position) {
        this.position = position;
        isChanged = true;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setImportant(boolean important) {
        this.important = important;
    }

    @Override
    public boolean equals(Object another) {
        if (another == this) return true;
        return another instanceof Task && getPosition() == ((Task) another).getPosition();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(getText());
        dest.writeInt(getPosition());
        dest.writeByte((byte) (isImportant() ? 1 : 0));
    }

    public static final Parcelable.Creator<Task> CREATOR
            = new Parcelable.Creator<Task>() {
        @Override
        public Task createFromParcel(Parcel source) {
            String title = source.readString();
            String text = source.readString();
            int pos = source.readInt();
            boolean i = source.readByte() > 0;
            return new Task(pos, text, i);
        }

        @Override
        public Task[] newArray(int size) {
            return new Task[size];
        }
    };
}
