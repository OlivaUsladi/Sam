import com.example.backend.dto.TagDtos.*;
import com.example.backend.entity.TagEntity;
import com.example.backend.repository.TagRepository;
import com.example.backend.repository.TransactionRepository;
import com.example.backend.service.TagService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TagServiceTest {

    @Mock private TagRepository tagRepository;
    @Mock private TransactionRepository transactionRepository;

    @InjectMocks
    private TagService tagService;

    private TagEntity tag(int id, int userId, String name) {
        TagEntity t = new TagEntity();
        t.setId(id);
        t.setUserId(userId);
        t.setName(name);
        return t;
    }

    @Test
    void list_returnsUserTags() {
        when(tagRepository.findByUserIdOrderByNameAsc(1))
                .thenReturn(List.of(tag(1, 1, "Еда"), tag(2, 1, "Транспорт")));

        List<TagResponse> result = tagService.list(1);

        assertEquals(2, result.size());
        assertEquals("Еда", result.get(0).name());
    }

    @Test
    void create_whenNameExists_throws409() {
        when(tagRepository.existsByUserIdAndNameIgnoreCase(1, "Еда")).thenReturn(true);

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> tagService.create(1, new CreateTagRequest("Еда")));
        assertTrue(ex.getMessage().contains("уже есть"));
    }

    @Test
    void create_success_savesTrimmed() {
        when(tagRepository.existsByUserIdAndNameIgnoreCase(1, "  Еда  ")).thenReturn(false);
        when(tagRepository.save(any(TagEntity.class))).thenAnswer(inv -> {
            TagEntity e = inv.getArgument(0); e.setId(10); return e;
        });

        TagResponse resp = tagService.create(1, new CreateTagRequest("  Еда  "));

        assertEquals("Еда", resp.name());
        assertEquals(10, resp.id());
    }

    @Test
    void update_whenNotFound_throws404() {
        when(tagRepository.findByIdAndUserId(5, 1)).thenReturn(Optional.empty());
        assertThrows(ResponseStatusException.class,
                () -> tagService.update(1, 5, new UpdateTagRequest("Новое")));
    }

    @Test
    void update_success_changesName() {
        TagEntity existing = tag(5, 1, "Старое");
        when(tagRepository.findByIdAndUserId(5, 1)).thenReturn(Optional.of(existing));
        when(tagRepository.save(existing)).thenReturn(existing);

        TagResponse resp = tagService.update(1, 5, new UpdateTagRequest("Новое"));

        assertEquals("Новое", resp.name());
    }

    @Test
    void delete_whenNotFound_throws404() {
        when(tagRepository.findByIdAndUserId(5, 1)).thenReturn(Optional.empty());
        assertThrows(ResponseStatusException.class, () -> tagService.delete(1, 5));
    }

    @Test
    void delete_success_clearsTransactionsAndDeletes() {
        TagEntity existing = tag(5, 1, "Еда");
        when(tagRepository.findByIdAndUserId(5, 1)).thenReturn(Optional.of(existing));

        tagService.delete(1, 5);

        verify(transactionRepository).clearTagFromUserTransactions(5, 1);
        verify(tagRepository).delete(existing);
    }
}
