

race condition 해결 방법
- 1개의 스레드만 데이터에 접근하게끔 합니다.


## synchronized 키워드
  - 실패함
    - 이유 : 스프링의 트랜잭션 어노테이션 동작 방식 때문
      - 트랜잭션 어노테이션 달면 비즈니스 로직 앞뒤로 트랜잭션 로직을 심은 일종의 래핑 클래스를 새로 만들어서 실행함
      
      ```java
        public void decrease(Long id, Long quantity) {
            startTransaction();
            bizLogic();
            endTransaction();
        }
      ```
      - 트랜잭션 정상 종료하고, 디비에 업데이트 할 때 문제가 발생할 수 있음
      - decrease 메소드가 완료되었고 실제 데이터베이스에 업데이트 하기 전에 다른 스레드가 decrease 메소드를 호출할 수 있음
      - 다른 스레드는 갱신되기 전의 값을 가져가서 이전과 동일한 문제가 발생하는 겁니다.

      - ex> 예를 들어 decrease(id, quantity) 를 10시에 종료되었고, 10시 5분에 endTransaction() 업데이트를 수행합니다. 그러면 10시부터 10시 5분 까지 다른 스레드가 decrease 메소드를 호출할 수 있게 됩니다. 그러면 다른 스레드는 갱신되기 이전의 값을 가져가서 이전과 동일한 문제가 발생하는 것임 
      - 그러면 이와 관련해서 이 문제를 모면려면 transaction 어노테이션을 주석처리하면 됩니다.
      - 하지만 이는 좋은 접근이 아닙니다.
      - 자바의 synchronized 는 하나의 서버 안에서만 보장됨 (n대 서버에서는 보장이 안되는거지)
      - 서버 1, 서버 2로 바꿔서 생각을 해보면 됨
      - synchronized는 하나의 프로세스 안에서만 보장이 됨, 여러 스레드에서 동시에 데이터에 접근할 수 있게 되면서 race condition 이 발생하게 됨


## Mysql 을 활용한 다양한 방법 

[1] Pessimistic Lock  
- 실제로 데이터에 Lock 을 걸어서 정합성을 맞추는 방법입니다.exclusive lock 을 걸게되면 다른 트랜잭션에서는 lock 이 해제되기전에 데이터를 가져갈 수 없게 됩ㄴ디ㅏ.
  데드락이 걸릴 수 있기때문에 주의하여 사용하여야 합니다.

[2] Optimistic Lock
- 실제로 Lock 을 이용하지 않고 버전을 이용함으로써 정합성을 맞추는 방법입니다. 먼저 데이터를 읽은 후에 upate 를 수행할 때 현재 내가 읽은 버전이 맞는지 확인하여 업데이트합니다. 내가 읽은 버전에서 수정사항이 생겼을 경우에는 application 에서 다시 읽은후에 작업을 수행해야 합니다.

[3] Named Lock 
- 이름을 가진 metadata locking 입니다. 이름을 가진 lock 을 획득한 후 해제할때까지 다른 세션은 이 lock 을 획득할 수있도록 합니다. 주의할 점으로는 transaction 이 종료될 때 lock 이 자동으로 해제되지 않습니다. 별도의 명령어로 해제를 수행해주거나 선점시간이 끝나야 해제됩니다.


pessimistic Lock 의 장점
- 충돌이 빈번하다면 낙관 락보다는 성능이 좋을 수 있다. 그리고 락을 통해 데이터를 제어하기 때문에 락이 어느정도 보장됩니다. 
- 하지만 별도의 락을 잡기 때문에 성능감소가 있을 수 있음

optimistic lock 
- 실제로 락을 이용하지 않고 버전을 이용해서 정합성을 맞추는 것 ()

## redis

- Lettuce
  - setnx 명령어를 활용하여 분산락 구현  (setnx -> set if not exist 줄임말, 키 없을때만 set 하는 명령어)
  - spin lock 방식  (retry 로직 개발자가 작성해야 함 -> 락을 획득하려는 스레드가 락을 사용할 수 있는지 반복하면서 락 획득을 시도하는 방식)
- Redisson
  - pub-sub 기반으로 Lock 구현 제공 (채널 하나 만들고 락을 점유중인 스레드가 락을 획득하려고 대기중인 스레드에게 해제를 알려주면 안내를 받은 스레드가 락 획득 시도하는 방식)
  - (스레드-1) 나 끝났어 ==> 채널 (락 획득 시도해 하고 스레드 2에 알려줌) -> (스레드 2)


```shell
setnx 1 lock
```

```shell
MISCONF Redis is configured to save RDB snapshots, but it is currently not able to persist on disk. Commands that may modify the data set are disabled, because this instance is configured to report errors during writes if RDB snapshotting fails (stop-writes-on-bgsave-error option). Please check the Redis logs for details about the RDB error.
```

