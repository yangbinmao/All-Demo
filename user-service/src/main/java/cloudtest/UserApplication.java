package cloudtest;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;


/**
 * Created by ybm on 2019/11/25 14:31.
 */


@EnableDiscoveryClient  //表示是注册端，Eureka的注解是@EnableEurekaClient,但是@EnableDiscoveryClient时spring的注册客户端。既可以兼容Eureka,也可以支持其他的注册中心。
@SpringBootApplication
public class UserApplication {
    public static void main(String[] args) {
        SpringApplication.run(UserApplication.class);
    }
}
