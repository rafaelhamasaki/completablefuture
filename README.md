# CompletableFuture Chapter

Aplicação criada para apresentação do _chapter_ sobre programação concorrente utilizando o `CompletableFuture`.

## Pré-requisitos

- Java 11
- Criação de conta de desenvolvedor no Spotify
- Registro de uma aplicação no Spotify para obtenção de um `client_id` e um `client_secret`

Mais informações na [documentação oficial](https://developer.spotify.com/documentation/web-api/quick-start/).

## Execução

```bash
mvnw spring-boot:run \
    -Dspring-boot.run.arguments="\
        --clientId=<client_id da sua aplicação>\
        --clientSecret=<client_secret da sua aplicação>"
```

## Exemplo de requisição

```bash
curl 'localhost:8080?artist=Snarky%20Puppy&artist=Hiatus%20Kaiyote'
```

## Nota
- Não esqueça de fazer _escape_ dos parâmetros de _query_ - Ou utilize o Postman ou alguma ferramenta que faça isso automaticamente

## Referências
- JavaDoc - https://docs.oracle.com/en/java/javase/17/docs/api/java.base/java/util/concurrent/CompletableFuture.html
- CompletableFuture for Asynchronous Programming in Java 8 - https://community.oracle.com/tech/developers/discussion/4418058/completablefuture-for-asynchronous-programming-in-java-8
- Guide to CompletableFuture - https://www.baeldung.com/java-completablefuture
- CompletableFuture – The Difference Between thenApply/thenApplyAsync - https://4comprehension.com/completablefuture-the-difference-between-thenapply-thenapplyasync/
