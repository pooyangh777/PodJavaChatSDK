package chatSdk.dataTransferObject.contacts.outPut;


import chatSdk.dataTransferObject.BaseRequestObject;

public class RequestGetContact extends BaseRequestObject {

    RequestGetContact(Builder builder) {
        super(builder);
    }

    public static class Builder extends BaseRequestObject.Builder<Builder>{

         public RequestGetContact build(){
           return new RequestGetContact(this);
         }

        @Override
        protected Builder self() {
            return this;
        }
    }
}