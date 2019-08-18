## Open Tibia Server

#### Servidor de Tibia para o protocolo 7.6

O projeto se encontra inacabado, mas extremamente simples de compilar e rodar.

Tela de Login (busca de personagens + MOTD + conexão com banco) funcionando perfeitamente.

![wallpaper da versao 7](https://www.tibiawiki.com.br/images/5/50/Client_Artwork_7.0.jpg)

#### Informações
- OTServer criado inteiramente do zero;
- 100% do código é Scala, compatível com Java e Kotlin;
- Servidor TCP utiliza o Socket nativo do Java (sem `sleep`);
- Conexão com o banco feita pelo OrmLite;
- Dispensa script de configuração de banco (feito antes de iniciar o servidor TCP);
- Cria um banco H2 criptografado em arquivo, não precisa de serviços como o MySQL rodando;
- Log4J exibe todas as informações (senha é criptografada com MD5 + Salt antes de aparecer);
- Código fonte compilado pelo Maven;
- Configuração feita no arquivo `otserver.properties`;

#### Como compilar o servidor

###### Instalar a versão mais recente da JDK 8
* Baixe direto do site da [Oracle](https://www.oracle.com/technetwork/pt/java/javase/downloads/jdk8-downloads-2133151.html) (Testado com a JDK 1.8.0_211), ou utilizar a OpenJDK (JRE sozinha não possui o compilador `javac`);

###### Instalar a versão mais recente do Maven 3
* Baixe direto do site da [Apache](http://maven.apache.org/download.cgi), o Maven precisa de uma variável do ambiente `M2_HOME` apontando para a raiz da instalação e incluir na variável `PATH` dessa maneira: `%M2_HOME%\bin` para o Windows e `$M2_HOME/bin` para sistemas baseado em Linux;

###### Compilar
* Abra uma CLI (command-line-interface, podendo ser o CMD ou Powershell no Windows, ou um Terminal no Linux);
* Navegue até a pasta com o código fonte do OTServer pela CLI;
* Chame o comando: `mvn clean package`;
* Espere o Maven baixar as dependências e compilar o JAR executável;
* Quando a mensagem `BUILD SUCCESS` aparecer, abra a pasta RELEASE criada dentro da raiz do projeto;
* A seguinte estrutura foi criada:
  * `otserver4s-???.jar` (`???` -> número da versão informada no `pom.xml`)
  * `lib/` (diretório com todas as dependências do projeto)

Essa pasta RELEASE possui o OTServer já compilado e é o suficiente para rodar em qualquer dispositivo com a JRE 8 instalada.

###### Rodar
* Abra uma CLI na pasta RELEASE;
* Chame o comando: `java -jar otserver4s-pre-alpha.jar` (para a versão `pre-alpha`, por exemplo);
* Espere a mensagem `Servidor iniciado na porta ????...` (onde `????` é a porta que o servidor está rodando);
* Pronto, seu servidor já está rodando!

###### Client local (sem IP Changer)

* Baixe e instale o Client 7.6 (o [TibiaBR](https://www.tibiabr.com/downloads/clients-antigos/) tem uma lista de clients antigos);
* Certifique-se que o servidor está rodando na porta `7171`;
* Adicione as linhas abaixo no seu arquivo `hosts`:


```
127.0.0.1 tibia1.cipsoft.com
127.0.0.1 tibia2.cipsoft.com
127.0.0.1 server.tibia.com
127.0.0.1 server2.tibia.com
```

O Mock criado na inicialização do servidor gera apenas uma conta:

```
Account Number: 123
Password: abc
```

Agora basta abrir o Tibia e tentar se logar.
