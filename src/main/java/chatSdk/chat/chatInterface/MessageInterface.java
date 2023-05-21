package chatSdk.chat.chatInterface;

import chatSdk.dataTransferObject.message.outPut.*;

public interface MessageInterface {
    String sendTextMessage2(SendMessageRequest request);

    String editMessage2(EditMessageRequest request);

    String deleteMessage2(DeleteMessageRequest request);

    String pinMessage(PinMessageRequest request);

    String deliveredMessageList(DeliveredMessageListRequest request);

    String deliveryMessage2(DeliveryMessageRequest request);

    String forwardMessage2(ForwardMessageRequest request);

    String replyMessage(ReplyMessageRequest request);

    String seenMessageList(SeenMessageListRequest request);

    String seenMessage(SeenMessageRequest request);

    String unReadMessageCount2(AllUnReadMessageCountRequest request);
}
