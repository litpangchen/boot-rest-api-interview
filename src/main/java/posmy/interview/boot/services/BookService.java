package posmy.interview.boot.services;

import posmy.interview.boot.model.Book;
import posmy.interview.boot.model.User;
import posmy.interview.boot.vo.ResponseResult;

public interface BookService {

    ResponseResult<?> getBooks();

    /**
     * Get Book By Id
     *
     * @param id
     * @return
     */
    Book getBookById(Integer id);

    ResponseResult<?> updateBook(Book form);

    /**
     * Create Book record
     *
     * @param bookName
     * @param author
     * @return
     */
    ResponseResult<?> createBook(String bookName, String author);

    /**
     * Member borrow book
     *
     * @param bookId
     * @param user
     * @return
     */
    ResponseResult<?> borrowBook(Integer bookId, User user);

    /**
     * Member return book
     *
     * @param bookId
     * @param user
     * @return
     */
    ResponseResult<?> returnBook(Integer bookId, User user);

    /**
     * delete book by id
     *
     * @param id book id
     * @return
     */
    ResponseResult<?> deleteBook(Integer id);
}
