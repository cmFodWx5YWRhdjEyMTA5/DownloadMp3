package com.freedownloader.data.musicarchive;

import com.freedownloader.bean.MusicArchiveBean;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;

/**
 * Created by liyanju on 2018/6/20.
 */

public class MusicArchiveDeserializer implements JsonDeserializer<MusicArchiveBean> {

    @Override
    public MusicArchiveBean deserialize(JsonElement jsonElement, Type typeOfT,
                                        JsonDeserializationContext context)
            throws JsonParseException {

        final JsonObject jsonObject = jsonElement.getAsJsonObject();
        MusicArchiveBean model = new MusicArchiveBean();
        if (jsonObject.has("total_pages")) {
            model.total_pages = jsonObject.get("total_pages").getAsInt();
        }

        JsonArray aTracksJA;
        if (jsonObject.has("aTracks")) {
            aTracksJA = jsonObject.getAsJsonArray("aTracks");
        } else if (jsonObject.has("dataset")) {
            aTracksJA = jsonObject.getAsJsonArray("dataset");
        } else {
            aTracksJA = new JsonArray();
        }

        for (int i = 0; i < aTracksJA.size(); i++) {
            JsonObject trackJO = aTracksJA.get(i).getAsJsonObject();
            MusicArchiveBean.Content content = context.deserialize(trackJO,
                    MusicArchiveBean.Content.class);
            model.contentList.add(content);
        }

        return model;
    }
}
