package cloudtest.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;


/**
 * Created by ybm on 2019/11/18 14:48.
 */
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
//@JsonIgnoreProperties(value={"hibernateLazyInitializer","handler","fieldHandler"})
public class People {
    @Id
    @GeneratedValue
    private Long pid;
    private String name;

}
