package cloudtest.controller;


import cloudtest.domain.People;
import cloudtest.repository.PeopleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;


/**
 * Created by ybm on 2019/11/15 15:05.
 */
@RestController
//@RequestMapping("/get1")
public class TestController {

@Autowired
private PeopleRepository peopleRepository;


    @GetMapping("/getpeople")
    public People getpeople(){
        return peopleRepository.getOne(1L);
    }

    @GetMapping("/findpeople")
    public People findpeople(int id){
        if (id%2==0){
        throw new RuntimeException();//模拟降级
        }
        return peopleRepository.findById(1L).get();
    }

}
