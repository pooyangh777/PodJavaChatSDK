package chatSdk.chat.chatInterface;

import chatSdk.dataTransferObject.user.outPut.BlockListRequest;
import chatSdk.dataTransferObject.user.outPut.BlockRequest;
import chatSdk.dataTransferObject.user.outPut.UnBlockRequest;

public interface UserInterface {
    String blockList(BlockListRequest request);

    String unBlock(UnBlockRequest request);

    String block(BlockRequest request);
}
