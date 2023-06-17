package chatSdk.networking.api;


import chatSdk.chat.Chat;
import chatSdk.dataTransferObject.contacts.inPut.ContactRemove;
import chatSdk.dataTransferObject.contacts.inPut.Contacts;
import chatSdk.dataTransferObject.contacts.inPut.SearchContactVO;
import chatSdk.dataTransferObject.contacts.inPut.UpdateContact;
import chatSdk.dataTransferObject.contacts.outPut.AddContactRequest;
import chatSdk.networking.retrofithelper.ChatResponse;
import com.google.gson.JsonObject;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.http.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public interface ContactApi {

    @POST("srv/core/nzh/addContacts")
    @FormUrlEncoded
    Call<Contacts> addContact(@Header("_token_") String token
            , @Header("_token_issuer_") int tokenIssuer
            , @Field("firstName") String firstName
            , @Field("lastName") String lastName
            , @Field("email") String email
            , @Field("uniqueId") String uniqueId
            , @Field("cellphoneNumber") String cellphoneNumber
    );

    /* addContact Without type code */

    @POST("/srv/core/nzh/addContacts")
    @FormUrlEncoded
    Call<Contacts> addContact(@Header("_token_") String token
            , @Header("_token_issuer_") int tokenIssuer
            , @Field("firstName") String firstName
            , @Field("lastName") String lastName
            , @Field("email") String email
            , @Field("uniqueId") String uniqueId
            , @Field("cellphoneNumber") String cellphoneNumber
            , @Field("typeCode") String typeCode
    );

    /* addContacts With type code*/
    @POST("nzh/addContacts")
    @FormUrlEncoded
    Call<Response<Contacts>> addContacts(@Header("_token_") String token
            , @Header("_token_issuer_") int tokenIssuer
            , @Field("firstName") ArrayList<String> firstName
            , @Field("lastName") ArrayList<String> lastName
            , @Field("email") ArrayList<String> email
            , @Field("uniqueId") ArrayList<String> uniqueId
            , @Field("cellphoneNumber") ArrayList<String> cellphoneNumber
            , @Field("typeCode") ArrayList<String> typeCode
    );


    @POST("nzh/addContacts")
    Call<Response<List<Contacts>>> addContacts(
            @Header("_token_") String token,
            @Header("_token_issuer_") int tokenIssuer,
            @Body List<AddContactRequest> requests
    );


    /* addContacts Without type code*/
    @POST("srv/core/nzh/addContacts")
    @FormUrlEncoded
    Call<Response<Contacts>> addContacts(@Header("_token_") String token
            , @Header("_token_issuer_") int tokenIssuer
            , @Header("typeCode") String typeCode
            , @Field("firstName") ArrayList<String> firstName
            , @Field("lastName") ArrayList<String> lastName
            , @Field("email") ArrayList<String> email
            , @Field("uniqueId") ArrayList<String> uniqueId
            , @Field("cellphoneNumber") ArrayList<String> cellphoneNumber
    );

    @POST("addContacts")
    Call<Response<Contacts>> addContacts(
            @Header("_token_") String token,
            @Header("_token_issuer_") int tokenIssuer,
            @Body RequestBody requestBody
            );
    @retrofit2.http.POST("addContacts")
    Call<ChatResponse<Chat.ContactResponse>> addContacts(@Body List<AddContactRequest> request);
    @retrofit2.http.POST("/srv/core/nzh/addContacts")
    Call<JsonObject> addContacts(  @Header("_token_") String token,
                                                           @Header("_token_issuer_") String tokenIssuer,
                                                           @Body List<AddContactRequest> request);
    @FormUrlEncoded
    @POST("/srv/core/nzh/addContacts")
    Call<JsonObject> addContacts(@Header("token") String token,
                             @Header("token_issuer") String tokenIssuer,
                             @FieldMap Map<String, List<AddContactRequest>> contactLists);

//    @POST("addContacts")
//    @FormUrlEncoded
//    Call<Response<Contacts>> addContacts(
//            @Header("_token_") String token,
//            @Header("_token_issuer_")
//            @FieldMap Map<String, String> fields
//    );

//    @POST("srv/core/nzh/addContacts")
//    @FormUrlEncoded
//    Call<Response<Contacts>> addContacts(@Header("_token_") String token, @Header("_token_issuer_") int tokenIssuer, @QueryMap Map queryParameters);


    @POST("/srv/basic-platform/nzh/removeContacts")
    @FormUrlEncoded
    Call<ContactRemove> removeContact(@Header("_token_") String token
            , @Header("_token_issuer_") int tokenIssuer
            , @Field("id") long userId);


    @POST("/srv/basic-platform/nzh/removeContacts")
    @FormUrlEncoded
    Call<ContactRemove> removeContact(@Header("_token_") String token
            , @Header("_token_issuer_") int tokenIssuer
            , @Field("id") long userId
            , @Field("typeCode") String typeCode
    );

    /* Update contact without type code*/
    @POST("/srv/basic-platform/nzh/updateContacts")
    @FormUrlEncoded
    Call<UpdateContact> updateContact(@Header("_token_") String token
            , @Header("_token_issuer_") int tokenIssuer
            , @Field("id") long id
            , @Field("firstName") String firstName
            , @Field("lastName") String lastName
            , @Field("email") String email
            , @Field("uniqueId") String uniqueId
            , @Field("cellphoneNumber") String cellphoneNumber);


    /* Update contact with type code*/
    @POST("/srv/basic-platform/nzh/updateContacts")
    @FormUrlEncoded
    Call<UpdateContact> updateContact(@Header("_token_") String token
            , @Header("_token_issuer_") int tokenIssuer
            , @Field("id") long id
            , @Field("firstName") String firstName
            , @Field("lastName") String lastName
            , @Field("email") String email
            , @Field("uniqueId") String uniqueId
            , @Field("cellphoneNumber") String cellphoneNumber
            , @Field("typeCode") String typeCode);

    @GET("/srv/basic-platform/nzh/listContacts")
    Call<SearchContactVO> searchContact(@Header("_token_") String token
            , @Header("_token_issuer_") int tokenIssuer
            , @Query("id") String id
            , @Query("firstName") String firstName
            , @Query("lastName") String lastName
            , @Query("email") String email
            , @Query("offset") String offset
            , @Query("size") String size
            , @Query("typeCode") String typeCode
            , @Query("q") String query
            , @Query("cellphoneNumber") String cellphoneNumber);
}
