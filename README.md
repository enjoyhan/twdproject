# 휴대폰 구매(Tword-Direct shop)

본 예제는 MSA/DDD/Event Storming/EDA 를 포괄하는 분석/설계/구현/운영 전단계를 커버하도록 구성한 예제입니다.
이는 클라우드 네이티브 애플리케이션의 개발에 요구되는 체크포인트들을 통과하기 위한 예시 답안을 포함합니다.
- 체크포인트 : https://workflowy.com/s/assessment-check-po/T5YrzcMewfo4J6LW

# 서비스 시나리오

Tword-Direct shop 커버하기

기능적 요구사항
1. 매니저가 대리점에 신규 휴대폰을 등록한다.
2. 고객이 원하는 대리점에서 휴대폰을 주문한다.
3. 주문이 되면 주문 내역이 대리점에 전달된다.
4. 대리점 직원이 확인하여 휴대폰을 배송한다.
5. 고객이 주문을 취소할 수 있다.
6. 주문이 취소되면 대리점의 휴대폰 상태는 개통가능 상태로 변경된다.
7. 고객이 주문상태를 중간중간 조회한다.
8. 주문상태가 바뀔 때 마다 메시지로 알림을 보낸다

*****

비기능적 요구사항
1. 트랜잭션
    - 주문이 완료되지 않으면 배송이 되어서는 안된다. (Sync 호출)
2. 장애격리
    - 대리점 관리 기능이 수행되지 않더라도 주문은 365일 24시간 받을 수 있어야 한다 (Async, event-driven, Eventual Consistency)
    - 배송시스템이 과중되면 사용자를 잠시동안 받지 않고 주문을 잠시후에 하도록 유도한다 (Circuit breaker, fallback)
3. 성능
    - 고객이 자주 배송상태를 주문시스템(프론트엔드)에서 확인할 수 있어야 한다 (CQRS)
    - 배송 상태가 바뀔때마다 메시지를 통해 알림을 줄 수 있어야 한다 (Event driven)

*****

 
# 분석/설계

## AS-IS 조직 (Horizontally-Aligned)
  ![image](https://user-images.githubusercontent.com/77129832/119316165-96ca3680-bcb1-11eb-9a91-f2b627890bab.png)

## TO-BE 조직 (Vertically-Aligned)  
  ![image](https://user-images.githubusercontent.com/33124483/124523339-ab155d80-de31-11eb-8df9-8e066f6f5c67.png)

## Event Storming 결과
* MSAEz 로 모델링한 이벤트스토밍 결과:  http://www.msaez.io/#/storming/dlLZ0XpVBZVynkmeTOxA7cMe5pr1/mine/00dbbc5e9d8c8cae7f8b9d71433abfe5

### 이벤트 도출
![image](https://user-images.githubusercontent.com/33124483/124223486-c422c380-db3e-11eb-8855-87489fecaad3.png)

### 부적격 이벤트 탈락
![image](https://user-images.githubusercontent.com/33124483/124223513-d1d84900-db3e-11eb-95ce-dc75789a9724.png)

    - 과정중 도출된 잘못된 도메인 이벤트들을 걸러내는 작업을 수행함
    - 등록시>RoomSearched, 예약시>RoomSelected :  UI 의 이벤트이지, 업무적인 의미의 이벤트가 아니라서 제외

### 액터, 커맨드 부착하여 읽기 좋게
![image](https://user-images.githubusercontent.com/33124483/124226752-917bc980-db44-11eb-85f6-6b44d8494a71.png)

### 어그리게잇으로 묶기
![image](https://user-images.githubusercontent.com/33124483/124226768-9771aa80-db44-11eb-8bbb-2e47879c735b.png)

    - shop, order, delivery 는 그와 연결된 command 와 event 들에 의하여 트랜잭션이 유지되어야 하는 단위로 그들 끼리 묶어줌

### 바운디드 컨텍스트로 묶기

![image](https://user-images.githubusercontent.com/33124483/124226874-bb34f080-db44-11eb-8ffe-ff467ef7b41b.png)

    - 도메인 서열 분리 
        - Core Domain:  shop, oder : 없어서는 안될 핵심 서비스이며, 연간 Up-time SLA 수준을 99.999% 목표, 배포주기는 order 의 경우 1주일 1회 미만, shop 의 경우 1개월 1회 미만
        - Supporting Domain:   message, mypage : 경쟁력을 내기위한 서비스이며, SLA 수준은 연간 60% 이상 uptime 목표, 배포주기는 각 팀의 자율이나 표준 스프린트 주기가 1주일 이므로 1주일 1회 이상을 기준으로 함.
        - General Domain:   delivery : 결제서비스로 3rd Party 외부 서비스를 사용하는 것이 경쟁력이 높음 

### 폴리시 부착 (괄호는 수행주체, 폴리시 부착을 둘째단계에서 해놔도 상관 없음. 전체 연계가 초기에 드러남)

![image](https://user-images.githubusercontent.com/33124483/124229844-2bde0c00-db49-11eb-9cec-3aba820489d1.png)

### 폴리시의 이동과 컨텍스트 매핑 (점선은 Pub/Sub, 실선은 Req/Resp)

![image](https://user-images.githubusercontent.com/33124483/124229177-2fbd5e80-db48-11eb-82f6-87abd7328fff.png)


### 완성된 1차 모형

![image](https://user-images.githubusercontent.com/33124483/124229184-321fb880-db48-11eb-93c6-0cede54c6cd5.png)

    - Mypage Model 추가

### 1차 완성본에 대한 기능적/비기능적 요구사항을 커버하는지 검증

![image](https://user-images.githubusercontent.com/33124483/124229812-2385d100-db49-11eb-8dca-f792716f68df.png)

	1. 매니저가 대리점에 신규 휴대폰과 판매수량을 등록한다. (OK)
	2. 고객이 원하는 대리점에서 휴대폰을 주문한다. (OK)
	3. 주문이 되면 주문 내역이 대리점에 전달된다. (OK)
	4. 대리점 직원이 확인하여 휴대폰을 배송한다. (OK)
	5. 고객이 주문을 취소할 수 있다. (OK)
	6. 주문이 취소된 수량만큼 대리점의 휴대폰 수량이 증가한다. (OK)
	7. 고객이 주문상태를 중간중간 조회한다. (OK)
	8. 주문상태가 바뀔 때 마다 메시지로 알림을 보낸다 (?)
    
### 모델 수정

![image](https://user-images.githubusercontent.com/33124483/124230141-98590b00-db49-11eb-875e-a3b8503acb35.png)
    
    - 수정된 모델은 모든 요구사항을 커버함.

### 비기능 요구사항에 대한 검증

![image](https://user-images.githubusercontent.com/33124483/124231830-c63f4f00-db4b-11eb-894c-c7b46bd37e2c.png)

1. 트랜잭션
    - 주문이 완료되지 않으면 배송이 되어서는 안된다. (Sync 호출)
2. 장애격리
    - 대리점 관리 기능이 수행되지 않더라도 주문은 365일 24시간 받을 수 있어야 한다 (Async, event-driven, Eventual Consistency)
    - 배송시스템이 과중되면 사용자를 잠시동안 받지 않고 주문을 잠시후에 하도록 유도한다 (Circuit breaker, fallback)
3. 성능
    - 고객이 자주 배송상태를 주문시스템(프론트엔드)에서 확인할 수 있어야 한다 (CQRS)
    - 배송 상태가 바뀔때마다 메시지를 통해 알림을 줄 수 있어야 한다 (Event driven)


## 헥사고날 아키텍처 다이어그램 도출

![image](https://user-images.githubusercontent.com/33124483/124523435-08a9aa00-de32-11eb-8652-ec33db0ad98d.png)


    - Chris Richardson, MSA Patterns 참고하여 Inbound adaptor와 Outbound adaptor를 구분함
    - 호출관계에서 PubSub 과 Req/Resp 를 구분함
    - 서브 도메인과 바운디드 컨텍스트의 분리:  각 팀의 KPI 별로 아래와 같이 관심 구현 스토리를 나눠가짐


# 구현:

분석/설계 단계에서 도출된 헥사고날 아키텍처에 따라, 각 BC별로 대변되는 마이크로 서비스들을 스프링부트로 구현하였다. 
구현한 각 서비스를 로컬에서 실행하는 방법은 아래와 같다 (각자의 포트넘버는 8081 ~ 8088 이다)

```
   shop : 8081
   order : 8082
   delivery : 8083
   message : 8084
   mypage : 8085
   geteway : 8088
```
![image](https://user-images.githubusercontent.com/33124483/124414702-2e7d7300-dd8e-11eb-99d0-11183db9ba52.png)


## Saga 패턴
- 각 서비스의 트랜잭션은 단일 서비스 내의 데이터를 갱신하는 일종의 로컬 트랜잭션 방법이고 서비스의 트랜잭션이 완료 후에 다음 서비스가 트리거 되어, 트랜잭션을 실행하는 패턴임
- 아래 그림과 같이 Saga패턴에 맞춘 트랜잭션 처리(빨간색), 취소 시 자동으로 Roll-Back처리(파란색)가 되도록 연쇄적인 트리거 처리를 함
![image](https://user-images.githubusercontent.com/33124483/124935256-75928f00-e040-11eb-900f-589149f9e4ad.png)


## CQRS

- 핸드폰 주문, 주문취소, 배송 등의 Status 에 대하여 고객(Customer)이 조회 할 수 있도록 CQRS 로 구현하였다.
- order, delivery 개별 Aggregate Status 를 통합 조회하여 성능 Issue 를 사전에 예방할 수 있다.
- 비동기식으로 Kafka를 통해 이벤트를 수신하게 되면 별도 CQRS 테이블에 관리한다.

```{.java}
package twdproject;
import twdproject.config.kafka.KafkaProcessor;
...

@Service
public class MypageViewHandler {
    @Autowired
    private MypageRepository mypageRepository;

    @StreamListener(KafkaProcessor.INPUT)
    public void whenOrdered_then_Create(@Payload twdproject.Ordered Ordered) {
        System.out.println("\n\n##### MYPAGE whenOrdered_then_Create : " + Ordered.toJson() + "\n\n");
        if(!Ordered.validate()) return;
        Mypage Mypage = new Mypage();
        Mypage.setOrderId(Ordered.getOrderId());
        Mypage.setEqpStatus(Ordered.getEqpStatus());
        mypageRepository.save(Mypage);
    }

    @StreamListener(KafkaProcessor.INPUT)
    public void whenOrderCancelled_then_Update(@Payload OrderCancelled OrderCancelled) {
        System.out.println("\n\n##### MYPAGE whenOrderCancelled_then_Update : " + OrderCancelled.toJson() + "\n\n");
        if(!OrderCancelled.validate()) return;
        java.util.Optional<Mypage> cOptional = mypageRepository.findById(OrderCancelled.getOrderId());
        Mypage view = cOptional.get();
        view.setOrderId(OrderCancelled.getOrderId());
        view.setEqpStatus(OrderCancelled.getEqpStatus());
        mypageRepository.save(view);
    }

    @StreamListener(KafkaProcessor.INPUT)
    public void whenDeliveryStarted_then_Update(@Payload DeliveryStarted DeliveryStarted) {
        System.out.println("\n\n##### MYPAGE whenDeliveryStarted_then_Update : " + DeliveryStarted.toJson() + "\n\n");
        if(!DeliveryStarted.validate()) return;
        java.util.Optional<Mypage> cOptional = mypageRepository.findById(DeliveryStarted.getOrderId());
        Mypage view = cOptional.get();
        view.setEqpStatus(DeliveryStarted.getEqpStatus());
        view.setDeliveryId(DeliveryStarted.getDeliveryId());

        mypageRepository.save(view);
    }
}
```

- 휴대폰상태가 이벤트에 따라 변경 
![image](https://user-images.githubusercontent.com/33124483/124523611-c6349d00-de32-11eb-8e39-22b6af3a2c9d.png)

## API 게이트웨이

- Gateway 생성을 통하여 마이크로서비스들의 진입점을 통일시킴
  [gateway > src > main > resource > application.yml]

```{.java}
	spring:
	  profiles: default
	  cloud:
	    gateway:
	      routes:
		- id: shop
		  uri: http://localhost:8081
		  predicates:
		    - Path=/shops/** 
		- id: order
		  uri: http://localhost:8082
		  predicates:
		    - Path=/orders/** 
		- id: delivery
		  uri: http://localhost:8083
		  predicates:
		    - Path=/deliveries/** 
		- id: message
		  uri: http://localhost:8084
		  predicates:
		    - Path=/messages/** 
		- id: mypage
		  uri: http://localhost:8085
		  predicates:
		    - Path= /mypages/**
	      globalcors:
		corsConfigurations:
		  '[/**]':
		    allowedOrigins:
		      - "*"
		    allowedMethods:
		      - "*"
		    allowedHeaders:
		      - "*"
		    allowCredentials: true
```

- Gateway 서비스 기동 후 각 서비스로 접근이 가능한지 확인
- Gateway Port 8088을 통해 8081 shop을 통해 휴대폰 등록, 8081 order 통해 주문 및 8085 mypage 통해 주문상태 확인

![image](https://user-images.githubusercontent.com/33124483/124523787-7bffeb80-de33-11eb-8ee4-766dceec1e91.png)
   
# Correlation

#### 1.휴대폰등록
![image](https://user-images.githubusercontent.com/33124483/124523935-f597d980-de33-11eb-873b-cde2117ec816.png)

#### 2.휴대폰주문
![image](https://user-images.githubusercontent.com/33124483/124444079-33a3e780-ddb9-11eb-8cf1-88ee75520652.png)

#### 3.주문 취소
![image](https://user-images.githubusercontent.com/33124483/124444318-6d74ee00-ddb9-11eb-88ac-eae447cad05d.png)

#### 4.취소 후,단말기 상태
![image](https://user-images.githubusercontent.com/33124483/124444382-7fef2780-ddb9-11eb-9145-2f2ad626957d.png)

#### 5.휴대폰배송
![image](https://user-images.githubusercontent.com/33124483/124444858-fc820600-ddb9-11eb-8071-256f4ecc4086.png)

## DDD 의 적용

- 각 서비스내에 도출된 핵심 Aggregate Root 객체를 Entity 로 선언하였다. (예시는shop 마이크로 서비스). 

[shop > src > main > java > shop.java]
```{.java}
package twdproject;

import javax.persistence.*;
import org.springframework.beans.BeanUtils;
import java.util.List;
import java.util.Date;

@Entity
@Table(name="Shop_table")
public class Shop {

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Long id;
    private Long shopId;
    private Long eqpSernum;
    private String eqpNm;
    private String eqpStatus;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
    public Long getShopId() {
        return shopId;
    }

    public void setShopId(Long shopId) {
        this.shopId = shopId;
    }
    public String getEqpNm() {
        return eqpNm;
    }

    public void setEqpNm(String eqpNm) {
        this.eqpNm = eqpNm;
    }
    public String getEqpStatus() {
        return eqpStatus;
    }

    public void setEqpStatus(String eqpStatus) {
        this.eqpStatus = eqpStatus;
    }

    public Long getEqpSernum() {
        return eqpSernum;
    }

    public void setEqpSernum(Long eqpSernum) {
        this.eqpSernum = eqpSernum;
    }

}

```
- Entity Pattern 과 Repository Pattern 을 적용하여 JPA 를 통하여 다양한 데이터소스 유형 (RDB or NoSQL) 에 대한 별도의 처리가 없도록 데이터 접근 어댑터를 자동 생성하기 위하여 Spring Data REST 의 RestRepository 를 적용하였다

[shop > src > main > java > ShopRepository.java]
```{.java}
package twdproject;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(collectionResourceRel="shops", path="shops")
public interface ShopRepository extends PagingAndSortingRepository<Shop, Long>{

}

```
- 적용 후 REST API 의 테스트
```
# shop 서비스의 휴대폰등록
http POST http://localhost:8088/shops shopId=1 eqpNm="galaxy_s21" eqpSernum=1

# order 서비스의 주문 요청
http POST http://localhost:8088/orders shopId=1 eqpNm="galaxy_s21" eqpSernum=1 orderId=1 eqpStatus="Order"

# mapage 서비스의 예약 상태 확인
http GET http://localhost:8088/mypages

```

## 동기식 호출(Sync) 과 Fallback 처리

#### 동기식 호출(Sync)
- 분석단계에서의 조건 중 하나로 order → delivery 간의 호출은 동기식 일관성을 유지하는 트랜잭션으로 처리하기로 하였다. 
  호출 프로토콜은 이미 앞서 Rest Repository 에 의해 노출되어있는 REST 서비스를 FeignClient 를 이용하여 호출하도록 한다.

[order > src > main > java > twdproject > external > DeliveryService.java]
```{.java}
package twdproject.external;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.stereotype.Component;

import java.util.Date;

@FeignClient(name="delivery", url="http://localhost:8083")
public interface DeliveryService {
    
    @RequestMapping(method= RequestMethod.GET, path="/startDelivery")
    public boolean startDelivery(@RequestParam("orderId") Long orderId,
                              @RequestParam("shopId") Long shopId,
                              @RequestParam("eqpNm") String eqpNm,
                              @RequestParam("eqpStatus") String eqpStatus,
                              @RequestParam("eqpSernum") Long eqpSernum);
}
```

- 주문요청을 받은 직후(@PostPersist) 가능상태 및 배송을 동기(Sync)로 요청하도록 처리

```{.java}
@Entity
@Table(name="Order_table")
public class Order {
...

    @PostPersist
    public void onPostPersist(){
        Ordered ordered = new Ordered();
        boolean rslt = OrderApplication.applicationContext.getBean(twdproject.external.DeliveryService.class).startDelivery(this.getOrderId(),this.getShopId() ,this.getEqpNm(),this.getEqpStatus(),this.getEqpSernum());  
	//Order에서 startDelivery이 완료가 되면 응답이 올 것이고, boolean 값으로 판단 후 최종 저장처리를 한다.
        if(rslt) {
            ordered.setOrderId(this.getOrderId());
            ordered.setShopId(this.getShopId());
            ordered.setEqpNm(this.getEqpNm());
            ordered.setEqpSernum(this.getEqpSernum());
            ordered.setEqpStatus(this.getEqpStatus());
            BeanUtils.copyProperties(this, ordered);
            ordered.publishAfterCommit();
        }
    }
```

#### 배송 (delivery) 서비스를 잠시 내려놓음 (ctrl+c)
![image](https://user-images.githubusercontent.com/33124483/124448019-f0e40e80-ddbc-11eb-832f-a8125859537c.png)

#### 주문 요청 (Fail)
![image](https://user-images.githubusercontent.com/33124483/124448096-06593880-ddbd-11eb-9943-66979d0eae2a.png)

#### 결제서비스 재기동
```
cd delivery
mvn spring-boot:run
```
![image](https://user-images.githubusercontent.com/33124483/124448260-36084080-ddbd-11eb-9eaa-6610fdcfdfe6.png)

#### 예약 요청 (Success)
![image](https://user-images.githubusercontent.com/33124483/124449216-2d643a00-ddbe-11eb-9fe9-5aa97b71165b.png)

#### Fallback 처리
- Order-delivery의 Request/Response 구조에 Spring Hystrix를 사용하여 FallBack 기능 구현
1. [order > src > main > java > twdproject > external > DeliveryService.java]에 configuration, fallback 옵션 추가
2. configuration 클래스 및 fallback 클래스 추가
3. [order > src > main > resources > application.yml]에 hystrix

[order > src > main > java > twdproject > external > DeliveryService.java]
```{.java}

package twdproject.external;
...
import feign.Feign;
import feign.hystrix.HystrixFeign;
import feign.hystrix.SetterFactory;

import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixCommandKey;
import com.netflix.hystrix.HystrixCommandProperties;

@FeignClient(name="delivery", url="http://localhost:8083")
public interface DeliveryService {
    
    @RequestMapping(method= RequestMethod.GET, path="/startDelivery")
    public boolean startDelivery(@RequestParam("orderId") Long orderId,
                              @RequestParam("shopId") Long shopId,
                              @RequestParam("eqpNm") String eqpNm,
                              @RequestParam("eqpStatus") String eqpStatus,
                              @RequestParam("eqpSernum") Long eqpSernum);

    @Component
    class DeliveryServiceFallback implements DeliveryService {
        @Override
        public boolean startDelivery(Long orderId, Long shopId, String eqpNm, String eqpStatus, Long eqpSernum){
            System.out.println("\n###PaymentServiceFallback works####\n");   // fallback 메소드 작동 테스트
            return true;
        }
    }

    @Component
    class DeliveryServiceConfiguration {
        Feign.Builder feignBuilder(){
            SetterFactory setterFactory = (target, method) -> HystrixCommand.Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey(target.name()))
            .andCommandKey(HystrixCommandKey.Factory.asKey(Feign.configKey(target.type(), method)))
            // 위는 groupKey와 commandKey 설정
            // 아래는 properties 설정
            .andCommandPropertiesDefaults(HystrixCommandProperties.defaultSetter()
                .withExecutionIsolationStrategy(HystrixCommandProperties.ExecutionIsolationStrategy.SEMAPHORE)
                .withMetricsRollingStatisticalWindowInMilliseconds(10000) // 기준시간
                .withCircuitBreakerSleepWindowInMilliseconds(3000) // 서킷 열려있는 시간
                .withCircuitBreakerErrorThresholdPercentage(50)) // 에러 비율 기준 퍼센트
                ; // 최소 호출 횟수
            return HystrixFeign.builder().setterFactory(setterFactory);
        }        
    }

}
```

[order > src > main > resources > application.yml]
```
feign:
  hystrix:
    enabled: true
```

4. delivdery 서비스 중지해도 오류나지 않고 수행처리함.
![image](https://user-images.githubusercontent.com/33124483/124718856-80b2c580-df41-11eb-972d-e329668310c2.png)

## 비동기식 호출 / 시간적 디커플링 / 장애격리 / 최종 (Eventual) 일관성 테스트

Order에서 주문 이루어 진 후(Ordered) 상태가 Update되고, 오더취소(OrderCancelled) 후 다시 단말기상태가 update 되는 행위는 비동기식으로 처리한다.
 
- 이를 위하여 주문이 취소되면 취소 되었다는 이벤트를 카프카로 송출한다. (Publish)
 
[order > src > main > java > twdproject > Order.java]
```
package twdproject;

import javax.persistence.*;
import org.springframework.beans.BeanUtils;

@Entity
@Table(name="Order_table")
public class Order {

    ....

    @PostRemove
    public void onPostRemove() {

        OrderCancelled orderCancelled = new OrderCancelled();
        
        // this 부분은 http POST 할 때 파라미터를 넣어서 호출할 때 this 객체에 담기고
        // 디폴트로 값을 세팅하려면 this. 으로 항목에 값을 세팅해야함   
        
        // 현재 OrderId와 paymentId가 동일하게 증가하므로 걍 값 세팅
        this.setOrderId(this.getOrderId());
        this.setEqpStatus("OrderCancel");

        orderCancelled.setOrderId(this.getOrderId());
        orderCancelled.setEqpNm(this.getEqpNm());
        orderCancelled.setEqpSernum(this.getEqpSernum());
        orderCancelled.setEqpStatus(this.getEqpStatus());

        BeanUtils.copyProperties(this, orderCancelled);
        orderCancelled.publishAfterCommit();

    }
    
    ....
}
```

- Order 서비스에서는 주문취소(Ordercancelled)를 받으면 단말상태 변경 PolicyHandler 를 구현한다
  [shop > src > main > java > twdproject > policyhandler.java]

```
package twdproject;

@Service
public class PolicyHandler{
    @Autowired ShopRepository shopRepository;

    @StreamListener(KafkaProcessor.INPUT)
    public void wheneverOrderCancelled_CancelEqp(@Payload OrderCancelled orderCancelled){
        
        if(!orderCancelled.validate()) return;
        System.out.println("\n\n##### listener orderCancelled : " + orderCancelled.toJson() + "\n\n");

        java.util.Optional<Shop> ShopOptional = shopRepository.findById(orderCancelled.getShopId());
        System.out.println("\n\n#################### ShopOptional.get() : " +ShopOptional.get() + "\n\n");

        Shop shop = ShopOptional.get();
        shop.setEqpStatus("OrderAvailable");

        shopRepository.save(shop);
            
    }
}

```

![image](https://user-images.githubusercontent.com/33124483/124458112-92705d80-ddc7-11eb-8080-9d5e9701d550.png)

그 외 메시지 서비스는 주문/배송과 완전히 분리되어있으며, 이벤트 수신에 따라 처리되기 때문에, 메시지 서비스가 유지보수로 인해 잠시 내려간 상태 라도 예약을 받는데 문제가 없다.


#### 메시지 서비스 (message) 를 잠시 내려놓음 (ctrl+c)
![image](https://user-images.githubusercontent.com/33124483/124458486-fc890280-ddc7-11eb-8deb-3bffe49098c9.png)


#### 주문 요청
![image](https://user-images.githubusercontent.com/33124483/124458549-11fe2c80-ddc8-11eb-8ad5-d44d9d98fa8f.png)


#### 주문 상태 확인
![image](https://user-images.githubusercontent.com/33124483/124458597-24786600-ddc8-11eb-8022-6e81b9567bbe.png)


# 운영

## CI/CD 설정

각 구현체들은 각자의 source repository 에 구성되었고, 사용한 CI/CD는 buildspec.yml을 이용한 AWS codebuild를 사용하였습니다.

- CodeBuild 프로젝트를 생성하고 AWS_ACCOUNT_ID, KUBE_URL, KUBE_TOKEN 환경 변수 세팅을 한다
```
SA 생성
kubectl apply -f eks-admin-service-account.yml
```
![codebuild(sa)](https://user-images.githubusercontent.com/38099203/119293259-ff52ec80-bc8c-11eb-8671-b9a226811762.PNG)
```
Role 생성
kubectl apply -f eks-admin-cluster-role-binding.yml
```
![codebuild(role)](https://user-images.githubusercontent.com/38099203/119293300-1abdf780-bc8d-11eb-9b07-ad173237efb1.PNG)
```
Token 확인
kubectl -n kube-system get secret
kubectl -n kube-system describe secret eks-admin-token-rjpmq
```
![image](https://user-images.githubusercontent.com/33124483/124719845-88bf3500-df42-11eb-97c8-42b61c5c443f.png)
```
buildspec.yml 파일 
마이크로 서비스 shop의 yml 파일 이용하도록 세팅
```
![image](https://user-images.githubusercontent.com/33124483/124713629-c2d90880-df3b-11eb-8e6f-b2103d7dd073.png)

- codebuild 실행
```
codebuild 프로젝트 및 빌드 이력
```
![image](https://user-images.githubusercontent.com/33124483/124713913-18151a00-df3c-11eb-9d8f-d3426207fe21.png)
![image](https://user-images.githubusercontent.com/33124483/124714036-3bd86000-df3c-11eb-9873-a30ac8e8d52d.png)


- codebuild 빌드 내역 (shop 서비스 세부)

![image](https://user-images.githubusercontent.com/33124483/124714132-57436b00-df3c-11eb-9827-20574f895a28.png)

- codebuild 빌드 내역 (전체 이력 조회)

![image](https://user-images.githubusercontent.com/33124483/124714171-67f3e100-df3c-11eb-8e16-841b76fb3484.png)
![image](https://user-images.githubusercontent.com/33124483/124719526-34b45080-df42-11eb-9a0d-27203db1c721.png)

## 서킷 브레이킹
- Spring Spring FeignClient + Hystrix 옵션을 사용하여 테스팅 진행 주문(order) → 배송(delivery) 시 연결을 REST API로 Response/Request로 구현되거 있으며, 과도한 주문으로 배송에 문제가 될 때 서킷브레이커로 장애격리

[order > src > main > resources > application.yml]
```
feign:
  hystrix:
    enabled: true
    
hystrix:
  command:
    default:
      execution.isolation.thread.timeoutInMilliseconds: 610
```

[delivery > src > main > java > twdproject> delivery.java] - 딜레이 발생시킴
```
        try{
            Thread.currentThread().sleep((long) (400 + Math.random() * 220));
        } catch (InterruptedException e){
            e.printStackTrace();
        }
```

- 동시사용자 1로 부하 생성 시 모두 정상
```
siege -c1 -t1S -r5 -v --content-type "application/json" 'http://order:8080/orders POST {"orderId":"1"}'
```
![image](https://user-images.githubusercontent.com/33124483/124907489-3144c600-e023-11eb-8370-edd1a799df70.png)


- 동시사용자 200로 부하 생성 시 에러 256개 발생
```
siege -c1 -t1S -r5 -v --content-type "application/json" 'http://order:8080/orders POST {"orderId":"1"}'
```
![image](https://user-images.githubusercontent.com/33124483/124912998-93082e80-e029-11eb-9cbc-7dc8ed2c8a9d.png)
![image](https://user-images.githubusercontent.com/33124483/124913126-b7fca180-e029-11eb-9351-7042192de6fb.png)

- 운영시스템은 죽지 않고 지속적으로 CB 에 의하여 적절히 회로가 열림과 닫힘이 벌어지면서 자원을 보호하고 있음을 보여줌.
  virtualhost 설정과 동적 Scale out (replica의 자동적 추가,HPA) 을 통하여 시스템을 확장 해주는 후속처리가 필요.


### 오토스케일 아웃
앞서 CB 는 시스템을 안정되게 운영할 수 있게 해줬지만 사용자의 요청을 100% 받아들여주지 못했기 때문에 이에 대한 보완책으로 자동화된 확장 기능을 적용하고자 한다. 

- order deployment.yml 파일에 resources 설정을 추가한다
- 
![image](https://user-images.githubusercontent.com/33124483/124914229-1413f580-e02b-11eb-8c4f-8b044e092a66.png)

- 명령어로 HPA 설정
```
# CPU 사용량이 20%를 넘으면 pod를 3개까지 늘림
kubectl autoscale deployment order --cpu-percent=20 --min=1 --max=3
```
![image](https://user-images.githubusercontent.com/33124483/124947531-df179b00-e04a-11eb-90af-d24d6bcba564.png)

- 부하를 동시사용자 200명, 50초 동안 걸어준다.
```
siege -c200 -t50S -r5 -v --content-type "application/json" 'http://order:8080/orders POST {"orderId":"1","shopId":"1","eqpNm":"Iphone13","eqpStatus":"Order","eqpSernum":"1"}'
```
- 오토스케일이 어떻게 되고 있는지 모니터링을 걸어둔다
```
kubectl get deploy order -w
```

![image](https://user-images.githubusercontent.com/33124483/124946667-2cdfd380-e04a-11eb-8589-e1c6d59686b0.png)
![image](https://user-images.githubusercontent.com/33124483/124946514-0de14180-e04a-11eb-93bd-379d2c27fdde.png)



## 무정지 재배포

1. Readiness를 주석처리하여 사전 테스트

![image](https://user-images.githubusercontent.com/33124483/124939008-bf30a900-e043-11eb-8bb6-ad583bdde61e.png)

2. siege로 접속하여 부하를 넣고, 신규버전으로 update 진행 시 서비스 중단 확인

```
kubectl set image deploy order order=879772956301.dkr.ecr.eu-central-1.amazonaws.com/final-order:v6
```
![image](https://user-images.githubusercontent.com/33124483/124939334-fa32dc80-e043-11eb-8f80-2195abe55817.png)

3. Readiness를 주석해제

![image](https://user-images.githubusercontent.com/33124483/124939628-3b2af100-e044-11eb-9e9d-4eb3a826a8c7.png)

4. siege로 접속하여 부하를 넣고, 신규버전으로 update 진행 시 서비스 무중단 확인

```
kubectl set image deploy order order=879772956301.dkr.ecr.eu-central-1.amazonaws.com/final-order:v6
```
![image](https://user-images.githubusercontent.com/33124483/124939940-7fb68c80-e044-11eb-9625-52726894bf91.png)
![image](https://user-images.githubusercontent.com/33124483/124942260-6ca4bc00-e046-11eb-933b-dd37d3ea191c.png)


## Config Map/ Persistence Volume
- Database 연결 및 Secret 설정은 재고인 cellphone 서비스에 설정함
[shop > src > main > resource > application.yml]

![image](https://user-images.githubusercontent.com/33124483/124943877-d4a7d200-e047-11eb-979e-c65f1ddac25a.png)

- Password는 Secret에서 비밀번호를 가져오도록 적용
[shop > kubernetes > deployment.yml]

![image](https://user-images.githubusercontent.com/33124483/124944365-36683c00-e048-11eb-864e-ebe9021c344e.png)

- secret객체를 설정하기 위한 yaml파일을 만들어서 설정
- 아래 이미지에서 보이듯이 admin을 BASE64 형으로 인코딩한 값으로 생성함
```
kubectl create -f sql-secret.yaml
# secret 생성 확인
kubectl get secrets
```

![image](https://user-images.githubusercontent.com/33124483/124958367-0c694680-e055-11eb-80a9-2cdaef379d10.png)

- MySQL을 위한 Pod 생성을 위해 yaml 파일 생성 후 기동 및 Pod 활성화 확인

![image](https://user-images.githubusercontent.com/33124483/124967472-6f5fdb00-e05f-11eb-8067-25b6b196000c.png)

- MySQL Pod로 진입하여 작동 확인

![image](https://user-images.githubusercontent.com/33124483/124967806-ce255480-e05f-11eb-9fb0-432bc3dfbc33.png)

- k8s DNS 체계에서 접근가능하게 하기 위해 ClusterIP로 서비스 생성

![image](https://user-images.githubusercontent.com/33124483/124968020-18a6d100-e060-11eb-89c5-ec84954ad700.png)

- Pod가 오류로 종료되어버리면 데이터의 유실이 발생함. 이를 해결하기 위해 PersistenceVolume으로 된 파일시스템에 연결함
- sql-secret.yaml 수정
- 코드 내 aws-ebs라는 PVC가 존재하지 않아서 Pod의 상태를 보면 Pending 상태로 대기중임

![image](https://user-images.githubusercontent.com/33124483/124971591-5a397b00-e064-11eb-92fc-45f2c874305c.png)

- 이를 해결하기 위해서 yaml 파일을 만들어서 PVC 생성함 이후 mysql Pod의 상태가 변함을 확인
![image](https://user-images.githubusercontent.com/33124483/124971811-9f5dad00-e064-11eb-8f15-3411f26904d8.png)
![image](https://user-images.githubusercontent.com/33124483/124971947-c2885c80-e064-11eb-88f9-99624f440ca8.png)


## Self-healing (Liveness Probe)
1. yml 파일에 Liveness 적용

![image](https://user-images.githubusercontent.com/33124483/124948362-93192600-e04b-11eb-8141-a13fd0037821.png)

2. yml파일 apply적용 및 deploy에 적용확인
```
# order > kubernetes 이동하여 Liveness yml파일에 있는 것 확인한 뒤 apply 진행
kubectl apply -f deployment.yml
```

![image](https://user-images.githubusercontent.com/33124483/124949065-35d1a480-e04c-11eb-8709-c1479aae6e9e.png)

3. Pod가 에러로 종료가 될 때까지 siege로 계속해서 부하 발생
```
kubectl exec -it siege -- /bin/bash
siege -c200 -t120S -r5 -v --content-type "application/json" 'http://order:8080/orders POST {"orderId":"1","shopId":"1","eqpNm":"Iphone13","eqpStatus":"Order","eqpSernum":"1"}'
```
4. Pod가 재기동이 발생을 하고, 정상화 확인

![image](https://user-images.githubusercontent.com/33124483/124950386-53533e00-e04d-11eb-894e-f74b517ec155.png)


5. kubectl get po -w로 모니터링하면서 RESTART로 재기동이 발생하는 모습 확인
![image](https://user-images.githubusercontent.com/33124483/124950038-096a5800-e04d-11eb-951c-12e07136dee8.png)


