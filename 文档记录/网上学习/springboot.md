sprintboot代码热更新

https://www.jb51.net/article/185288.htm



```java
if (!redisService.set(redisK,String.valueOf(goodsId), "NX", "EX", 120)) {
                result.withError(MIAOSHA_QUEUE_ING.getCode(), MIAOSHA_QUEUE_ING.getMessage());
                return result;
            }
```



- EX second ：设置键的过期时间为 second 秒。 SET key value EX second 效果等同于 SETEX key second value 。
- PX millisecond ：设置键的过期时间为 millisecond 毫秒。 SET key value PX millisecond 效果等同于 PSETEX key millisecond value 。
- NX ：只在键不存在时，才对键进行设置操作。 SET key value NX 效果等同于 SETNX key value 。
- XX ：只在键已经存在时，才对键进行设置操作。



rabbitMq

主题交换器

https://www.cnblogs.com/wuhenzhidu/p/10802749.html



springboot事务手动回滚

try catch 后手动回滚
