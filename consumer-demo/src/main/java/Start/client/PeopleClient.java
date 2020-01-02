package Start.client;

import Start.domain.People;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;


@FeignClient(value = "user-server",fallback =PeopleClientFallback.class)
public interface PeopleClient {
    @GetMapping("findpeople?id={id}")
    People getPeopleById(@PathVariable int id);
}
