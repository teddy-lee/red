package io.oldering.tvfoot.red.model;

import android.support.annotation.Nullable;

import com.google.auto.value.AutoValue;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;

@AutoValue
public abstract class Competition {
    public abstract String getCode();

    public abstract String getName();

    @Nullable
    public abstract String getCountry();

    @Nullable
    public abstract String getUrl();

    @Nullable
    public abstract String getGender();

    public static TypeAdapter<Competition> typeAdapter(Gson gson) {
        return new AutoValue_Competition.GsonTypeAdapter(gson);
    }

    public static Competition create(String code, String name, String country, String url, String gender) {
        return new AutoValue_Competition(code, name, country, url, gender);
    }
}