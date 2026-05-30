import com.example.backend.dto.SourceDtos.*;
import com.example.backend.entity.SourceEntity;
import com.example.backend.repository.SourceRepository;
import com.example.backend.service.SourceService;
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
class SourceServiceTest {

    @Mock private SourceRepository sourceRepository;

    @InjectMocks
    private SourceService sourceService;

    private SourceEntity source(int id, int userId, String name, String type) {
        SourceEntity e = new SourceEntity();
        e.setId(id);
        e.setUserId(userId);
        e.setName(name);
        e.setType(type);
        return e;
    }

    @Test
    void list_returnsUserSources() {
        when(sourceRepository.findByUserIdOrderByNameAsc(1))
                .thenReturn(List.of(source(1, 1, "Карта Сбер", "card"),
                                    source(2, 1, "Наличные", "cash")));

        List<SourceResponse> result = sourceService.list(1);

        assertEquals(2, result.size());
    }

    @Test
    void create_whenDuplicate_throws409() {
        when(sourceRepository.existsByUserIdAndNameIgnoreCase(1, "Карта")).thenReturn(true);

        assertThrows(ResponseStatusException.class,
                () -> sourceService.create(1, new CreateSourceRequest("Карта", "card")));
    }

    @Test
    void create_success_trimsName() {
        when(sourceRepository.existsByUserIdAndNameIgnoreCase(1, "  Карта  ")).thenReturn(false);
        when(sourceRepository.save(any(SourceEntity.class))).thenAnswer(inv -> {
            SourceEntity e = inv.getArgument(0); e.setId(7); return e;
        });

        SourceResponse resp = sourceService.create(1, new CreateSourceRequest("  Карта  ", "card"));

        assertEquals("Карта", resp.name());
        assertEquals("card", resp.type());
    }

    @Test
    void update_whenNotFound_throws404() {
        when(sourceRepository.findByIdAndUserId(5, 1)).thenReturn(Optional.empty());
        assertThrows(ResponseStatusException.class,
                () -> sourceService.update(1, 5, new UpdateSourceRequest("Имя", "card")));
    }

    @Test
    void update_success_updatesFields() {
        SourceEntity e = source(5, 1, "Старое", "cash");
        when(sourceRepository.findByIdAndUserId(5, 1)).thenReturn(Optional.of(e));
        when(sourceRepository.save(e)).thenReturn(e);

        SourceResponse resp = sourceService.update(1, 5,
                new UpdateSourceRequest("Новое", "card"));

        assertEquals("Новое", resp.name());
        assertEquals("card", resp.type());
    }

    @Test
    void delete_whenNotFound_throws404() {
        when(sourceRepository.findByIdAndUserId(5, 1)).thenReturn(Optional.empty());
        assertThrows(ResponseStatusException.class, () -> sourceService.delete(1, 5));
    }

    @Test
    void delete_success_deletes() {
        SourceEntity e = source(5, 1, "X", "card");
        when(sourceRepository.findByIdAndUserId(5, 1)).thenReturn(Optional.of(e));

        sourceService.delete(1, 5);

        verify(sourceRepository).delete(e);
    }
}
