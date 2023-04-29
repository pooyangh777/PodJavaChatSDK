package chatSdk.dataTransferObject.user.inPut;


import chatSdk.dataTransferObject.message.inPut.MessageVO;

import java.util.List;

public class ResultHistory {

    private List<MessageVO> history;
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

    public List<MessageVO> getHistory() {
        return history;
    }

    public void setHistory(List<MessageVO> history) {
        this.history = history;
    }

}
