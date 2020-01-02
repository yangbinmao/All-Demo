package Start.client;

import Start.domain.People;
import lombok.val;
import org.springframework.stereotype.Component;

@Component
public class PeopleClientFallback implements PeopleClient {
    @Override
    public People getPeopleById(int id) {
        People people = new People();
        people.setName("未知信息");
        return people;
    }
}
