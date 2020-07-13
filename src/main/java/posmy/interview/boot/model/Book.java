package posmy.interview.boot.model;

import com.fasterxml.jackson.annotation.JsonInclude;

import javax.persistence.*;
import java.io.Serializable;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Entity
@Table(name = "book")
public class Book implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "bookName", unique = true, updatable = false)
    private String bookName;

    @Column(name = "author")
    private String author;

    @OneToOne()
    @JoinColumn(name = "userId", referencedColumnName = "id")
    private User user;

    /**
     * 1 Represent Available
     * 2 Represent Borrowed
     */
    @Column(name = "bookStatus")
    private Integer bookStatus = 1;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getBookName() {
        return bookName;
    }

    public void setBookName(String bookName) {
        this.bookName = bookName;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Integer getBookStatus() {
        return bookStatus;
    }

    public void setBookStatus(Integer bookStatus) {
        this.bookStatus = bookStatus;
    }

    public interface BookStatus {
        Integer AVAILABLE = 1;
        Integer BORROWED = 2;
    }

    @Override
    public String toString() {
        return "Book{" +
                "id=" + id +
                ", bookName='" + bookName + '\'' +
                ", author='" + author + '\'' +
                ", user=" + user +
                ", bookStatus=" + bookStatus +
                '}';
    }
}
