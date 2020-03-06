package com.example.idene.whatsapp.activity;

import android.content.Intent;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Switch;

import com.example.idene.whatsapp.R;
import com.example.idene.whatsapp.config.ConfiguracaoFirebase;
import com.example.idene.whatsapp.fragment.ContatosFragment;
import com.example.idene.whatsapp.fragment.ConversasFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.miguelcatalan.materialsearchview.MaterialSearchView;
import com.ogaclejapan.smarttablayout.SmartTabLayout;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItemAdapter;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItems;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth autenticaco;
    private MaterialSearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        autenticaco = ConfiguracaoFirebase.getFirebaseAutenticacao();

        //Configurar a toolbar
        Toolbar toolbar = findViewById(R.id.toolbarPrincipal);
        toolbar.setTitle("WhatsApp");
        setSupportActionBar(toolbar);//suporte a versoes anteriores

        //Configurar abas
        final FragmentPagerItemAdapter adapter = new FragmentPagerItemAdapter(
                getSupportFragmentManager(),
                FragmentPagerItems.with(this)
                        .add("Conversas",ConversasFragment.class)
                        .add("Contatos",ContatosFragment.class)
                .create()
        );
        final ViewPager viewPager = findViewById(R.id.viewPager);
        viewPager.setAdapter(adapter);

        SmartTabLayout viewPagerTab = findViewById(R.id.viewPagerTab);
        viewPagerTab.setViewPager( viewPager);

        //configuracao do search view
        searchView = findViewById(R.id.materialSearchPrincipal);

        //listener para o search view
        searchView.setOnSearchViewListener(new MaterialSearchView.SearchViewListener() {
            @Override
            public void onSearchViewShown() {//quando é apresentado para o usuario

            }

            @Override
            public void onSearchViewClosed() {//quando fecha a barra de pesquisa
                ConversasFragment fragment = (ConversasFragment) adapter.getPage(0);
                fragment.recarregarConversas();

            }
        });

        //listeb=ner para caixa de texto
        searchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {//esse metodo e chamado quando o usuario digita o dado e confirma
                //Log.d("evento","onQueryTextSubmit");
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {//chamado em tempo de execucao
                //Log.d("evento","onQueryTextChange");

                //Verifica se esta pesquisando Conversas ou Contatos a partir da tab que esta aativa
                switch (viewPager.getCurrentItem()){//viewPager sabe qual fragmento esta sendo carregado, getCurrentItem() recupera o item selecionado
                    case 0:
                        ConversasFragment conversasFragment = (ConversasFragment) adapter.getPage(0);
                        if (newText != null && !newText.isEmpty()){
                            conversasFragment.pesquisarConversas(newText.toLowerCase());//toLowerCase converte o texto em letrasminusculas
                        }else{
                            conversasFragment.recarregarConversas();
                        }
                        break;
                    case 1:
                        ContatosFragment contatosFragment = (ContatosFragment) adapter.getPage(1);
                        if (newText != null && !newText.isEmpty()){
                            contatosFragment.pesquisarContatos(newText.toLowerCase());//toLowerCase converte o texto em letrasminusculas
                        }else{
                            contatosFragment.recarregarContatos();
                        }
                        break;
                }



                return true;
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main,menu);

        //configurar botao de pesquisa
        MenuItem item = menu.findItem(R.id.menuPesquisa);
        searchView.setMenuItem(item);

        return super.onCreateOptionsMenu(menu);
    }


    //recuperar itens de menu selecionados

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case R.id.menuSair:
                deslogarUsuario();
                finish();
                break;
            case R.id.menuConfiguracoes:
                abrirConfiguracoes();
                break;

        }

        return super.onOptionsItemSelected(item);
    }

    public void deslogarUsuario(){

        try{
            autenticaco.signOut();
        }catch (Exception e){
            e.printStackTrace();

        }

    }

    public void abrirConfiguracoes(){
        Intent intent = new Intent(MainActivity.this,ConfiguracoesActivity.class);
        startActivity(intent);
    }
}
