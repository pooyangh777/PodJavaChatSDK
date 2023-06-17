package chatSdk.dataTransferObject.contacts.outPut;


import chatSdk.dataTransferObject.contacts.inPut.Contact;
import chatSdk.dataTransferObject.contacts.inPut.Contacts;
import chatSdk.networking.api.ContactApi;
import lombok.Getter;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.util.List;

@Getter
public class AddContactRequest{
    private String firstName;
    private String lastName;
    private String cellphoneNumber;
    private String email;
    private String uniqueId;

    public AddContactRequest() {
    }

    public AddContactRequest(Builder builder) {
        this.firstName = builder.firstName;
        this.lastName = builder.lastName;
        this.cellphoneNumber = builder.cellphoneNumber;
        this.email = builder.email;
        this.uniqueId = builder.uniqueId;
    }

    public static class Builder{
        private String firstName;
        private String lastName;
        private String cellphoneNumber;
        private String email;
        private String uniqueId;

        public String getFirstName() {
            return firstName;
        }

        public Builder setFirstName(String firstName) {
            this.firstName = firstName;
            return this;
        }

        public String getLastName() {
            return lastName;
        }

        public Builder setLastName(String lastName) {
            this.lastName = lastName;
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

        public String getUniqueId() {
            return uniqueId;
        }

        public Builder setUniqueId(String uniqueId) {
            this.uniqueId = uniqueId;
            return this;
        }
        public AddContactRequest build(){
            return new AddContactRequest(this);
        }
    }
}



