# SNS PROJECT

## 목차

1. [프로젝트 소개](#프로젝트-소개)
2. [Swagger]()



## 프로젝트 소개

멋쟁이 사자처럼 최종 프로젝트로, SNS 개발을 위한 프로젝트 입니다.



## Swagger

도메인 적용 : http://soyeong.cloud:8080/swagger-ui/

ec2 (퍼블릭 IPv4 DNS) : http://ec2-43-201-32-133.ap-northeast-2.compute.amazonaws.com:8080/swagger-ui/



## 미션 요구사항 분석 체크리스트

### 1주차 미션 요약

------

**[접근 방법]**

#### 1️⃣ 회원가입

✅ userName과 password만을 입력으로 필요로 한다

✅ password는  `BCryptPasswordEncoder`를 이용하여 암호화를 거친 후 데이터베이스에 저장

✅ userName이 중복되는 경우 `409 Error` 가 발생하고 Response로 `DUPLICATED_USER_NAME` 과 `~은(는) 이미 있습니다.` return

✅ 회원가입 성공 시 `200` 상태코드와 Response로 `SUCCESS` resultCode와 `userId와 userName을 담은 result` 객체가 return

✅ 회원가입 성공과 실패의 경우를 `given().willReturn() `또는 `given().willThrow()`  을 이용하여 UserService를 통해 나오는 결과를 미리 예측하고 mock Test 진행 👉 Controller Test

<br />

#### 2️⃣ 로그인

✅ userName과 password만을 입력으로 필요로 한다

✅ userName를 통해 존재하는 회원인지 검증 후, 존재하지 않는다면 `ErrorCode.USERNAME_NOT_FOUND` 와 `~은(는) 없는 회원입니다.` return

✅ 입력된 password와 DB에 암호화 되어 있던 password를  `BCryptPasswordEncoder`를 이용하여 일치하는지 확인

✅ 로그인 성공 시  `200` 상태코드와 Response로 `SUCCESS` resultCode와  `jwt를 담은 result` return 👉 토큰 발행

✅ 로그인 성공과 실패의 경우를 `given().willReturn()` 또는 `given().willThrow()` 을 이용하여 UserService를 통해 나오는 결과를 미리 예측하고 mock Test 진행 👉 Controller Test

<br />

#### 3️⃣ 포스트 등록

✅ title과 body만을 입력으로 필요로 한다

✅ userName를 통해 존재하는 회원인지 검증 후, 존재하지 않는다면 `ErrorCode.USERNAME_NOT_FOUND` 와 `~은(는) 없는 회원입니다.` return

✅ 등록 성공 시  `200` 상태코드와 Response로 `SUCCESS` resultCode와  `포스트 등록 완료 message와 postId를 담은 result` return

✅ 포스트 등록 성공과 실패의 경우를 `given().willReturn()` 또는 `given().willThrow()` 을 이용하여 PostService를 통해 나오는 결과를 미리 예측하고 mock Test 진행 👉 Controller Test

 - 포스트 등록 성공
 - 포스트 작성 실패 - JWT를 Bearer Token으로 보내지 않은 경우
- 포스트 작성 실패 - JWT가 유효하지 않은 경우

✅ 포스트 등록 성공과 실패의 경우를 `when().willReturn()` 또는 `when().thenThrow()` 를 이용하여 PostRepository를 통해 나오는 결과를 미리 예측하고 mock Test 진행 👉 Service Test

- 포스트 등록 성공
- 포스트 작성 실패 - 회원이 존재하지 않을 때

<br />

#### 4️⃣ 포스트 상세 조회

✅  PathVariable로 id(포스트 번호)를 넘겨줌

✅ 포스트 상세 조회 성공 시 `200` 상태코드와 Response로 `SUCCESS` resultCode와  `조회한 포스트의 정보(id, title, body, userName, createdAt, lastModifiedAt)를 담은 result` return

✅ 포스트 상세 조회 성공의 경우를 `given().willReturn()` 을 이용하여 PostService를 통해 나오는 결과를 미리 예측하고 mock Test 진행 👉 Controller Test

✅ 포스트 상세 조회 성공의 경우를 `when().willReturn()` 를 이용하여 PostRepository를 통해 나오는 결과를 미리 예측하고 mock Test 진행 👉 Service Test

<br />

#### 5️⃣ 포스트 목록 조회

✅ 페이징 처리를 위한 Pageable 객체 활용 (`@PageableDefault`를 활용한 사이즈 정의)

✅ 포스트 목록 조회 성공 시 `200` 상태코드와 Response로 `SUCCESS` resultCode와  `조회한 포스트 목록을 담은 result` return

✅ 포스트 상세 조회 성공의 경우를 `given().willReturn()` 을 이용하여 PostService를 통해 나오는 결과를 미리 예측하고 mock Test 진행 👉 Controller Test

<br />

#### 6️⃣ 포스트 수정

✅ title과 body만을 입력으로 필요로 한다

✅ PathVariable로 id(포스트 번호)를 넘겨주며, `Authentication` 을 이용하여 현재 로그인한 회원에 대한 정보를 확인할 수 있어야 한다

✅ 수정 성공 시  `200` 상태코드와 Response로 `SUCCESS` resultCode와  `포스트 수정 완료 message와 postId를 담은 result` return

✅ 포스트 수정 성공과 실패의 경우를 `given().willReturn()` 또는 `given().willThrow()` 을 이용하여 PostService를 통해 나오는 결과를 미리 예측하고 mock Test 진행 👉 Controller Test

 - 포스트 수정 성공
 - 포스트 수정 실패 - 인증 실패
- 포스트 수정 실패 - 작성자 불일치
- 포스트 수정 실패 - 데이터베이스 에러

✅ 포스트 수정 성공과 실패의 경우를 `when().willReturn()` 또는 `when().thenThrow()` 를 이용하여 PostRepository를 통해 나오는 결과를 미리 예측하고 mock Test 진행 👉 Service Test

- 포스트 수정 성공

- 포스트 수정 실패 - 포스트 존재하지 않음

- 포스트 수정 실패 - 작성자와 유저가 일치하지 않는 경우 

  👉 포스트를 작성한 User와 현재 로그인한 User를 다르게 설정

- 포스트 수정 실패 - 유저 존재하지 않음

<br />

#### 7️⃣ 포스트 삭제

✅ PathVariable로 id(포스트 번호)를 넘겨주며, `Authentication` 을 이용하여 현재 로그인한 회원에 대한 정보를 확인할 수 있어야 한다

✅ 삭제 성공 시  `200` 상태코드와 Response로 `SUCCESS` resultCode와  `포스트 삭제 완료 message와 postId를 담은 result` return

✅ 포스트 삭제 성공과 실패의 경우를 `given().willReturn()` 또는 `given().willThrow()` 을 이용하여 PostService를 통해 나오는 결과를 미리 예측하고 mock Test 진행 👉 Controller Test

 - 포스트 삭제 성공
 - 포스트 삭제  실패 - 인증 실패
- 포스트 삭제  실패 - 작성자 불일치
- 포스트 삭제 실패 - 데이터베이스 에러

✅ 포스트 삭제 성공과 실패의 경우를 `when().willReturn()` 또는 `when().thenThrow()` 를 이용하여 PostRepository를 통해 나오는 결과를 미리 예측하고 mock Test 진행 👉 Service Test

- 포스트 삭제 성공

- 포스트 삭제 실패 - 포스트 존재하지 않음

- 포스트 삭제  실패 - 작성자와 유저가 일치하지 않는 경우 

  👉 포스트를 작성한 User와 현재 로그인한 User를 다르게 설정

- 포스트 삭제 실패 - 유저 존재하지 않음

<br />

#### 8️⃣ 회원 권한 수정

- `@Secured` 와 `@EnableGlobalMethodSecurity(securedEnabled = true)` 를 적용함으로써 `ADMIN` 권한을 가지고 있는 User만 접근 가능
- 혹시 모를 예외에 대비하여 Service에서도 현재 로그인한 User의 권한이 `ADMIN` 인지 확인하는 예외 처리를 추가로 진행 
- `USER` 권한을 가진 회원이 접근 시 `403 Error` 가 발생하여 이를 ErrorCode에 추가하였으나 예외처리는 아직 하지 못함😢



**[특이사항]**

- 더 깔끔한 코드를 작성할 수도 있었을 것 같은데 기능의 작동을 먼저 생각하다보니 코드가 조금 지저분해진 것 같음 
- 8️⃣ 회원 권한 수정 시 `USER` 권한을 가진 회원이 접근했을 때 발생하는 `403 Error`에 대한 예외처리를 하지 못한 점이 아쉬움
- `@Secured` 외에도 사용자의 접근 권한에 대해 설정하는 어노테이션에 대해 학습이 더 필요
- JPA의 Dirty Check (변경 감지) 에 대해 이번 개발을 진행하며 처음 알게 되어 추가로 학습을 할 예정



**[참고한 자료 및  소스 코드]**

- 이전에 학습했던 Git과 정리 내용 : https://github.com/Soyeong4250/spring-security-exercise

- 교재 : 스프링 부트 핵심 가이드
