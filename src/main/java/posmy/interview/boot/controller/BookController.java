package posmy.interview.boot.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import posmy.interview.boot.constants.StatusResponse;
import posmy.interview.boot.model.Book;
import posmy.interview.boot.model.User;
import posmy.interview.boot.vo.ResponseResult;

@RestController()
public class BookController extends BaseController {

    @GetMapping("/books")
    public ResponseResult<?> getBooks() {
        return bookService.getBooks();
    }

    @GetMapping("/books/{id}")
    public ResponseResult<?> getBookById(@PathVariable("id") Integer id) {
        Book bookById = bookService.getBookById(id);
        if (bookById == null) {
            return new ResponseResult<>(StatusResponse.ENTITY_NOT_FOUND);
        }
        return new ResponseResult<>(bookById);
    }

    /**
     * This rest api only available for librarian
     *
     * @param bookName book name
     * @param author   author
     * @return response result
     */
    @PostMapping(value = "/books", consumes = "application/x-www-form-urlencoded", produces = "application/json")
    public ResponseResult<?> createBook(
            @RequestParam("bookName") String bookName,
            @RequestParam("author") String author) {
        return bookService.createBook(bookName, author);
    }

    /**
     * This rest api opened for librarian
     *
     * @param bookId Specific book id
     * @return response result
     */
    @PutMapping("/books/{id}")
    public ResponseResult<?> updateBookById(@PathVariable("id") Integer bookId,
                                            @RequestParam(value = "author", required = false) String author,
                                            @RequestParam(value = "bookStatus", required = false) Integer bookStatus) {
        Book form = new Book();
        form.setId(bookId);
        form.setAuthor(author);
        form.setBookStatus(bookStatus);
        return bookService.updateBook(form);
    }

    @PatchMapping("/books/borrow/{id}")
    public ResponseResult<?> borrowBook(@PathVariable("id") Integer bookId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) auth.getPrincipal();
        return bookService.borrowBook(bookId, user);
    }

    @PatchMapping("/books/return/{id}")
    public ResponseResult<?> returnBook(@PathVariable("id") Integer bookId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) auth.getPrincipal();
        return bookService.returnBook(bookId, user);
    }

    @DeleteMapping("/books/{id}")
    public ResponseResult<?> deleteBookById(@PathVariable("id") Integer id) {
        return bookService.deleteBook(id);
    }
}
