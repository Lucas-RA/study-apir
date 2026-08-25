# Study APIR

API desenvolvida para estudos de **Spring Boot**: configuração de ambientes (profiles), acesso a banco de dados MySQL, containerização com Docker, publicação no Docker Hub e deploy em **Azure Container Apps**.

Exemplos deste guia:

- Imagem local: `study-api:1.0`
- Imagem no Docker Hub: `270506lu/study-api:1.0.0`
- Aplicação: porta `8080` (dev/prd) — o `application.properties` padrão usa `9000`
- Banco local: MySQL na porta `3306`

Ao repetir o processo em outro projeto, substitua os nomes pelos valores daquele projeto.

## Sumário

1. [Pré-requisitos](#1-pré-requisitos)
2. [Estrutura do projeto](#2-estrutura-do-projeto)
3. [Profiles do Spring Boot](#3-profiles-do-spring-boot)
4. [Execução local sem Docker](#4-execução-local-sem-docker)
5. [Preparar o projeto para Docker](#5-preparar-o-projeto-para-docker)
6. [Build da imagem Docker](#6-build-da-imagem-docker)
7. [MySQL local via Docker](#7-mysql-local-via-docker)
8. [Executar a aplicação no Docker](#8-executar-a-aplicação-no-docker)
9. [Variáveis de ambiente](#9-variáveis-de-ambiente)
10. [Publicar no Docker Hub](#10-publicar-no-docker-hub)
11. [Deploy no Azure Container Apps](#11-deploy-no-azure-container-apps)
12. [Problemas comuns](#12-problemas-comuns)
13. [Comandos Docker úteis](#13-comandos-docker-úteis)
14. [Segurança](#14-segurança)
15. [Ordem resumida do processo](#15-ordem-resumida-do-processo)
16. [Referências](#16-referências)

---

## 1. Pré-requisitos

- Java 17 (versão usada no `pom.xml`)
- Maven ou Maven Wrapper (`mvnw` / `mvnw.cmd`)
- MySQL, caso o banco seja executado localmente sem Docker
- Docker Desktop em execução
- Conta no Docker Hub (para publicar a imagem)
- Assinatura Azure com permissão para criar Container Apps (para o deploy)

## 2. Estrutura do projeto

Arquivos relevantes para este guia, na raiz do projeto:

```text
Dockerfile
.dockerignore
pom.xml
src/main/resources/application.properties
src/main/resources/application-dev.properties
src/main/resources/application-prd.properties
```

## 3. Profiles do Spring Boot

O Spring Boot carrega configurações diferentes por ambiente, controladas pela variável `SPRING_PROFILES_ACTIVE`:

- `application.properties`: configuração padrão (porta `9000`, sem profile ativo)
- `application-dev.properties`: desenvolvimento (porta `8080`, schema `dbdev`)
- `application-prd.properties`: produção (porta `8080`, credenciais 100% por variável de ambiente)

### application-dev.properties (resumo)

```properties
server.port=8080
spring.datasource.url=jdbc:mysql://${DB_SERVER_URL:host.docker.internal}:${DB_SERVER_PORT:3306}/dbdev?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC
spring.datasource.username=${DB_USER:root}
spring.datasource.password=${DB_PWD:root_pwd}
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
```

Em dev, o `ddl-auto=update` permite que o Hibernate ajuste o schema automaticamente — útil para estudo, mas não recomendado em produção.

### application-prd.properties (resumo)

```properties
server.port=8080
spring.datasource.url=jdbc:mysql://${DB_SERVER_URL}:${DB_SERVER_PORT}/${DB_SCHEMA}?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC
spring.datasource.username=${DB_USER}
spring.datasource.password=${DB_PWD}
spring.jpa.hibernate.ddl-auto=none
spring.jpa.show-sql=false
```

Em produção não há valores padrão: `DB_SERVER_URL`, `DB_SERVER_PORT`, `DB_SCHEMA`, `DB_USER` e `DB_PWD` precisam ser fornecidos por variável de ambiente. O `ddl-auto=none` evita que a aplicação altere o schema — crie as tabelas com uma migration SQL antes de iniciar a aplicação.

## 4. Execução local sem Docker

### Definir as variáveis de ambiente

**PowerShell:**

```powershell
$env:DB_SERVER_URL="localhost"
$env:DB_SERVER_PORT="3306"
$env:DB_SCHEMA="dbdev"
$env:DB_USER="root"
$env:DB_PWD="root_pwd"
$env:SPRING_PROFILES_ACTIVE="dev"
```

**Bash:**

```bash
export DB_SERVER_URL=localhost
export DB_SERVER_PORT=3306
export DB_SCHEMA=dbdev
export DB_USER=root
export DB_PWD=root_pwd
export SPRING_PROFILES_ACTIVE=dev
```

### Executar

```powershell
.\mvnw.cmd spring-boot:run
```

```bash
./mvnw spring-boot:run
```

Também é possível escolher o profile diretamente, sem exportar a variável:

```powershell
.\mvnw.cmd spring-boot:run "-Dspring-boot.run.profiles=dev"
```

A aplicação fica disponível em `http://localhost:8080` (profile `dev`/`prd`) ou `http://localhost:9000` (sem profile, configuração padrão).

## 5. Preparar o projeto para Docker

### 5.1 Padronizar o nome do JAR

O Dockerfile precisa saber qual arquivo copiar para a imagem final. O `pom.xml` já está configurado para isso:

```xml
<build>
    <finalName>app</finalName>
</build>
```

Depois do build, o artefato esperado é `target/app.jar`. Sem essa configuração, o Maven geraria um nome como `study-apir-0.0.2.jar`, e uma instrução `COPY .../target/app.jar` no Dockerfile falharia com `not found`.

Para conferir o artefato gerado:

```powershell
Get-ChildItem .\target\*.jar
```

### 5.2 Dockerfile

Multi-stage build: o Maven compila na primeira imagem e apenas o JAR é levado para a imagem final.

```dockerfile
# Etapa 1: compilação
FROM maven:3.9.8-eclipse-temurin-21 AS build
WORKDIR /opt/app
COPY . .
RUN mvn clean package -DskipTests

# Etapa 2: execução
FROM eclipse-temurin:21-alpine-3.21
WORKDIR /opt/app
COPY --from=build /opt/app/target/app.jar /opt/app/app.jar
ENV SPRING_PROFILES_ACTIVE=dev
CMD ["java", "-Dspring.profiles.active=${SPRING_PROFILES_ACTIVE}", "-jar", "app.jar"]
```

> **Atenção:** o `pom.xml` está em Java 17, mas as imagens acima usam Temurin 21. Ajuste as duas etapas para `maven:3.9.8-eclipse-temurin-17` e `eclipse-temurin:17-alpine` para manter a mesma versão do projeto.

> **Atenção:** a forma exec JSON do `CMD` (`["java", "-Dspring.profiles.active=${SPRING_PROFILES_ACTIVE}", ...]`) **não expande** variáveis de ambiente — o shell não é chamado nesse formato, então `${SPRING_PROFILES_ACTIVE}` seria passado literalmente. Para que a variável seja expandida corretamente, use a forma shell:
>
> ```dockerfile
> CMD ["sh", "-c", "java -Dspring.profiles.active=$SPRING_PROFILES_ACTIVE -jar app.jar"]
> ```

### 5.3 .dockerignore

Evita enviar arquivos desnecessários ao contexto do build:

```text
target/
.git
.gitignore
Dockerfile
docker-compose.yml
*.md
*.log
```

O `target/` pode ser ignorado porque o Maven o recria dentro da etapa `build` da imagem.

## 6. Build da imagem Docker

Execute na raiz do projeto, onde estão `Dockerfile` e `pom.xml`:

```powershell
docker build -t study-api:1.0 .
```

O ponto final (`.`) é o contexto do build — sem ele, o Docker retorna erro pedindo um argumento.

Listar imagens:

```powershell
docker images
```

## 7. MySQL local via Docker

Se não houver MySQL rodando na máquina, crie um container para o banco:

**PowerShell:**

```powershell
docker run --name mysql-study `
  -p 3306:3306 `
  -e MYSQL_ROOT_PASSWORD=root_pwd `
  -e MYSQL_DATABASE=dbdev `
  -d mysql:8.0
```

**Bash:**

```bash
docker run --name mysql-study \
  -p 3306:3306 \
  -e MYSQL_ROOT_PASSWORD=root_pwd \
  -e MYSQL_DATABASE=dbdev \
  -d mysql:8.0
```

Verifique o container e aguarde a mensagem `ready for connections`:

```powershell
docker ps
docker logs -f mysql-study
```

## 8. Executar a aplicação no Docker

No Docker Desktop para Windows/Mac, `host.docker.internal` permite que o container acesse o MySQL publicado na máquina host.

**PowerShell:**

```powershell
docker run --name study-api `
  -p 8080:8080 `
  -e DB_SERVER_URL=host.docker.internal `
  -e DB_SERVER_PORT=3306 `
  -e DB_SCHEMA=dbdev `
  -e DB_USER=root `
  -e DB_PWD=root_pwd `
  -e SPRING_PROFILES_ACTIVE=dev `
  study-api:1.0
```

**Bash:**

```bash
docker run --name study-api \
  -p 8080:8080 \
  -e DB_SERVER_URL=host.docker.internal \
  -e DB_SERVER_PORT=3306 \
  -e DB_SCHEMA=dbdev \
  -e DB_USER=root \
  -e DB_PWD=root_pwd \
  -e SPRING_PROFILES_ACTIVE=dev \
  study-api:1.0
```

O primeiro número de `-p` é a porta na máquina host; o segundo é a porta dentro do container. Teste em `http://localhost:8080`.

Ver logs:

```powershell
docker logs -f study-api
```

## 9. Variáveis de ambiente

| Variável | Descrição | Exemplo |
|---|---|---|
| `DB_SERVER_URL` | Endereço do servidor do banco de dados | `localhost` / `host.docker.internal` |
| `DB_SERVER_PORT` | Porta do banco de dados | `3306` |
| `DB_SCHEMA` | Nome do schema | `dbdev` |
| `DB_USER` | Usuário do banco de dados | `root` |
| `DB_PWD` | Senha do banco de dados | `root_pwd` |
| `SPRING_PROFILES_ACTIVE` | Profile ativo do Spring Boot | `dev` / `prd` |

Nunca deixe usuário, senha ou host fixos no código — em produção esses valores só existem como variável de ambiente (veja [Segurança](#14-segurança)).

## 10. Publicar no Docker Hub

### 10.1 Criar o repositório

No Docker Hub:

1. Crie um repositório chamado `study-api`.
2. Defina-o como público se o Azure for consumir a imagem sem credenciais.
3. Anote seu usuário do Docker Hub.

### 10.2 Criar um token de acesso

Em *Account Settings* → *Personal access tokens*, crie um PAT com a permissão `Read & Write`. Isso permite enviar imagens sem permitir apagá-las. Nunca salve ou compartilhe o token.

### 10.3 Login

```powershell
docker login -u SEU_USUARIO
```

Ao ser solicitada a senha, cole o PAT (é normal que nada apareça durante a digitação). Se o token for exposto acidentalmente, revogue-o no Docker Hub e crie outro.

### 10.4 Tag e push

Não confunda o nome do container (`study-api`) com o nome:tag da imagem (`study-api:1.0`). A tag parte sempre da imagem local:

```powershell
docker tag study-api:1.0 SEU_USUARIO/study-api:1.0.0
docker push SEU_USUARIO/study-api:1.0.0
```

Exemplo usado neste projeto:

```powershell
docker tag study-api:1.0 270506lu/study-api:1.0.0
docker push 270506lu/study-api:1.0.0
```

O push terminou corretamente quando aparecem várias camadas como `Pushed` e uma linha como:

```text
1.0.0: digest: sha256:... size: ...
```

## 11. Deploy no Azure Container Apps

A imagem precisa estar em um registry acessível. Uma imagem pública do Docker Hub pode ser usada diretamente: `270506lu/study-api:1.0.0`.

No Portal do Azure, em *Create Container App*:

**Basics**

- Subscription: sua assinatura
- Resource group: crie ou selecione um grupo
- Container app name: nome único, ex. `study-api-dev`
- Deployment source: Container image
- Region: uma região permitida pela assinatura
- Container Apps environment: crie um novo ambiente na mesma região

**Container**

- Image source: Docker Hub or other registries
- Image type: Public
- Registry login server: `docker.io`
- Image and tag: `270506lu/study-api:1.0.0`
- Target port: `8080`

**Ingress**

- Habilite ingress
- Escolha *External* para acesso público
- Porta de destino: `8080`

**Environment variables**

```text
SPRING_PROFILES_ACTIVE=prd
DB_SERVER_URL=<hostname-do-mysql-no-azure>
DB_SERVER_PORT=3306
DB_SCHEMA=<schema>
DB_USER=<usuario>
DB_PWD=<senha>
```

`host.docker.internal` **não funciona no Azure** — esse nome só existe para o Docker Desktop acessar a máquina local. No Azure, informe o hostname real do banco hospedado e libere o acesso de rede necessário.

## 12. Problemas comuns

**`COPY .../target/app.jar` falha com `not found`**
O `finalName` não está configurado no `pom.xml`, ou o build (`mvn clean package`) não foi executado antes do `COPY`. Veja [5.1](#51-padronizar-o-nome-do-jar).

**`docker build` pede um argumento / falha sem contexto**
Faltou o `.` no final do comando `docker build -t nome:versao .`.

**`Connection refused` em `host.docker.internal:3306`**
O MySQL não está iniciado, ainda está inicializando, ou não está escutando nessa porta. Não é um problema de porta da API — confirme com `docker logs -f mysql-study` até aparecer `ready for connections`.

**Nome de container já em uso**

```powershell
docker rm -f study-api
```

**`RequestDisallowedByAzure` / região bloqueada**
A política da assinatura bloqueou aquela região para Container Apps, mesmo que o Resource Group tenha sido criado nela com sucesso. Escolha uma região autorizada no campo *Region* e use a mesma região para o Container Apps Environment — o grupo de recursos pode ficar em outra região.

Para criar um grupo em Brazil South via CLI:

```bash
az group create \
  --name study-apir-rg-br \
  --location brazilsouth
```

O comando funcionar não garante que Container Apps, Log Analytics ou o ambiente gerenciado estejam liberados nessa região — a política pode bloquear especificamente esses recursos. Nesse caso, use outra região permitida ou solicite liberação ao administrador da assinatura.

## 13. Comandos Docker úteis

```powershell
# Containers em execução
docker ps

# Todos os containers
docker ps -a

# Logs
docker logs -f study-api

# Parar e remover a aplicação
docker stop study-api
docker rm study-api

# Remover forçadamente um nome ocupado
docker rm -f study-api

# Listar imagens
docker images

# Remover imagem local
docker rmi study-api:1.0

# Testar a porta do MySQL no Windows
Test-NetConnection localhost -Port 3306
```

## 14. Segurança

Não versione credenciais reais no repositório. Evite armazenar senhas, tokens ou outras credenciais diretamente no código-fonte.

Recomenda-se um arquivo `.env` local para desenvolvimento, adicionado ao `.gitignore`:

```gitignore
.env
```

Um `.env.example` pode ser versionado como referência, sem valores reais:

```env
DB_SERVER_URL=localhost
DB_SERVER_PORT=3306
DB_SCHEMA=dbdev
DB_USER=root
DB_PWD=root_pwd
SPRING_PROFILES_ACTIVE=dev
```

## 15. Ordem resumida do processo

1. Ajustar os profiles e usar variáveis de ambiente em vez de credenciais fixas.
2. Confirmar `<finalName>app</finalName>` no `pom.xml`.
3. Criar/revisar o `Dockerfile` multi-stage.
4. Criar o `.dockerignore`.
5. Construir a imagem: `docker build -t nome:versao .`
6. Subir um MySQL local para teste, se necessário.
7. Executar a aplicação com `docker run` e as variáveis `DB_*`.
8. Login no Docker Hub com um PAT `Read & Write`.
9. `docker tag` para `usuario/repositorio:tag`.
10. `docker push` para publicar a imagem.
11. Criar o Container App usando a imagem pública, porta `8080` e região permitida.
12. No Azure, configurar o banco real — nunca `host.docker.internal`.

## 16. Referências

- [Dockerfile e multi-stage build do professor](https://github.com/acnaweb/java/blob/main/docs/docker.md)
- [Profiles do Spring Boot do professor](https://github.com/acnaweb/java/blob/main/docs/profile.md)
- [Docker Docs](https://docs.docker.com/)
- [Spring Boot externalized configuration](https://docs.spring.io/spring-boot/reference/features/external-config.html)
