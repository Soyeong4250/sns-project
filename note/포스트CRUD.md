#### 1️⃣ 포스트 등록
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

#### 2️⃣ 포스트 상세 조회

✅  PathVariable로 id(포스트 번호)를 넘겨줌

✅ postId를 통해 존재하는 포스트인지 검증 후, 존재하지 않는다면 `ErrorCode.POST_NOT_FOUND` 와 `해당 포스트가 없습니다.` return

✅ 포스트 상세 조회 성공 시 `200` 상태코드와 Response로 `SUCCESS` resultCode와  `조회한 포스트의 정보(id, title, body, userName, createdAt, lastModifiedAt)를 담은 result` return

✅ 포스트 상세 조회 성공의 경우를 `given().willReturn()` 을 이용하여 PostService를 통해 나오는 결과를 미리 예측하고 mock Test 진행 👉 Controller Test

✅ 포스트 상세 조회 성공의 경우를 `when().willReturn()` 를 이용하여 PostRepository를 통해 나오는 결과를 미리 예측하고 mock Test 진행 👉 Service Test

<br />

#### 3️⃣ 포스트 목록 조회

✅ 페이징 처리를 위한 Pageable 객체 활용 (`@PageableDefault`를 활용한 사이즈 정의)

✅ 포스트 목록 조회 성공 시 `200` 상태코드와 Response로 `SUCCESS` resultCode와  `조회한 포스트 목록을 담은 result` return

✅ 포스트 상세 조회 성공의 경우를 `given().willReturn()` 을 이용하여 PostService를 통해 나오는 결과를 미리 예측하고 mock Test 진행 👉 Controller Test

<br />

#### 4️⃣ 포스트 수정

✅ title과 body만을 입력으로 필요로 한다

✅ PathVariable로 id(포스트 번호)를 넘겨주며, `Authentication` 을 이용하여 현재 로그인한 회원에 대한 정보를 확인할 수 있어야 한다

✅ userName를 통해 존재하는 회원인지 검증 후, 존재하지 않는다면 `ErrorCode.USERNAME_NOT_FOUND` 와 `~은(는) 없는 회원입니다.` return

✅ postId를 통해 존재하는 포스트인지 검증 후, 존재하지 않는다면 `ErrorCode.POST_NOT_FOUND` 와 `해당 포스트가 없습니다.` return

✅`ADMIN`이 아니거나 작성자와 불일치하는 경우 `ErrorCode.INVALID_PERMISSION`와 `사용자가 권한이 없습니다.` return

✅ 권한이 `ADMIN`이라면 작성자와 불일치해도 수정 가능

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

#### 5️⃣ 포스트 삭제

✅ PathVariable로 id(포스트 번호)를 넘겨주며, `Authentication` 을 이용하여 현재 로그인한 회원에 대한 정보를 확인할 수 있어야 한다

✅ userName를 통해 존재하는 회원인지 검증 후, 존재하지 않는다면 `ErrorCode.USERNAME_NOT_FOUND` 와 `~은(는) 없는 회원입니다.` return

✅ postId를 통해 존재하는 포스트인지 검증 후, 존재하지 않는다면 `ErrorCode.POST_NOT_FOUND` 와 `해당 포스트가 없습니다.` return

✅`ADMIN`이 아니거나 작성자와 불일치하는 경우 `ErrorCode.INVALID_PERMISSION`와 `사용자가 권한이 없습니다.` return

✅ 권한이 `ADMIN`이라면 작성자와 불일치해도 포스트 삭제 가능

✅ postId를 통해 존재하는 포스트인지 검증 후, 존재하지 않는다면 `ErrorCode.POST_NOT_FOUND` 와 `해당 포스트가 없습니다.` return

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