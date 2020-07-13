package posmy.interview.boot.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import posmy.interview.boot.model.Book;

@Repository
public interface BookDao extends JpaRepository<Book, Integer> {

    Book findBookByBookName(String bookName);
}
