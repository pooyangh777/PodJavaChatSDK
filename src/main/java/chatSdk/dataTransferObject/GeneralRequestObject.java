package chatSdk.dataTransferObject;

public abstract class GeneralRequestObject  {
    private String typeCode;

    protected GeneralRequestObject(Builder<?> builder){
        this.typeCode = builder.typeCode;
    }

    public String getTypeCode() {
        return typeCode;
    }

    public void setTypeCode(String typeCode) {
        this.typeCode = typeCode;
    }

    protected abstract static class Builder<T extends Builder> {
        private String typeCode;
        protected abstract GeneralRequestObject build();


        public T typeCode(String typeCode){
            this.typeCode = typeCode;
            return self();
        }

        protected abstract T self();
    }
}
