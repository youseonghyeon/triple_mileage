## 트리플여행자 클럽 마일리지 서비스

> 트리플 사용자들이 장소에 리뷰를 작성할 때 포인트를 부여하고, 전체/개인에  
> 대한 포인트 부여 히스토리와 개인별 누적 포인트를 관리하고자 합니다.

## 초기 설정

### Q타입 생성

* Gradle -> Tasks -> other -> compileQuerydsl 또는
* 프로젝트 파일에서 아래 명령어 입력
  > ```./gradlew clean compileQuerydsl```

### DB 연결
application.yml 파일 위치   
-> `src/main/resources/application.yml`   
-> `src/test/resources/application.yml`  
* 호스트, DB이름, username, password 입력
* DDL(자동 등록) 위치 : `src/main/resources/db/schema.sql`

![db](https://user-images.githubusercontent.com/78669797/175276964-c07620c6-d97b-44a9-85c6-7b0018b5b6f5.png)  

## 실행 방법

### 이벤트
POST `http://localhost:8080/events`
### 리뷰 추가
* 리뷰 추가 메서드가 실행되기 전에 해당 UUID의 Photo, User, Place의 더미 객체가 생성됩니다.
* 내용을 작성하면 +1점, 사진을 1개이상 등록하면 +1점, Place에서 첫 리뷰를 작성하면 +1점이 부여됩니다.
``` json
{  
  "type": "REVIEW",  
  "action": "ADD",  
  "reviewId": "240a0658-dc5f-4878-9381-ebb7b2667722",  
  "content": "좋아요!",  
  "attachedPhotoIds": ["e4d1a64e-a531-46de-88d0-ff0ed70c0bb8", "afb0cef2-851d-4a50-bb07-9cc15cbdc332"],  
  "userId": "3ede0ef2-92b7-4817-a5f3-0c575361f746",  
  "placeId": "2e4baf1c-5acb-4efb-a1af-eddada31b099"  
 }
```
* 결과
```json
{
    "result": "success",
    "message": "리뷰가 성공적으로 등록되었습니다."
}
```
<br>

### 리뷰 수정
* 리뷰 내용과 사진이 수정됩니다.
* 내용을 지우면 -1, 내용을 새로 작성하면 +1점이 부여됩니다.
* 사진을 지우면 -1, 사진을 새로 등록하면 +1점이 부여됩니다.
``` json
{  
  "type": "REVIEW",  
  "action": "MOD",  
  "reviewId": "240a0658-dc5f-4878-9381-ebb7b2667722",  
  "content": "좋아요 triple!",  
  "attachedPhotoIds": [],  
  "userId": "3ede0ef2-92b7-4817-a5f3-0c575361f746",  
  "placeId": "2e4baf1c-5acb-4efb-a1af-eddada31b099"  
 }
```
* 결과
```json
{
    "result": "success",
    "message": "리뷰가 성공적으로 수정되었습니다."
}
```
<br>

### 리뷰 삭제
* 해당 Place에서 발생했던 마일리지의 총 합 * (-1) 점이 부여됩니다.
* 리뷰는 삭제되지만, Point History에서는 리뷰Id가 기록됩니다.
``` json
{  
  "type": "REVIEW",  
  "action": "DELETE",  
  "reviewId": "240a0658-dc5f-4878-9381-ebb7b2667722",  
  "userId": "3ede0ef2-92b7-4817-a5f3-0c575361f746",  
  "placeId": "2e4baf1c-5acb-4efb-a1af-eddada31b099"  
 }
```
* 결과
```json
{
    "result": "success",
    "message": "리뷰가 성공적으로 삭제되었습니다."
}
```

<br>  

### 포인트 조회
GET `http://localhost:8080/point/{userId}`
* 현재 유저의 마일리지가 출력됩니다.
* 예시
```
http://localhost:8080/point/3ede0ef2-92b7-4817-a5f3-0c575361f746
```
* 결과
```json
{
  "mileage": 8
}
```
<br>  

### 포인트 history 조회
GET `http://localhost:8080/history/{userId}`
* 현재 유저의 마일리지와 마일리지 목록이 날짜(시간) 오름차순으로 출력됩니다.
* 예시
```
http://localhost:8080/history/3ede0ef2-92b7-4817-a5f3-0c575361f746
```
* 결과
```json
{
  "totalMileage": 0,
  "history": [
    {
      "type": "REVIEW",
      "action": "ADD",
      "value": 3,
      "reviewId": "240a0658-dc5f-4878-9381-ebb7b2667722",
      "date": "2022-06-23T20:33:59.140479"
    },
    {
      "type": "REVIEW",
      "action": "MOD",
      "value": -1,
      "reviewId": "240a0658-dc5f-4878-9381-ebb7b2667722",
      "date": "2022-06-23T20:34:25.427655"
    },
    {
      "type": "REVIEW",
      "action": "DELETE",
      "value": -2,
      "reviewId": "240a0658-dc5f-4878-9381-ebb7b2667722",
      "date": "2022-06-23T20:34:52.57399"
    }
  ]
}
```


# ERD
* point_history 테이블의 review_id는 연관관계 없음
* user(1) : (N)point_history
* user(1) : (N)review
* review(1) : (N)photo
* review(N) : (1)place

<img width="764" alt="스크린샷 2022-06-23 오후 11 13 06" src="https://user-images.githubusercontent.com/78669797/175320495-21a7a1ba-18f3-4133-9c29-44571ae2a8cf.png">
