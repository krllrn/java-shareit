package ru.practicum.shareit.RepositoryTests;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.shareit.item.CommentRepository;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
public class CommentRepositoryTest {

    private final CommentRepository commentRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    User user = new User("test@test.com", "Test Testov");
    Item item1 = new Item(1L, user, "Name1", "Description1", true, null, null);
    Comment comment = new Comment("Test comment");

    @Autowired
    public CommentRepositoryTest(CommentRepository commentRepository, ItemRepository itemRepository, UserRepository userRepository) {
        this.commentRepository = commentRepository;
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
    }

    @Test
    public void testFindAllByItemId() {
        userRepository.save(user);
        comment.setItemId(item1.getId());
        comment.setAuthorName(user.getName());
        itemRepository.save(item1);
        commentRepository.save(comment);
        List<Comment> commentList = commentRepository.findAllByItemId(item1.getId());

        assertEquals(comment.getText(), commentList.get(0).getText());
        assertEquals(comment.getAuthorName(), commentList.get(0).getAuthorName());
    }
}
