package chatSdk.dataTransferObject.message.inPut;


public class ResultNewMessage {
    private long threadId;
    private Message messageVO ;

    public long getThreadId() {
        return threadId;
    }

    public void setThreadId(long threadId) {
        this.threadId = threadId;
    }

    public Message getMessageVO() {
        return messageVO;
    }

    public void setMessageVO(Message messageVO) {
        this.messageVO = messageVO;
    }
}
