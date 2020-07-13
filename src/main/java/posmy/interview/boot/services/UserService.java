package posmy.interview.boot.services;

import posmy.interview.boot.model.User;
import posmy.interview.boot.vo.ResponseResult;

public interface UserService {

    /**
     * Create an new user
     *
     * @param username
     */
    ResponseResult<?> createUser(String username, Integer userRoleId);

    /**
     * Get list of users
     *
     * @return
     */
    ResponseResult<?> getUsers();

    /**
     * get user by username
     *
     * @param username username
     * @return User object
     */
    User getUserByUserName(String username);

    /**
     * Get user by id
     *
     * @param id user id
     * @return response result
     */
    User getUserById(Integer id);

    /**
     * delte user by id
     *
     * @param id user id
     * @return response result
     */
    ResponseResult<?> deleteUserById(Integer id);

    /**
     * delete all member type users
     *
     * @return response result
     */
    ResponseResult<?> deleteAllUser();
}
