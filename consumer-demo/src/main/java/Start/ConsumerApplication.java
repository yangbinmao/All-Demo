package Start;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.SpringCloudApplication;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.netflix.hystrix.EnableHystrix;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;
//@EnableCircuitBreaker //服务的熔断
// //@EnableHystrix   //这个本身是hystrix的注解，但是要用上边的熔断，包含了这个注解，所以就不用这个了。
//@EnableDiscoveryClient  //注册中心服务端注解，表示我要把地址给注册中心
//@SpringBootApplication

@EnableFeignClients   //优化RestTemplate传入参数的注解
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
