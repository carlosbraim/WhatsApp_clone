package com.example.idene.whatsapp.model;

import com.example.idene.whatsapp.config.ConfiguracaoFirebase;
import com.example.idene.whatsapp.helper.UsuarioFirebase;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;
import com.google.firebase.database.FirebaseDatabase;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class Usuario implements Serializable { //implements Serializable: permite passar objeto

    private String id;
    private String nome;
    private String email;
    private String senha;
    private String foto;


    public Usuario() {
    }

    public void salvar(){

        DatabaseReference firebaseref = ConfiguracaoFirebase.getFibaseDatabase();
        DatabaseReference usuario = firebaseref.child("usuarios").child(getId());

        usuario.setValue(this);//salvar o objeto todo

    }
    public void atualizar(){


        String identificadorUsuario = UsuarioFirebase.getIdentificadorUsuario();
        DatabaseReference database = ConfiguracaoFirebase.getFibaseDatabase();

        DatabaseReference usuariosRef = database.child("usuarios")
                .child(identificadorUsuario);

        //para fazer update tenho que criar um map
        Map<String,Object> valoresUsuarios = converterParaMap();
        usuariosRef.updateChildren(valoresUsuarios);

    }

    @Exclude
    public Map<String,Object> converterParaMap(){

        HashMap<String,Object> usuarioMap = new HashMap<>();
        usuarioMap.put("email",getEmail());
        usuarioMap.put("nome",getNome());
        usuarioMap.put("foto",getFoto());

        return  usuarioMap;
    }

    public String getFoto() {
        return foto;
    }

    public void setFoto(String foto) {
        this.foto = foto;
    }

    public String getNome() {
        return nome;
    }
    @Exclude//remove o id pois ja temos essa informação
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
    @Exclude //remove a senha
    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }
}
