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
import com.example.idene.whatsapp.model.Usuario;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class GrupoSelecionadoAdapter extends RecyclerView.Adapter<GrupoSelecionadoAdapter.MyVieHolder> {


    private List<Usuario> contatosSelecionados;
    private Context context;

    public GrupoSelecionadoAdapter(List<Usuario> listaContatos, Context c) {
        this.contatosSelecionados = listaContatos;
        this.context = c;
    }

    @NonNull
    @Override
    public GrupoSelecionadoAdapter.MyVieHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemLista = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.adapter_grupo_selecionado,viewGroup,false);

        return new GrupoSelecionadoAdapter.MyVieHolder(itemLista);
    }

    @Override
    public void onBindViewHolder(@NonNull GrupoSelecionadoAdapter.MyVieHolder myVieHolder, int i) {

        Usuario usuario = contatosSelecionados.get(i);
        myVieHolder.nome.setText(usuario.getNome());

        //configurar a foto
        if ( usuario.getFoto() != null){
            Uri uri = Uri.parse( usuario.getFoto());
            Glide.with(context).load(uri).into(myVieHolder.foto); //se ouver uma foto uso a biblioteca glide se nao usa a imagem padrao
        }else{

            myVieHolder.foto.setImageResource(R.drawable.padrao);
        }

    }

    @Override
    public int getItemCount() {
        return contatosSelecionados.size();//retorna o tamanho da lista
    }

    public class MyVieHolder extends RecyclerView.ViewHolder{

        CircleImageView foto;
        TextView nome;

        public MyVieHolder(@NonNull View itemView) {
            super(itemView);

            foto = itemView.findViewById(R.id.imageViewFotoMembroSelecionado);
            nome = itemView.findViewById(R.id.textNomeMembroSelecionado);

        }
    }
}
