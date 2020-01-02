package cloudtest.repository;



import cloudtest.domain.People;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by ybm on 2019/11/25 10:37.
 */
public interface PeopleRepository extends JpaRepository<People,Long> {
}
