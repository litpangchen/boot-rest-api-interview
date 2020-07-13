package posmy.interview.boot.services.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import posmy.interview.boot.constants.StatusResponse;
import posmy.interview.boot.dao.BookDao;
import posmy.interview.boot.model.Book;
import posmy.interview.boot.model.User;
import posmy.interview.boot.services.BookService;
import posmy.interview.boot.vo.ResponseResult;

import java.util.Optional;

@Service
public class BookServiceImpl implements BookService {

    private BookDao bookDao;

    @Autowired
    public void setBookDao(BookDao bookDao) {
        this.bookDao = bookDao;
    }

    @Override
    public ResponseResult<?> getBooks() {
        return new ResponseResult<>(this.bookDao.findAll());
    }

    @Override
    public Book getBookById(Integer id) {
        Optional<Book> bookById = this.bookDao.findById(id);
        return bookById.orElse(null);
    }

    @Override
    public ResponseResult<?> updateBook(Book form) {

        Book book = getBookById(form.getId());
        if (book == null) {
            return new ResponseResult<>(StatusResponse.ENTITY_NOT_FOUND);
        }
        if (!StringUtils.isEmpty(form.getAuthor())) {
            book.setAuthor(form.getAuthor());
        }
        if (form.getBookStatus() != null) {
            book.setBookStatus(form.getBookStatus());
        }
        this.bookDao.save(book);
        return new ResponseResult<>();
    }

    @Override
    public ResponseResult<?> createBook(String bookName, String author) {

        Book book = this.bookDao.findBookByBookName(bookName);

        if (book != null) {
            return new ResponseResult<>(StatusResponse.DUPLICATED_ENTITY);
        }

        book = new Book();
        book.setBookName(bookName);
        book.setAuthor(author);
        this.bookDao.save(book);

        return new ResponseResult<>();
    }

    @Override
    public ResponseResult<?> borrowBook(Integer bookId, User user) {

        // check books is exists
        Book book = getBookById(bookId);
        if (book == null) {
            return new ResponseResult<>(StatusResponse.ENTITY_NOT_FOUND);
        }

        // check books already borrowed by other member
        if (book.getBookStatus().equals(Book.BookStatus.BORROWED)) {
            return new ResponseResult<>(StatusResponse.RESOURCE_NOT_AVAILABLE);
        }

        // mark books borrow by  user
        book.setUser(user);
        book.setBookStatus(Book.BookStatus.BORROWED);
        this.bookDao.save(book);
        return new ResponseResult<>();
    }

    @Override
    public ResponseResult<?> returnBook(Integer bookId, User user) {

        // check books is exists
        Book book = getBookById(bookId);
        if (book == null) {
            return new ResponseResult<>(StatusResponse.ENTITY_NOT_FOUND);
        }

        // check is borrowed
        if (book.getBookStatus().equals(Book.BookStatus.AVAILABLE)) {
            return new ResponseResult<>(StatusResponse.INVALID_OR_MISSING_PARAMETERS);
        }

        // if userId is not same , means is not borrowed by the user
        if (!book.getUser().getId().equals(user.getId())) {
            return new ResponseResult<>(StatusResponse.INVALID_OR_MISSING_PARAMETERS);
        }

        // mark book as available
        book.setUser(null);
        book.setBookStatus(Book.BookStatus.AVAILABLE);
        this.bookDao.save(book);
        return new ResponseResult<>();
    }

    @Override
    public ResponseResult<?> deleteBook(Integer bookId) {

        // check books is exists
        Book book = getBookById(bookId);
        if (book == null) {
            return new ResponseResult<>(StatusResponse.ENTITY_NOT_FOUND);
        }
        this.bookDao.deleteById(bookId);
        return new ResponseResult<>();
    }
}
