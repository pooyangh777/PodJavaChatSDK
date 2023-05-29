package chatSdk.chat.listeners;

import chatSdk.dataTransferObject.ChatResponse2;
import chatSdk.dataTransferObject.map.inPut.ResultMapReverse;
import chatSdk.dataTransferObject.map.outPut.OutPutMapNeshan;

public interface MapListener extends Listener {

    default void onMapSearch(String content, OutPutMapNeshan outPutMapNeshan) {}
    default void onMapRouting(String content) {}
    default void OnMapReverse(String json, ChatResponse2<ResultMapReverse> response) {}
}
