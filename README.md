# allra-backend-assignment  
올라 핀테크 백엔드 과제전형
지원자: 이재홍  

## 📘 프로젝트 개요  
이 저장소는 **올라(Allra) 핀테크 백엔드 과제**를 위해 개발된 **Spring Boot 기반 API 서버**입니다.  
요구된 기능은 RESTful 방식으로 구현되었으며, 계층별 구조(Controller–Service–Repository)와 테스트 코드가 포함되어 있습니다.

## ⚙️ 프로젝트 구조 정리
- Spring Boot 루트 기반 빌드 구조 (`build.gradle`, `settings.gradle` 루트 위치)
- Gradle Wrapper(`gradlew`)로 버전 고정 빌드 및 실행
- Render 배포 환경 자동 인식 및 빌드 검증 완료

## ⚙️ 주요 기술 스택
| 구분 | 내용 |
|------|------|
| 언어 | Java 21 |
| 프레워크 | Spring Boot 3.4.11 |
| 빌드 도구 | Gradle (Wrapper 포함) |
| ORM | Spring Data JPA |
| DB | MySQL 8.x |
| QueryDSL | querydsl-jpa (Jakarta 버전) |
| Lombok | 보일러플레이트 코드 제거 |
| 테스트 | JUnit5, Mockito, MockMvc |

---

## 💻 개발 환경 (권장)
- OS: **Windows 11**  
- JDK: **Java 21** (Temurin 등 호환 배포판 가능)  
- IDE: **IntelliJ IDEA / VS Code**  
  - Lombok 플러그인 활성화  
  - Annotation Processing ON  
- DB: **MySQL 8.x 이상**  
- 빌드 도구: **Gradle Wrapper (`gradlew`, `gradlew.bat`)** — 별도 설치 불필요

---

## ⚙️ 애플리케이션 주요 설정
- Spring Boot 애플리케이션 클래스: `BackendAssignmentApplication.java`
- 애플리케이션 설정: `application.yaml`
  - 기본 DB URL: `jdbc:mysql://localhost:3306/allra?createDatabaseIfNotExist=true&serverTimezone=Asia/Seoul`
  - 기본 DB 계정: `root` / `9181` (샘플값 — 실제 환경에서는 변경부탁드립니다)
  - JPA: `hibernate.ddl-auto: update` (스키마 자동 반영)
  - SQL 초기화: `spring.sql.init.mode: always` (앱 실행 시 `data.sql`이 항상 실행됩니다)
- 초기 데이터
  - Spring Boot실행시 `data.sql` (샘플 유저(USER) 데이터 삽입(5명))
  - Spring Boot실행시 `data.sql` (샘플 유저(PRODUCT) 데이터 삽입(16개))
- 유저(USER)테스트 데이터 관리:
  - UserControllerTest에서 JUnit 테스트는 기존 USER데이터를 정리 후 @BeforeEach로 테스트 데이터를 삽입하고,
    @AfterEach로 userRepository.deleteAll()을 실행하여 테스트 종료 후 USER테이블 데이터를 자동 정리합니다.
  - 따라서 bootRun 시에는 USER테이블 데이터가 `data.sql`에 의해 다시 삽입됩니다.

---

## 빌드 / 실행 / 테스트 (Windows PowerShell, Git Bash 예)
레포지토리 루트에서 Gradle Wrapper 사용

PowerShell 예:

```powershell
# 테스트 실행
.\gradlew.bat test

# 애플리케이션 실행 (개발용)
.\gradlew.bat bootRun

# 실행 가능한 JAR 생성
.\gradlew.bat bootJar

# 빌드된 JAR 실행
java -jar .\build\libs\backend-assignment-0.0.1-SNAPSHOT.jar
```
* 참고: Git Bash 또는 WSL을 사용하는 경우 `./gradlew` 명령을 사용해도 됩니다.

Git Bash 관련 요점

- Git Bash에서는 Gradle Wrapper를 `./gradlew`로 실행합니다.
- Windows에서 Git Bash 사용 시 `gradlew`에 실행 권한이 필요할 수 있어 `chmod +x gradlew`를 권장합니다.
- 환경변수 설정은 Bash 스타일(`export`) 또는 한 줄로 명령 앞에 지정하는 방식(임시)으로 가능합니다.

명령 예 (Git Bash / Bash)

``` bash
# Gradle 빌드
./gradlew clean build
./gradlew clean build -x test

# 테스트 실행
./gradlew test

# 애플리케이션 실행 (개발)
./gradlew bootRun

# 실행 가능한 JAR 생성
./gradlew bootJar

# 빌드된 JAR 실행
java -jar build/libs/backend-assignment-0.0.1-SNAPSHOT.jar

```

## 데이터베이스 설정
본 프로젝트는 기본적으로 **MySQL**을 사용합니다. 접속 정보는 `application.yaml`에서 관리되며, 민감 정보(특히 비밀번호)는 환경 변수로 주입. -> 로컬에서는 기본값 설정으로 해두지만 스테이지/운영환경을 분리하기 위한 확장성때문에 환경변수로 설정함.

* 환경 변수로 DB 설정하기 (권장)
application.yaml 예시 (변수 사용):

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/allra?createDatabaseIfNotExist=true&serverTimezone=Asia/Seoul
    username: ${DB_USERNAME:root}
    password: ${DB_PASSWORD:9181}
    driver-class-name: com.mysql.cj.jdbc.Driver
```

* PowerShell (Windows) 예시:

```powershell
$env:DB_USERNAME = "root"
$env:DB_PASSWORD = "9181"
.\gradlew.bat bootRun
```

* macOS / Linux (Git Bash) 예시:

```bash
export DB_USERNAME=root
export DB_PASSWORD=9181
./gradlew bootRun
```

💡 환경변수가 없을 경우, ${DB_USERNAME:root}, ${DB_PASSWORD:9181} 기본값이 자동 적용됩니다.

---

## 공통 API 응답 및 페이징 처리 설계
본 프로젝트는 API 일관성과 유지보수성을 높이기 위해
공통 응답 Wrapper(ApiResponse) 및 페이징 전용 Wrapper(PageResponse) 구조를 적용했습니다.

🔹 ApiResponse (공통 응답 포맷)
모든 API 응답은 아래와 같은 공통 구조를 따릅니다.

{
  "status": 200,
  "message": "요청이 성공적으로 처리되었습니다.",
  "data": { ... }
}

필드	설명
1. status	HTTP 상태 코드 (예: 200, 404, 400 등)
2. message	처리 결과 메시지
3. data	실제 응답 데이터

설계 의도:

응답 형식을 통일하여 클라이언트에서 일관성 있는 응답 처리가 가능
성공(success), 실패(fail), 리소스 없음(notFound) 등
주요 HTTP 상태별로 정적 팩토리 메서드 제공

예:
return ApiResponse.success("상품 목록 조회 성공", data);
return ApiResponse.fail(HttpStatus.BAD_REQUEST, "잘못된 요청입니다.");

🔹 PageResponse (페이징 처리 구조)
상품 목록 등 페이징이 필요한 API는 PageResponse<T> 클래스를 통해 응답합니다.
Spring Data JPA의 Page<T> 객체를 감싸 아래와 같이 직관적인 구조로 반환합니다.

{
  "row": [ { ... }, { ... } ],
  "pageInfo": {
    "page": 1,
    "size": 10,
    "totalElements": 45,
    "totalPages": 5,
    "last": false
  }
}

필드	설명
1. row	실제 데이터 목록
2. pageInfo.page	현재 페이지 번호 (1부터 시작)
3. pageInfo.size	페이지 크기
4. pageInfo.totalElements	전체 데이터 개수
5. pageInfo.totalPages	전체 페이지 수
6. pageInfo.last	마지막 페이지 여부

* 설계 포인트:
PageRequest는 0부터 시작하므로 컨트롤러에서 (page <= 0) ? 0 : page - 1로 보정 처리
응답에서는 page.getNumber() + 1 로 1부터 시작하는 사용자 기준 페이지 번호로 표시
PageResponse를 공통으로 사용하여 페이징 응답 구조의 일관성을 유지

🔹 예시: /products API 응답 구조
```json
{
  "status": 200,
  "message": "상품 목록 조회 성공",
  "data": {
    "row": [
      {
        "id": 101,
        "name": "에어맥스 270",
        "brand": "나이키",
        "category": "신발",
        "price": 159000,
        "stock": 15,
        "soldOut": false,
        "createdAt": "2025-11-05T13:08:21"
      }
    ],
    "pageInfo": {
      "page": 1,
      "size": 10,
      "totalElements": 16,
      "totalPages": 2,
      "last": false
    }
  }
}
```

🔹 설계 요약
계층	역할
1. Controller	요청 파라미터 보정(page - 1), 응답 래핑(ApiResponse.success)
2. Service	비즈니스 로직 수행 및 DTO 변환
3. Repository	JPA를 통한 DB 액세스
4. Global DTO	ApiResponse, PageResponse를 통한 통합 응답 구조 관리

💬 참고: 본 프로젝트는 단순한 서비스 계층 로직을 위해 별도의 `ServiceImpl` 구현체를 두지 않고,  
> 인터페이스 없이 `@Service` 클래스로 직접 구성했습니다.  
> 이는 과제 규모상 불필요한 계층 분리를 피하고 코드 가독성을 높이기 위한 설계 선택입니다.

이 구조를 통해 API 일관성과 확장성을 확보했으며,
심사 환경에서도 직관적인 응답 확인이 가능하도록 설계되었습니다.

## QueryDSL / 코드 생성
- `build.gradle`에 QueryDSL 설정이 되어 있어 빌드 시 `build/generated/querydsl`에 소스가 생성됩니다. IDE에서 해당 폴더를 소스 루트로 등록하면 됩니다.

## Lombok
- Lombok을 사용합니다. IDE에서 Lombok 플러그인을 활성화하고, annotation processing을 켜세요 (IntelliJ: Settings > Build, Execution, Deployment > Compiler > Annotation Processors).

## 자주 발생하는 문제와 해결법
- MySQL 연결 실패: MySQL이 실행 중인지, 포트(3306)가 열려있는지 확인하세요. `application.yaml`의 URL/계정/비밀번호를 확인하세요.
- JDK 버전 문제: Gradle toolchain은 Java 21을 요구합니다. 설치된 Java 버전을 확인하거나 `JAVA_HOME`을 Java 21로 맞추세요.
- Lombok 관련 에러: IDE 플러그인 설치 및 annotation processor 활성화를 확인하세요.

## API 테스트 시 유의사항
> 💡 참고: Postman에서 한글 검색 파라미터를 테스트할 경우, 
> URL 직접 입력 대신 **Params 탭**을 이용하면 UTF-8 인코딩이 정상적으로 적용됩니다.

## 상품 조회(PRODUCT) API 사용 예시
| 기능 | 요청 예시 |
|------|------------|
| 전체 목록 조회 | `GET /api/products?page=1&size=10` |
| 카테고리 검색 | `GET /api/products?category=전자제품&page=1&size=10` |
| 상품명 검색 | `GET /api/products?name=그램&page=1&size=10` |
| 가격 범위 검색 | `GET /api/products?minPrice=1000&maxPrice=50000&page=1&size=10` |
| 상세 조회 | `GET /api/products/{id}` |

> ※ `category(카테고리)`, `name(상품명)`, `minPrice(최소금액)`, `maxPrice(최대금액)` 파라미터는 선택적으로 조합하여 검색할 수 있습니다.
> (예: `/api/products?category=전자제품&name=LG&minPrice=100000&page=1&size=5`)

## 🧩 테스트 전략  
기능 구현 단계에서는 개발 속도를 우선하고,  
핵심 로직 검증과 안정화 과정에서 단위·통합 테스트를 추가하는 전략을 적용함.

## 프로젝트 환경 요약
- 이 프로젝트는 Visual Studio Code / Spring Boot + Java 21 기반의 Gradle 프로젝트입니다. 로컬 환경(Windows 11)에서는 PowerShell을 사용해 `gradlew.bat`로 빌드/테스트/실행하시면 됩니다. MySQL을 준비하고 `application.yaml`의 접속 정보를 필요에 따라 변경해 주세요.

--------------------------------------------------------------------------------------------

## 👤 USER API 기능 구현 당시 AI 도움기록
### 🧭 해결하려던 문제
Spring Boot 환경에서 `UserRepository`, `UserService`, `UserController` 계층을 구성하고,  
전체 사용자 조회 및 단일 사용자 조회 API를 구현하는 과정에서  
데이터 초기화(`data.sql`), Optional 처리, 예외 응답 구조, Swagger 문서화 관련 문제를 해결하고자 함.

---

### 💬 대화 요약

---

**Q: 단일 사용자 조회 시 존재하지 않을 경우 어떻게 처리하나요?**  
**A:** REST 표준에 따라 null 반환 대신 비즈니스 예외(`BusinessException`)를 발생시키는 것이 적절함.  
`userRepository.findById(id).orElseThrow(() -> new BusinessException("사용자를 찾을 수 없습니다."))`  
패턴으로 수정함.

---

**Q: 더미 유저 데이터를 초기화하려면 어떤 설정이 필요한가요?**  
**A:** `data.sql` 파일 사용 가능.  
`spring.sql.init.mode=always`와 `ddl-auto=create`를 함께 설정해야  
애플리케이션 시작 시 테이블 생성 후 데이터 삽입이 정상 동작함.

---

**Q: Swagger에서 사용자 API를 그룹화하려면 어떻게 하나요?**  
**A:** `@Tag(name = SwaggerTags.USER_NAME, description = SwaggerTags.USER_DESC)`  
를 컨트롤러 상단에 선언해 Swagger UI에서 “👤 사용자 API”로 구분되도록 설정함.

---

**Q: Optional이 비어 있을 때 404 응답을 반환하려면 어떻게 하나요?**  
**A:** Controller 레벨에서 `ResponseEntity.status(HttpStatus.NOT_FOUND)`를 직접 반환하거나,  
Service 레벨에서 예외를 던지고 `@ControllerAdvice`를 통해 일괄 처리하는 방식을 권장함.

---

**Q: `User` 테이블명 충돌 문제는 어떻게 해결했나요?**  
**A:** H2/MySQL 모두 `user`는 예약어이므로,  
`@Table(name = "users")`로 테이블명을 변경하여 충돌을 방지함.

---

**Q: 테스트 환경에서 한글 데이터가 깨지는 문제를 해결하려면?**  
**A:** `spring.datasource.url` 설정에  
`characterEncoding=UTF-8` 및 `serverTimezone=Asia/Seoul` 옵션을 추가하여 해결함.

---

### ✅ 최종 적용 결과
- `GET /api/users` → 전체 사용자 목록 조회  
- `GET /api/users/{id}` → 단일 사용자 상세 조회 (404 예외 처리 포함)  
- Swagger 그룹화 및 통합 응답 DTO 적용  
- `data.sql` 기반 더미 유저 자동 삽입 정상 작동  

---

✔️ **AI 사용 목적:**  
Controller·Service 계층 설계 정립, 예외 처리 구조 개선, Swagger 문서 자동화  

✔️ **활용 범위:**  
DTO 변환, 예외 응답 표준화, 데이터 초기화 설정, 문서화 구조 설계  

✔️ **대표 프롬프트 예시:**  
- “Optional이 비어 있을 때 REST API에서 어떤 응답을 주는 게 맞나요?”  
- “Swagger에서 사용자 API를 그룹 단위로 관리하려면 어떻게 하나요?”  
- “User 테이블명 충돌을 H2에서 어떻게 해결하나요?”

--------------------------------------------------------------------------------------------

## 🧩 PRODUCT 테이블 상품 목록 API 기능 구현 당시 AI 도움기록
### 🧭 해결하려던 문제
Spring Boot 환경에서 `ProductRepository`, `ProductService`, `ProductController` 계층을 구성하고,  
QueryDSL 기반의 상품 검색(`searchProducts`) 및 상세 조회(`findById`) 기능을 구현·테스트하는 과정에서  
검색 조건 처리, 테스트 데이터 검증, QueryDSL 동작 검증 관련 이슈를 해결하고자 함.

---

### 💬 대화 요약

**Q: ControllerTest와 ServiceTest의 차이는 무엇인가요?**  
**A:** `ControllerTest`는 HTTP 요청/응답 흐름과 JSON 직렬화를 검증하는 통합 테스트,  
`ServiceTest`는 비즈니스 로직(쿼리 실행·변환·트랜잭션)을 단위로 검증하는 구조라고 설명함.

---

**Q: Repository 테스트는 꼭 필요한가요?**  
**A:** 필요함. QueryDSL 기반의 커스텀 쿼리(`searchProducts`)의 JPQL 구문과 조건 빌더(`BooleanBuilder`)가  
정상 작동하는지 검증하는 것이 핵심임.

---

**Q: 테스트 환경은 어떻게 분리하나요?**  
**A:** `application-test.yml`을 별도로 구성하여  
`jdbc:h2:mem:testdb`와 `ddl-auto=create-drop` 설정으로 테스트 DB를 독립적으로 운용함.

---

**Q: 최종 테스트 검증 방향은 어떻게 정리되었나요?**  
**A:** Repository 로직(`searchProducts`)과 동일하게 조건 조합 기반 테스트를 구성하고,  
데이터 매칭 및 검색 결과의 일관성을 검증하는 형태로 마무리함.

---

## 🛒 CART 테이블 사용자별 장바구니 API 기능 구현 당시 AI 참고 기록
### 🧭 해결하려던 문제
사용자별 장바구니(`CartEntity`)와 장바구니 상품(`CartItemEntity`)의 CRUD 기능을 구현하며  
상품 중복 처리, 권한 검증, 공통 검증 유틸 설계, RESTful 경로 구조 확립,  
그리고 Service 단 테스트 구조 설계를 함께 점검함.

---

### 💬 대화 요약

**Q: 동일 상품 추가 시 중복 처리는 설계 기준에서 어떤 방향으로 하면 좋을까요?**  
> 동일 상품이 이미 존재하면 `quantity + 1`로 수량을 증가시키고,  
> 존재하지 않으면 새 `CartItemEntity`를 생성하여 `EntityManager.persist()`로 저장하는 방식으로 구성함.

---

**Q: 수량 수정과 삭제 API는 규격은 어떤 기준으로 구분 하는게 좋을까요?**  
> - 수량 수정: `PATCH /users/{userId}/carts/{cartId}/items/{cartItemId}`  
> - 상품 삭제: `DELETE /users/{userId}/carts/{cartId}/items/{cartItemId}`  
> - 장바구니 전체 삭제: `DELETE /users/{userId}/carts/{cartId}`  
> RESTful 원칙에 맞게 엔드포인트를 명확히 분리함.

---

**Q: 권한 검증은 어떤 방향으로 처리하면 좋을까요?**  
> `AuthValidator.validateOwnership(entityUserId, requestUserId, targetName)`  
> 공통 유틸을 활용해 단순 조건문 대신 예외(`BusinessException`) 중심으로 통일하고,  
> Spring Security 미적용 환경에서도 최소한의 접근 제어가 가능하도록 설계함.

---

**Q: Service 단 테스트는 어떤 단위로 구성히면 좋을까요?**  
> 실제 DB 호출이 아닌 Mockito 기반 Mock 테스트로  
> `addProductsToCart`, `updateCartItemQuantity`, `deleteCartItem`, `deleteEntireCart` 등  
> 각 로직의 분기(수량 증가, 예외 발생, 삭제 호출 등)를 검증함.  
> Repository는 별도로 통합 테스트(`CartRepositoryTest`)로 구성하여 QueryDSL 조인 동작을 확인함.

---

**Q: 최종 구조는 어떻게 정리되었나요?**  
> - `CartController` CRUD API 완성 (`GET`, `POST`, `PATCH`, `DELETE`)  
> - `AuthValidator` 유틸 도입으로 권한 검증 공통화  
> - 동일 상품 수량 증가 및 전체 삭제 로직 분리  
> - Service 단 Mock 기반 테스트 구성 완료  
> - `SwaggerTags` 기반 API 문서 자동화 적용  

---

✔️ **AI 참고 목적:**  
Service 계층 테스트 구조 검토, 로직 분기 검증 방식 점검,  
예외 처리 및 검증 로직 통일, Swagger 문서 구성 최적화  

✔️ **활용 범위:**  
비즈니스 로직 정리, 테스트 설계 확인, API 문서 정비, RESTful 구조 검증



> 💬 본 대화 내용은 AI(ChatGPT)를 활용하여 코드 설계, 디버깅, 테스트 개선을 수행한 내역을 정리한 것입니다.
