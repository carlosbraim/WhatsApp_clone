package com.example.idene.whatsapp.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.example.idene.whatsapp.R;
import com.example.idene.whatsapp.activity.ChatActivity;
import com.example.idene.whatsapp.adapter.ConversasApadpter;
import com.example.idene.whatsapp.config.ConfiguracaoFirebase;
import com.example.idene.whatsapp.model.Conversa;
import com.example.idene.whatsapp.helper.RecyclerItemClickListener;
import com.example.idene.whatsapp.helper.UsuarioFirebase;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class ConversasFragment extends Fragment {

    private RecyclerView recyclerViewConversas;
    private List<Conversa> listaConversas = new ArrayList<>();
    private ConversasApadpter adpter;
    private DatabaseReference database;
    private DatabaseReference conversasRef;
    private ChildEventListener childEventListenerConversas;



    public ConversasFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        /*/ Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_conversas, container, false);*/
        View view = inflater.inflate(R.layout.fragment_conversas, container, false);

        recyclerViewConversas = view.findViewById(R.id.recyclerListaConversas);

        //configurar adapter
        adpter = new ConversasApadpter(listaConversas,getActivity());



        //configurar recyclerview
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerViewConversas.setLayoutManager(layoutManager);
        recyclerViewConversas.setHasFixedSize(true);
        recyclerViewConversas.setAdapter(adpter);

        //configurar evento de clique
        recyclerViewConversas.addOnItemTouchListener(new RecyclerItemClickListener(
                getActivity(),
                recyclerViewConversas,
                new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {

                        List<Conversa> listaConversasAtualizada = adpter.getConversas();//recuperar conversa selecionada de conversas adapter que sempre tera uma lista atualizada
                        Conversa conversaSelecionada = listaConversasAtualizada.get(position);//recuperar a position da lista de conversas

                        if (conversaSelecionada.getIsGroup().equals("true")){
                            //chat grupo
                            Intent i = new Intent(getActivity(),ChatActivity.class);
                            i.putExtra("chatGrupo",conversaSelecionada.getGrupo());
                            startActivity(i);
                        }else {
                            //chat de contato
                            Intent i = new Intent(getActivity(),ChatActivity.class);
                            i.putExtra("chatContato",conversaSelecionada.getUsuarioExibicao());
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
        ));


        //configura conversa ref
        String identificadorUsuario = UsuarioFirebase.getIdentificadorUsuario();//retorna o id usuario em base 64
         database = ConfiguracaoFirebase.getFibaseDatabase();
         conversasRef = database.child("conversas")
                        .child(identificadorUsuario);

        return  view;
    }

    @Override
    public void onStart() {
        super.onStart();
        recuperarConversas();
    }

    @Override
    public void onStop() {
        super.onStop();
        conversasRef.removeEventListener(childEventListenerConversas);
    }

    public void pesquisarConversas(String texto){
        //Log.d("pesquisa",texto);
        List<Conversa> listaConversasBusca = new ArrayList<>();
        for (Conversa conversa: listaConversas){

            if (conversa.getUsuarioExibicao() != null ){
                String nome = conversa.getUsuarioExibicao().getNome().toLowerCase();
                String ultimaMsg = conversa.getUltimaMensagem().toLowerCase();

                if (nome.contains(texto) || ultimaMsg.contains(texto)){
                    listaConversasBusca.add(conversa);
                }
            }else{
                String nome = conversa.getGrupo().getNome().toLowerCase();
                String ultimaMsg = conversa.getUltimaMensagem().toLowerCase();

                if (nome.contains(texto) || ultimaMsg.contains(texto)){
                    listaConversasBusca.add(conversa);
                }
            }

        }

        adpter = new ConversasApadpter(listaConversasBusca,getActivity());
        recyclerViewConversas.setAdapter(adpter);
        adpter.notifyDataSetChanged();
    }

    public void recarregarConversas(){
        adpter = new ConversasApadpter(listaConversas,getActivity());
        recyclerViewConversas.setAdapter(adpter);
        adpter.notifyDataSetChanged();
    }

    public void recuperarConversas(){

        childEventListenerConversas = conversasRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                //recuperar conversas
                Conversa conversa = dataSnapshot.getValue(Conversa.class);
                listaConversas.add(conversa);
                adpter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

}
