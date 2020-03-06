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

public class ContatosAdapter extends RecyclerView.Adapter<ContatosAdapter.MyVieHolder> {

    private List<Usuario> contatos;
    private Context context;

    public ContatosAdapter(List<Usuario> listaContatos, Context c) {
        this.contatos = listaContatos;
        this.context = c;
    }

    public List<Usuario> getContatos(){
        return this.contatos;
    }

    @NonNull
    @Override
    public MyVieHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemLista = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.adapter_contatos,viewGroup,false);

        return new MyVieHolder(itemLista);
    }

    @Override
    public void onBindViewHolder(@NonNull MyVieHolder myVieHolder, int i) {

        Usuario usuario = contatos.get(i);
        boolean cabecalho = usuario.getEmail().isEmpty();//quando estiver vazio e cabecalho

        myVieHolder.nome.setText(usuario.getNome());
        myVieHolder.email.setText(usuario.getEmail());
        //configurar a foto
        if ( usuario.getFoto() != null){
            Uri uri = Uri.parse( usuario.getFoto());
            Glide.with(context).load(uri).into(myVieHolder.foto); //se ouver uma foto uso a biblioteca glide se nao usa a imagem padrao
        }else{
            if (cabecalho){
                myVieHolder.foto.setImageResource(R.drawable.icone_grupo);
                myVieHolder.email.setVisibility(View.GONE);
            }else {
                myVieHolder.foto.setImageResource(R.drawable.padrao);
            }
        }


    }

    @Override
    public int getItemCount() {
        return contatos.size();//retorna o tamanho da lista
    }

    public class MyVieHolder extends RecyclerView.ViewHolder{

        CircleImageView foto;
        TextView nome,email;

        public MyVieHolder(@NonNull View itemView) {
            super(itemView);

            foto = itemView.findViewById(R.id.imageViewFotoContato);
            nome = itemView.findViewById(R.id.textNomeContato);
            email = itemView.findViewById(R.id.textEmailContato);


        }
    }

}
