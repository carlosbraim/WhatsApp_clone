package com.example.idene.whatsapp.activity;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.idene.whatsapp.R;
import com.example.idene.whatsapp.config.ConfiguracaoFirebase;
import com.example.idene.whatsapp.helper.Base64Custom;
import com.example.idene.whatsapp.helper.Permissao;
import com.example.idene.whatsapp.helper.UsuarioFirebase;
import com.example.idene.whatsapp.model.Usuario;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;

import de.hdodenhof.circleimageview.CircleImageView;

public class ConfiguracoesActivity extends AppCompatActivity {

    //criar permissoes
    private String[] permissoesNecessarias = new String[]{
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA
    };

    private ImageButton imageButtonCamera,imageButtonGaleria;
    private static final  int SELECAO_CAMERA=100;
    private static final  int SELECAO_GALERIA=200;
    private CircleImageView circleImageViewPerfil;
    private EditText editPerfilNome;
    private ImageView imageAtualizarNome;
    private StorageReference storageReference;
    private String identificadorUsuario;
    private Usuario usuarioLogado;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configuracoes);

        //storageReference instanciando a referencia
        storageReference = ConfiguracaoFirebase.getFirebaseStorage();
        //salvar o id do usuario
        identificadorUsuario = UsuarioFirebase.getIdentificadorUsuario();
        //validar as permissoes
        Permissao.validarPermissoes(permissoesNecessarias,this,1);
        //
        usuarioLogado = UsuarioFirebase.getDadosUsuarioLogado();


        imageButtonCamera = findViewById(R.id.imageButtonCamera);
        imageButtonGaleria = findViewById(R.id.imageButtonGaleria);
        circleImageViewPerfil = findViewById(R.id.circleImageViewFotoPerfil);
        editPerfilNome= findViewById(R.id.editPerfilNome);
        imageAtualizarNome = findViewById(R.id.imageAtualizarNome);


        //Configurar a toolbar
        Toolbar toolbar = findViewById(R.id.toolbarPrincipal);
        toolbar.setTitle("Configurações");
        setSupportActionBar(toolbar);//suporte a versoes anteriores
        //configuração para exibir o botao de voltar no toolbar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);//getSupportActionBar() objeto que nos permite alterar um objeto toolbar


        //Recuperar dados do usuario
        FirebaseUser usuario = UsuarioFirebase.getUsuarioAtual();
        Uri url = usuario.getPhotoUrl();

        if(url != null){
            Glide.with(ConfiguracoesActivity.this).load(url).into(circleImageViewPerfil);
        }else {
            circleImageViewPerfil.setImageResource(R.drawable.padrao);
        }
        //recupera o nome do usuario no firebase
        editPerfilNome.setText(usuario.getDisplayName());


        //clic
        //abrir camera
        imageButtonCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);//ACTION_IMAGE_CAPTURE usar a camera
                if(i.resolveActivity(getPackageManager()) != null){//verifica se a intent conseguiu resolver o pedido de abrir a camera caso o usuario tenha camera
                    startActivityForResult(i,SELECAO_CAMERA);
                }

            }
        });

        //abrir galeria
        imageButtonGaleria.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);//ACTION_PICK escolher um fode um local
                if(i.resolveActivity(getPackageManager()) != null){//verifica se a intent conseguiu resolver o pedido de abrir a galeria de fotos caso o usuario tenha camera
                    startActivityForResult(i,SELECAO_GALERIA);
                }

            }
        });

        imageAtualizarNome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String nome = editPerfilNome.getText().toString();
                boolean retorno = UsuarioFirebase.atualizarNomeUsuario(nome);
                if(retorno){

                    usuarioLogado.setNome(nome);
                    usuarioLogado.atualizar();

                    Toast.makeText(ConfiguracoesActivity.this,"Nome alterado com sucesso!",Toast.LENGTH_SHORT).show();
                }

            }
        });

    }

    //recuperar os dados da intente chamada
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK){
            Bitmap imagem =null;

            try {

                switch(requestCode){
                    case SELECAO_CAMERA:
                        imagem = (Bitmap) data.getExtras().get("data");
                        break;
                    case SELECAO_GALERIA:
                        Uri localImagemSeleciona = data.getData();
                        imagem = MediaStore.Images.Media.getBitmap(getContentResolver(),localImagemSeleciona);
                        break;
                }

                //configurar a imagem dentro do imageview
                if (imagem != null){

                    circleImageViewPerfil.setImageBitmap( imagem );

                    //recuperar dados da imagem para o firebase
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    imagem.compress(Bitmap.CompressFormat.JPEG,70,baos);
                    byte[] dadosImagem = baos.toByteArray();

                    //Salvar imagem no firebase
                    StorageReference imagemRef = storageReference
                            .child("imagens")
                            .child("perfil")
                            //.child(identificadorUsuario)
                            //.child("perfil.jpeg");
                            .child(identificadorUsuario + ".jpeg");

                    UploadTask uploadTask = imagemRef.putBytes(dadosImagem);
                    uploadTask.addOnFailureListener(new OnFailureListener() {//Mensagem falha
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(ConfiguracoesActivity.this,"Erro ao fazer upçoad da imagem!",Toast.LENGTH_SHORT).show();


                        }
                    }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {//Mensagem sucesso
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Toast.makeText(ConfiguracoesActivity.this,"Sucesso ao fazer upçoad da imagem!",Toast.LENGTH_SHORT).show();

                        taskSnapshot.getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri url) {
                                atualizaFotosUsuario(url);
                            }
                        });
                        }
                    });

                }

            }catch (Exception e){
                e.printStackTrace();
            }
        }

    }


    public void atualizaFotosUsuario(Uri url){

        boolean retorno = UsuarioFirebase.atualizarFotUsuario(url);
        if (retorno){
            usuarioLogado.setFoto(url.toString());
            usuarioLogado.atualizar();
            Toast.makeText(ConfiguracoesActivity.this,"Sua foto foi alterada!",Toast.LENGTH_SHORT).show();

        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        for (int permissaoResultado:grantResults) {//percorrer o array de inteiros grantResults para saber se alguma permissao foi negada

            if (permissaoResultado ==PackageManager.PERMISSION_DENIED){
                alertaValidacaoPermissao();
            }

        }
    }

    private void alertaValidacaoPermissao(){

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Permissões Negadas");
        builder.setMessage("Para utilizar o app é necessário aceitar as permissoes");
        builder.setCancelable(false);//obriga o usuario a clicar no confirmar sem sair do dialog
        builder.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();

    }
}


