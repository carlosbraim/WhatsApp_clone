package com.example.idene.whatsapp.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.example.idene.whatsapp.R;
import com.example.idene.whatsapp.activity.ChatActivity;
import com.example.idene.whatsapp.activity.GrupoActivity;
import com.example.idene.whatsapp.adapter.ContatosAdapter;
import com.example.idene.whatsapp.adapter.ConversasApadpter;
import com.example.idene.whatsapp.config.ConfiguracaoFirebase;
import com.example.idene.whatsapp.helper.RecyclerItemClickListener;
import com.example.idene.whatsapp.helper.UsuarioFirebase;
import com.example.idene.whatsapp.model.Conversa;
import com.example.idene.whatsapp.model.Usuario;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class ContatosFragment extends Fragment {


    private RecyclerView recyclerViewListaContatos;
    private ContatosAdapter adapter;
    private ArrayList<Usuario> listaContatos = new ArrayList<>();
    private DatabaseReference usuariosRef;
    private ValueEventListener valueEventListenerContatos;
    private FirebaseUser usuarioAtual;


    public ContatosFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_contatos, container, false);

        //Configuraçõs iniciais
        recyclerViewListaContatos = view.findViewById(R.id.recyclerViewListaContatos);
        usuariosRef = ConfiguracaoFirebase.getFibaseDatabase().child("usuarios");
        usuarioAtual = UsuarioFirebase.getUsuarioAtual();


        //Configrurar adapter
        adapter = new ContatosAdapter(listaContatos,getActivity());

        //Configrurar recyclerview
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());//getActivity() passa a activit principal
        recyclerViewListaContatos.setLayoutManager(layoutManager);
        recyclerViewListaContatos.setHasFixedSize(true);
        recyclerViewListaContatos.setAdapter(adapter);

        //configurar evento de clique no recyclerview
        recyclerViewListaContatos.addOnItemTouchListener(
                new RecyclerItemClickListener(
                        getActivity(),
                        recyclerViewListaContatos,
                        new RecyclerItemClickListener.OnItemClickListener() {
                            @Override
                            public void onItemClick(View view, int position) {//recupera item clicado

                                List<Usuario> listaUsuariosAtualizada = adapter.getContatos();

                                Usuario usuarioSelecionado = listaUsuariosAtualizada.get(position);//recupera a position e dsalvamos em usualio selecionado
                                boolean cabecalho = usuarioSelecionado.getEmail().isEmpty();

                                if (cabecalho){
                                    Intent i = new Intent(getActivity(),GrupoActivity.class);
                                    startActivity(i);
                                }else {
                                    Intent i = new Intent(getActivity(),ChatActivity.class);
                                    i.putExtra("chatContato",usuarioSelecionado);
                                    startActivity(i);
                                }

                            }

                            @Override
                            public void onLongItemClick(View view, int position) {

                            }

                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                            }
                        }
                )
        );



        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        recuperarContatos();
    }

    @Override
    public void onStop() {
        super.onStop();
        usuariosRef.removeEventListener(valueEventListenerContatos);
    }

    public void recuperarContatos(){

        //limpa a listagem de contatos
        listaContatos.clear();

        /*define usuario com e-mail vazio
         * em caso de email vazio o usuario sera utilizado como
         * cabecalho, exibindo novo grupo*/
        Usuario itemGrupo = new Usuario();
        itemGrupo.setNome("Novo grupo");
        itemGrupo.setEmail("");
        listaContatos.add(itemGrupo);

        valueEventListenerContatos = usuariosRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot dados: dataSnapshot.getChildren()){//percorrer todos os usuarios

                    Usuario usuario = dados.getValue(Usuario.class);

                    String emailUsuarioAtual = usuarioAtual.getEmail();
                    if (!emailUsuarioAtual.equals(usuario.getEmail())){
                        listaContatos.add(usuario);
                    }
                }

                adapter.notifyDataSetChanged();//notificar que os dados foram alterados

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }


    public void pesquisarContatos(String texto){
        //Log.d("pesquisa",texto);
        List<Usuario> listaContatosBusca = new ArrayList<>();
        for (Usuario usuario: listaContatos) {//lista de usuario

            String nome = usuario.getNome().toLowerCase();//formatar letras toLowerCase para minusculo
            if (nome.contains(texto)) {
                listaContatosBusca.add(usuario);
            }
        }

        adapter = new ContatosAdapter(listaContatosBusca,getActivity());
        recyclerViewListaContatos.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    public void recarregarContatos(){
        adapter = new ContatosAdapter(listaContatos,getActivity());
        recyclerViewListaContatos.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

}
