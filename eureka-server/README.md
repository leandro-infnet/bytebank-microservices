# 🔍 ByteBank Eureka Server

Servidor de descoberta de serviços (**Service Discovery**) do ecossistema ByteBank. Baseado no **Spring Cloud Netflix Eureka**.

---

## 📖 Sobre o Projeto

Este componente atua como a "Lista Telefônica" da arquitetura distribuída. Ele é responsável por:

1.  Receber o registro dos microsserviços (Monólito e Transaction Service) quando eles sobem.
2.  Fornecer o endereço (IP/Porta) de um serviço para o outro, eliminando a necessidade de hardcode de URLs.
3.  Monitorar a saúde (_heartbeat_) das instâncias ativas.

---

## 🛠️ Tecnologias Utilizadas

- ☕ **Java 21**
- 🌱 **Spring Boot 3**
- ☁️ **Spring Cloud Netflix Eureka Server**
- 🏛️ **Maven**

---

## 🚀 Como Executar

Este deve ser **sempre o primeiro serviço** a ser iniciado (após o Banco de Dados), pois os outros dependem dele para se registrarem.

### Passos

1.  No diretório do servidor:
    ```bash
    ./mvnw spring-boot:run
    ```
2.  Aguarde a inicialização completa.

---

## 📊 Dashboard e Monitoramento

O Eureka fornece um painel visual para inspecionar a topologia do sistema.

- **URL de Acesso:** `http://localhost:8761`

Ao acessar o dashboard, você deverá ver na seção **"Instances currently registered with Eureka"**:

- `BYTEBANK-MONOLITH`
- `BYTEBANK-TRANSACTION-SERVICE`

_(Nota: Eles só aparecerão após você subir as respectivas aplicações)._
