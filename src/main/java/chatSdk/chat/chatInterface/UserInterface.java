package chatSdk.chat.chatInterface;

import chatSdk.dataTransferObject.user.outPut.BlockListRequest;
import chatSdk.dataTransferObject.user.outPut.BlockRequest;
import chatSdk.dataTransferObject.user.outPut.UnBlockRequest;

public interface UserInterface {
    String blockList2(BlockListRequest request);

    String unBlock2(UnBlockRequest request);

    String block2(BlockRequest request);
}
