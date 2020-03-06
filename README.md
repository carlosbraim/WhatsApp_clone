# WhatsApp_clone
WhatsApp clone é um projeto que contem muita complexidade pois envolve banco de dados NoSQL para salvar imagens com dimensões variadas
e autenticação e validação de conta no FIREBASE, foi utilizado várias bibliotecas para desenvolver funções de Layout dentro dessa aplicação.
Vou descrever algumas funções e como foi feito **mais não esqueça que o código está todo comentado e possui o arquivo Android.docx com algumas informações.** 


## Neste projetos é possível aprender como:
- Editar o perfil
- Usar Bibliotecas 
- Adicionar contatos e criar grupos 
- Criar usuário dentro da plataforma
- Conversas em grupos e envio de imagens
- Preparar o ambiente de desenvolvimento
- Regras de como Logar na plataforma fazendo validações seguras

### Sempre antes de iniciar o desenvolvimento é necessário adicionar as dependências:

    dependencies {
            implementation fileTree(dir: 'libs', include: ['*.jar'])
            implementation 'com.android.support:appcompat-v7:28.0.0'
            implementation 'com.android.support.constraint:constraint-layout:1.1.3'
            implementation 'com.android.support:support-v4:28.0.0'
            testImplementation 'junit:junit:4.12'
            androidTestImplementation 'com.android.support.test:runner:1.0.2'
            androidTestImplementation 'com.android.support.test.espresso:espresso-core:3.0.2'
            /*Dependencias firebase*/
            implementation 'com.google.firebase:firebase-core:16.0.1'
            implementation 'com.google.firebase:firebase-database:16.0.1'
            implementation 'com.google.firebase:firebase-storage:16.0.1'
            implementation 'com.google.firebase:firebase-auth:16.0.1'
            implementation 'com.firebaseui:firebase-ui-storage:0.6.0'
            implementation 'com.android.support:design:28.0.0'

            /*Dependencias para abas*/
            implementation 'com.ogaclejapan.smarttablayout:library:1.6.1@aar'
            implementation 'com.ogaclejapan.smarttablayout:utils-v4:1.6.1@aar'

            /*Dependencia circleview*/
            implementation 'de.hdodenhof:circleimageview:3.0.0'

            /*dependencia para material search view*/
            implementation 'com.miguelcatalan:materialsearchview:1.4.0'
            */
   
#### GitHub detalhamento de como funciona a imagem em circulo:
https://github.com/hdodenhof/CircleImageView

#### GitHub pesquisa dentro do App:
https://github.com/MiguelCatalan/MaterialSearchView

#### SmartTabLayout para navegar de contatos para conversas:
https://github.com/ogaclejapan/SmartTabLayout/


### Dependência  do build e do Google:

        dependencies {
                classpath 'com.android.tools.build:gradle:3.2.0'
                classpath 'com.google.gms:google-services:4.0.1'
                // NOTE: Do not place your application dependencies here; they belong
                // in the individual module build.gradle files
            }

### Criar usuário e Logar na plataforma:
<img src="/WhatsApp_clone/Prints_tela/zap2.png" width="150"> <img src="/WhatsApp_clone/Prints_tela/zap1.png" width="150">

Esta etapa é utilizado o banco do FIREBASE para criar o usuário, tratativa de campos foram feitas no código em Java e funções do próprio 
FIREBASE para validar se o usuário é valido.É realizado a validação de dados no FIREBASE, esse processo só ocorre depois de todos os dados
inseridos no App forem validado pelo aplicativo local, depois enviado para o Banco externo para comparação do mesmo tudo criptografado em base64.

### Logado:
<img src="/WhatsApp_clone/Prints_tela/zap3.png" width="150">
O usuário  consegue ver seus amigos e conversar com algum deles, é possível  realizar pesquisa de conversa e nome do usuário , sair ou configurar
o perfil e trocar para contatos.

### Conversas:
<img src="/WhatsApp_clone/Prints_tela/zap4.png" width="150">
Nessa etapa podemos ver conversas e envio de imagens do celular ou da câmera do aparelho.

### Contatos e grupos:
<img src="/WhatsApp_clone/Prints_tela/zap5.png" width="150"> <img src="/WhatsApp_clone/Prints_tela/zap6.png" width="150">

O mesmo pode pesquisar novos contatos e criar grupos adicionando os participantes e nome do grupo.

### Editar o perfil:
<img src="/WhatsApp_clone/Prints_tela/zap7.png" width="150">
É possível editar nome e adicionar imagem do dispositivo no perfil, caso o mesmo queira tirar a foto na hora e só ligar a câmera,
essas informações são as que aparece para o usuário.









