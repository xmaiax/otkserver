## Open Tibia Kotlin Server

#### Servidor de Tibia para o protocolo 7.6

O projeto se encontra inacabado, mas extremamente simples de compilar, rodar e alterar.

Tela de Login (busca de personagens + MOTD + conexão com banco) funcionando perfeitamente.

![screenshot do otkserver rodando](https://github.com/felipeMaia92/otkserver/raw/master/screenshot.jpg)

#### Informações
- OTServer criado inteiramente do zero;
- 100% do código é Kotlin, compatível com Java, Scala e outras linguagens baseadas em JVM ~~(adeus, Scala!)~~;
- Servidor TCP utiliza ~~o framework Apache Mina~~ Java NIO Socket (Channels e tudo mais);
- Conexão com o banco feita pelo `JPA` (Spring-Boot);
- Dispensa script de configuração de banco;
- Cria um banco `H2` criptografado em arquivo, não precisa de serviços como o MySQL rodando (suporta qualquer framework que utilize um driver JDBC);
- Log4J exibe todas as informações (senha é criptografada com MD5 + Salt antes de aparecer);
- Código fonte compilado pelo `Maven` 3+;
- Configuração feita no arquivo `application.properties`;

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
  * `otkserver-???.jar` (`???` -> número da versão informada no `pom.xml`)

Essa pasta RELEASE possui o OTServer já compilado e é o suficiente para rodar em qualquer computador com a JRE 8 instalada.

O projeto compilado pesa menos de 60MB, possui um Rest Service.

##### Rodar
* Abra uma CLI na pasta RELEASE;
* Chame o comando: `java -jar otkserver-mk-i.jar` (para a versão `mk-i`, por exemplo);
* Espere a mensagem do Spring informando quando a aplicação terminou de inicializar;
* Pronto, seu OTServer já está rodando!

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

* Atualmente qualquer conta e senha funcionam;

* Agora basta abrir o Tibia e tentar se logar.
