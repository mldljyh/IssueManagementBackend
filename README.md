# CAU SWE Term Project - Team3: IssueManagementBackend

## 프로젝트 개요

이 프로젝트는 CAU SWE Term Project IssueManagementBackend를 위한 백엔드 애플리케이션입니다. Java Spring Boot 프레임워크를 기반으로 하며, RESTful API를 통해 프론트엔드와 통신합니다. 

## 관련 프로젝트
**프론트엔드 웹:** [https://github.com/uj-lee/IssueManagementFront](https://github.com/uj-lee/IssueManagementFront)

**프론트엔드 콘솔:** [https://github.com/mldljyh/IssueManagement_Console](https://github.com/mldljyh/IssueManagement_Console)

## 예시 사이트
**프론트엔드 웹 사이트:** [https://issue.mldljyh.tech](https://issue.mldljyh.tech)

**백엔드 API 사이트:** [https://swe.mldljyh.tech](https://swe.mldljyh.tech)

## 구조
- **backend:**
    - **config:** 애플리케이션 설정 파일들이 위치한다.
    - **controller:** RESTful API 엔드포인트를 처리하는 컨트롤러 클래스들이 위치한다.
    - **dto:** 데이터 전송 객체 (DTO) 클래스들이 위치한다.
    - **exceptions:** 예외 처리 클래스들이 위치한다.
    - **model:** 데이터베이스 엔터티와 도메인 모델 클래스들이 위치한다.
    - **repository:** 데이터베이스 접근을 위한 JPA Repository 인터페이스들이 위치한다.
    - **security:** JWT 토큰 기반 보안 설정과 관련된 클래스들이 위치한다.
    - **service:** 비즈니스 로직을 처리하는 서비스 인터페이스와 구현체 클래스들이 위치한다.
    - **util:** 유틸리티 함수들을 모아놓은 클래스들이 위치한다.
    - 
## 기술 스택

* Java 22
* Spring Boot 3.2.5
* Spring Data JPA
* Spring Security
* PostgreSQL
* Azure OpenAI
* ModelMapper
* Caffeine Cache
* JWT (JSON Web Token)
* Lombok
* Springdoc OpenAPI UI
* OkHttp

## 기능

* 사용자 관리 (회원가입, 로그인)
* 프로젝트 관리 (생성, 조회, 삭제)
* 이슈 관리 (생성, 조회, 수정, 삭제, 검색)
* 댓글 관리 (생성, 조회, 수정, 삭제)
* 이슈 통계 조회
* Azure OpenAI 연동을 통한 이슈 임베딩 및 추천 담당자 제공

## 설치 및 실행 방법

### 1. application.properties 설정

`backend/src/main/resources/application.properties` 파일을 열고 다음 설정 정보를 입력합니다.

```properties
# Database Configuration
spring.datasource.url=YOUR_DATABASE_URL
spring.datasource.username=YOUR_DATABASE_USERNAME
spring.datasource.password=YOUR_DATABASE_PASSWORD

# JWT Configuration
jwt.secret=YOUR_JWT_SECRET
jwt.expiration=3600

# Sprint Cache Configuration
spring.cache.type=caffeine
spring.cache.caffeine.spec=maximumSize=1000,expireAfterAccess=1h

# Server compression Configuration
server.compression.enabled=true
server.compression.mime-types=application/json,application/xml,text/html,text/xml,text/plain
server.compression.min-response-size=1024
server.tomcat.max-http-header-size=65536
server.tomcat.max-save-post-size=-1
server.tomcat.max-swallow-size=-1
server.tomcat.max-file-size=50MB
server.tomcat.max-request-size=50MB
```

- `YOUR_DATABASE_URL`:  Azure PostgreSQL 서버의 JDBC 연결 문자열을 입력합니다.
- `YOUR_DATABASE_USERNAME`: Azure PostgreSQL 데이터베이스 사용자 이름을 입력합니다.
- `YOUR_DATABASE_PASSWORD`:  Azure PostgreSQL 데이터베이스 암호를 입력합니다.
- `YOUR_JWT_SECRET`: JWT 토큰 생성 및 검증에 사용할 비밀 키를 입력합니다.

### 2. Azure PostgreSQL 설정

1. Azure Portal에서 PostgreSQL 서버를 생성합니다.
2. 서버에 데이터베이스를 생성합니다.
3. 데이터베이스 연결을 위한 방화벽 규칙을 설정합니다.
4. `application.properties` 파일에 데이터베이스 연결 정보를 입력합니다.

### 3. Azure OpenAI 설정

1. Azure Portal에서 OpenAI 서비스를 생성합니다.
2. 텍스트 임베딩 모델을 배포합니다. (예: `text-embedding-3-small`)
3. 데이터베이스에서 아래와 같은 명령어를 실행합니다
```sql
CREATE EXTENSION vector;
CREATE EXTENSION azure_ai;

select azure_ai.set_setting('azure_openai.endpoint', 'https://<endpoint>.openai.azure.com'); 
select azure_ai.set_setting('azure_openai.subscription_key', '<API Key>');

CREATE TABLE issue_embeddings (
    issue_id integer NOT NULL REFERENCES issues(id),
    issue_embedding vector(1536),
    CONSTRAINT unique_issue_id UNIQUE (issue_id)
);
```

더 자세한 정보는 [Azure OpenAI 공식 문서](https://learn.microsoft.com/ko-kr/azure/postgresql/flexible-server/generative-ai-azure-openai)를 참조하세요.

### 4. 컴파일 및 실행

프로젝트 루트 디렉토리에서 다음 명령어를 실행하여 애플리케이션을 빌드하고 실행합니다.

```bash
mvn clean package
java -jar target/backend-0.0.1-SNAPSHOT.jar
```

애플리케이션이 실행되면 `http://localhost:8080` 에서 접속할 수 있습니다.

## API 문서

API 문서는 Springdoc OpenAPI UI를 사용하여 제공됩니다. 애플리케이션이 실행된 후 `http://localhost:8080/swagger-ui/index.html` 에서 확인할 수 있습니다.
