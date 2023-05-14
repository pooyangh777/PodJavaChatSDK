package chatSdk.dataTransferObject.contacts.outPut;

import chatSdk.dataTransferObject.chat.ChatMessageType;
import chatSdk.dataTransferObject.thread.outPut.BaseRequest;
import lombok.Getter;

@Getter
public class GetContactsRequest extends BaseRequest {
    Long count;
    Long offset;
    Integer id; // contact id to client app can query and find a contact in cache core data with id
    String cellphoneNumber;
    String email;
    Integer coreUserId;
    String order;
    String query;
    String summery;

    public GetContactsRequest(Builder builder) {
        super(builder.uniqueId, ChatMessageType.GET_CONTACTS);
        this.count = builder.count;
        this.offset = builder.offset;
        this.id = builder.id;
        this.cellphoneNumber = builder.cellphoneNumber;
        this.email = builder.email;
        this.coreUserId = builder.coreUserId;
        this.order = builder.order;
        this.query = builder.query;
        this.summery = builder.summery;
    }

    @Override
    public String getChatMessageContent() {
        return gson.toJson(this);
    }

    public static class Builder {
        private Long count = 25l;
        private Long offset = 0l;
        private Integer id; // contact id to client app can query and find a contact in cache core data with id
        private String cellphoneNumber;
        private String email;
        private Integer coreUserId;
        private String order;
        private String query;
        private String summery;
        private String uniqueId;

        public Long getCount() {
            return count;
        }

        public Builder setCount(Long count) {
            this.count = count;
            return this;
        }

        public Long getOffset() {
            return offset;
        }

        public Builder setOffset(Long offset) {
            this.offset = offset;
            return this;
        }

        public String getUniqueId() {
            return uniqueId;
        }

        public Builder setUniqueId(String uniqueId) {
            this.uniqueId = uniqueId;
            return this;
        }

        public Integer getId() {
            return id;
        }

        public Builder setId(Integer id) {
            this.id = id;
            return this;
        }

        public String getCellphoneNumber() {
            return cellphoneNumber;
        }

        public Builder setCellphoneNumber(String cellphoneNumber) {
            this.cellphoneNumber = cellphoneNumber;
            return this;
        }

        public String getEmail() {
            return email;
        }

        public Builder setEmail(String email) {
            this.email = email;
            return this;
        }

        public Integer getCoreUserId() {
            return coreUserId;
        }

        public Builder setCoreUserId(Integer coreUserId) {
            this.coreUserId = coreUserId;
            return this;
        }

        public String getOrder() {
            return order;
        }

        public Builder setOrder(String order) {
            this.order = order;
            return this;
        }

        public String getQuery() {
            return query;
        }

        public Builder setQuery(String query) {
            this.query = query;
            return this;
        }

        public String getSummery() {
            return summery;
        }

        public Builder setSummery(String summery) {
            this.summery = summery;
            return this;
        }

        public GetContactsRequest build() {
            return new GetContactsRequest(this);
        }
    }
}
