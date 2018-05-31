package com.downloadermp3.data.soundcloud;

import com.downloadermp3.bean.SCloudModel;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;

/**
 * Created by liyanju on 2018/5/18.
 */

public class SoundCloudDesrializer implements JsonDeserializer<SCloudModel> {

    @Override
    public SCloudModel deserialize(JsonElement json,
                                   Type typeOfT,
                                   JsonDeserializationContext context) throws JsonParseException {
        SCloudModel soundCloudModel = new SCloudModel();
        JsonArray jsonArray = json.getAsJsonArray();
        for (int i = 0; i < jsonArray.size(); i++) {
            JsonObject trackJO = jsonArray.get(i).getAsJsonObject();
            SCloudModel.SCloudResult soundCloudResult = context.deserialize(trackJO,
                    SCloudModel.SCloudResult.class);
            soundCloudModel.arrayList.add(soundCloudResult);
        }
        return soundCloudModel;
    }
}
