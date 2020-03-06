package com.example.idene.whatsapp.adapter;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.idene.whatsapp.R;
import com.example.idene.whatsapp.model.Conversa;
import com.example.idene.whatsapp.model.Grupo;
import com.example.idene.whatsapp.model.Usuario;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ConversasApadpter extends RecyclerView.Adapter<ConversasApadpter.MyViewHolder> {

    private List<Conversa> conversas;
    private Context context;

    public ConversasApadpter(List<Conversa> lista, Context c) {
        this.conversas = lista;
        this.context = c;
    }

    public List<Conversa> getConversas(){
        return this.conversas;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemLista = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.adapter_contatos,viewGroup,false);
        return new MyViewHolder(itemLista);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int i) {

        Conversa conversa = conversas.get(i);//i = position

        myViewHolder.ultimaMensagem.setText(conversa.getUltimaMensagem());

        if (conversa.getIsGroup().equals("true")){//verificar se é um grupo

            Grupo grupo = conversa.getGrupo();
            myViewHolder.nome.setText(grupo.getNome());

            if (grupo.getFoto() != null){ //recuperar a foto do usuario
                Uri uri = Uri.parse( grupo.getFoto());
                Glide.with(context).load(uri).into(myViewHolder.foto); //se ouver uma foto uso a biblioteca glide se nao usa a imagem padrao
            }else{
                myViewHolder.foto.setImageResource(R.drawable.padrao);//caso nao tenha foto é configurado a foto padrao
            }

        }else{

            Usuario usuario = conversa.getUsuarioExibicao();//recuperar usuario
            if (usuario != null){
                myViewHolder.nome.setText(usuario.getNome());//rucuperar nome do usuario

                if (usuario.getFoto() != null){ //recuperar a foto do usuario
                    Uri uri = Uri.parse( usuario.getFoto());
                    Glide.with(context).load(uri).into(myViewHolder.foto); //se ouver uma foto uso a biblioteca glide se nao usa a imagem padrao
                }else{
                    myViewHolder.foto.setImageResource(R.drawable.padrao);//caso nao tenha foto é configurado a foto padrao
                }
            }

        }


    }

    @Override
    public int getItemCount() {
        return conversas.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        CircleImageView foto;
        TextView nome, ultimaMensagem;

        public MyViewHolder (View itemView){
            super(itemView);

            foto = itemView.findViewById(R.id.imageViewFotoContato);
            nome = itemView.findViewById(R.id.textNomeContato);
            ultimaMensagem = itemView.findViewById(R.id.textEmailContato);
        }
    }
}
