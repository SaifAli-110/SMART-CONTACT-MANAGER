package in.sp.main.Repository;

import in.sp.main.Model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserRepository extends JpaRepository<User,Integer> {

    //	you can run SQL query using ---> JPA, ORM, HIBERNATE
//	you can implement own method.
//    1.
    @Query("select u from User u where u.userEmail = :userEmail")
    public User findByUserEmail(@Param("userEmail") String userEmail);


}
