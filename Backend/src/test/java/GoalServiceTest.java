import com.example.backend.dto.GoalDtos.*;
import com.example.backend.entity.GoalEntity;
import com.example.backend.repository.GoalRepository;
import com.example.backend.service.GoalService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GoalServiceTest {

    @Mock private GoalRepository goalRepository;

    @InjectMocks
    private GoalService goalService;

    private GoalEntity goal(int id, int userId, String name, BigDecimal target,
                            BigDecimal current, LocalDate targetDate) {
        GoalEntity g = new GoalEntity();
        g.setId(id);
        g.setUserId(userId);
        g.setName(name);
        g.setTargetAmount(target);
        g.setCurrentAmount(current);
        g.setTargetDate(targetDate);
        g.setMonthlyAmount(BigDecimal.ZERO);
        return g;
    }

    @Test
    void list_returnsUserGoals() {
        when(goalRepository.findByUserIdOrderByCreatedAtDesc(1))
                .thenReturn(List.of(
                        goal(1, 1, "Ноутбук", new BigDecimal("120000"), BigDecimal.ZERO, null),
                        goal(2, 1, "Машина", new BigDecimal("1000000"), BigDecimal.ZERO, null)
                ));

        List<GoalResponse> result = goalService.list(1);

        assertEquals(2, result.size());
    }

    @Test
    void get_whenNotFound_throws404() {
        when(goalRepository.findByIdAndUserId(5, 1)).thenReturn(Optional.empty());
        assertThrows(ResponseStatusException.class, () -> goalService.get(1, 5));
    }

    @Test
    void get_success_returnsResponse() {
        when(goalRepository.findByIdAndUserId(5, 1)).thenReturn(
                Optional.of(goal(5, 1, "X", new BigDecimal("1000"), BigDecimal.ZERO, null)));

        GoalResponse resp = goalService.get(1, 5);

        assertEquals(5, resp.id());
    }

    @Test
    void create_withAutoMonthlyAmount_calculatesByTargetDate() {
        when(goalRepository.save(any(GoalEntity.class))).thenAnswer(inv -> {
            GoalEntity e = inv.getArgument(0);
            e.setId(1);
            return e;
        });
        LocalDate target = LocalDate.now().plusMonths(12).withDayOfMonth(1);

        CreateGoalRequest req = new CreateGoalRequest(
                "Ноутбук", null, new BigDecimal("120000"), target, null);

        GoalResponse resp = goalService.create(1, req);

        assertNotNull(resp);
        assertNotNull(resp.monthlyAmount());
        // 120000 / 12 = 10000
        assertEquals(0, new BigDecimal("10000").compareTo(resp.monthlyAmount()));
    }

    @Test
    void create_withExplicitMonthly_keepsValue() {
        when(goalRepository.save(any(GoalEntity.class))).thenAnswer(inv -> {
            GoalEntity e = inv.getArgument(0); e.setId(1); return e;
        });
        CreateGoalRequest req = new CreateGoalRequest(
                "X", null, new BigDecimal("50000"),
                LocalDate.now().plusMonths(6), new BigDecimal("7500"));

        GoalResponse resp = goalService.create(1, req);

        assertEquals(0, new BigDecimal("7500").compareTo(resp.monthlyAmount()));
    }

    @Test
    void create_withoutTargetDate_returnsNullMonthly() {
        when(goalRepository.save(any(GoalEntity.class))).thenAnswer(inv -> {
            GoalEntity e = inv.getArgument(0); e.setId(1); return e;
        });
        CreateGoalRequest req = new CreateGoalRequest(
                "X", null, new BigDecimal("50000"), null, null);

        GoalResponse resp = goalService.create(1, req);

        assertNull(resp.monthlyAmount());
    }

    @Test
    void create_whenAlreadyReachedTarget_monthlyIsZero() {
        when(goalRepository.save(any(GoalEntity.class))).thenAnswer(inv -> {
            GoalEntity e = inv.getArgument(0); e.setId(1); return e;
        });
        CreateGoalRequest req = new CreateGoalRequest(
                "X", null, new BigDecimal("0"),
                LocalDate.now().plusMonths(6), null);

        GoalResponse resp = goalService.create(1, req);

        // remaining=0 → 0
        assertEquals(0, BigDecimal.ZERO.compareTo(resp.monthlyAmount()));
    }

    @Test
    void update_whenNotFound_throws404() {
        when(goalRepository.findByIdAndUserId(5, 1)).thenReturn(Optional.empty());
        UpdateGoalRequest req = new UpdateGoalRequest(
                "X", null, new BigDecimal("10"), BigDecimal.ZERO, null, null);
        assertThrows(ResponseStatusException.class, () -> goalService.update(1, 5, req));
    }

    @Test
    void update_success_updatesFields() {
        GoalEntity existing = goal(5, 1, "Old", new BigDecimal("100"), BigDecimal.ZERO, null);
        when(goalRepository.findByIdAndUserId(5, 1)).thenReturn(Optional.of(existing));
        when(goalRepository.save(existing)).thenReturn(existing);

        UpdateGoalRequest req = new UpdateGoalRequest(
                "New", "desc", new BigDecimal("2000"),
                new BigDecimal("500"), null, new BigDecimal("125"));

        GoalResponse resp = goalService.update(1, 5, req);

        assertEquals("New", resp.name());
        assertEquals(0, new BigDecimal("2000").compareTo(resp.targetAmount()));
        assertEquals(0, new BigDecimal("125").compareTo(resp.monthlyAmount()));
    }

    @Test
    void delete_whenNotFound_throws404() {
        when(goalRepository.findByIdAndUserId(5, 1)).thenReturn(Optional.empty());
        assertThrows(ResponseStatusException.class, () -> goalService.delete(1, 5));
    }

    @Test
    void delete_success_deletes() {
        GoalEntity existing = goal(5, 1, "X", BigDecimal.ZERO, BigDecimal.ZERO, null);
        when(goalRepository.findByIdAndUserId(5, 1)).thenReturn(Optional.of(existing));

        goalService.delete(1, 5);

        verify(goalRepository).delete(existing);
    }
}
