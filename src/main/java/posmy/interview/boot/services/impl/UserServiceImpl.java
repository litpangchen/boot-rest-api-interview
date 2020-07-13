package posmy.interview.boot.services.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import posmy.interview.boot.constants.StatusResponse;
import posmy.interview.boot.constants.UserRole;
import posmy.interview.boot.dao.UserDao;
import posmy.interview.boot.model.User;
import posmy.interview.boot.services.UserService;
import posmy.interview.boot.vo.ResponseResult;

import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    private UserDao userDao;
    private PasswordEncoder passwordEncoder;

    @Autowired
    public void setUserDao(UserDao userDao) {
        this.userDao = userDao;
    }

    @Autowired
    public void setPasswordEncoder(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public ResponseResult<?> createUser(String username, Integer userRoleId) {

        // throw error if invalid user role id
        try {
            UserRole.getRoleById(userRoleId);
        } catch (Exception ex) {
            return new ResponseResult<>(StatusResponse.INVALID_OR_MISSING_PARAMETERS);
        }

        // check username is exists
        User userByUsername = this.userDao.findUserByUsername(username);

        if (userByUsername != null) {
            return new ResponseResult<>(StatusResponse.DUPLICATED_ENTITY);
        }

        String password = passwordEncoder.encode(username);

        User user = new User();
        user.setUsername(username);
        user.setPassword(password);
        user.setUserRoleId(userRoleId);

        this.userDao.save(user);
        return new ResponseResult<>();
    }

    @Override
    public ResponseResult<?> getUsers() {
        return new ResponseResult<>(this.userDao.findAll());
    }

    @Override
    public User getUserByUserName(String username) {
        return this.userDao.findUserByUsername(username);
    }

    @Override
    public User getUserById(Integer id) {
        Optional<User> user = this.userDao.findById(id);
        return user.orElse(null);
    }

    @Override
    public ResponseResult<?> deleteUserById(Integer id) {

        // check user id is exists
        User user = getUserById(id);
        if (user == null) {
            return new ResponseResult<>(StatusResponse.ENTITY_NOT_FOUND);
        }
        this.userDao.deleteById(id);
        return new ResponseResult<>();
    }

    @Override
    public ResponseResult<?> deleteAllUser() {
        // only delete all member Id
        Integer deleted = this.userDao.deleteByUserRoleId(UserRole.MEMBER.getId());
        return new ResponseResult<>();
    }
}
