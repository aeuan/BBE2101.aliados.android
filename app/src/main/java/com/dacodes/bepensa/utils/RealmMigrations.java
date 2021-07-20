package com.dacodes.bepensa.utils;

import io.realm.DynamicRealm;
import io.realm.RealmMigration;
import io.realm.RealmObjectSchema;
import io.realm.RealmSchema;

public class RealmMigrations implements RealmMigration {

    @Override
    public void migrate(DynamicRealm realm, long oldVersion, long newVersion) {
        RealmSchema schema = realm.getSchema();
        if (oldVersion == 1) {
            RealmObjectSchema opportunity = schema.get("OflinePostOpportunity");
            //userSchema.addField("allow_reports", boolean.class);
            // Get Schema of a Particular Class. Change --Testing-- with the respected pojo class name
            // Add a new temporary field with the new type which you want to migrate. 'id_tmp' is a temporary integer field.
            opportunity.addField("id_tmp", String.class);

            // Set the previous value to the new temporary field
            opportunity.transform(obj -> {
                // Implement the functionality to change the type. Below I just transformed a string type to int type by casting the value. Implement your methodology below.
                String id = ""+obj.getInt("id_colaborator");

                obj.setString("id_tmp", id);
            });

            // Remove the existing field
            opportunity.removeField("id_colaborator");

            // Rename the temporary field which hold the transformed value to the field name which you insisted to migrate.
            opportunity.renameField("id_tmp", "id_colaborator");

            //cambiar de int id a string
            RealmObjectSchema survey = schema.get("OflineSurveyEntity");
            survey.addField("id_tmp", String.class);
            survey.transform(obj -> {
                // Implement the functionality to change the type. Below I just transformed a string type to int type by casting the value. Implement your methodology below.
                String id = ""+obj.getInt("id_colaborator");

                obj.setString("id_tmp", id);
            });
            survey.removeField("id_colaborator");
            survey.renameField("id_tmp", "id_colaborator");

            //cambiar de int id a string
            RealmObjectSchema medias = schema.get("OflineListMedia");
            medias.addField("id_tmp", String.class);
            medias.transform(obj -> {
                // Implement the functionality to change the type. Below I just transformed a string type to int type by casting the value. Implement your methodology below.
                String id = ""+obj.getInt("id_colaborator");

                obj.setString("id_tmp", id);
            });
            medias.removeField("id_colaborator");
            medias.renameField("id_tmp", "id_colaborator");
        }else if (oldVersion == 2){
            RealmObjectSchema opportunity = schema.get("OflineBrandsEntity");
            opportunity.addField("extra_field_title", String.class);
            opportunity.addField("opportunity_types",String.class);
            RealmObjectSchema postOpportunity = schema.get("OflinePostOpportunity");
            postOpportunity.addField("extra_field_title", String.class);
            postOpportunity.addField("extra_field_value", String.class);
            postOpportunity.addField("address",String.class);
        }else if (oldVersion==5){
            RealmObjectSchema division = schema.get("OflineDivisionEntity");
            division.addField("brands_title", String.class);

            RealmObjectSchema brands = schema.get("OflineBrandsEntity");
            brands.addField("required_location",Boolean.class);
            brands.addField("required_media",Boolean.class);

            RealmObjectSchema postOpportunity = schema.get("OflinePostOpportunity");
            postOpportunity.addField("require_location",Boolean.class);
            postOpportunity.addField("container_location",Boolean.class);
        }else{
            RealmObjectSchema division = schema.get("OflineDivisionEntity");
            division.addField("states",String.class);
        }
        /*else if (oldVersion==2){
            RealmObjectSchema opportunity = schema.get("OflineBrandsEntity");
            opportunity.addField("extra_field_title", String.class);

            RealmObjectSchema postOpportunity = schema.get("OflinePostOpportunity");
            postOpportunity.addField("extra_field_title", String.class);
            postOpportunity.addField("extra_field_value", String.class);
        }else if (oldVersion==3){
            RealmObjectSchema objectSchema= schema.get("OflineBrandsEntity");
            objectSchema.addField("opportunity_types",String.class);
        }else if (oldVersion==4){
            RealmObjectSchema objectSchema= schema.get("OflinePostOpportunity");
            objectSchema.addField("address",String.class);
        }*/
    }
}
