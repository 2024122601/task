# Spring Boot JWT 인증/인가 프로젝트

---

## 프로젝트 설명

이 프로젝트는 Spring Boot 기반 JWT 인증 및 인가 기능을 구현한 백엔드 애플리케이션입니다.  
회원가입과 로그인 기능을 통해 JWT 토큰을 발급하며, 관리자 권한 부여 API를 통해 역할 기반 접근 제어를 제공합니다.  
AWS EC2에 배포되어 운영 중이며, Swagger UI를 통해 API 문서가 제공됩니다.

---

## 주요 기술 스택

- Java 17
- Spring Boot 3.1.4
- Spring Security
- JWT (io.jsonwebtoken 라이브러리)
- springdoc-openapi (Swagger UI)
- AWS EC2 배포
- Gradle 빌드

---

## 실행 방법 (로컬 환경)

1. Java 17 이상 설치 필요  
2. 프로젝트 클론 및 이동

```bash
git clone https://github.com/2024122601/task.git
cd task

Gradle 빌드

```bash
./gradlew build
```

### 3. 로컬 서버 실행

```bash
./gradlew bootRun
```

### 4. 실행 후 접속

* **Swagger UI**: [http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html)
* **API 기본 URL**: [http://localhost:8080](http://localhost:8080)

---

## AWS EC2 배포 정보

* **EC2 API 서버 주소**: [http://3.107.58.108:8080](http://3.107.58.108:8080)
* **Swagger UI 주소**: [http://3.107.58.108:8080/swagger-ui/index.html](http://3.107.58.108:8080/swagger-ui/index.html)

---

## GitHub Repository

* [https://github.com/2024122601/task](https://github.com/2024122601/task)

---

## API 주요 명세

### 1. 회원가입 (Signup)

* **URL**: `/signup`
* **Method**: `POST`

#### Request Body

```json
{
  "username": "user1",
  "password": "password123",
  "email": "user1@example.com"
}
```

#### Response (201 Created)

```json
{
  "id": 1,
  "username": "user1",
  "email": "user1@example.com",
  "roles": ["USER"]
}
```

#### 오류 응답 (400 Bad Request)

```json
{
  "error": {
    "code": "VALIDATION_ERROR",
    "message": "입력값이 올바르지 않습니다."
  }
}
```

---

### 2. 로그인 (Login)

* **URL**: `/login`
* **Method**: `POST`

#### Request Body

```json
{
  "username": "user1",
  "password": "password123"
}
```

#### Response (200 OK)

```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "tokenType": "Bearer"
}
```

#### 오류 응답 (401 Unauthorized)

```json
{
  "error": {
    "code": "AUTHENTICATION_FAILED",
    "message": "아이디 또는 비밀번호가 올바르지 않습니다."
  }
}
```

---

### 3. 관리자 권한 부여 (Grant Admin Role)

* **URL**: `/admin/users/{userId}/roles`
* **Method**: `PATCH`

#### Path Variable

* `userId`: 권한을 부여할 사용자 ID

#### Header

* `Authorization: Bearer {accessToken}` (ADMIN 권한 필요)

#### Response (200 OK)

```json
{
  "id": 2,
  "username": "user2",
  "email": "user2@example.com",
  "roles": ["USER", "ADMIN"]
}
```

#### 오류 응답 (403 Forbidden)

```json
{
  "error": {
    "code": "ACCESS_DENIED",
    "message": "관리자 권한이 필요합니다."
  }
}
```

#### 오류 응답 (404 Not Found)

```json
{
  "error": {
    "code": "USER_NOT_FOUND",
    "message": "사용자를 찾을 수 없습니다."
  }
}
```
