# 🏦 ByteBank Monolith API

API RESTful legada responsável pelo núcleo de cadastro do ecossistema ByteBank. Atua como o serviço de domínio para gestão de clientes e contas bancárias.

---

## 📖 Sobre o Projeto

O ByteBank Monolith é o serviço central que detém a "verdade" sobre os dados cadastrais. Ele permite a gestão completa de usuários (clientes) e a administração de contas bancárias.

Originalmente um monólito, este serviço agora opera integrado a uma arquitetura de microsserviços, servindo dados cruciais para outros componentes como o **Transaction Service** via API REST.

---

## 🛠️ Tecnologias Utilizadas

- ☕ **Java 21**
- 🌱 **Spring Boot 3**
- ☁️ **Spring Cloud Netflix Eureka Client**: Para registro e descoberta de serviço.
- 💾 **Spring Data JPA & Hibernate**: Para persistência de dados.
- 🔐 **Spring Security**: Para _hashing_ de senhas e segurança básica.
- ✔️ **Jakarta Bean Validation**: Para validação dos dados de entrada.
- 🏛️ **Maven**: Gerenciador de dependências e _build_.
- 📄 **Lombok**: Para redução de _boilerplate_.

---

## 🚀 Como Executar

Este serviço deve ser executado em conjunto com o **Eureka Server** e o banco de dados **PostgreSQL**.

### Pré-requisitos

- Java (JDK) 21.
- Banco de dados PostgreSQL rodando na porta `5432` (Banco: `bytebank_db`).
- Eureka Server rodando na porta `8761`.

### Passos

1.  No diretório raiz do monólito:
    ```bash
    ./mvnw spring-boot:run
    ```
2.  A API estará disponível em `http://localhost:8080`.
3.  Verifique o registro no Eureka em `http://localhost:8761`.

---

## 🗺️ Modelo de Domínio

O serviço gerencia as entidades fundamentais do sistema bancário:

- **User:** O cliente portador de CPF e credenciais.
- **Account:** A conta bancária (Agência/Número) vinculada a um User.

---

## ✨ Funcionalidades

- **Gestão de Usuários**: Cadastro, consulta e atualização de dados de clientes.
- **Gestão de Contas**: Criação de contas, bloqueio/desbloqueio e consulta de saldo.
- **API Interna**: Fornece endpoints exclusivos para integração com o _Transaction Service_ (validação de contas e atualização de saldo).

---

## 📚 Endpoints da API

Documentação dos recursos disponíveis neste serviço.

### 👤 Usuários (`/users`)

| Verbo  | Rota          | Descrição                | Exemplo de Corpo                                                                                          |
| :----- | :------------ | :----------------------- | :-------------------------------------------------------------------------------------------------------- |
| `POST` | `/users`      | Cria um novo usuário.    | `{ "name": "Ana", "email": "ana@email.com", "password": "12345678", "documentNumber": "111.222.333-44" }` |
| `GET`  | `/users/{id}` | Busca usuário por ID.    | N/A                                                                                                       |
| `GET`  | `/users`      | Lista todos os usuários. | N/A                                                                                                       |

### 🏦 Contas (`/accounts`)

| Verbo  | Rota                            | Descrição                                                       | Exemplo de Corpo                                                     |
| :----- | :------------------------------ | :-------------------------------------------------------------- | :------------------------------------------------------------------- |
| `POST` | `/accounts`                     | Cria nova conta para um usuário.                                | `{ "userId": 1, "agencyNumber": "0001", "accountType": "CHECKING" }` |
| `GET`  | `/accounts/{id}`                | Busca conta por ID.                                             | N/A                                                                  |
| `GET`  | `/accounts/search`              | **(Interno)** Busca conta pelo número. Usado pelo Feign Client. | `?number=1234-5`                                                     |
| `POST` | `/accounts/{id}/balance/update` | **(Interno)** Atualiza o saldo. Usado pelo Transaction Service. | `{ "amount": 100.00 }`                                               |

---

## 🔗 Integração

Este serviço se comunica com:

- **Eureka Server**: Para se registrar como `BYTEBANK-MONOLITH`.
- **Transaction Service**: Recebe chamadas para validar dados e efetivar débitos/créditos.
