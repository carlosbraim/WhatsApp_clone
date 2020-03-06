package com.example.idene.whatsapp.config;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class ConfiguracaoFirebase {

    private static DatabaseReference database;
    private static FirebaseAuth auth;
    private static StorageReference storage;


    //Retornar a instancia do firebase para manupalacao de dados
    public static  DatabaseReference getFibaseDatabase(){
        if (database == null){
            database = FirebaseDatabase.getInstance().getReference();
        }
        return database;
    }

    //Retornar a instancia do firebaseAuth para manipulacao de login dos usuarios
    public static FirebaseAuth getFirebaseAutenticacao(){
        if ( auth == null ){
            auth = FirebaseAuth.getInstance();
        }
        return auth;
    }

    //recuperar a instancia do storago no fiebase
    public static StorageReference getFirebaseStorage(){
        if ( storage == null ){
            storage = FirebaseStorage.getInstance().getReference();
        }
        return storage;
    }
}
