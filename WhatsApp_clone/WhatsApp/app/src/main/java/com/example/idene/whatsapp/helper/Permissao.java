package com.example.idene.whatsapp.helper;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;

public class Permissao {

    public static boolean validarPermissoes(String[] permissoes, Activity activity,int requestCode){

        //verificar se o usuario esta com a versão acima do Marshmallow pois apartir desta versão que foi criado o recurso
        if (Build.VERSION.SDK_INT >= 23){

            List<String> listaPermissoes = new ArrayList<>();

            /*Percorre as permisões passadas, verifiar uma a uma
            se ja tem a permissao liberada*/
            for (String permissao: permissoes){//percorre todas as permissoes passada por parametro percorrendocada permissao para fazer as validaçoes

               Boolean temPermissao = ContextCompat.checkSelfPermission(activity,permissao) == PackageManager.PERMISSION_GRANTED;//apos verificar se a permissao foi consedida e verificado se a permissao e igual a que o android salva
               if (!temPermissao) listaPermissoes.add(permissao);

            }
            //caso a lista esteja vazia, não é necessario solicitar permissão
            if (listaPermissoes.isEmpty()) return true;

            //converter a lista para ser passado como parametro para a requestPermissions pois ela so funciona com array e eu tenho apenas uma lista de listaPermissoes
            String[] novasPermissoes = new String[listaPermissoes.size()];
            listaPermissoes.toArray(novasPermissoes);

            //solicitar permissao
            ActivityCompat.requestPermissions(activity, novasPermissoes,requestCode);//codigo(requestCode) para controlar de onde foi solicitado as permissoes

        }

        return true;
    }
}
