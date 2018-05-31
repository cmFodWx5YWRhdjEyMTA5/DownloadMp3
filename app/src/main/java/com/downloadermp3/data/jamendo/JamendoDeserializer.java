package com.downloadermp3.data.jamendo;

import com.downloadermp3.bean.JamendoModel;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;

/**
 * Created by liyanju on 2018/5/16.
 */

public class JamendoDeserializer implements JsonDeserializer<JamendoModel> {

    @Override
    public JamendoModel deserialize(JsonElement jsonElement, Type typeOfT,
                                    JsonDeserializationContext context)
            throws JsonParseException {
        final JsonObject jsonObject = jsonElement.getAsJsonObject();
        JsonArray jsonArray = jsonObject.get("results").getAsJsonArray();

        JamendoModel jamendoModel = new JamendoModel();
        for (int i = 0; i < jsonArray.size(); i++) {
            JsonObject trackJO = jsonArray.get(i).getAsJsonObject();
            JamendoModel.JamendoResult jamendoResult = context.deserialize(trackJO,
                    JamendoModel.JamendoResult.class);
            jamendoResult.duration = jamendoResult.duration * 1000;
            jamendoModel.arrayList.add(jamendoResult);
        }
        return jamendoModel;
    }
}
