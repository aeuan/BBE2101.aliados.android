package com.dacodes.bepensa.utils;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;

import androidx.appcompat.app.AlertDialog;

import com.dacodes.bepensa.AppController;
import com.dacodes.bepensa.activities.LoginActivity;
import com.dacodes.bepensa.entities.ofline.packageBrands.OflineBrandsEntity;
import com.dacodes.bepensa.entities.ofline.packageDivisions.OflineDivisionEntity;
import com.dacodes.bepensa.entities.ofline.packageOpportunity.OflineOpportunity;
import com.dacodes.bepensa.entities.ofline.packageOpportunity.OflineOpportunityType;
import com.dacodes.bepensa.entities.ofline.packageSurvey.OflineSurveyEntity;
import com.dacodes.bepensa.entities.ofline.postOpportunity.OflineListMedia;
import com.dacodes.bepensa.entities.ofline.postOpportunity.OflineMediaOpportunity;
import com.dacodes.bepensa.entities.ofline.postOpportunity.OflinePostOpportunity;

import io.realm.Realm;
import io.realm.RealmResults;

public class AlertDIalogLogout {

    Activity activity;
    Context mContext;
    AppController appController;
    Realm realm;
    survey survey;
    opportunity opportunity;
    logout logout;

    public interface survey{
        void dialogSurvey(int status);
    }

    public interface opportunity{
        void dialogOpportunity(int status);
    }

    public interface logout{
        void dialogLogout(int status);
    }

    public AlertDIalogLogout(Activity activity,AppController appController,survey survey,opportunity opportunity,logout logout) {
        this.activity = activity;
        this.appController=appController;
        this.survey=survey;
        this.opportunity=opportunity;
        this.logout=logout;
        realm=Realm.getDefaultInstance();
    }

    public AlertDIalogLogout(Context mContext,AppController appController,survey survey,opportunity opportunity,logout logout) {
        this.mContext = mContext;
        this.appController=appController;
        this.survey=survey;
        this.opportunity=opportunity;
        this.logout=logout;
        realm=Realm.getDefaultInstance();
    }

    public AlertDIalogLogout(Activity activity, Context mContext, AppController appController) {
        this.activity = activity;
        this.mContext = mContext;
        this.appController = appController;
        realm=Realm.getDefaultInstance();
    }

    public void checkDataBase(){
        String id_colaborator=appController.getUsername();
        RealmResults<OflinePostOpportunity> result1 = realm.where(OflinePostOpportunity.class).equalTo("id_colaborator",id_colaborator).findAll();
        RealmResults<OflineListMedia> result4 = realm.where(OflineListMedia.class).equalTo("id_colaborator",id_colaborator).findAll();
        RealmResults<OflineMediaOpportunity> result6 = realm.where(OflineMediaOpportunity.class).findAll();

        if (result1.size()>0){
            alertDialogOpportunityActivity(result1,result4,result6);
        }else{
            opportunity.dialogOpportunity(1);
        }
        final RealmResults<OflineDivisionEntity> result2 = realm.where(OflineDivisionEntity.class).findAll();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                result2.deleteAllFromRealm();
            }
        });

        final RealmResults<OflineBrandsEntity> result3 = realm.where(OflineBrandsEntity.class).findAll();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                result3.deleteAllFromRealm();
            }
        });


        RealmResults<OflineSurveyEntity> result5 = realm.where(OflineSurveyEntity.class).equalTo("has_sync_load",true).equalTo("id_colaborator",id_colaborator).findAll();
        RealmResults<OflineSurveyEntity> result12 = realm.where(OflineSurveyEntity.class).equalTo("has_sync_load",false).findAll();
        if (result5.size()>0){
            alertDialogSurveyActivityt(result5,result12);
        }else{
            final RealmResults<OflineSurveyEntity> result9 = realm.where(OflineSurveyEntity.class).findAll();
            survey.dialogSurvey(1);
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    result9.deleteAllFromRealm();
                }
            });

        }

        final RealmResults<OflineOpportunity> result7 = realm.where(OflineOpportunity.class).findAll();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                result7.deleteAllFromRealm();
            }
        });

        final RealmResults<OflineOpportunityType> result8 = realm.where(OflineOpportunityType.class).findAll();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                result8.deleteAllFromRealm();
            }
        });

        logout.dialogLogout(1);

    }

    public void checkDataBaseTerminate(){
        String id_colaborator=appController.getUsername();
        RealmResults<OflinePostOpportunity> result1 = realm.where(OflinePostOpportunity.class).equalTo("id_colaborator",id_colaborator).findAll();
        RealmResults<OflineListMedia> result4 = realm.where(OflineListMedia.class).equalTo("id_colaborator",id_colaborator).findAll();
        RealmResults<OflineMediaOpportunity> result6 = realm.where(OflineMediaOpportunity.class).findAll();

        if (result1.size()>0){
            alertDialogOpportunityActivity(result1,result4,result6);
        }else{
            opportunity.dialogOpportunity(1);
        }
        final RealmResults<OflineDivisionEntity> result2 = realm.where(OflineDivisionEntity.class).findAll();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                result2.deleteAllFromRealm();
            }
        });

        final RealmResults<OflineBrandsEntity> result3 = realm.where(OflineBrandsEntity.class).findAll();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                result3.deleteAllFromRealm();
            }
        });


        RealmResults<OflineSurveyEntity> result5 = realm.where(OflineSurveyEntity.class).equalTo("has_sync_load",true).equalTo("id_colaborator",id_colaborator).findAll();
        final RealmResults<OflineSurveyEntity> result10 = realm.where(OflineSurveyEntity.class).equalTo("has_sync_load",false).findAll();
        if (result5.size()>0){
            alertDialogSurveyActivityt(result5,result10);
        }else{
            final RealmResults<OflineSurveyEntity> result9 = realm.where(OflineSurveyEntity.class).findAll();
            survey.dialogSurvey(1);
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    result9.deleteAllFromRealm();
                }
            });

        }

        final RealmResults<OflineOpportunity> result7 = realm.where(OflineOpportunity.class).findAll();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                result7.deleteAllFromRealm();
            }
        });

        final RealmResults<OflineOpportunityType> result8 = realm.where(OflineOpportunityType.class).findAll();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                result8.deleteAllFromRealm();
            }
        });

    }

    public void checkDataBaseTerminateContext(){
        RealmResults<OflinePostOpportunity> result1 = realm.where(OflinePostOpportunity.class).findAll();
        RealmResults<OflineListMedia> result4 = realm.where(OflineListMedia.class).findAll();
        RealmResults<OflineMediaOpportunity> result6 = realm.where(OflineMediaOpportunity.class).findAll();

        if (result1.size()>0){
            alertDialogOpportunityContext(result1,result4,result6);
        }else{
            opportunity.dialogOpportunity(1);
        }
        final RealmResults<OflineDivisionEntity> result2 = realm.where(OflineDivisionEntity.class).findAll();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                result2.deleteAllFromRealm();
            }
        });

        final RealmResults<OflineBrandsEntity> result3 = realm.where(OflineBrandsEntity.class).findAll();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                result3.deleteAllFromRealm();
            }
        });


        RealmResults<OflineSurveyEntity> result5 = realm.where(OflineSurveyEntity.class).equalTo("has_sync_load",true).findAll();
        final RealmResults<OflineSurveyEntity> result10 = realm.where(OflineSurveyEntity.class).equalTo("has_sync_load",false).findAll();
        if (result5.size()>0){
            alertDialogSurveyContext(result5,result10);
        }else{
            final RealmResults<OflineSurveyEntity> result9 = realm.where(OflineSurveyEntity.class).findAll();
            survey.dialogSurvey(1);
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    result9.deleteAllFromRealm();
                }
            });

        }

        final RealmResults<OflineOpportunity> result7 = realm.where(OflineOpportunity.class).findAll();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                result7.deleteAllFromRealm();
            }
        });

        final RealmResults<OflineOpportunityType> result8 = realm.where(OflineOpportunityType.class).findAll();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                result8.deleteAllFromRealm();
            }
        });

    }

    public void sesionTerminate(){
        checkDataBaseTerminate();
        alertDialogActivity();
    }


    public void sesionTerminateContext(){
        checkDataBaseTerminateContext();
        alertDialogContext();
    }

    public void alertDialogActivity(){
        if (activity!=null) {
            final AlertDialog.Builder builder = new AlertDialog.Builder(activity);
            builder.setMessage("Se ha iniciado sesión desde otro dispositivo con la misma cuenta de usuario")
                    .setCancelable(false)
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            logout.dialogLogout(1);
                        }
                    })
                    .show();
        }
    }

    public void alertDialogContext(){
        final AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setMessage("Se ha iniciado sesión desde otro dispositivo con la misma cuenta de usuario")
                .setCancelable(false)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        logout.dialogLogout(1);
                    }
                })
                .show();
    }

    public void alertDialogSurveyActivityt(final RealmResults<OflineSurveyEntity> realmResults,
                                           final RealmResults<OflineSurveyEntity> response){
        final AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setMessage("Aun tienes encuestas sin sincronizar ¿Deseas conservar los datos en el telefono?")
                .setCancelable(false)
                .setPositiveButton("Si", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        survey.dialogSurvey(1);
                        Log.e("sixe",""+realmResults.size());
                        Log.e("asd",""+response.size());
                        realm.executeTransaction(new Realm.Transaction() {
                            @Override
                            public void execute(Realm realm) {
                                response.deleteAllFromRealm();
                            }
                        });
                        dialog.dismiss();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        survey.dialogSurvey(1);
                        realm.executeTransaction(new Realm.Transaction() {
                            @Override
                            public void execute(Realm realm) {
                                realmResults.deleteAllFromRealm();
                                response.deleteAllFromRealm();
                            }
                        });
                        dialogInterface.dismiss();
                        Log.e("sixe",""+realmResults.size());
                        Log.e("asd",""+response.size());
                    }
                })
                .show();
    }

    public void alertDialogSurveyContext(final RealmResults<OflineSurveyEntity> realmResults,
                                           final RealmResults<OflineSurveyEntity> response){
        final AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setMessage("Aun tienes encuestas sin sincronizar ¿Deseas conservar los datos en el telefono?")
                .setCancelable(false)
                .setPositiveButton("Si", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        survey.dialogSurvey(1);
                        Log.e("sixe",""+realmResults.size());
                        Log.e("asd",""+response.size());
                        realm.executeTransaction(new Realm.Transaction() {
                            @Override
                            public void execute(Realm realm) {
                                response.deleteAllFromRealm();
                            }
                        });
                        dialog.dismiss();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        survey.dialogSurvey(1);
                        realm.executeTransaction(new Realm.Transaction() {
                            @Override
                            public void execute(Realm realm) {
                                realmResults.deleteAllFromRealm();
                                response.deleteAllFromRealm();
                            }
                        });
                        dialogInterface.dismiss();
                        Log.e("sixe",""+realmResults.size());
                        Log.e("asd",""+response.size());
                    }
                })
                .show();
    }

    public void alertDialogOpportunityActivity(final RealmResults<OflinePostOpportunity> realmResults
            , final RealmResults<OflineListMedia> result4 , final RealmResults<OflineMediaOpportunity> result6){
        final AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setMessage("Aun tienes oportunidades sin sincronizar ¿Deseas conservar los datos en el telefono?")
                .setCancelable(false)
                .setPositiveButton("Si", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        opportunity.dialogOpportunity(1);
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                        opportunity.dialogOpportunity(1);
                        realm.executeTransaction(new Realm.Transaction() {
                            @Override
                            public void execute(Realm realm) {
                                realmResults.deleteAllFromRealm();
                                result4.deleteAllFromRealm();
                                result6.deleteAllFromRealm();
                            }
                        });
                    }
                })
                .show();
    }

    public void alertDialogOpportunityContext(final RealmResults<OflinePostOpportunity> realmResults
            , final RealmResults<OflineListMedia> result4 , final RealmResults<OflineMediaOpportunity> result6){
        final AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setMessage("Aun tienes oportunidades sin sincronizar ¿Deseas conservar los datos en el telefono?")
                .setCancelable(false)
                .setPositiveButton("Si", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        opportunity.dialogOpportunity(1);
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                        opportunity.dialogOpportunity(1);
                        realm.executeTransaction(new Realm.Transaction() {
                            @Override
                            public void execute(Realm realm) {
                                realmResults.deleteAllFromRealm();
                                result4.deleteAllFromRealm();
                                result6.deleteAllFromRealm();
                            }
                        });
                    }
                })
                .show();
    }

    public void logoutAction(){
        appController.clearPreferences();
        Intent intent = new Intent(activity, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        mContext.startActivity(intent);
        realm.close();
    }

}
