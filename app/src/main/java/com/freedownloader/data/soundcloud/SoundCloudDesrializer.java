package com.freedownloader.data.soundcloud;

import com.freedownloader.bean.SCloudBean;
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

public class SoundCloudDesrializer implements JsonDeserializer<SCloudBean> {

    @Override
    public SCloudBean deserialize(JsonElement json,
                                  Type typeOfT,
                                  JsonDeserializationContext context) throws JsonParseException {
        SCloudBean soundCloudModel = new SCloudBean();
        JsonArray jsonArray = json.getAsJsonArray();
        for (int i = 0; i < jsonArray.size(); i++) {
            JsonObject trackJO = jsonArray.get(i).getAsJsonObject();
            SCloudBean.SCloudResult soundCloudResult = context.deserialize(trackJO,
                    SCloudBean.SCloudResult.class);
            soundCloudModel.arrayList.add(soundCloudResult);
        }
        return soundCloudModel;
    }
}
