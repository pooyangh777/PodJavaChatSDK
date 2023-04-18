package chatSdk.dataTransferObject;

import chatSdk.dataTransferObject.BaseOutPut;

public class ChatResponse<T> extends BaseOutPut {

    private T result;

    public T getResult() {
        return result;
    }

    public void setResult(T result) {
        this.result = result;
    }
}
