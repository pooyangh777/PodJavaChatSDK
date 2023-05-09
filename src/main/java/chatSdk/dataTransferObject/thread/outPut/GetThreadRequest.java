package chatSdk.dataTransferObject.thread.outPut;

import chatSdk.dataTransferObject.chat.ChatMessageType;
import com.google.gson.Gson;
import lombok.Getter;

@Getter
public class GetThreadRequest extends BaseRequest {
    private Integer count;
    private Integer offset;
    private String name;
    private Boolean news;
    private Boolean archived;
    private Integer threadIds;
    private Integer creatorCoreUserId;
    private Integer partnerCoreUserId;
    private Integer partnerCoreContactId;
    private String metadataCriteria;
    private Boolean isGroup;

    public GetThreadRequest(Builder builder) {
        super(builder.uniqueId, builder.subjectId, ChatMessageType.GET_THREADS);
        this.count = builder.count;
        this.offset = builder.offset;
        this.name = builder.name;
        this.news = builder.news;
        this.archived = builder.archived;
        this.threadIds = builder.threadIds;
        this.creatorCoreUserId = builder.creatorCoreUserId;
        this.partnerCoreUserId = builder.partnerCoreUserId;
        this.partnerCoreContactId = builder.partnerCoreContactId;
        this.metadataCriteria = builder.metadataCriteria;
        this.isGroup = builder.isGroup;
    }

    @Override
    public String getChatMessageContent() {
        return gson.toJson(this);
    }

    public static class Builder {
        private Long subjectId;
        private String uniqueId;
        private Integer count = 25;
        private Integer offset = 0;
        private String name;
        private Boolean news;
        private Boolean archived;
        private Integer threadIds;
        private Integer creatorCoreUserId;
        private Integer partnerCoreUserId;
        private Integer partnerCoreContactId;
        private String metadataCriteria;
        private Boolean isGroup;

        public Long getSubjectId() {
            return subjectId;
        }

        public Builder setSubjectId(Long subjectId) {
            this.subjectId = subjectId;
            return this;
        }


        public Integer getCount() {
            return count;
        }

        public Builder setCount(Integer count) {
            this.count = count;
            return this;
        }

        public Integer getOffset() {
            return offset;
        }

        public Builder setOffset(Integer offset) {
            this.offset = offset;
            return this;
        }

        public String getName() {
            return name;
        }

        public Builder setName(String name) {
            this.name = name;
            return this;
        }

        public Boolean getNews() {
            return news;
        }

        public Builder setNews(Boolean news) {
            this.news = news;
            return this;
        }

        public Boolean getArchived() {
            return archived;
        }

        public Builder setArchived(Boolean archived) {
            this.archived = archived;
            return this;
        }

        public Integer getThreadIds() {
            return threadIds;
        }

        public Builder setThreadIds(Integer threadIds) {
            this.threadIds = threadIds;
            return this;
        }

        public Integer getCreatorCoreUserId() {
            return creatorCoreUserId;
        }

        public Builder setCreatorCoreUserId(Integer creatorCoreUserId) {
            this.creatorCoreUserId = creatorCoreUserId;
            return this;
        }

        public Integer getPartnerCoreUserId() {
            return partnerCoreUserId;
        }

        public Builder setPartnerCoreUserId(Integer partnerCoreUserId) {
            this.partnerCoreUserId = partnerCoreUserId;
            return this;
        }

        public Integer getPartnerCoreContactId() {
            return partnerCoreContactId;
        }

        public Builder setPartnerCoreContactId(Integer partnerCoreContactId) {
            this.partnerCoreContactId = partnerCoreContactId;
            return this;
        }

        public String getMetadataCriteria() {
            return metadataCriteria;
        }

        public Builder setMetadataCriteria(String metadataCriteria) {
            this.metadataCriteria = metadataCriteria;
            return this;
        }

        public Boolean getGroup() {
            return isGroup;
        }

        public Builder setGroup(Boolean group) {
            isGroup = group;
            return this;
        }

        public String getUniqueId() {
            return uniqueId;
        }

        public Builder setUniqueId(String uniqueId) {
            this.uniqueId = uniqueId;
            return this;
        }

        public GetThreadRequest build() {
            return new GetThreadRequest(this);
        }
    }
}
