package com.example.dayout_organizer.models.popualrPlace;


import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;


import com.example.dayout_organizer.models.room.popularPlaceRoom.converters.PopularDataConverter;

import java.io.Serializable;
import java.util.List;

import static com.example.dayout_organizer.config.AppConstants.POPULAR_PLACE_TABLE;


@Entity(tableName = POPULAR_PLACE_TABLE)
public class PopularPlace implements Serializable {

    @Ignore
    public boolean success;
    @Ignore
    public String message;

    @TypeConverters(PopularDataConverter.class)
    public List<PopularPlaceData> data;


    @PrimaryKey(autoGenerate = true)
    int modelId;

}