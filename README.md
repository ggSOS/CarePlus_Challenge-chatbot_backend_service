# CarePlus_Challenge-chatbot_backend_service

Serviço de back-end para registro de conversas em um chatbot

## Integrantes

- rm553187 - Gabriel Borba
- rm553842 - Gustavo Gouvêa Soares
- rm553945 - Henrique Rafael Gomes de Souza
- rm554223 - Pedro Henrique Mello

## Tecnologias

- Java 17+
- Spring Boot
- Spring Security + JWT
- Spring Data JPA
- MySQL
- Flyway
- Lombok
- Swagger(OpenAPI)

## Estrutura do Baco de Dados

### Modelo Relacional Conceitual

![Modelo Conceitual](static/Conceitual.png)

### Modelo Relacional Lógico

![Modelo Lógico](static/Logico.png)

## Arquitetura em camadas

```mermaid
graph TD
    A["Controller - UsuarioController · MensagemController · AuthLoginController"]
    B["Service - UsuarioService · MensagemService · AuthLoginService"]
    C["Repository - UsuarioRepository · MensagemRepository · AuthLoginRepository"]
    D[(MySQL - usuarios · mensagens · auth_logins)]
    A --> B
    B --> C
    C --> D
```

## Diagrama ER

```mermaid
erDiagram
  usuarios {
    bigint id PK
    varchar celular
    boolean ativo
  }
  mensagens {
    bigint id PK
    varchar role
    varchar content
    datetime data_hora
    bigint usuario_id FK
  }
  auth_logins {
    bigint id PK
    varchar login
    varchar senha
    varchar perfil
  }
  usuarios ||--o{ mensagens : possui
```

## Casos de uso

```mermaid
graph LR
    PUB([Público])
    ADM([Admin / Owner])
    OWN([Owner])
    PUB --> GET_MSG["GET /mensagens"]
    PUB --> GET_MSG_USR["GET /mensagens/usuario"]
    PUB --> LOGIN["POST /login"]
    ADM --> POST_MSG["POST /mensagens"]
    ADM --> DEL_MSG["DELETE /mensagens"]
    ADM --> GET_USR["GET /usuarios"]
    ADM --> POST_USR["POST /usuarios"]
    ADM --> DEL_USR["DELETE /usuarios"]
    OWN --> GET_AUTH["GET /auth-logins"]
    OWN --> POST_AUTH["POST /auth-logins"]
    OWN --> DEL_AUTH["DELETE /auth-logins"]
```

## Pré-requisitos

- JDK 17+

- Maven

- MySQL rodando localmente
  - iniciar servidor por cmd

    ```powershell
    net start mysql95
    ```

  - criar banco de dados
    ```sql
    Drop Database if Exists chatbot;
    Create Database chatbot;
    ```

- Variáveis de ambiente:
  - DB_PASSWORD (obrigatório)
  - DB_URL (conforme necessário)
  - DB_USER (conforme necessário)
  - JWT_TOKEN (conforme necessário)

A aplicação sobe na porta `8080`.

Alterar entre `spring.profiles.active=prod` e `spring.profiles.active=dev` em `src\main\resources\application.properties` para escolher o modo de produção(seguro) ou de monitoramento por log + documentação swagger(não seguro), respectivamente.

## Logins Registrados para Autenticação

```json
{
  "login": "ownerLogin",
  "senha": "owner"
}
```

```json
{
  "login": "adminLogin",
  "senha": "admin"
}
```

```json
{
  "login": "commonLogin",
  "senha": "common"
}
```

## Controle de Acesso

A API utiliza autenticação via **Bearer Token JWT**. As permissões por rota são:

| Rota              | Método | Acesso         |
| ----------------- | ------ | -------------- |
| `/login`          | `POST` | Público        |
| `/health-check`   | `GET`  | Público        |
| `/mensagens/**`   | `GET`  | Público        |
| `/mensagens/**`   | demais | ADMIN ou OWNER |
| `/usuarios/**`    | todos  | ADMIN ou OWNER |
| `/auth-logins/**` | todos  | OWNER          |

## Endpoints

### Autenticação

| Método | Rota     | Descrição                       |
| ------ | -------- | ------------------------------- |
| `POST` | `/login` | Efetuar login e obter token JWT |

Body da requisição:

```json
{
  "login": "ownerLogin",
  "senha": "owner"
}
```

### Health Check

| Método | Rota            | Descrição                           |
| ------ | --------------- | ----------------------------------- |
| `GET`  | `/health-check` | Verificar se a aplicação está no ar |

### Usuários

| Método   | Rota                | Descrição                       |
| -------- | ------------------- | ------------------------------- |
| `POST`   | `/usuarios`         | Cadastrar novo usuário          |
| `GET`    | `/usuarios`         | Listar todos os usuários ativos |
| `GET`    | `/usuarios/all`     | Listar todos os usuários        |
| `GET`    | `/usuarios/id/{id}` | Buscar usuário por ID           |
| `DELETE` | `/usuarios/{id}`    | Excluir usuário                 |

Body para cadastro:

```json
{
  "celular": "5511942426769"
}
```

### Mensagens

| Método   | Rota                       | Descrição                      |
| -------- | -------------------------- | ------------------------------ |
| `POST`   | `/mensagens`               | Cadastrar nova mensagem        |
| `GET`    | `/mensagens`               | Listar todas as mensagens      |
| `GET`    | `/mensagens/usuario/{id}`  | Listar mensagens de um usuário |
| `GET`    | `/mensagens/mensagem/{id}` | Buscar mensagem por ID         |
| `DELETE` | `/mensagens/{id}`          | Excluir mensagem               |

Corpo para cadastro:

```json
{
  "role": "user",
  "content": "olá",
  "usuario_id": "1"
}
```

### Logins de Autenticação

| Método   | Rota                | Descrição              |
| -------- | ------------------- | ---------------------- |
| `POST`   | `/auth-logins`      | Cadastrar novo login   |
| `GET`    | `/auth-logins`      | Listar todos os logins |
| `GET`    | `/auth-logins/{id}` | Buscar login por ID    |
| `DELETE` | `/auth-logins/{id}` | Excluir login          |

Corpo para cadastro:

```json
{
  "login": "ownerLogin",
  "senha": "$2a$10$hash_bcrypt_aqui",
  "perfil": "OWNER"
}
```

> A senha deve ser enviada já encriptada com BCrypt. Para gerar um hash, execute a classe `EncriptadorDeSenha` localizada em `src\test\java\br\com\chatbot\`.

## Migrations

As migrations do banco de dados são gerenciadas pelo Flyway e ficam em `src/main/resources/db/migration`. Ao subir a aplicação, o Flyway aplica automaticamente todas as migrations pendentes.
