package posmy.interview.boot.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;
import posmy.interview.boot.services.BookService;
import posmy.interview.boot.services.UserService;

@RestController
public class BaseController {

    public UserService userService;
    public BookService bookService;

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @Autowired
    public void setBookService(BookService bookService) {
        this.bookService = bookService;
    }
}
