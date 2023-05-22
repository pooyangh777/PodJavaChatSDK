package chatSdk.chat.chatInterface;

import chatSdk.dataTransferObject.message.outPut.*;

public interface MessageInterface {
    String sendTextMessage(SendMessageRequest request);

    String editMessage(EditMessageRequest request);

    String deleteMessage(DeleteMessageRequest request);

    String pinMessage(PinMessageRequest request);

    String deliveredMessageList(DeliveredMessageListRequest request);

    String deliveryMessage(DeliveryMessageRequest request);

    String forwardMessage(ForwardMessageRequest request);

    String replyMessage(ReplyMessageRequest request);

    String seenMessageList(SeenMessageListRequest request);

    String seenMessage(SeenMessageRequest request);

    String unReadMessageCount(AllUnReadMessageCountRequest request);
}
