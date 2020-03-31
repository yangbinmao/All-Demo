# 好用的pom配置

```xml
        <!--lombok依赖包-->
        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
            <optional>true</optional>
        </dependency>
         <!--这是一个json的jar包-->
        <dependency>
            <groupId>com.alibaba</groupId>
            <artifactId>fastjson</artifactId>
            <version>1.2.6</version>
        </dependency>
 		<!--这是一个mybatis的jar包,有点像mybatisplus的作用-->
        <dependency>
            <groupId>tk.mybatis</groupId>
            <artifactId>mapper-spring-boot-starter</artifactId>
            <version>2.0.3</version>
        </dependency>
     <!--lang3 StringUntil工具的包 StringUntil 验证工具箱-->
        <dependency>
            <groupId>org.apache.commons</groupId>
            <artifactId>commons-lang3</artifactId>
             <version>3.9</version>
        </dependency>
 <!--spring整合的loggin日志集合-->
   		<dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-loggin</artifactId>
        </dependency>
```

# 一些坑

1.使用getOne报错

​	答：在实体类上加上@JsonIgnoreProperties(value = {"hibernateLazyInitializer", "handler"})，或者使用findXXX相关的办法。现在一般都是用findbyid（）

2.@Component和@Configuration区别

​	答：共同点：都是注入ioc容器的一个配置注解。

​			不同点：@Configuration是配置进去的单例模式。每次都是同一个对象。@Component是配置信息，表示每次调用都是会new一个新的对象。Component注解的类里面要调用单一对象，就是用@Autowired来注入。

# SpringBoot

## java配置

### Spring属性注入

java配置主要靠java和一些注解，比较常用的注解有：

@Configuration:声明一个类作为配置类，代替xml文件；

@Bean:申明在方法上，将方法的返回值加入Bean容器,代替\<bean>标签。

@PropertySource：指定外部属性文件。

```java
//方式一
//首先创建一个jdbc.properties配置文件。配好属性
//然后在下面引用
@Configuration		//声明一个类作为配置类，代替xml文件；
@PropertySource("classpath:jdbc.properties")	//指定外部属性文件。
public class JdbcConfig{
    @Value("${jdbc.url}")	//引用的值就是外部文件里面的值。
    String url;
    @Value("${jdbc.driverClassName}")
    String deiverClassName;
    @Value("${jdbc.username}")
    String username;
    @Value("${jdbc.password}")
    String password;
    
    @Bean	//申明在方法上，将方法的返回值加入Bean容器,代替\<bean>标签。他这里是要配置的DataSoutce这个数据库，所以设置好对应值后，就把这个值返回。就配置好了这个bean。
    public  DataSoutce dataSource(){
        DruidDataSoutce dataSource=new DruidDataSource()
        dataSource.setDriverClassName(driverClassName)
        dataSource.setUrl(url)
        dataSource.setUsername(username)
        dataSource.setPassword(password)
        return dataSource;
    }
}



```

### SpringBoot属性注入

#### 方式一：适用反复使用，多人调用

```java
//方式二		SpringBoot配置属性类	这种方式擅长于很多人使用。
//spingBoot里，如果有application.properties配置类（把配置信息放里面）,下边java.calss就可以使用注解@ConfigurationProperties来直接引用application.properties.xml内资源
@ConfigurationProperties(prefix="jdbc") //告诉spring 在配置文件里，相关属性的前缀是"jdbc",表示这是一个属性配置类。后边使用@EnableConfigurationProperties(属性配置类.class)来使用这个配置。
@Data		//lombok的注解。得到get、set等方法
public class JdbcProperties{
    String url;
    String deiverClassName;
    String username;
    String password;
}

//然后调用该配置文件的使用就可以这样写
@Configuration
@EnableConfigurationProperties(JdbcProperties.class)	//表示使用这个属性配置。
public class JdbcConfig{    
    @Bean	//申明在方法上，将方法的返回值加入Bean容器,代替\<bean>标签。他这里是要配置的DataSoutce这个数据库，所以设置好对应值后，就把这个值返回。就配置好了这个bean。
    public  DataSoutce dataSource(JdbcProperties prop){
        DruidDataSoutce dataSource=new DruidDataSource()
        dataSource.setDriverClassName(prop.getDriverClassName())
        dataSource.setUrl(prop.GetUrl())
        dataSource.setUsername(prop.getUsername())
        dataSource.setPassword(prop.getPassword())
        return dataSource;
    }
}


```

#### 方式二：使用一次，一个人使用

```java
//方式三 适用于一个人用。
public class JdbcConfig{    
    @Bean
   	@ConfigurationProperties(prefix="jdbc")
    public  DataSoutce dataSource(){
      return new DruidDataSource();     
    }
    //流程：
    //1.spring扫描的时候。发现@Bean注解，然后进入。返回DruidDataSource对象，
    //2.然后扫描到@ConfigurationProperties注解。到application.properties.xml类去找有没有缀是"jdbc",的配置。然后匹配返回的返回DruidDataSource对象有没有这些属性，有的话，通过set方法，注入.
    //3.然后返回返回DruidDataSource对象。
}
```

## 自动配置原理

@SpringBootApplication  spring启动的配资注解主要是有三个作用。

​	1.申明是一个配置类。就是@Configuration的作用

​	2.开启自动配置。@EnableAutoConfiguration的作用

​	3.扫描包文件。@ComponentScan的作用。如果不配置，就默认扫描使用这个注解的包。

## 拦截器

### 创建拦截器

建立自己的拦截器类。

首先需要实现HandlerInterceptor这个类，然后重写对应拦截位置的方法。

MyInterceptor.java

```java
public class MyInterceptor implements HandlerInterceptor {
    private static final Logger logger= LoggerFactory.getLogger(MyInterceptor.class);

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        logger.debug("preHandle method is running ");
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        logger.debug("postHandle method is running ");
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        logger.debug("afterCompletion method is running ");
    }
}
```

### 在spring中配置拦截器

创一个类，实现WebMvcConfigurer这个接口,并且在类上注解@Configuration这个注解，表示这个是一个配置类。重写addInterceptors（这是Spingmvc的拦截器）这个方法。

MVCconfig.java

```java
@Configuration
public class MCVconfg implements WebMvcConfigurer {
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        //使用registry这个类，添加一个拦截器配置。
        // new MyInterceptor() 这个就是一个拦截器的配置，前面自己写的
        // addPathPatterns 添加匹配规则
        //  "/**" 表示全匹配
        registry.addInterceptor(new MyInterceptor()).addPathPatterns("/**");
    }
}
```

# SpringCloud

## 	

​	

## RestTemplate

微服务的场景模拟

​	**RestTemplate:这个类的作用就是通过http请求跨域获取资源。**

**作用对象:**服务的消费者，从注册中心获取服务地址，然后调用。

常用http客户端工具：

- HTTPClient
- OKhttp
- HTTPURLConnection

Spring提供了一个RestTemplate模版工具类，对基于Http的客户端进行了封装，并实现；额对json的序列化，和反序列化。支持上面的三种工具

- HTTPClient	
- OKhttp
- HTTPURLConnection (默认的)

首先在启动类下添加一个bean；

```java
    @Bean
    public RestTemplate restTemplate(){
        return new RestTemplate();//默认使用的就是HTTPURLConnection ,如果要用其他的，就要去引用其他的依赖。
    }
```

写一个测试

```java
public class HttpTempleateTest {
    @Autowired
    private RestTemplate restTemplate;//先引用模版工具类
    @Test
    public  void HttpTest(){
        People people = restTemplate.getForObject("http://localhost:8086/getpeople", People.class);
        System.out.println("people = " + people);
    }
}
```

**结合负载均衡后**

```java
//结合负载均衡，需要在启动类restTemplate()方法加@Bean（表示注入到ioc容器） 和 @LoadBalanced （表示是负载均衡）；

//然后直接在controller层如下写就可以了。
@GetMapping("/consumer/findpeople3")
    public People findpeople3(){
       String url="http://user-server/findpeople";
       //user-server 要访问的服务的名字，在你想访问的服务的yml里面那个name，
        //findpeople 方法的映射名字。
        return restTemplate.getForObject(url, People.class);
    }
```



## Eureka注册中心

- Eureka：就是服务注册中心（可以是一个集群），对外暴露自己的地址。
- 提供者：启动后向Eureka注册自己信息（地址，提供什么服务）
- 消费者：向Eureka订阅服务，Eureka会将对应的所有提供者地址列表发送给消费者，并且定期更新。
- 心跳（续约）：提供者定期通过Http方式向Eureka刷新自己的状态

**使用对象：**Eureka注册中心；自己一个独立的调度中心。心跳机制获取全部注册到注册中心的服务，并且进行管理。

建立注册中心，首先需要注册中心的这个项目的pom引入Eureka的依赖；

```xml
 		<dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-netflix-eureka-server</artifactId>
            <version>2.0.4.RELEASE</version>
        </dependency>
```

建立启动类,引入@EnableEurekaServer注解；

```java
@EnableEurekaServer
@SpringBootApplication
public class EurekaServer {
    public static void main(String[] args) {
        SpringApplication.run(EurekaServer.class);
    }
}
```

注意，避免端口如果有冲突，可以改一下端口，在application里面改一下端口。

```yml
server:
  port: 10086
```

设置Eureka三部曲：

- 引依赖：在pom引用服务器或者客户端的相关依赖

  ```xml
  <!-- 在eureka服务端的pom文件里配置-->  
  <!--        eureka服务端端-->
          <dependency>
              <groupId>org.springframework.cloud</groupId>
              <artifactId>spring-cloud-starter-netflix-eureka-server</artifactId>
              <version>2.0.4.RELEASE</version>
          </dependency>
  
  
  <!-- 在eureka注册端的pom文件里配置-->  
  <!--eureka注册端-->
          <dependency>
              <groupId>org.springframework.cloud</groupId>
              <artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
              <version>2.0.4.RELEASE</version>
          </dependency>
  ```

- 设置注解

  ```java
  //客户端所在模块的启动器的类上输入注解,表示这是客户服务端
  @EnableEurekaServer
  //注册端所在模块的启动器的类上输入注解,表示这是注册端
  @EnableDiscoveryClient  //表示是注册端，Eureka的注解是@EnableEurekaClient,但是@EnableDiscoveryClient时spring的注册客户端。既可以兼容Eureka,也可以支持其他的注册中心。
  
  ```

- 配置名字和地址

  ```yml
  #==================客户端===================
  #设置服务的端口地址
  server:
    port: 10086
  #设置服务的默认名称
  spring:
    application:
      name: eureak-server
  #因为eureka是集群布置，现在我们本地只有一个，所以他会报一个找不到其他的端口的错误。
  #它既是服务端，也是客户端，避免报错，所以把它自己注册给自己
  eureka:
    client:
      service-url:
        defaultZone: http://127.0.0.1:10086/eureka  #,http://127.0.0.1:10087/eureka  如果有多个注册中心就把地址加进去
      register-with-eureka: false #设置当前服务是否要注册到eureka
  # instance:
   # prefer-ip-address: true   #希望使用ip地址
    #ip-address: 127.0.0.1     #设置ip地址为127.0.0.1
    server:
      eviction-interval-timer-in-ms: 60000 #eureka检测到服务挂了后（默认是心跳后90s没有在心跳，就会过默认60s后，在注册中心删除所有挂了的服务）
      
      
  #==================服务端===================
  
  server:
    port: 8086
  spring:
    #通用的数据源配置
    datasource:
      driverClassName: com.mysql.jdbc.Driver
      url: jdbc:mysql://localhost:3306/test?useSSL=false&useUnicode=true&characterEncoding=utf8
      username: root
      password: 123456
    jpa:
      #这个参数是在建表的时候，将默认的存储引擎切换为 InnoDB 用的
      #database-platform: org.hibernate.dialect.MySQL5InnoDBDialect
      #配置在日志中打印出执行的 SQL 语句信息。
      show-sql: true
      hibernate:
        #配置指明在程序每次只会更新数据库表里的信息
        ddl-auto: update
   		#  1：ddl-auto=create-drop 表示当JPA应用的时候自动创建表，在解应用的时候删除相应的表，这个在做测试的时候比较有用，但在开发过程中不这么用
  		#  2：ddl-auto=create 这个在每次应用启动的时候都会创建数据库表（会删除以前数据库里的数据。
  		#  3：ddl-auto=update 这个属性的作用是:每次只会更新数据库表里的信息
    application:
      name: user-server	#设置这个服务的名字
      
  
  
  eureka:
    client:
      service-url:
        defaultZone: http://127.0.0.1:10086/eureka,http://127.0.0.1:10087/eureka
     #fetch-registry: true #要不要从eureka服务中心拉取所以列表，默认为true
     #registry-fetch-interval-seconds: 30 #每30s去服务中心拉取一次列表
    instance:
      lease-renewal-interval-in-seconds: 30 #设置eureka心跳评率，默认30s
      lease-expiration-duration-in-seconds: 90 #如果过了90s都没有心跳表示服务就挂了，默认90s
  
  
  logging:
    level:
      com.ybmspringbootjpa.jpa: debug
  ```


## 负载均衡Ribbon

Ribbon是Netflix发布的负载均衡器，他有助于控制HTTP和TCP客户端的行为。为Ribbon配置服务提供者地址列表后，Ribbon就可以基于某种负载均衡算法，自动地帮助服务消费者去请求。Ribbo默认为我们提供了很多负载均衡算法，例如轮询，随机等，当然，我们也可以为Ribbon实现自定义的负载均衡算法。

**使用对象**：服务的消费者，需要获取服务的一方；服务访问注册中心，但是不知道访问服务中心那个地址，所以在消费者访问注册中心，注册中心均衡条件进行分配。

### 1.引依赖（在pom）

首先引用jar包

```xml
      <!--ribbon负载均衡处理-->
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-netflix-ribbon</artifactId>
            <version>2.0.4.RELEASE</version>
        </dependency>
```

### 2.加注解(在启动类)

在启动类的restTemplate方法--就是跨域调接口这个方法上边增加一个注解 @LoadBalanced

@LoadBalanced:表示是负载均衡，实现是在这个方法前加了一个拦截器，然后在把地址做对应映射详情见下一步。

```java
@EnableDiscoveryClient  //注册中心服务端注解，表示我要把地址给注册中心
@SpringBootApplication()
public class ConsumerApplication {

    public static void main(String[] args) {
        SpringApplication.run(ConsumerApplication.class, args);
    }

    @Bean
    @LoadBalanced //表示是负载均衡，注意：如果是下边使用负载均衡普通法（见下面）的，就不能用这个注解。这个注解会先拦截，返回的连接，如果负载均衡普通使用了，就会找不到实际地址。
    public RestTemplate restTemplate(){
        return new RestTemplate();//默认使用的就是HTTPURLConnection ,如果要用其他的，就要去引用其他的依赖。
    }
}
```

### 3.改路径（在控制器）	

最后，直接在Controller(控制器)方法上使用这个restTemplate的方法就可以了

```java
//步骤样式（具体实现）
//没有负载均衡
    @Autowired
    private RestTemplate restTemplate; //跨域获取资源

    @Autowired
    private DiscoveryClient discoveryClient;//发现客户端。

@GetMapping("/consumer/findpeople")
    public People findpeople(){
        //根据服务名称来获取服务实例
        List<ServiceInstance> instances = discoveryClient.getInstances("user-server");
        //从实例中取出ip和端口
        ServiceInstance instance = instances.get(0);

            log.debug("http://"+instance.getHost()+":"+instance.getPort()+"/findpeople");
        return restTemplate.getForObject("http://"+instance.getHost()+":"+instance.getPort()+"/findpeople", People.class);
    }

//===================实现负载均衡（普通法）=================

    @Autowired
    private RestTemplate restTemplate;  

	@Autowired
    private RibbonLoadBalancerClient client;


    @GetMapping("/consumer/findpeople2")
    public People findpeople2(){
        //根据服务名称来获取服务实例
        //使用ribbon  实现负载均衡
        //client.choose自动获取一个url服务地址
        ServiceInstance instance = client.choose("user-server");
        log.debug("http://"+instance.getHost()+":"+instance.getPort()+"/findpeople");
        return restTemplate.getForObject("http://"+instance.getHost()+":"+instance.getPort()+"/findpeople", People.class);
    }




//====================负载均衡常用用法(高级)====================
//直接用下边就可以了，也只需要引用RestTemplate


//首先在启动类上给注解@LoadBalanced,这个作用是给在启动类的RestTemplate方法给一个负载均衡拦截器。这样。在下边方法调用的时候。他就可以把地址变成对应的映射对应。


@Autowired
    private RestTemplate restTemplate;

@GetMapping("/consumer/findpeople3")
    public People findpeople3(){
       String url="http://user-server/findpeople";
        //user-server 要访问的服务的名字，在yml里面那个name，
        //findpeople 方法的映射名字。
        return restTemplate.getForObject(url, People.class);
    }



```

4.轮询策略

```yaml

#修改ribbon的负载均衡策略
user-server:    #这个是服务的名称（就是你想让那个服务负载均衡策略变化）
  ribbon:		#固定写法 ribbon ,下边方法名：方法策略
    NFLoadBalancerRuleClassName: com.netflix.loadbalancer.RandomRule # 当前是要修改为随机，默认是轮询
```

## Hystrix

Hystrix,英文的意思是豪猪，全身是刺，看起来不好惹，是一种保护机制

### 雪崩问题

​	比如一个汽车生产线，生产不同的零件，最后组装成车，但是一个零件突然供应不上，那么车就无法生产，就是雪崩。

Hystrix解决雪崩问题的手段有两个：

- 线程隔离
- 服务熔断

线程隔离，服务降级

​	线程隔离就是给所有的服务一定的线程数量，类似线程池的效果。

​	服务降级：优先保证核心服务，而非核心服务不可用或弱可用。	

用户的请求故障时，不会被阻塞，更不会无休止的等待或者看到系统崩溃，至少可以看到一个执行结果（例如返回友好的提示。）

服务降级虽然会导致请求失败，但是不会导致阻塞，而且最多会影响这个依赖服务对应的线程池中的资源。对其他服务没有响应。

触发Hystrix	服务降级的情况：

- 线程池已满
- 请求超时



**使用对象：** **服务降级是服务的消费者降级**。就是你要调用其他的服务，但是异常了，不出来就会一直等，所以处理后，异常，或者等待的时间到了，就直接可以返回异常，

### 1.引依赖

```xml
 <!--Hystrix 线程隔离，服务熔断-->
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-netflix-hystrix</artifactId>
            <version>2.0.4.RELEASE</version>
        </dependency>
```

### 2.加注解(在启动类)

```java
//@EnableCircuitBreaker //服务的熔断
//  //@EnableHystrix   //这个本身是hystrix的注解，但是要用上边的熔断，包含了这个注解，所以就不用这个了。
//@EnableDiscoveryClient  //注册中心服务端注解，表示我要把地址给注册中心
//@SpringBootApplication


@SpringCloudApplication   //替代了上边三个注解。他里面把上边三个都写完了。
public class ConsumerApplication {

    public static void main(String[] args) {
        SpringApplication.run(ConsumerApplication.class, args);
    }

    @Bean
    @LoadBalanced //表示是负载均衡
    public RestTemplate restTemplate(){
        return new RestTemplate();//默认使用的就是HTTPURLConnection ,如果要用其他的，就要去引用其他的依赖。
    }
}
```

### 3.写配置

```yaml
#这个是设置hystrix全局的默认延迟时间，可以直接在方法上写，也可以在配置写。都可以。
hystrix: #设置hystrix
  command: #设置它的指令
    default:
      execution:
        isolation:
          thread:
            timeoutInMilliseconds:3000   #设置全局的hystrix等待响应的延时时间
```

### 4.编写降级逻辑（controller层）

```java
//方法一
// @HystrixCommand 容断降级的注解。写在方法上。
    // fallbackMethod= "findpeople4forback" 如果发生降级，需要访问的方法，findpeople4forback是下边自己定义的方法。
    //注意：本身方法，和降级后的方法返回类型一定要是一样的。
    @GetMapping("/consumer/findpeople4")
    @HystrixCommand(fallbackMethod = "findpeople4forback",commandProperties = {            @HystrixProperty(name="execution.isolation.thread.timeoutInMilliseconds",value = "2000")//设置等待时间，默认是1000毫秒
    })  //失败容错,降级处理的注解
    public String findpeople4(){
        String url="http://user-server/findpeople";
        return restTemplate.getForObject(url, String.class);
        //下边三句话，是对restTemplate理解话术，和本章无关。
        //  restTemplate.getForObject(url, String.class)返回类型是根据String.class这个位置属性决定的。
        //  其实默认是个json格式，json本就是String格式，
        //  所以如果写成people.class,等于就是他把json，和People自动转化了一下。
    }


//降级的方法,传入参数类型和返回类型要和主方法一样。
    public String findpeople4forback(){
        return "不好意思，服务器太拥挤了";
    }



//总结：
//1.加@HystrixCommand(fallbackMethod = "findpeople4forback")，设置降级的方法。
//2.写降级的方法，要求传入参数类型和返回类型要和本身方法是一样的。
//3.设置监听的时间


//=================方-------法-------二===============================：

//1.还可以直接在类名上写注解@DefaultProperties(defaultFallback = "findpeople4forback")，表示设置默认降级处理的方法。调用的方法一般是没有传入参数，然后返回值一般是String。
//2.在方法上就只需要写@HystrixCommand 就可以了，表示这个是需要进行降级处理的，如果超时，直接调用@DefaultProperties里面的默认的降级方法。
//注意：当然，如果在这里也还是可以直接写方法一，表示这个降级方法，是主方法独有的。

```

## 服务熔断

​	熔断器，也叫做断路器，其英文单词为：Circuit Breaker

**Hystrix熔断检查机制：**在服务发生异常或响应超时的时候，Hystrix会进行降级处理。同时开启运行熔断机制，熔断机制：指Hystrix时刻监视的整个服务，当服务在一定次数的被调用中，发了降级达到一定次数后，Hystrix就会开启熔断休眠时间窗，这期间所有直接访问这个服务都会直接降级，返回降级对应的方法，休眠时间窗时间过后，Hystrix会开启半熔断机制，就是指请求部分直接访问降级的方法，放部分调用访问主方法，看还会发生降级，如果还是有降级，就再进入熔断机制；然后持续这个循环。直到，当放进来的服务，都不会发生降级，他就会关闭这个熔断机制。

```java
@HystrixCommand(commandProperties = {
//降级时间设置         @HystrixProperty(name="execution.isolation.thread.timeoutInMilliseconds",value = "2000"),//设置降级等待时间，默认是1000毫秒
    
 //熔断相关设置   
            @HystrixProperty(name = "circuitBreaker.requestVolumeThreshold",value = "10") ,   //设置熔断检查次数，默认是20次，达到了次数后，就会进入熔断检查机制
            @HystrixProperty(name = "circuitBreaker.sleepWindowInMilliseconds",value = "10000"),//设置熔断后休眠时间窗持续时间，默认5秒，熔断后会进入这个休眠时间，时间过后就会开始进行尝试熔断检查测试机制
            @HystrixProperty(name="circuitBreaker.errorThresholdPercentage",value = "60"),//设置失败降级的百分比，默认是50%，达到这个百分比后，就会直接熔断。
    })   //降级处理就会调用默认的方法
    public String findpeople4(int id){
        String url="http://user-server/findpeople?id="+id;
        return restTemplate.getForObject(url, String.class);
        //  restTemplate.getForObject(url, String.class)返回类型是根据String.class决定的。
        //  其实默认是个json格式，json本就是String格式，
        //  所以如果写成people.class,等于就是他把json，和People自动转化了一下。
    }
```

## Feign

在前面的学习中，我们使用了Ribbon的负载均衡功能，大大简化了远程调用时的代码；

```java
     String url="http://user-server/findpeople";
   	 return restTemplate.getForObject(url, People.class);
```

以后会遇到大量重复代码，格式基本相同，无非参数不同

作用：feign意思是伪装，可以把Rest的请求进行隐藏，伪装成类似SpringMVC的Controller一样，你不要在拼装url,拼接参数等等，一切都交给Feign去做。

作用对象：RestTpemplgte一样，他只是优化了RestTpemplgte功能，对象就是服务的消费方

1.引入依赖

```
    <!--Feign 优化RestTpemplgte功能-->
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-openfeign</artifactId>
            <version>2.0.4.RELEASE</version>
        </dependency>
```

2.加注解（启动类）

```java
@EnableFeignClients   //加入feign注解，开启feign功能。优化RestTemplate传入参数的注解 
@SpringCloudApplication   
public class ConsumerApplication {

    public static void main(String[] args) {
        SpringApplication.run(ConsumerApplication.class, args);
    }

    @Bean
    @LoadBalanced //表示是负载均衡
    public RestTemplate restTemplate(){
        return new RestTemplate();//默认使用的就是HTTPURLConnection ,如果要用其他的，就要去引用其他的依赖。
    }
}

```

3.建立Client包，建立XXXClient接口

```java
@FeignClient("user-server")  //表示告诉请求要找的是user-server这个服务。
public interface PeopleClient {
    @GetMapping("findpeople?id={id}") //以get的请求方式请求user-server下这个路径。
    People getPeopleById(@PathVariable("id") int id);
}

```

4.在控制器注入XXXClient接口。

```java
//通过Feign自动寻找服务进行调用
    @Autowired
    PeopleClient peopleClient;
    @GetMapping("/findpeople")
    public People getpeoplebyFeign(int id){
        People peopleById = peopleClient.getPeopleById(id);
        return peopleById;
    }
```

**注意**：当用了Feign后RestTemplate 也就没有必要了。就可以在启动类删除RestTemplate Bean，只留一个主函数就可以了。在Frign的依赖包里面，已经引用了Ribbon的依赖包，也就自动的做了负载均衡。所以其实在pom中就不需要引用这些，也引用了Hystrix有熔断降级策略，但是还是要引用Hystrix的包，然后需要一个Feign的引用就可以了。但时，如果自己不写Hystrix依赖的配置方式，用Feign的熔断机制，就需要自己重新按照Feign的方式进行配置一下。配置如下

```yml
# 使用Feign包中ribbon（负载均衡）的配置方式
ribbon:
  ConnectionTimeOut: 500 #连接超时时长，默认是1s--1000  如果5毫秒没有建立连接，就抛出异常
  ReadTimeOut: 2000 #读取超时时长，默认是1s--1000  如果建立连接但是2s没有响应，就抛出异常
#使用Feign包中hystrix（熔断降级）的配置方式
feign: #开启feign的熔断降级功能，默认是关闭的
  hystrix:
    enabled: true

```

实现feign的中的熔断方式：

1. 开启熔断功能

2. 定义一个类，实现XXXclient类，做为fallback处理类

3. ```java
   @Component  //注入到spring容器
   public class PeopleClientFallback implements PeopleClient {
       @Override
       public People getPeopleById(int id) {
           People people = new People();
           people.setName("未知信息");
           return people;
       }
   }
   //注意：这就有了XXXClient中有几个方法，就会有几个fallback处理方法。
   ```

   在熔断的接口中。注解指定熔断降级的类

   ```java
   @FeignClient(value = "user-server",fallback =PeopleClientFallback.class)
   public interface PeopleClient {
       @GetMapping("findpeople?id={id}")
       People getPeopleById(@PathVariable int id);
   }
   ```


## SpringCloud总结

到此为止，也就是springCloud的就出来

### 框架思路：

1. 注册中心：Eureka注册中心
2. 服务的提供方：
3. 服务的调用方：

### 组件及作用：

1. RestTemplate：服务的调用方通过服务提供方提供的url地址根据HTTP请求，调用服务。

2. Hystrix：服务的熔断和降低处理策略。

3. Ribbon：负载均衡的策略方法，实际开发中不是很容易体现。

   ​	**注意**：RestTemplate，Hystrix都是在启动器类上注解的，加上SpringBootApplication 这个注解，最后合成了一个注解@SpringCloudApplication。

4. Feign：使服务的调用抽象化，创建XXXClient接口，通过接口结合springMVC注解，使服务调用方，在调用的服务的时候，就像在调用自己接口一样方便，简洁。

### 小结

​	实际上在SpringCloud开发过程中。要调用其他的服务的接口，只需要更据feign的使用方式，引入依赖，在启动类上加入注解，创建一个XXXClient接口，类上加上@FeignClient("服务名称",fallback =熔断降级类的名称.class) ，然后在这个类写你需要调用的接口方法。最后，就可以在想使用的地方，直接使用了。

## Zuul网关

Zuul是开源的微服务网关，他可以和Eureka,Ribbon,Hystrix配合使用。

Zull的核心是一些列的过滤器，这些过滤器恶意完成以下功能：

- 身份认证与安全：识别每个资源的验证要求，并拒绝与要求不符的请求。
- 审查与监控：在边缘位置追踪有意义的数据和统计结果，从而带来精确的生产视图。
- 动态路由：动态地将请求路由到不同的后端集群。
- 压力测试：逐渐增加指向集群的流量，以了解性能。
- 负载分配：为每一种负载类型分配对应容量，并启用超出限定值得请求。
- 静态响应处理：在边缘位置直接建立部分响应，从而避免其转发到内部集群
- 多区域弹性：跨域AWS Region 进行请求路由，旨在实现ELB(Elastic Load Balancing)使用的多样化，以及让系统的边缘更贴近系统的使用者。

**使用对象：**zuul是一个网关，顾名思义，就是所有网络项目都是必须通过这里进行转发，进行调用，他会自动进行负载均衡。是一个单独的服务。专门提供对外接口的。



### 1.引依赖

首先建立一个微服务，一般网关的微服务名称是gateway

```xml
 		<dependencies>
<!--        zuul 网关-->
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-netflix-zuul</artifactId>
            <version>2.0.4.RELEASE</version>
        </dependency>
        <!--eureka注册端 因为要使用到注册中心，所以同时要引用注册中心。并且将网关的服务注册到注册中心。-->
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
            <version>2.0.4.RELEASE</version>
        </dependency>
    </dependencies>
```



### 2.加注解

在启动类上注解

```java
@SpringBootApplication
@EnableZuulProxy   //zuul注解
public class GatewayApplication {
    public static void main(String[] args) {
        SpringApplication.run(GatewayApplication.class);
    }
}
```

### 3.写配置

```yml
#配置端口号
server:
  port: 10010
#配置服务的名字
spring:
  application:
    name: zuul-gateway
#将服务注册进入Eureka注册中心
eureka:
  client:
    serviceUrl:
      defaultZone: http://127.0.0.1:10086/eureka

#配置网关的相关信息。下面这个就是默认配置，也就是说其实可以不用配置的。
zuul:
  routes:
    user-server: /user-server/**  #默认配置格式就是这样的。
#    user-server: /user/**   这个就是自己设置的样式，这样配置后，默认配置也是一样生效的。
  ignored-services: #忽略这个服务，也就是不能这样去访问这个服务。
    - consumer-server
```

### 4.过滤器

[详解zuul拦截器]: https://www.cnblogs.com/a8457013/p/8352349.html

- 正常流程
  - 请求到达首先经过pre类型过滤器，而后到达routing类型，进行路由，请求就到达真正的服务提供者，执行请求，返回结果，会到达post过滤器，而后返回响应。
- 异常流程：
  - 整个过程中，pre或者routing过滤器出现异常，都会直接进入error过滤器，再error处理完毕后，会将请求交给POST过滤器，最后返回给用户。
  - 如果是error过滤器自己出现异常，最终也会进入POST过滤器，而后返回。
  - 如果是POST过滤器出现异常，会跳转到error过滤器，但是与pre和routing不同的时，请求不会再到达POST过滤了。

配置 ：继承ZuulFilter，实现他自己的4个方法。

1.`filterType()`：返回一个字符串代表过滤器的类型，在zuul中定义了四种不同生命周期的过滤器类型，具体如下：

- `pre`：可以在请求被路由之前调用
- `route`：在路由请求时候被调用
- `post`：在route和error过滤器之后被调用
- `error`：处理请求时发生错误时被调用

   Zuul的主要请求生命周期包括“pre”，“route”和“post”等阶段。对于每个请求，都会运行具有这些类型的所有过滤器。

2.`filterOrder() `：通过int值来定义过滤器的执行顺序,数字越大，优先级越低  

3.`shouldFilter()`：返回一个boolean类型来判断该过滤器是否要执行，所以通过此函数可实现过滤器的开关。在上例中，我们直接返回true，所以该过滤器总是生效

4.`run()`：过滤器的具体逻辑。需要注意，这里我们通过`ctx.setSendZuulResponse(false)`令zuul过滤该请求，不对其进行路由，然后通过`ctx.setResponseStatusCode(401)`设置了其返回的错误码

```java
@Component   //将服务注入进spring容器
@Slf4j
public class LoginFilter extends ZuulFilter {

    @Override
    public String filterType() {
        return FilterConstants.PRE_TYPE;//设置拦截的地方；pre表示一进来就拦截。
    }

    @Override
    public int filterOrder() {
        return FilterConstants.PRE_DECORATION_FILTER_ORDER-1;   //设置拦截位置
    }

    @Override
    public boolean shouldFilter() {
        return true;    //设置要不要过滤，默认为false。
    }

    @Override
    public Object run() throws ZuulException {   //这里就写业务逻辑
        //获取请求参数
            //获取请求上下文
        RequestContext ctx = RequestContext.getCurrentContext();
            //获取request
        HttpServletRequest request = ctx.getRequest();
            //获取请求参数accese.token
        String token = request.getParameter("accese.token");
        //判断是否存在
        if (StringUtils.isBlank(token)){
            //不存在，未登录，则拦截
            log.info("访问没有accese.token，成功拦截");
            ctx.setSendZuulResponse(false);  //这一步就是拦截
            ctx.setResponseStatusCode(HttpStatus.SC_FORBIDDEN);//设置拦截返回码
            return null;
        }
        log.info("访问成功，不做拦截");
        return null;
    }
}
```

### 5.负载均衡和熔断

```yml
hystrix: #设置hystrix
  command: #设置它的指令
    default: #设置全局默认的配置
      execution:
        isolation:
          thread:
            timeoutInMilliseconds:3000   #设置全局的hystrix等待响应的延时时间
ribbon: #注意：ribbon的时长不能超过hystrix的时长
		##timeoutInMilliseconds>=(ConnectionTimeout+ReadTimeout)*2
  ConnectionTimeOut: 500 #连接超时时长，默认是1s--1000  如果5毫秒没有建立连接，就抛出异常
  ReadTimeOut: 1000 #读取超时时长，默认是1s--1000  如果建立连接但是2s没有响应，就抛出异常
```

