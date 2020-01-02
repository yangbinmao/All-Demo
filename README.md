# SpringCloud-demo
SpringCloud-demo
  zuul网关
  eureka注册中心
  user-server服务的提供者（访问数据库）
  consumer-server服务的消费者。

流程：请求只能通过zuul进入，zuul进行判断传入请求是否带accese.token参数，然后根据负载均衡策略，在eureka注册中心找对应负载均衡过的服务，请求转发到这个服务；
ps:转发的服务可以到consumer-server，但是consumer-server可以调用User-server,也要通过eureka注册中心。
