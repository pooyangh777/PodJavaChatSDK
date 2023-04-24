package chatSdk.dataTransferObject.thread.inPut;


import java.util.List;

public class ResultThreads {
    private List<Conversation> conversations;
    private long contentCount;
    private boolean hasNext;
    private long nextOffset;

    public long getContentCount() {
        return contentCount;
    }

    public void setContentCount(long contentCount) {
        this.contentCount = contentCount;
    }

    public boolean isHasNext() {
        return hasNext;
    }

    public void setHasNext(boolean hasNext) {
        this.hasNext = hasNext;
    }

    public long getNextOffset() {
        return nextOffset;
    }

    public void setNextOffset(long nextOffset) {
        this.nextOffset = nextOffset;
    }

    public List<Conversation> getThreads() {
        return conversations;
    }

    public void setThreads(List<Conversation> conversations) {
        this.conversations = conversations;
    }
}
