import com.example.backend.dto.TransactionDtos.*;
import com.example.backend.entity.SourceEntity;
import com.example.backend.entity.TagEntity;
import com.example.backend.entity.TransactionEntity;
import com.example.backend.repository.SourceRepository;
import com.example.backend.repository.TagRepository;
import com.example.backend.repository.TransactionRepository;
import com.example.backend.service.TransactionService;
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
class TransactionServiceTest {

    @Mock private TransactionRepository transactionRepository;
    @Mock private SourceRepository sourceRepository;
    @Mock private TagRepository tagRepository;

    @InjectMocks
    private TransactionService transactionService;

    private SourceEntity userSource(int id) {
        SourceEntity s = new SourceEntity();
        s.setId(id); s.setUserId(1); s.setName("S"); s.setType("card");
        return s;
    }

    private TagEntity userTag(int id) {
        TagEntity t = new TagEntity();
        t.setId(id); t.setUserId(1); t.setName("T");
        return t;
    }

    private TransactionEntity sampleTx() {
        TransactionEntity t = new TransactionEntity();
        t.setId(10);
        t.setUserId(1);
        t.setName("Кофе");
        t.setAmount(new BigDecimal("100"));
        t.setType("expense");
        t.setTransactionDate(LocalDate.now());
        t.setSourceId(1);
        return t;
    }

    // ----- LIST -----

    @Test
    void list_withTypeAndDates_callsBetweenQuery() {
        LocalDate from = LocalDate.of(2025, 5, 1);
        LocalDate to = LocalDate.of(2025, 5, 31);
        when(transactionRepository
                .findByUserIdAndTypeAndTransactionDateBetweenOrderByTransactionDateDescIdDesc(
                        1, "expense", from, to))
                .thenReturn(List.of(sampleTx()));

        List<TransactionResponse> r = transactionService.list(1, "expense", from, to);

        assertEquals(1, r.size());
    }

    @Test
    void list_typeOnly_callsTypeQuery() {
        when(transactionRepository
                .findByUserIdAndTypeOrderByTransactionDateDescIdDesc(1, "income"))
                .thenReturn(List.of());

        List<TransactionResponse> r = transactionService.list(1, "income", null, null);

        assertNotNull(r);
        verify(transactionRepository).findByUserIdAndTypeOrderByTransactionDateDescIdDesc(1, "income");
    }

    @Test
    void list_datesOnly_callsBetweenQuery() {
        LocalDate from = LocalDate.of(2025, 1, 1);
        LocalDate to = LocalDate.of(2025, 1, 31);
        when(transactionRepository
                .findByUserIdAndTransactionDateBetweenOrderByTransactionDateDescIdDesc(1, from, to))
                .thenReturn(List.of());

        transactionService.list(1, null, from, to);

        verify(transactionRepository)
                .findByUserIdAndTransactionDateBetweenOrderByTransactionDateDescIdDesc(1, from, to);
    }

    @Test
    void list_noFilters_callsBaseQuery() {
        when(transactionRepository.findByUserIdOrderByTransactionDateDescIdDesc(1))
                .thenReturn(List.of());

        transactionService.list(1, null, null, null);

        verify(transactionRepository).findByUserIdOrderByTransactionDateDescIdDesc(1);
    }

    @Test
    void listByTag_returnsTaggedTransactions() {
        when(transactionRepository
                .findByUserIdAndTagIdOrderByTransactionDateDescIdDesc(1, 10))
                .thenReturn(List.of(sampleTx()));

        List<TransactionResponse> r = transactionService.listByTag(1, 10);

        assertEquals(1, r.size());
    }

    // ----- CREATE -----

    @Test
    void create_whenIncomeWithTag_throws400() {
        when(sourceRepository.findByIdAndUserId(1, 1)).thenReturn(Optional.of(userSource(1)));
        CreateTransactionRequest req = new CreateTransactionRequest(
                "Зарплата", new BigDecimal("50000"), "income", null,
                LocalDate.now(), 1, 5);

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> transactionService.create(1, req));
        assertTrue(ex.getMessage().contains("Тэг можно ставить только на расход"));
    }

    @Test
    void create_whenSourceNotOwned_throws() {
        when(sourceRepository.findByIdAndUserId(99, 1)).thenReturn(Optional.empty());
        CreateTransactionRequest req = new CreateTransactionRequest(
                "X", new BigDecimal("100"), "expense", null,
                LocalDate.now(), 99, null);

        assertThrows(ResponseStatusException.class, () -> transactionService.create(1, req));
    }

    @Test
    void create_expenseWithTag_success() {
        when(sourceRepository.findByIdAndUserId(1, 1)).thenReturn(Optional.of(userSource(1)));
        when(tagRepository.findByIdAndUserId(5, 1)).thenReturn(Optional.of(userTag(5)));
        when(transactionRepository.save(any(TransactionEntity.class))).thenAnswer(inv -> {
            TransactionEntity e = inv.getArgument(0); e.setId(100); return e;
        });
        CreateTransactionRequest req = new CreateTransactionRequest(
                "Кофе", new BigDecimal("100"), "expense", null,
                LocalDate.now(), 1, 5);

        TransactionResponse resp = transactionService.create(1, req);

        assertEquals(100, resp.id());
        assertEquals(5, resp.tagId());
    }

    @Test
    void create_incomeWithoutTag_success() {
        when(sourceRepository.findByIdAndUserId(1, 1)).thenReturn(Optional.of(userSource(1)));
        when(transactionRepository.save(any(TransactionEntity.class))).thenAnswer(inv -> {
            TransactionEntity e = inv.getArgument(0); e.setId(101); return e;
        });
        CreateTransactionRequest req = new CreateTransactionRequest(
                "Зарплата", new BigDecimal("50000"), "income", null,
                LocalDate.now(), 1, null);

        TransactionResponse resp = transactionService.create(1, req);

        assertEquals(101, resp.id());
        assertNull(resp.tagId());
    }

    // ----- UPDATE -----

    @Test
    void update_whenNotFound_throws404() {
        when(transactionRepository.findByIdAndUserId(100, 1)).thenReturn(Optional.empty());
        UpdateTransactionRequest req = new UpdateTransactionRequest(
                "X", new BigDecimal("10"), "expense", null,
                LocalDate.now(), 1, null);
        assertThrows(ResponseStatusException.class,
                () -> transactionService.update(1, 100, req));
    }

    @Test
    void update_success_updatesFields() {
        TransactionEntity existing = sampleTx();
        when(transactionRepository.findByIdAndUserId(10, 1)).thenReturn(Optional.of(existing));
        when(sourceRepository.findByIdAndUserId(1, 1)).thenReturn(Optional.of(userSource(1)));
        when(transactionRepository.save(existing)).thenReturn(existing);

        UpdateTransactionRequest req = new UpdateTransactionRequest(
                "Чай", new BigDecimal("150"), "expense", "Доп.",
                LocalDate.now(), 1, null);

        TransactionResponse resp = transactionService.update(1, 10, req);

        assertEquals("Чай", resp.name());
        assertEquals(0, new BigDecimal("150").compareTo(resp.amount()));
    }

    // ----- DELETE -----

    @Test
    void delete_whenNotFound_throws404() {
        when(transactionRepository.findByIdAndUserId(100, 1)).thenReturn(Optional.empty());
        assertThrows(ResponseStatusException.class, () -> transactionService.delete(1, 100));
    }

    @Test
    void delete_success_deletes() {
        TransactionEntity existing = sampleTx();
        when(transactionRepository.findByIdAndUserId(10, 1)).thenReturn(Optional.of(existing));

        transactionService.delete(1, 10);

        verify(transactionRepository).delete(existing);
    }

    // ----- REPLACE TAG ASSIGNMENT -----

    @Test
    void replaceTagAssignment_clearsAndAssignsNewIds() {
        when(tagRepository.findByIdAndUserId(5, 1)).thenReturn(Optional.of(userTag(5)));

        transactionService.replaceTagAssignment(1, 5, List.of(10, 11));

        verify(transactionRepository).clearTagFromUserTransactions(5, 1);
        verify(transactionRepository).updateTagForUserTransactions(List.of(10, 11), 5, 1);
    }

    @Test
    void replaceTagAssignment_withEmptyIds_onlyClears() {
        when(tagRepository.findByIdAndUserId(5, 1)).thenReturn(Optional.of(userTag(5)));

        transactionService.replaceTagAssignment(1, 5, List.of());

        verify(transactionRepository).clearTagFromUserTransactions(5, 1);
        verify(transactionRepository, never()).updateTagForUserTransactions(any(), anyInt(), anyInt());
    }
}
