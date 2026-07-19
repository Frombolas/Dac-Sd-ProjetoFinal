# Sistema de Pedidos e Pagamentos (Kafka + Gateway de Pagamento)

Projeto que integra as disciplinas de **Sistemas Distribuídos** e **Desenvolvimento de Aplicações Corporativas**: um fluxo de pedidos assíncrono via Apache Kafka acoplado a uma integração real com um gateway de pagamento.

## Arquitetura

```
Cliente --REST--> [order-service] --Kafka (pedidos-criados)--> [payment-service] --REST--> Gateway de Pagamento
                        |                                              |
                        H2                                            H2
```

- **order-service** (porta 8081): recebe pedidos via REST, persiste em H2 e publica um evento `OrderCreatedEvent` no tópico Kafka `pedidos-criados`.
- **payment-service** (porta 8082): consome o tópico `pedidos-criados`, chama o gateway de pagamento (Stripe, modo teste) para gerar a cobrança, persiste o resultado e expõe endpoints de consulta de status e histórico.

## Tópico Kafka

| Tópico | Producer | Consumer | Payload |
|---|---|---|---|
| `pedidos-criados` | order-service | payment-service (group `payment-service`) | `OrderCreatedEvent { pedidoId, descricao, valor, criadoEm }` |

## Endpoints

### order-service — `http://localhost:8081`
| Método | Rota | Descrição |
|---|---|---|
| POST | `/api/pedidos` | Cria um pedido e publica o evento no Kafka |
| GET | `/api/pedidos/{id}` | Consulta um pedido |
| GET | `/api/pedidos` | Lista todos os pedidos |

Swagger: `http://localhost:8081/swagger-ui.html`

### payment-service — `http://localhost:8082`
| Método | Rota | Descrição |
|---|---|---|
| GET | `/api/pagamentos/pedido/{pedidoId}` | Consulta o status do pagamento de um pedido |
| GET | `/api/pagamentos` | Lista o histórico de pagamentos |

Swagger: `http://localhost:8082/swagger-ui.html`

## Como executar

1. Subir a infraestrutura (Kafka + Kafka UI):
   ```bash
   docker compose up -d
   ```
   Kafka UI disponível em `http://localhost:8090` para acompanhar o fluxo de mensagens no tópico.

2. Definir a chave do gateway (Stripe, modo teste — copie `payment-service/.env.example` para `.env`
   e preencha, ou exporte direto):
   ```bash
   export STRIPE_SECRET_KEY=sk_test_sua-chave-de-teste
   ```

3. Rodar cada serviço (em terminais separados):
   ```bash
   cd order-service && mvn spring-boot:run
   cd payment-service && mvn spring-boot:run
   ```

4. Criar um pedido:
   ```bash
   curl -X POST http://localhost:8081/api/pedidos \
     -H "Content-Type: application/json" \
     -d '{"descricao": "Assinatura mensal", "valor": 99.90}'
   ```
   O payment-service consome o evento e processa o pagamento em segundos; consulte o status em `/api/pagamentos/pedido/{id}`.

## Requisitos

- Java 21+
- Maven 3.9+
- Docker e Docker Compose
- [Bruno](https://www.usebruno.com/) (ou Postman/Insomnia) para os testes de integração manuais

## Testando com Bruno

A coleção fica na pasta `bruno/` (arquivos `.bru`, texto puro, versionados no repositório). Para usar:

1. Abrir o Bruno → "Open Collection" → selecionar a pasta `bruno/`
2. Selecionar o ambiente **Local** (já aponta para `localhost:8081` e `localhost:8082`)
3. Rodar as requisições em ordem (1 a 5): criar pedido → consultar pedido → listar pedidos →
   consultar pagamento do pedido → listar pagamentos. O `pedidoId` retornado no passo 1 é
   capturado automaticamente e reutilizado nos passos seguintes.

Como o consumo do Kafka é assíncrono, a requisição "Consultar Pagamento do Pedido" espera alguns
segundos antes de disparar; se ainda vier 404, é só rodar de novo.

## Testes automatizados

- `OrderServiceTest` / `PaymentServiceTest`: testes unitários da camada de serviço (Mockito),
  cobrindo criação/publicação no Kafka, chamada ao gateway e os cenários de erro (`*NotFoundException`)
- `OrderControllerTest` / `PaymentControllerTest`: testes de integração da camada REST (`@WebMvcTest`
  + `MockMvc`), cobrindo os status HTTP (200/201/400/404) e o `GlobalExceptionHandler`
- `*ApplicationTests`: sobem o contexto Spring completo (JPA + Kafka) para validar a configuração

Rodar com `mvn test` dentro de cada serviço.
