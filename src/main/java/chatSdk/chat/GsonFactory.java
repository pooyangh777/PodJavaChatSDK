package chatSdk.chat;

import chatSdk.dataTransferObject.message.outPut.AnnotationExclusionStrategy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public final class GsonFactory {
    public static Gson gson = new GsonBuilder().setExclusionStrategies(new AnnotationExclusionStrategy()).create();
}
