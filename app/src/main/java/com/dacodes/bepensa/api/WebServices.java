package com.dacodes.bepensa.api;

import androidx.annotation.Nullable;

import com.dacodes.bepensa.DeviceId;
import com.dacodes.bepensa.entities.CategoriesEntity;
import com.dacodes.bepensa.entities.DetailPromotionsEntity;
import com.dacodes.bepensa.entities.EventosEntity;
import com.dacodes.bepensa.entities.LoginUser;
import com.dacodes.bepensa.entities.Opportunity;
import com.dacodes.bepensa.entities.PackegeOpportunity.BrandEntity;
import com.dacodes.bepensa.entities.PackegeOpportunity.DivisionEntity;
import com.dacodes.bepensa.entities.ProfileEntity;
import com.dacodes.bepensa.entities.ProfileUserEntity;
import com.dacodes.bepensa.entities.PushRegistrationModelResponse;
import com.dacodes.bepensa.entities.RecoverPassword;
import com.dacodes.bepensa.entities.ResponseSettings;
import com.dacodes.bepensa.entities.SuggestionsEntity;
import com.dacodes.bepensa.entities.awardsRedeem.AwardRedeemResponse;
import com.dacodes.bepensa.entities.feed.FeedResponseEntity;
import com.dacodes.bepensa.entities.promotion.PromotionsEntity;
import com.dacodes.bepensa.entities.surveys.SurveyResult;
import com.dacodes.bepensa.entities.surveys.SurveySucces;
import com.dacodes.bepensa.entities.surveys.result.SurveyDataEntity;
import com.dacodes.bepensa.models.AwardsModel;
import com.dacodes.bepensa.models.ChatResponseModel;
import com.dacodes.bepensa.models.ComunicadosResponseModel;
import com.dacodes.bepensa.models.EventosResponseModel;
import com.dacodes.bepensa.models.NewData;
import com.dacodes.bepensa.models.NewOpportunityResponse;
import com.dacodes.bepensa.models.NotificacionesResponseModel;
import com.dacodes.bepensa.models.OpportunityDetailModel;
import com.dacodes.bepensa.models.OpportunityModel;
import com.dacodes.bepensa.models.PointsResponseModel;
import com.dacodes.bepensa.models.RedeemResponse;
import com.dacodes.bepensa.models.ResponseVersionPlayStore;
import com.dacodes.bepensa.models.SurveyResponseModel;
import com.dacodes.bepensa.models.ValidateRegistrationEntity;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.HTTP;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.Url;

public interface WebServices {

    @POST("v1/auth/login")
    @Headers("Content-Type: application/json")
    Call<ProfileEntity> loginUser(@Body LoginUser loginUser);

    /**
     * Obtener divisiones de bepensa
     * @return lista de divisiones
     */
    @GET("v1/divisions/")
    Call<List<DivisionEntity>> getDivisions ();

    @GET("v1/divisions/authorized")
    Call<List<DivisionEntity>> getDivisionsState(
            @Header("Authorization")String token,
            @Query("state")int state);


    /**
     * Obtener oportunidades de bepensa
     * @return lista de oportunidades
     */
//    @GET("v1/opportunities/myrecords")

    @GET
    Call<OpportunityModel> getOpportunities(
            @Url String url,
            @Header("Authorization") String token
    );

    @GET
    Call<ProfileUserEntity> getProfile(
            @Url String url,
            @Header("Authorization") String token
    );

    /**
     * Obtener ranking de bepensa
     * @return lista de personas en el ranking
     */
    @GET("v1/collaborators/ranking")
    Call<List<ProfileEntity>> getRanking (
            @Header("Authorization") String token
    );



    /**
     * Obtener brands
     * @return lista de personas en el ranking
     */
    @GET("v1/brands/{id_division}")
    Call<List<BrandEntity>> getBrands (
            @Header("Authorization") String token,
            @Path("id_division") int id_division
    );

    @GET
    Call<List<BrandEntity>> getBrand (
            @Url String url,
            @Header("Authorization") String token
    );

    /**
     * Obtener oportunities types
     * @return lista de personas en el ranking
     */
    @GET("v1/brands/{id_division}")
    Call<List<BrandEntity>> getOportunitiesTypes (
            @Header("Authorization") String token,
            @Path("id_division") int id_division
    );

    @GET("v1/promotions/categories")
    Call<List<CategoriesEntity>> getCategories(
            @Header("Authorization") String token
    );

    @GET
    Call<List<Opportunity>> getOportunitiesType (
            @Url String url,
            @Header("Authorization") String token
    );

    /**
     * Obtener detalle de una oportunidad de bepensa
     * @return detalle de una oportunidad
     */
    @GET("v1/opportunities/report/{opportunityId}")
    Call<OpportunityDetailModel> getOpportunityDetailModel(
            @Header("Authorization") String token,
            @Path("opportunityId") String opportunityId
    );

    @GET//("v1/collaborators/surveys/{survey_id}")
    Call<SurveyDataEntity> getSurveyDetailModel(
            @Header("Authorization") String token,
            @Url String survey_id
    );
    /**
     * Obtener detalle de una oportunidad de bepensa
     * @return detalle de una oportunidad
     */
    @GET//("v1/events/{eventId}")
    Call<EventosEntity> getEventDetail(
            @Header("Authorization") String token,
            @Url String eventId
    );

    /**
     * Endpoint para validar un usuario antes de registrarlo
     * @param username numero de cliente de bepensa
     * @param email email del usuario
     * @param contact telefono de contacto
     * @return modelo de success para pasar a la siguiente pantalla
     */
    @FormUrlEncoded
    @POST("v1/auth/registry/validate")
    Call<ValidateRegistrationEntity> validateUser(
            @Field("username") String username,
            @Field("division") String email,
            @Field("password") String contact
    );

    @FormUrlEncoded
    @POST("v1/collaborators/suggestions")
    Call<SuggestionsEntity> postSuggestions(
            @Header("Authorization") String token,
            @Field("message") String message
    );

    @FormUrlEncoded
    @POST("v1/auth/recover-password")
    Call<SurveyResult> postPassword(
            @Header("Authorization") String token,
            @Field("username") String username,
            @Field("division") int division,
            @Field("birthday") String birthday
    );


    /**
     * Endpoint para registrar un colaborador de bepensa
     * @param username numero de cliente de bepensa
     * @param email email del usuario
     * @param contact telefono de contacto
     * @param password password del usuario
     * @param division division de bepensa
     * @param code_sms codigo de verificacion por msj
     * @return login para bepensa
     */
    @FormUrlEncoded
    @POST("v1/auth/registry")
    Call<ProfileEntity> registerUser(
            @Field("username") String username,
            @Field("email") String email,
            @Field("contact") String contact,
            @Field("password") String password,
            @Field("division") String division,
            @Field("code_sms") int code_sms
            );

    /*
      Oportunidades online
     */

    @FormUrlEncoded
    @POST("v1/opportunities/report")
    Call<NewOpportunityResponse> postOpportunity(
            @Header("Authorization") String token,
            @Field("address") String address,
            @Field("version") String version,
            @Field("division") int division,
            @Field("type") int type,
            @Field("brand_items") String brands,
            @Field("description") String description,
            @Field("lat") double lat,
            @Field("lng") double lng,
            @Field("state") String state,
            @Field("city") String city,
            @Field("has_media") int has_media,
            @Field("region")int region,
            @Field("extra_field_title") String extra_field_title,
            @Field("extra_field_value") String extra_field_value);

    @FormUrlEncoded
    @POST("v1/opportunities/report")
    Call<NewOpportunityResponse> postOpportunity(
            @Header("Authorization") String token,
            @Field("address") String address,
            @Field("version") String version,
            @Field("division") int division,
            @Field("type") int type,
            @Field("brand_items") String brands,
            @Field("description") String description,
            @Field("lat") double lat,
            @Field("lng") double lng,
            @Field("state") String state,
            @Field("city") String city,
            @Field("has_media") int has_media,
            @Field("region")int region,
            @Field("extra_field_title") String extra_field_title);

    @FormUrlEncoded
    @POST("v1/opportunities/report")
    Call<NewOpportunityResponse> postOpportunity(
            @Header("Authorization") String token,
            @Field("address") String address,
            @Field("version") String version,
            @Field("division") int division,
            @Field("type") int type,
            @Field("brand_items") String brands,
            @Field("description") String description,
            @Field("lat") double lat,
            @Field("lng") double lng,
            @Field("state") String state,
            @Field("city") String city,
            @Field("has_media") int has_media,
            @Field("region")int region);

    @FormUrlEncoded
    @POST("v1/opportunities/report")
    Call<NewOpportunityResponse> postOpportunity(
            @Header("Authorization") String token,
            @Field("address") String address,
            @Field("version") String version,
            @Field("division") int division,
            @Field("brand_items") String brands,
            @Field("description") String description,
            @Field("lat") double lat,
            @Field("lng") double lng,
            @Field("state") String state,
            @Field("city") String city,
            @Field("has_media") int has_media,
            @Field("region")int region);

    @FormUrlEncoded
    @POST("v1/opportunities/report")
    Call<NewOpportunityResponse> postOpportunity(
            @Header("Authorization") String token,
            @Field("address") String address,
            @Field("version") String version,
            @Field("division") int division,
            @Field("brand_items") String brands,
            @Field("description") String description,
            @Field("lat") double lat,
            @Field("lng") double lng,
            @Field("state") String state,
            @Field("city") String city,
            @Field("has_media") int has_media,
            @Field("region")int region,
            @Field("extra_field_title") String extra_field_title,
            @Field("extra_field_value") String extra_field_value);

    @FormUrlEncoded
    @POST("v1/opportunities/report")
    Call<NewOpportunityResponse> postOpportunityNu(
            @Header("Authorization") String token,
            @Field("version") String version,
            @Field("division") int division,
            @Field("brand_items") String brands,
            @Field("description") String description,
            @Field("has_media") int has_media,
            @Field("extra_field_title") String extra_field_title,
            @Field("extra_field_value") String extra_field_value);

    @FormUrlEncoded
    @POST("v1/opportunities/report")
    Call<NewOpportunityResponse> postOpportunityNu(
            @Header("Authorization") String token,
            @Field("version") String version,
            @Field("division") int division,
            @Field("type") int type,
            @Field("brand_items") String brands,
            @Field("description") String description,
            @Field("has_media") int has_media,
            @Field("extra_field_title") String extra_field_title,
            @Field("extra_field_value") String extra_field_value);

    @FormUrlEncoded
    @POST("v1/opportunities/report")
    Call<NewOpportunityResponse> postOpportunityTitleOption(
            @Header("Authorization") String token,
            @Field("version") String version,
            @Field("division") int division,
            @Field("type") int type,
            @Field("brand_items") String brands,
            @Field("description") String description,
            @Field("has_media") int has_media,
            @Field("extra_field_title") String extra_field_title);

    @FormUrlEncoded
    @POST("v1/opportunities/report")
    Call<NewOpportunityResponse> postOpportunityTitleOption(
            @Header("Authorization") String token,
            @Field("version") String version,
            @Field("division") int division,
            @Field("brand_items") String brands,
            @Field("description") String description,
            @Field("has_media") int has_media,
            @Field("extra_field_title") String extra_field_title);

    @FormUrlEncoded
    @POST("v1/opportunities/report")
    Call<NewOpportunityResponse> postOpportunity(
            @Header("Authorization") String token,
            @Field("version") String version,
            @Field("division") int division,
            @Field("brand_items") String brands,
            @Field("description") String description,
            @Field("has_media") int has_media);

    @FormUrlEncoded
    @POST("v1/opportunities/report")
    Call<NewOpportunityResponse> postOpportunity(
            @Header("Authorization") String token,
            @Field("version") String version,
            @Field("division") int division,
            @Field("type") int type,
            @Field("brand_items") String brands,
            @Field("description") String description,
            @Field("has_media") int has_media);

    @FormUrlEncoded
    @POST("v1/opportunities/report")
    Call<NewOpportunityResponse> postOpportunity(
            @Header("Authorization") String token,
            @Field("address") String address,
            @Field("version") String version,
            @Field("division") int division,
            @Field("brand_items") String brands,
            @Field("description") String description,
            @Field("lat") double lat,
            @Field("lng") double lng,
            @Field("state") String state,
            @Field("city") String city,
            @Field("has_media") int has_media,
            @Field("region")int region,
            @Field("extra_field_title") String extra_field_title);

        /*
      Oportunidades Ofiline
     */

    @FormUrlEncoded
    @POST("v1/opportunities/report")
    Call<NewOpportunityResponse> postOpportunityLocal(
            @Header("Authorization") String token,
            @Field("hash_sync") String key_hash,
            @Field("address") String address,
            @Field("version") String version,
            @Field("division") int division,
            @Field("type") int type,
            @Field("brand_items") String brands,
            @Field("description") String description,
            @Field("lat") double lat,
            @Field("lng") double lng,
            @Field("state") String state,
            @Field("city") String city,
            @Field("has_media") int has_media,
            @Field("region")int region,
            @Field("extra_field_title") String extra_field_title,
            @Field("extra_field_value") String extra_field_value);

    @FormUrlEncoded
    @POST("v1/opportunities/report")
    Call<NewOpportunityResponse> postOpportunityLocal(
            @Header("Authorization") String token,
            @Field("hash_sync") String key_hash,
            @Field("version") String version,
            @Field("division") int division,
            @Field("type") int type,
            @Field("brand_items") String brands,
            @Field("description") String description,
            @Field("has_media") int has_media,
            @Field("extra_field_title") String extra_field_title,
            @Field("extra_field_value") String extra_field_value);

    @FormUrlEncoded
    @POST("v1/opportunities/report")
    Call<NewOpportunityResponse> postOpportunityLocal(
            @Header("Authorization") String token,
            @Field("hash_sync") String key_hash,
            @Field("address") String address,
            @Field("version") String version,
            @Field("division") int division,
            @Field("type") int type,
            @Field("brand_items") String brands,
            @Field("description") String description,
            @Field("lat") double lat,
            @Field("lng") double lng,
            @Field("state") String state,
            @Field("city") String city,
            @Field("has_media") int has_media,
            @Field("region")int region,
            @Field("extra_field_title") String extra_field_title);

    @FormUrlEncoded
    @POST("v1/opportunities/report")
    Call<NewOpportunityResponse> postOpportunityLocal(
            @Header("Authorization") String token,
            @Field("hash_sync") String key_hash,
            @Field("version") String version,
            @Field("division") int division,
            @Field("type") int type,
            @Field("brand_items") String brands,
            @Field("description") String description,
            @Field("has_media") int has_media,
            @Field("extra_field_title") String extra_field_title);

    @FormUrlEncoded
    @POST("v1/opportunities/report")
    Call<NewOpportunityResponse> postOpportunityLocal(
            @Header("Authorization") String token,
            @Field("hash_sync") String key_hash,
            @Field("address") String address,
            @Field("version") String version,
            @Field("division") int division,
            @Field("type") int type,
            @Field("brand_items") String brands,
            @Field("description") String description,
            @Field("lat") double lat,
            @Field("lng") double lng,
            @Field("state") String state,
            @Field("city") String city,
            @Field("has_media") int has_media,
            @Field("region")int region);

    @FormUrlEncoded
    @POST("v1/opportunities/report")
    Call<NewOpportunityResponse> postOpportunityLocal(
            @Header("Authorization") String token,
            @Field("hash_sync") String key_hash,
            @Field("version") String version,
            @Field("division") int division,
            @Field("type") int type,
            @Field("brand_items") String brands,
            @Field("description") String description,
            @Field("has_media") int has_media);

    @FormUrlEncoded
    @POST("v1/opportunities/report")
    Call<NewOpportunityResponse> postOpportunityLocal(
            @Header("Authorization") String token,
            @Field("hash_sync") String key_hash,
            @Field("address") String address,
            @Field("version") String version,
            @Field("division") int division,
            @Field("brand_items") String brands,
            @Field("description") String description,
            @Field("lat") double lat,
            @Field("lng") double lng,
            @Field("state") String state,
            @Field("city") String city,
            @Field("has_media") int has_media,
            @Field("region")int region);

    @FormUrlEncoded
    @POST("v1/opportunities/report")
    Call<NewOpportunityResponse> postOpportunityLocal(
            @Header("Authorization") String token,
            @Field("hash_sync") String key_hash,
            @Field("address") String address,
            @Field("version") String version,
            @Field("division") int division,
            @Field("brand_items") String brands,
            @Field("description") String description,
            @Field("lat") double lat,
            @Field("lng") double lng,
            @Field("state") String state,
            @Field("city") String city,
            @Field("has_media") int has_media,
            @Field("region")int region,
            @Field("extra_field_title") String extra_field_title,
            @Field("extra_field_value") String extra_field_value);

    @FormUrlEncoded
    @POST("v1/opportunities/report")
    Call<NewOpportunityResponse> postOpportunityLocal(
            @Header("Authorization") String token,
            @Field("hash_sync") String key_hash,
            @Field("address") String address,
            @Field("version") String version,
            @Field("division") int division,
            @Field("brand_items") String brands,
            @Field("description") String description,
            @Field("lat") double lat,
            @Field("lng") double lng,
            @Field("state") String state,
            @Field("city") String city,
            @Field("has_media") int has_media,
            @Field("region")int region,
            @Field("extra_field_title") String extra_field_title);


    /**
     * Obtener awards
     * @return lista de awards
     */
    @GET("v1/awards")
    Call<List<AwardsModel>> getAwards (
            @Header("Authorization") String token
    );


    /**
     * Post awards
     * @return lista de awards
     */
    @FormUrlEncoded
    @POST("v1/awards/redeem")
    Call<RedeemResponse> postAwards (
            @Header("Authorization") String token,
            @Field("award_id") int award_id
    );

    @Multipart
    @PUT("v1/opportunities/upload/{id}")
    Call<Void> putMediaOpportunity(
            @Header("Authorization") String token,
            @Path("id") int new_opportunity_id,
            @Part MultipartBody.Part media_image0,
            @Part MultipartBody.Part media_image1,
            @Part MultipartBody.Part media_image2,
            @Part MultipartBody.Part media_video0
    );


    @FormUrlEncoded
    @PUT("v1/auth/recover-password")
    Call<SurveySucces> putNewPassword(
            @Header("Authorization") String token,
            @Field("username") int new_opportunity_id,
            @Field("division") int division,
            @Field("password") String password
    );

    @FormUrlEncoded
    @PUT("v1/auth/recover-password")
    Call<SurveySucces> putResetPassword(
            @Header("Authorization") String token,
            @Field("username") int username,
            @Field("division") int division,
            @Field("password") String password,
            @Field("code_sms")int sms
    );

    @PUT("v1/auth/recoverpass-unsafe")
    @Headers("Content-Type: application/json")
    Call<Void> putResetPasswordV2(
            @Header("Authorization") String token,
            @Body RecoverPassword recoverPassword);

    @FormUrlEncoded
    @PUT("v1/notifications/{notice_id}")
    Call<Void> putNotification(
            @Header("Authorization") String token,
            @Path("notice_id") int notice_id,
            @Field("status") String status
    );

    @FormUrlEncoded
    @PUT("v1/collaborators/privacy")
    Call<Void> putRankingPrivacy(
            @Header("Authorization") String token,
            @Field("hidden_ranking") int hidden_ranking
    );

    @PUT("v1/collaborators/surveys")
    Call<SurveySucces> putSurveyQuestion(
            @Header("Authorization") String token,
            @Body SurveyResult surveyResult);

    @Multipart
    @PUT("v1/collaborators/profile")
    Call<ProfileEntity> putProfile(
            @Header("Authorization") String token,
            @Nullable @Part("contact") RequestBody contact,
            @Nullable @Part("email") RequestBody email,
            @Nullable @Part("sms_code")RequestBody sms_code,
            @Nullable @Part MultipartBody.Part avatar_image
    );

    @Multipart
    @PUT("v1/collaborators/profile")
    Call<SurveySucces> putEmail(
            @Header("Authorization") String token,
            @Part("email") RequestBody email
    );

    @FormUrlEncoded
    @PUT("v1/collaborators/validate/contact-change")
    Call<Void> ValidateContact(
            @Header("Authorization") String token,
            @Field("contact") String contact
    );

    /**
     * Endpoint para registrar el token de un usuario
     * @param device_id device id
     * @param registration_id registra
     * @param type tipo de sistema operativo
     * @param name modelo del dispositivo
     * @return response
     */
    @FormUrlEncoded
    @POST("v1/auth/devices")
    Call<PushRegistrationModelResponse> registerPushServices(
            @Header("Authorization") String token,
            @Field("device_id") String device_id,
            @Field("registration_id") String registration_id,
            @Field("type") String type,
            @Field("name") String name
    );

    @HTTP(method = "DELETE", path = "v1/auth/devices/{device_id}", hasBody = true)
    Call<SurveyResult> deleteDevice(
            @Header("Authorization") String token,
            @Path("device_id")String device_id,
            @Body DeviceId aut
    );

    /**
     * Endpoint para quitar registrar el token de un usuario
     * @param device_id device id
     * @return response
     */
    @FormUrlEncoded
    @DELETE("v1/auth/devices")
    Call<PushRegistrationModelResponse> unregisterPushServices(
            @Header("Authorization") String token,
            @Field("device_id") String device_id
    );



    /**
     * Endpoint para enviar por SMS codigo de verificación
     * @param employe_number codigo de colaborador
     * @param phone telefono al cual se enviara un SMS
     * @return respuesta
     */
    @FormUrlEncoded
    @POST("v1/auth/phone/validate")
    Call<ValidateRegistrationEntity> verifyPhoneNumber(
            @Field("employe_number") String employe_number,
            @Field("phone") String phone,
            @Field("division")int division,
            @Field("birthday")String imss
    );


    /**
     *
     * @param url
     * @param token
     * @return
     */
    @GET
    Call<EventosResponseModel> getEventos(
            @Url String url,
            @Header("Authorization") String token
    );

    @GET
    Call<AwardRedeemResponse> getAwardsRedeem(
            @Url String url,
            @Header("Authorization") String token
    );

    @GET
    Call<PromotionsEntity> getPromotions(
            @Url String url,
            @Header("Authorization") String token
    );

    @GET
    Call<DetailPromotionsEntity> getPromotionsDetail(
            @Url String url,
            @Header("Authorization") String token
    );

    @GET
    Call<SurveyResponseModel> getSurveys(
            @Url String url,
            @Header("Authorization") String token
    );

    @GET
    Call<FeedResponseEntity> getFeed(
            @Url String url,
            @Header("Authorization") String token
    );

    /**
     *
     * @param url
     * @param token
     * @return
     */
    @GET
    Call<NotificacionesResponseModel> getNotificaciones(
            @Url String url,
            @Header("Authorization") String token
    );

    @GET
    Call<NotificacionesResponseModel> getNotificaciones(
            @Url String url,
            @Header("Authorization") String token,
            @Query("themes") String themes
    );


    /**
     *
     * @param url
     * @param token
     * @return
     */
    @GET
    Call<ComunicadosResponseModel> getComunicados(
            @Url String url,
            @Header("Authorization") String token
    );


    /**
     *
     * @param url
     * @param token
     * @return
     */
    @GET
    Call<ChatResponseModel> getChat(
            @Url String url,
            @Header("Authorization") String token
    );


    /**
     *
     * @param token
     * @return modelo de puntos y posicion en el ranking de un colaborador
     */
    @GET("v1/collaborators/points")
    Call<PointsResponseModel> getPoints (
            @Header("Authorization") String token);

    @GET("v1/auth/mobile/version/{mobile}")
    Call<ResponseVersionPlayStore> getVersionPlay(
            @Path("mobile")String mobile,
            @Header("Authorization")String token
    );

    @GET("v1/notifications/themes-list")
    Call<List<CategoriesEntity>> getFilterThemesNotifications(@Header("Authorization") String token);

    @GET("v1/auth/settings/0/")
    Call<ResponseSettings> getSettings();

    @GET("v1/auth/mobile/newdata/")
    Call<NewData> getNewData(@Header("Authorization")String token);

}
