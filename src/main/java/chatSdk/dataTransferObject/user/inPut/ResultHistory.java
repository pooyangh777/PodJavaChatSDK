package chatSdk.dataTransferObject.user.inPut;


import chatSdk.dataTransferObject.message.inPut.Message;

import java.util.List;

public class ResultHistory {

    private List<Message> history;
    private Long contentCount;
    private Boolean hasNext;
    private Long nextOffset;


    public Long getContentCount() {
        return contentCount;
    }

    public void setContentCount(Long contentCount) {
        this.contentCount = contentCount;
    }

    public Boolean isHasNext() {
        return hasNext;
    }

    public void setHasNext(Boolean hasNext) {
        this.hasNext = hasNext;
    }

    public Long getNextOffset() {
        return nextOffset;
    }

    public void setNextOffset(Long nextOffset) {
        this.nextOffset = nextOffset;
    }

    public List<Message> getHistory() {
        return history;
    }

    public void setHistory(List<Message> history) {
        this.history = history;
    }

}
