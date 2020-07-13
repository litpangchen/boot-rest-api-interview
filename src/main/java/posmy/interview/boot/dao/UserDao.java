package posmy.interview.boot.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import posmy.interview.boot.model.User;

@Repository
public interface UserDao extends JpaRepository<User, Integer> {

    User findUserByUsername(String username);

    // cannot reliably process 'remove' call , need to put @Modifying and @Transactionnal
    @Modifying
    @Transactional
    Integer deleteByUserRoleId(Integer userRoleId);
}
