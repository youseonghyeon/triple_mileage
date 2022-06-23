## 트리플여행자 클럽 마일리지 서비스

> 트리플 사용자들이 장소에 리뷰를 작성할 때 포인트를 부여하고, 전체/개인에  
> 대한 포인트 부여 히스토리와 개인별 누적 포인트를 관리하고자 합니다.

## 초기 설정

### Q타입 생성

* Gradle -> Tasks -> other -> compileQuerydsl 또는
* 프로젝트 파일에서 아래 명령어 입력
  > ./gradlew clean compileQuerydsl

### DB 연결
application.yml 파일 위치   
-> `src/main/resources/application.yml`   
-> `src/test/resources/application.yml`  
* 호스트, DB이름, username, password 입력
* DDL(자동 등록) : `src/main/resources/ad/schema.sql`

![db](https://user-images.githubusercontent.com/78669797/175276964-c07620c6-d97b-44a9-85c6-7b0018b5b6f5.png)  

## 실행 방법

### 이벤트
POST `http://localhost:8080/events`
* 리뷰 추가 body (리뷰 추가 메서드가 실행되기 전에 해당UUID의 photo, user, place 더미 객체가 생성됩니다.)
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

* 리뷰 수정 body (content, photo 값이 수정됩니다.)
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

* 리뷰 삭제 body (해당 place에서 발생한 mileage가 복원됩니다.)
``` json
{  
  "type": "REVIEW",  
  "action": "DELETE",  
  "reviewId": "240a0658-dc5f-4878-9381-ebb7b2667722",  
  "userId": "3ede0ef2-92b7-4817-a5f3-0c575361f746",  
  "placeId": "2e4baf1c-5acb-4efb-a1af-eddada31b099"  
 }
```

<br>  

### 포인트 조회
GET `http://localhost:8080/point/{userId}`  
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
