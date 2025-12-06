# 💸 ByteBank Transaction Service

Microsserviço responsável pelo núcleo financeiro do ecossistema ByteBank. Este serviço orquestra as operações de movimentação de saldo, comunicando-se com o Monólito para validar dados e persistir atualizações.

---

## 📖 Sobre o Projeto

O Transaction Service foi extraído do monólito original seguindo o padrão de arquitetura de microsserviços. Ele isola a lógica de transações, permitindo que operações financeiras escalem independentemente da gestão de cadastros.

Sua principal característica é a comunicação síncrona com o serviço legado (Monólito) via **OpenFeign** para garantir a consistência dos dados bancários.

---

## 🛠️ Tecnologias Utilizadas

- ☕ **Java 21**
- 🌱 **Spring Boot 3**
- ☁️ **Spring Cloud OpenFeign**: Para comunicação declarativa REST com o Monólito.
- ☁️ **Spring Cloud Netflix Eureka Client**: Para descoberta de serviços.
- 💾 **Spring Data JPA & PostgreSQL**: Para persistência do histórico de transações.
- ✔️ **Jakarta Bean Validation**: Para validação de dados.
- 🏛️ **Maven**: Gerenciamento de dependências.

---

## 🚀 Como Executar

Este serviço depende que o **Eureka Server**, o **Banco de Dados** e o **Monólito** estejam online.

### Pré-requisitos

- Banco de dados PostgreSQL (Porta 5432).
- Eureka Server (Porta 8761).
- Bytebank Monolith (Porta 8080) - _Necessário para processar as transações_.

### Passos

1.  No diretório do serviço:
    ```bash
    ./mvnw spring-boot:run
    ```
2.  A API estará disponível em `http://localhost:8081`.

---

## 📚 Endpoints da API

Documentação das operações financeiras disponíveis.

### 💸 Transações (`/transactions`)

| Verbo  | Rota                       | Descrição                     | Exemplo de Corpo                                                                             |
| :----- | :------------------------- | :---------------------------- | :------------------------------------------------------------------------------------------- |
| `POST` | `/transactions/deposit`    | Realiza crédito em conta.     | `{ "destinationAccountNumber": "1234-5", "amount": 100.00 }`                                 |
| `POST` | `/transactions/withdrawal` | Realiza débito em conta.      | `{ "sourceAccountNumber": "1234-5", "amount": 50.00 }`                                       |
| `POST` | `/transactions/transfer`   | Transfere valor entre contas. | `{ "sourceAccountNumber": "1234-5", "destinationAccountNumber": "9876-0", "amount": 25.00 }` |

---

## 🔗 Integração (Como funciona)

Quando uma requisição chega neste serviço:

1.  **Validação:** O serviço consulta o **Eureka** para achar o `BYTEBANK-MONOLITH`.
2.  **Consulta:** Usa o **Feign Client** para verificar se a conta existe no Monólito.
3.  **Processamento:** Aplica regras de negócio (ex: saldo insuficiente).
4.  **Efetivação:** Ordena ao Monólito que atualize o saldo da conta.
5.  **Registro:** Salva o comprovante da transação no banco de dados local.
