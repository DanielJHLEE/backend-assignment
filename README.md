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

## 프로젝트 환경 요약
- 이 프로젝트는 Visual Studio Code / Spring Boot + Java 21 기반의 Gradle 프로젝트입니다. 로컬 환경(Windows 11)에서는 PowerShell을 사용해 `gradlew.bat`로 빌드/테스트/실행하시면 됩니다. MySQL을 준비하고 `application.yaml`의 접속 정보를 필요에 따라 변경해 주세요.

--------------------------------------------------------------------------------------------

## USER테이블 유저 목록 API 기능 구현 당시 AI 도움기록
### 해결하려던 문제
Spring Boot 기반의 올라 핀테크 과제 프로젝트에서 `User` 도메인의 CRUD 중
**유저 목록 조회(List)** 와 **상세 조회(Detail)** 기능을 구현하기 위해,
Controller → Service → Repository 계층 구조를 설계하고
더미 데이터, 엔티티 매핑, 테스트 코드(JUnit/Mockito)를 포함한 전체 구현을 진행함.

### 대화 요약

#### Q: `User` 기능을 구현할 때 Controller, Service, Repository 구조는 어떻게 나누는 게 좋은가요?
**A:**
Spring의 Layered Architecture 원칙에 따라 3계층으로 나누는 것이 적절합니다.
- Controller: API 요청 및 응답 처리
- Service: 비즈니스 로직 수행
- Repository: DB 접근 (JPA 사용)

#### Q: `UserService` 없이 Controller에서 Repository를 바로 호출해도 될까요?
**A:**
작은 규모의 기능이라도 Service 계층을 두는 것이 바람직합니다.
Controller는 요청 흐름만 담당하고, 로직(조회, 변환 등)은 Service에서 처리해야
유지보수성과 테스트 효율이 높아집니다.

#### Q: `UserDto`는 어떻게 구성하는 게 좋을까요? Request/Response를 분리해야 하나요?
**A:**
회원가입은 없고 조회만 있으므로 `UserDto.Response`만 사용해도 충분합니다.
필드는 `id`, `name`, `email`, `createdAt` 중심으로 구성하고
Lombok의 `@Builder`로 매핑을 간결하게 유지합니다.

#### Q: 더미 데이터를 자동으로 삽입하려면 어떻게 설정하나요?
**A:**
`data.sql` 파일을 `src/main/resources` 하위에 생성하고,
`application.yml`에 아래 설정을 추가합니다:

```yaml
spring:
  sql:
    init:
      mode: always
  jpa:
    defer-datasource-initialization: true

#### Q: User 테이블에 이미 데이터가 있는데 data.sql이 다시 실행되면 중복 오류가 납니다. 어떻게 방지하나요?
**A:**
테이블 초기화가 필요하지 않다면 spring.sql.init.mode=embedded로 변경하거나,
SQL에 INSERT IGNORE 구문을 사용하여 중복 삽입을 방지합니다.
또는 테스트 개발 단계에서는 deleteAll() 후 재삽입 로직을 테스트 전 단계에서 수행할 수 있습니다.

### Q: Controller와 Service 테스트는 각각 어떤 차이가 있나요?
**A:**
Service 테스트: 실제 DB 없이 로직만 검증 (@Test + Mockito.mock())

Controller 테스트: 실제 API 요청을 검증 (@SpringBootTest + MockMvc)
Service는 비즈니스 로직 단위 테스트,
Controller는 엔드포인트 단위 통합 테스트로 구분됩니다.

### Q: MockMvc를 쓰면 가짜(Mock) 객체로 테스트하는 건가요?
**A:**
1. UserServiceTest: JUnit + Mockito를 사용한 단위 테스트
findAll() 및 findById() 로직 검증

2. UserControllerTest: SpringBootTest + MockMvc 통합 테스트
/users, /users/{id} API 응답 상태 및 JSON 구조 검증

--------------------------------------------------------------------------------------------

## PRODUCT테이블 상품 목록 API기능 구현 당시 AI 도움기록
## 해결하려던 문제
Spring Boot 환경에서 `ProductRepository`, `ProductService`, `ProductController`의 계층 구조를 구성하고,  
QueryDSL 기반의 상품 검색 기능(`searchProducts`)과 상세 조회 기능(`findById`)의 테스트 코드를 작성 및 검증하는 과정에서  
데이터베이스 설정, 공백/대소문자 처리, Optional 반환 검증 등 다양한 테스트 실패 이슈를 해결하고자 함.

---

## 대화 요약

### Q: ProductControllerTest에서 service를 호출하는 이유가 뭔가요?
**A:** 컨트롤러 테스트는 실제 API 요청 흐름을 검증하기 위해 `MockMvc`로 컨트롤러를 호출하고,  
내부 로직은 `@MockBean`으로 `Service` 계층을 모킹(mocking)하여 컨트롤러만 독립적으로 테스트하는 구조가 맞다고 설명함.

---

### Q: ProductServiceTest와 ControllerTest의 차이는 무엇인가요?
**A:** `ServiceTest`는 비즈니스 로직(Repository 호출, 변환, 트랜잭션 등)을 단위 테스트하는 것이고,  
`ControllerTest`는 HTTP 요청/응답 흐름과 JSON 직렬화를 검증하기 위한 통합 테스트임을 구분하여 설명함.

---

### Q: Repository 테스트는 굳이 필요할까요?
**A:** 필요함. 특히 QueryDSL이나 커스텀 쿼리를 직접 작성한 경우,  
JPQL 문법이나 조건문(`BooleanBuilder`)이 실제로 잘 작동하는지 확인하기 위한 검증이 중요하다고 제안함.

---

### Q: Repository에 `save()` 메서드가 없는데 테스트는 어떻게 하나요?
**A:** Repository에서 `EntityManager.persist()`를 직접 사용하거나 `@DataJpaTest` 환경에서  
H2 인메모리 DB를 통해 `persist()` 후 `flush()`로 데이터를 반영하여 테스트하도록 안내함.

---

### Q: `application-test.yml`을 따로 만들어도 될까요?  
**A:** 가능함. `spring.datasource.url=jdbc:h2:mem:testdb` 설정으로 테스트 환경을 분리하고,  
`ddl-auto=create-drop` 옵션을 추가해 테스트 실행 시마다 테이블을 자동 생성/삭제하도록 구성하는 방법을 제시함.

---

### Q: `No qualifying bean of type 'ProductRepository'` 오류가 발생합니다.
**A:** `@SpringBootTest` 또는 `@DataJpaTest` 어노테이션을 추가해 스프링 컨텍스트를 로드하고,  
`@ActiveProfiles("test")`로 테스트 프로필을 활성화해야 `@Repository` 빈이 등록된다고 설명함.

---

### Q: `TransactionRequiredException`이 발생하는 이유가 뭔가요?
**A:** `EntityManager.persist()`를 사용한 후 트랜잭션이 없기 때문에 발생하는 예외로,  
테스트 클래스에 `@Transactional`을 추가해 해결하도록 안내함.

---

### Q: 테스트 실행 시 `user` 테이블 관련 오류가 뜹니다.
**A:** `user`는 H2/MySQL에서 예약어이므로 엔티티 테이블명(`@Table(name="users")`)을 변경하거나  
`MODE=MySQL`을 추가하여 호환 모드로 실행해야 한다고 설명함.

---

### Q: 공백 제거 로직 때문에 테스트 결과가 0건으로 나옵니다.
**A:** Repository 로직에서 `name.replaceAll("\\s+", "")`로 공백을 제거하기 때문에  
테스트에서도 `"아이폰15"`처럼 공백 없는 검색어를 사용해야 정상적으로 매칭된다고 설명함.

---

### Q: `"상품명 검색이 대소문자 구분 없이 정상 동작해야 한다"` 테스트에서 공백 테스트는 왜 제외했나요?
**A:** 이미 Repository 로직에서 검색어의 공백을 제거하고 비교하기 때문에  
공백 입력 자체를 테스트하는 것은 의미가 없다고 분석함.

---

### Q: `"상품 상세 조회 - ID로 조회 시 Optional 반환 확인"` 테스트의 의도는 올바른가요?
**A:** 이 테스트는 존재하는 ID 조회 시 Optional이 `Present` 상태임을 확인하는 것이므로 정상임.  
존재하지 않는 경우를 테스트하려면 별도로 `"존재하지 않는 ID 조회 시 Empty 반환"` 테스트를 추가하라고 설명함.

---

### Q: `"카테고리와 가격 범위 조건으로 필터링"` 테스트 외에 상품명 조건도 같이 테스트해야 할까요?
**A:** 맞음. 기존 테스트는 단일 조건 조합 검증용이고,  
새로 `"카테고리, 가격, 상품명"`을 모두 포함하는 복합 조건 테스트를 추가하는 것이 바람직하다고 제안함.

---

### Q: `"searchProducts_withCategoryPriceAndName"` 테스트가 실패합니다 (expected:1 but was:0)
**A:** 공백 제거 로직(`replaceAll("\\s+", "")`) 때문에 `"아이폰 15"`가 `"아이폰15"`로 변환되어  
DB 데이터 `"아이폰 15 프로"`와 매칭되지 않음.  
검색어를 `"아이폰15"`로 수정해 일관성 있게 맞추라고 안내함.

---

### Q: 최종적으로 테스트 코드는 이렇게 수정하면 되나요?
**A:** 맞음. 아래 구조가 Repository 로직과 완전히 일치하는 최적의 테스트다.  
```java
Page<ProductEntity> result = productRepository.searchProducts(
    "전자제품", "아이폰15", 1000000, 1600000, PageRequest.of(0, 10)
);
assertThat(result.getTotalElements()).isEqualTo(1);
assertThat(result.getContent().get(0).getName()).isEqualTo("아이폰15프로");

--------------------------------------------------------------------------------------------

> 💬 본 대화 내용은 AI(ChatGPT)를 활용하여 코드 설계, 디버깅, 테스트 개선을 수행한 내역을 정리한 것입니다.
