package com.example.sportjournal.models

import android.os.Parcel
import android.os.Parcelable

data class Exercise(
    var exerciseName: String = "",
    var exerciseMuscleId: Int = 0,
    var exerciseTypeId: Int = 0,
    var weight: Int = 0,
    var active: Boolean = false
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString()?:"",
        parcel.readInt(),
        parcel.readInt(),
        parcel.readInt(),
        parcel.readByte() != 0.toByte()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(exerciseName)
        parcel.writeInt(exerciseMuscleId)
        parcel.writeInt(exerciseTypeId)
        parcel.writeInt(weight)
        parcel.writeByte(if (active) 1 else 0)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Exercise> {
        override fun createFromParcel(parcel: Parcel): Exercise {
            return Exercise(parcel)
        }

        override fun newArray(size: Int): Array<Exercise?> {
            return arrayOfNulls(size)
        }
    }
}

data class ExerciseGroup(
    var exercisePair: Pair<String, ArrayList<Exercise>>,
    var expanded: Boolean = false
)