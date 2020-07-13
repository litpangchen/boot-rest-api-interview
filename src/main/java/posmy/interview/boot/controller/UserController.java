package posmy.interview.boot.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import posmy.interview.boot.constants.StatusResponse;
import posmy.interview.boot.constants.UserRole;
import posmy.interview.boot.model.User;
import posmy.interview.boot.vo.ResponseResult;

@RestController
public class UserController extends BaseController {

    @PostMapping("/users")
    public ResponseResult<?> createUser(@RequestParam("username") String username, @RequestParam("userRoleId") Integer userRoleId) {
        return userService.createUser(username, userRoleId);
    }

    @GetMapping("/users")
    public ResponseResult<?> getUsers() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) auth.getPrincipal();
        if (user.getUserRoleId().equals(UserRole.MEMBER.getId())) {
            return getUserById(user.getId());
        }
        return userService.getUsers();
    }

    @GetMapping("/users/{id}")
    public ResponseResult<?> getUserById(@PathVariable("id") Integer id) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) auth.getPrincipal();

        // member only can read own user info
        if (user.getUserRoleId().equals(UserRole.MEMBER.getId())) {
            id = user.getId();
        }
        user = userService.getUserById(id);
        if (user == null) {
            return new ResponseResult<>(StatusResponse.ENTITY_NOT_FOUND);
        }
        return new ResponseResult<>(user);
    }

    @DeleteMapping("/users/{id}")
    public ResponseResult<?> deleteUser(@PathVariable("id") Integer id) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) auth.getPrincipal();

        // member only can delete their account , librarian can delete any accounts
        if (user.getUserRoleId().equals(UserRole.MEMBER.getId())) {
            id = user.getId();
        }

        return userService.deleteUserById(id);
    }

    @DeleteMapping("/users")
    public ResponseResult<?> deleteAllUser() {
        return userService.deleteAllUser();
    }
}
