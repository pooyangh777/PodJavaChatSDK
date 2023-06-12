//package chatSdk.dataTransferObject.thread.outPut;
//
//import chatSdk.dataTransferObject.chat.ChatMessageType2;
//
//public class GetTagParticipantsRequest extends BaseRequest {
//    private Long id;
//
//    public GetTagParticipantsRequest(Builder builder) {
//        super(builder.uniqueId, ChatMessageType2.GET_TAG_PARTICIPANTS);
//        this.subjectId = builder.id;
//        this.id = builder.id;
//    }
//
//    @Override
//    public String getChatMessageContent() {
//        return gson.toJson(this);
//    }
//
//    public static class Builder {
//        private Long id;
//        private String uniqueId;
//
//        public Long getId() {
//            return id;
//        }
//
//        public Builder setId(Long id) {
//            this.id = id;
//            return this;
//        }
//
//        public String getUniqueId() {
//            return uniqueId;
//        }
//
//        public Builder setUniqueId(String uniqueId) {
//            this.uniqueId = uniqueId;
//            return this;
//        }
//
//        public GetTagParticipantsRequest build() {
//            return new GetTagParticipantsRequest(this);
//        }
//    }
//}
