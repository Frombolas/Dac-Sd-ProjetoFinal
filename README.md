# Sistema de Pedidos e Pagamentos (Kafka + Gateway de Pagamento)

Projeto que integra as disciplinas de **Sistemas Distribuídos** e **Desenvolvimento de Aplicações Corporativas**: um fluxo de pedidos assíncrono via Apache Kafka acoplado a uma integração real com um gateway de pagamento.

## Arquitetura

```
Cliente --REST--> [order-service] --Kafka (pedidos-criados)--> [payment-service] --REST--> Gateway de Pagamento
                        |                                              |
                        H2                                            H2
```

- **order-service** (porta 8081): recebe pedidos via REST, persiste em H2 e publica um evento `OrderCreatedEvent` no tópico Kafka `pedidos-criados`.
- **payment-service** (porta 8082): consome o tópico `pedidos-criados`, chama o gateway de pagamento (Mercado Pago, modo sandbox) para gerar a cobrança, persiste o resultado e expõe endpoints de consulta de status e histórico.

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

2. Definir o token de acesso do gateway (Mercado Pago sandbox):
   ```bash
   export MERCADOPAGO_ACCESS_TOKEN=seu-access-token-de-teste
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

