# Four Lanches App

## DOC

### 1. Tecnologias
* Java 17 
* Spring Boot
* Postgres SQL
* Docker
* [Swagger](http://localhost:8080/swagger-ui/index.html)

### 2. Pre-requisitos para rodar a aplicação
* [Java Version: 17](https://www.oracle.com/java/technologies/javase/jdk17-archive-downloads.html) 
* [Gradle](https://gradle.org/install/)
* [IDE IntelliJ](https://www.jetbrains.com/idea/)
* [Docker](https://www.docker.com/)
* Kubernetes - K8S

### 3. Para executar localmente com Docker
#### 3.1. Necessário um arquivo .env na raiz do projeto com a seguinte conteúdo:

Para executar localmente com o docker é necessário fazer uma cópia do arquivo .env.example, renomear a
cópia para .env e configurar as variavéis de acordo com o ambiente.

```
DATABASE_USERNAME=username_do_banco  
DATABASE_PASSWORD=password_do_banco
```

#### 3.2. Para buildar, executar as migration e rodar o app no docker pela primeira vez

`docker compose up --build -d`

#### 3.3. Para executar o app com docker após a primeira vez
Após rodar a primeira o comando acima, execute o seguinte comando abaixo para que apenas execute 
os containers sem a etapa de build e migration.

`docker compose up -d app db rabbitmq`

#### 3.4. Preparando o RabbitMQ
Após subir todos os containeres necessários, é preciso realizar os seguintes passos:
1. Acessar o rabbitmq no `localhost:15672` (configuração padrão) com  usários e senha configurados no arquivo `.env.`
2. Após logado é necessário criar as 3 filas e o exchange, usando os mesmos  valores das variaveis usadas no arquivo `.env`
3. E por final, deve ser criado os bindings no exchange criado anteriormente
   * Os binds criados devem: ser vazio (bind default) e um com o seguinte valor: `order.status.update`
> Observação: em caso de subir as tres aplicações localmente, deverá ser usado apenas uma instancia do rabbitmq no docker,
> ou um serviço online, como o cloudamqp.

### 4. Testando
Basta acessar a url do swagger disponivel em `http://localhost/swagger-ui/index.htm`

#### 4.1. Nota para executar o localmente  pelo Docker
Subindo a aplicação via docker, a mesma se encotrará disponível no localhost:8080 para ser acessada

### 5. Links Adicionais
