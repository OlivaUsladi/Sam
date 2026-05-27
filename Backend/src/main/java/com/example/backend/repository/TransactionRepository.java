package com.example.backend.repository;

import com.example.backend.entity.TransactionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface TransactionRepository extends JpaRepository<TransactionEntity, Integer> {

    List<TransactionEntity> findByUserIdOrderByTransactionDateDescIdDesc(Integer userId);

    List<TransactionEntity> findByUserIdAndTypeOrderByTransactionDateDescIdDesc(
            Integer userId, String type);

    List<TransactionEntity> findByUserIdAndTransactionDateBetweenOrderByTransactionDateDescIdDesc(
            Integer userId, LocalDate start, LocalDate end);

    List<TransactionEntity> findByUserIdAndTagIdOrderByTransactionDateDescIdDesc(
            Integer userId, Integer tagId);

    List<TransactionEntity> findByUserIdAndTypeAndTransactionDateBetweenOrderByTransactionDateDescIdDesc(
            Integer userId, String type, LocalDate start, LocalDate end);

    Optional<TransactionEntity> findByIdAndUserId(Integer id, Integer userId);

    @Query("update TransactionEntity t set t.tagId = :tagId where t.id in :ids and t.userId = :userId")
    @org.springframework.data.jpa.repository.Modifying(clearAutomatically = true)
    int updateTagForUserTransactions(@Param("ids") List<Integer> ids,
                                     @Param("tagId") Integer tagId,
                                     @Param("userId") Integer userId);

    @Query("update TransactionEntity t set t.tagId = null where t.tagId = :tagId and t.userId = :userId")
    @org.springframework.data.jpa.repository.Modifying(clearAutomatically = true)
    int clearTagFromUserTransactions(@Param("tagId") Integer tagId,
                                     @Param("userId") Integer userId);


    @Query("select (count(t) > 0) from TransactionEntity t " +
           "where t.userId = :userId " +
           "  and t.sourceId = :sourceId " +
           "  and t.type = :type " +
           "  and t.transactionDate = :date " +
           "  and lower(t.name) = lower(:name) " +
           "  and t.amount = :amount")
    boolean existsDuplicate(@Param("userId")   Integer userId,
                            @Param("sourceId") Integer sourceId,
                            @Param("type")     String type,
                            @Param("date")     LocalDate date,
                            @Param("name")     String name,
                            @Param("amount")   java.math.BigDecimal amount);
}
