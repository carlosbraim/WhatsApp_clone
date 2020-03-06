package com.example.idene.whatsapp.adapter;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.idene.whatsapp.R;
import com.example.idene.whatsapp.helper.UsuarioFirebase;
import com.example.idene.whatsapp.model.Mensagem;

import java.util.List;

public class MensagensAdapter extends RecyclerView.Adapter<MensagensAdapter.MyViewHolder> {

    private List<Mensagem> mensagens;
    private Context context;
    private static final int TIPO_REMETENTE = 0;
    private static final int TIPO_DETINATARIO = 1;

    public MensagensAdapter(List<Mensagem> lista, Context c) {
        this.mensagens = lista;
        this.context = c;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View item = null;
        if(i == TIPO_REMETENTE){

            item = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.adapter_mensagem_remetente,viewGroup,false);

        }else if (i == TIPO_DETINATARIO){
            item = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.adapter_mensagem_destinatario,viewGroup,false);
        }
        return new MyViewHolder(item);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int i) {

        Mensagem mensagem = mensagens.get(i);//recupera mensagem para cada item da lista
        String msg = mensagem.getMensagem();
        String imagem = mensagem.getImagem();

        if (imagem != null){
            Uri uri = Uri.parse(imagem);
            Glide.with(context).load(uri).into(myViewHolder.imagem);

            String nome = mensagem.getNome();
            if ( !nome.isEmpty()){//caso o nome nao esteija vazio
                myViewHolder.nome.setText(nome);
            }else {
                myViewHolder.nome.setVisibility(View.GONE);
            }

            //Econder o texto
            myViewHolder.mensagem.setVisibility(View.GONE);

        }else{
            myViewHolder.mensagem.setText(msg);

            String nome = mensagem.getNome();
            if ( !nome.isEmpty()){//caso o nome nao esteija vazio
                myViewHolder.nome.setText(nome);
            }else {
                myViewHolder.nome.setVisibility(View.GONE);
            }

            //Econder o imagem
            myViewHolder.imagem.setVisibility(View.GONE);
        }

    }

    @Override
    public int getItemCount() {
        return mensagens.size();
    }

    @Override
    public int getItemViewType(int position) {

        Mensagem mensagem = mensagens.get(position);//recupera mensagem para cada item da lista

        String idUsuario = UsuarioFirebase.getIdentificadorUsuario();

        if (idUsuario.equals(mensagem.getIdUsuario())){
            return TIPO_REMETENTE;
        }
        return TIPO_DETINATARIO;

    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        TextView mensagem;
        TextView nome;
        ImageView imagem;

        public MyViewHolder (View itemView){
            super(itemView);

            mensagem = itemView.findViewById(R.id.textMensagemTexto);
            imagem = itemView.findViewById(R.id.imageMensagemFoto);
            nome = itemView.findViewById(R.id.textNomeExibicao);

        }
    }
}
