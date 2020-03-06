package com.example.idene.whatsapp.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.idene.whatsapp.R;
import com.example.idene.whatsapp.adapter.MensagensAdapter;
import com.example.idene.whatsapp.config.ConfiguracaoFirebase;
import com.example.idene.whatsapp.helper.Base64Custom;
import com.example.idene.whatsapp.model.Conversa;
import com.example.idene.whatsapp.helper.UsuarioFirebase;
import com.example.idene.whatsapp.model.Grupo;
import com.example.idene.whatsapp.model.Mensagem;
import com.example.idene.whatsapp.model.Usuario;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity {

    private TextView textViewNome;
    private CircleImageView circleImageViewFoto;
    private Usuario usuarioDestinatario;
    private Usuario usuarioRemetente;
    private EditText editMensagem;
    private ImageView imageCamera;
    private DatabaseReference database;
    private StorageReference storage;
    private DatabaseReference mensagensRef;
    private ChildEventListener childEventListenerMensagens;

    //identificador usuarios temetente e destinatario
    private String idUsauarioRemetente;
    private String idUsuarioDestinatario;
    private Grupo grupo;

    private RecyclerView recyclerMensagens;
    private MensagensAdapter adapter;
    private List<Mensagem> mensagens = new ArrayList<>();
    private static final  int SELECAO_CAMERA=100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        //configuracao toolvar
        Toolbar toolbar =  findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //configuracao iniciais
        textViewNome = findViewById(R.id.textViewNomeChat);
        circleImageViewFoto = findViewById(R.id.circleImageFotoChat);
        editMensagem = findViewById(R.id.editMensagem);
        recyclerMensagens = findViewById(R.id.recyclerMensagens);
        imageCamera = findViewById(R.id.imageCamera);

        //recuperar dados do usuario remetente
        idUsauarioRemetente = UsuarioFirebase.getIdentificadorUsuario();
        usuarioRemetente = UsuarioFirebase.getDadosUsuarioLogado();


        //recuperar dados do usuario destinatario
        Bundle bundle = getIntent().getExtras();
        if (bundle != null){

            if (bundle.containsKey("chatGrupo")){
                //chat grupo
                grupo = (Grupo) bundle.getSerializable("chatGrupo");
                idUsuarioDestinatario = grupo.getId();//o id do grupo vem do propio grupo

                textViewNome.setText(grupo.getNome());//nome do grupo

                //recuperar imagem
                String foto = grupo.getFoto();
                if (foto != null){
                    Uri uri = Uri.parse(foto);
                    Glide.with(ChatActivity.this)
                            .load(uri)
                            .into(circleImageViewFoto);
                }else{
                    circleImageViewFoto.setImageResource(R.drawable.padrao);
                }


            }else{
                //chat usuario
                usuarioDestinatario =(Usuario) bundle.getSerializable("chatContato");
                textViewNome.setText(usuarioDestinatario.getNome());

                //recuperar imagem
                String foto = usuarioDestinatario.getFoto();
                if (foto != null){
                    Uri uri = Uri.parse(usuarioDestinatario.getFoto());
                    Glide.with(ChatActivity.this)
                            .load(uri)
                            .into(circleImageViewFoto);
                }else{
                    circleImageViewFoto.setImageResource(R.drawable.padrao);
                }

                //recuperar dados so usuario destinatario
                idUsuarioDestinatario = Base64Custom.codificarBase64(usuarioDestinatario.getEmail());
            }
        }

        //configuracao adapter
        adapter = new MensagensAdapter(mensagens,getApplicationContext());


        //configuracao recyclerview
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerMensagens.setLayoutManager(layoutManager);
        recyclerMensagens.setHasFixedSize(true);
        recyclerMensagens.setAdapter(adapter);



        database = ConfiguracaoFirebase.getFibaseDatabase();
        storage = ConfiguracaoFirebase.getFirebaseStorage();
        mensagensRef = database.child("mensagens")
                .child(idUsauarioRemetente)
                .child(idUsuarioDestinatario);


        //evento de clique na camera
        imageCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);//ACTION_IMAGE_CAPTURE usar a camera
                if(i.resolveActivity(getPackageManager()) != null){//verifica se a intent conseguiu resolver o pedido de abrir a camera caso o usuario tenha camera
                    startActivityForResult(i,SELECAO_CAMERA);
                }
            }
        });
    }

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
                }

                //configurar a imagem dentro do imageview
                if (imagem != null){

                    //recuperar dados da imagem para o firebase
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    imagem.compress(Bitmap.CompressFormat.JPEG,70,baos);
                    byte[] dadosImagem = baos.toByteArray();

                    //criar nome da imagem
                    String nomeImagem = UUID.randomUUID().toString();

                    //configurar referencia do firebase
                    StorageReference imagemRef = storage.child("imagens")
                            .child("fotos")
                            .child(idUsauarioRemetente)
                            .child(nomeImagem);

                    UploadTask uploadTask = imagemRef.putBytes(dadosImagem);
                    uploadTask.addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d("Erro","Erro ao fazer upload");
                            Toast.makeText(ChatActivity.this,"Erro ao fazer upçoad da imagem!",Toast.LENGTH_SHORT).show();
                        }
                    }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            Toast.makeText(ChatActivity.this,"Sucesso ao fazer upçoad da imagem!",Toast.LENGTH_SHORT).show();
                            taskSnapshot.getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri url) {

                                    Mensagem mensagem = new Mensagem();
                                    mensagem.setIdUsuario(idUsauarioRemetente);
                                    mensagem.setMensagem("imagem.jpeg");
                                    mensagem.setImagem(url.toString());
                                   // atualizaFotosUsuario(url);

                                    //salvar imagem remetente
                                    salvarMensagem(idUsauarioRemetente,idUsuarioDestinatario,mensagem);

                                    //salvar imagem destinatrio
                                    salvarMensagem(idUsuarioDestinatario,idUsauarioRemetente,mensagem);

                                    Toast.makeText(ChatActivity.this,"Sucesso ao enviar imagem!",Toast.LENGTH_SHORT).show();
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



    public void enviarMensagem(View view){

        String textoMensagem = editMensagem.getText().toString();

        if (!textoMensagem.isEmpty()){

            if ( usuarioDestinatario != null){

                Mensagem mensagem = new Mensagem();
                mensagem.setIdUsuario(idUsauarioRemetente);
                mensagem.setMensagem(textoMensagem);

                //salvar a mensagem para o remetente
                salvarMensagem(idUsauarioRemetente,idUsuarioDestinatario,mensagem);

                //salvar a mensagem para o destinatio
                salvarMensagem(idUsuarioDestinatario,idUsauarioRemetente,mensagem);

                //salvar conversa remetente
                salvarConversa(idUsauarioRemetente,idUsuarioDestinatario,usuarioDestinatario,mensagem,false);

                //salvar conversa Destinatario
                salvarConversa(idUsuarioDestinatario,idUsauarioRemetente,usuarioRemetente,mensagem,false);


            }else{

                for ( Usuario membro: grupo.getMembros()){

                    String idRemetenteGrupo = Base64Custom.codificarBase64(membro.getEmail());//pegar o email do usuario do grupo e converter em base 64
                    String idUsuarioLogadoGrupo = UsuarioFirebase.getIdentificadorUsuario();

                    Mensagem mensagem = new Mensagem();
                    mensagem.setIdUsuario(idUsuarioLogadoGrupo);
                    mensagem.setMensagem(textoMensagem);
                    mensagem.setNome(usuarioRemetente.getNome());//exibir o nome na mensagem

                    //salvar mensagem para o membro
                    salvarMensagem(idRemetenteGrupo,idUsuarioDestinatario,mensagem);

                    //salvar conversa
                    salvarConversa(idRemetenteGrupo,idUsuarioDestinatario,usuarioDestinatario,mensagem,true);//idUsuarioDestinatario é o grupo

                }

            }



        }else{
            Toast.makeText(ChatActivity.this,"Digite uma mensagem para enviar!",Toast.LENGTH_SHORT).show();
        }

    }

    private void salvarConversa(String idRemetente, String idDestinatario,Usuario usuarioExibicao,Mensagem msg , boolean isGroup){

        //salvar conversa remetente
        Conversa conversaRemetente = new Conversa();
        conversaRemetente.setIdRemetente(idRemetente);
        conversaRemetente.setIdDestinatario(idDestinatario);
        conversaRemetente.setUltimaMensagem(msg.getMensagem());

        if (isGroup){//conversa de grupo

            //conversa de grupo
            conversaRemetente.setIsGroup("true");
            conversaRemetente.setGrupo(grupo);

        }else{//conversa normal

            //conversa convencional
            conversaRemetente.setUsuarioExibicao(usuarioExibicao);
            conversaRemetente.setIsGroup("false");//não é preciso pois em Conversa no construpor ja inicia com false porem so para visualizar eu add

        }
        conversaRemetente.salvar();
    }

    private void salvarMensagem(String idRemetente, String idDestinatario,Mensagem msg){

        DatabaseReference database = ConfiguracaoFirebase.getFibaseDatabase();
        DatabaseReference mensagensRef = database.child("mensagens");
        mensagensRef.child(idRemetente)
                .child(idDestinatario)
                .push()
                .setValue(msg);

        //limpar o texto da caixa de texto
        editMensagem.setText("");

    }

    @Override
    protected void onStart() {
        super.onStart();
        recuperarMensagens();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mensagensRef.removeEventListener(childEventListenerMensagens);
    }

    private void recuperarMensagens(){

        childEventListenerMensagens = mensagensRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {//add item
                Mensagem mensagem = dataSnapshot.getValue(Mensagem.class);
                mensagens.add(mensagem);
                adapter.notifyDataSetChanged();//atualizar adapter
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {//modificar item

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {//remover item

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {//mover o item

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {//erro

            }
        });

    }

}
