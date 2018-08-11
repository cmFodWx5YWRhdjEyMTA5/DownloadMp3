package com.wedownloader.data.youtube;

import com.wedownloader.bean.YTbeBean;
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

public class YouTubeDesrializer implements JsonDeserializer<YTbeBean> {

    @Override
    public YTbeBean deserialize(JsonElement json, Type typeOfT,
                                JsonDeserializationContext context) throws JsonParseException {
        final JsonObject jsonObject = json.getAsJsonObject();
        YTbeBean youTubeModel = new YTbeBean();
        youTubeModel.nextPageToken = jsonObject.get("nextPageToken").getAsString();
        JsonArray items = jsonObject.get("items").getAsJsonArray();
        for (int i = 0; i < items.size(); i++) {
            JsonObject item = items.get(i).getAsJsonObject();
            YTbeBean.YTBSnippet snippet = context.deserialize(item.get("snippet").getAsJsonObject(),
                    YTbeBean.YTBSnippet.class);
//            snippet.statistics = context.deserialize(item.get("statistics").getAsJsonObject(),
//                    YouTubeModel.Snippet.Statistics.class);
//            snippet.contentDetails = context.deserialize(item.get("contentDetails").getAsJsonObject(),
//                    YouTubeModel.Snippet.ContentDetails.class);
            if (item.get("id").isJsonObject()) {
                snippet.vid = item.getAsJsonObject("id").get("videoId").getAsString();
            } else {
                snippet.vid = item.get("id").getAsString();
            }
            youTubeModel.list.add(snippet);
        }
        return youTubeModel;
    }
}
