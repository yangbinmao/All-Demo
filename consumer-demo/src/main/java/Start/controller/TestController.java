package Start.controller;


import Start.client.PeopleClient;
import Start.domain.People;

import com.netflix.ribbon.proxy.annotation.Hystrix;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.netflix.ribbon.RibbonLoadBalancerClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.List;


/**
 * Created by ybm on 2019/11/15 15:05.
 */
@RestController
//@RequestMapping("/get1")
//@DefaultProperties(defaultFallback = "defaultforback")
@Slf4j
public class TestController {
    @Autowired
    private RestTemplate restTemplate;

//    @Autowired
//    private DiscoveryClient discoveryClient;//发现客户端。

//    @Autowired
//    private RibbonLoadBalancerClient client;

/*
    @GetMapping("/consumer/findpeople")
    public People findpeople(){
        //根据服务名称来获取服务实例
        List<ServiceInstance> instances = discoveryClient.getInstances("user-server");
        //从实例中取出ip和端口
        ServiceInstance instance = instances.get(0);

            log.debug("http://"+instance.getHost()+":"+instance.getPort()+"/findpeople");
        return restTemplate.getForObject("http://"+instance.getHost()+":"+instance.getPort()+"/findpeople", People.class);
    }
*/


 /*
 @GetMapping("/consumer/findpeople2")
    public People findpeople2(){
        //根据服务名称来获取服务实例
        //使用ribbon  实现负载均衡
        //client.choose自动获取一个url服务地址
        ServiceInstance instance = client.choose("user-server");
        log.debug("http://"+instance.getHost()+":"+instance.getPort()+"/findpeople");
        return restTemplate.getForObject("http://"+instance.getHost()+":"+instance.getPort()+"/findpeople", People.class);
    }
    */



    //负载均衡

    @GetMapping("/consumer/findpeople3")
    public People findpeople3(){
       String url="http://user-server/findpeople";
       //user-server 要访问的服务的名字，在yml里面那个name，
        //findpeople 方法的映射名字。
        return restTemplate.getForObject(url, People.class);
    }


    //Hystrix容错处理，降级处理

    // @HystrixCommand 容断降级的注解。
    // fallbackMethod= "findpeople4forback" 如果发生降级，需要访问的方法，findpeople4forback是下边自己定义的方法。
    //注意：本身方法，和降级后的方法返回类型一定要是一样的。
    @GetMapping("/consumer/findpeople4")
    //@HystrixCommand(fallbackMethod = "findpeople4forback") //失败容错,降级处理的注解,会调用自己专有的方法。
  /*
   @HystrixCommand(commandProperties = {
            //  @HystrixProperty(name="execution.isolation.thread.timeoutInMilliseconds",value = "2000"),//设置降级等待时间，默认是1000毫秒
            @HystrixProperty(name = "circuitBreaker.requestVolumeThreshold",value = "10") ,   //设置熔断检查次数，默认是20次，达到了次数后，就会进入熔断检查机制
            @HystrixProperty(name = "circuitBreaker.sleepWindowInMilliseconds",value = "10000"),//设置熔断后休眠时间窗持续时间，默认5秒，熔断后会进入这个休眠时间，时间过后就会开始进行尝试熔断检查测试机制
            @HystrixProperty(name="circuitBreaker.errorThresholdPercentage",value = "60"),//设置失败降级的百分比，默认是50%，达到这个百分比后，就会直接熔断。
    })   //降级处理就会调用默认的方法
    */
    public String findpeople4(int id){
        String url="http://user-server/findpeople?id="+id;
        return restTemplate.getForObject(url, String.class);
        //  restTemplate.getForObject(url, String.class)返回类型是根据String.class决定的。
        //  其实默认是个json格式，json本就是String格式，
        //  所以如果写成people.class,等于就是他把json，和People自动转化了一下。
    }

    public String findpeople4forback(){
        return "不好意思，服务器太拥挤了，专用降级方法";
    }

    public String defaultforback(){
        return "不好意思，服务器太拥挤了，通用降级方法";
    }

    //通过Feign自动寻找服务进行调用
    @Autowired
    PeopleClient peopleClient;
    @GetMapping("/consumer/findpeople5")
    public People getpeoplebyFeign(int id){
        People peopleById = peopleClient.getPeopleById(id);
        return peopleById;
    }
}
