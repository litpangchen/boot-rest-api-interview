package posmy.interview.boot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.event.EventListener;
import posmy.interview.boot.constants.UserRole;
import posmy.interview.boot.services.BookService;
import posmy.interview.boot.services.UserService;

@SpringBootApplication()
public class Application {

    private final ApplicationContext context;

    public Application(ApplicationContext context) {
        this.context = context;
    }

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @EventListener(ApplicationReadyEvent.class)
    public void createDummyUserAndBookAfterWebStartup() {

        UserService userService = context.getBean(UserService.class);
        userService.createUser("librarian1", UserRole.LIBRARIAN.getId());
        userService.createUser("member1", UserRole.MEMBER.getId());

        BookService bookService = context.getBean(BookService.class);
        bookService.createBook("book1", "author1");
        bookService.createBook("book2", "author2");
    }
}
