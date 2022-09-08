package com.lkws.ttt.repository;

import com.lkws.ttt.model.DailyPrice;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Optional;

public interface DailyPriceRepository extends CrudRepository<DailyPrice, Long> {

    Optional<List<DailyPrice>> findDailyPriceByShareId(long id);

    Optional<List<DailyPrice>> findDailyPriceByShareIdAndDateAfter(@NotNull(message = "shareId not found") long shareId, String date);

    @Query(
            value = "SELECT max(date) FROM daily_price GROUP BY share_id",
            nativeQuery = true)
    Optional<List<String>> findNewestDates();

    @Query(
            value = "SELECT min(date) FROM daily_price WHERE date IN (SELECT max(date) FROM daily_price GROUP BY share_id)",
            nativeQuery = true)
    Optional<String> findFillUpDate();


    @Modifying(flushAutomatically = true)
    @Transactional
    @Query(
            value = "DELETE FROM daily_price WHERE date like '2022-02-01'",
            nativeQuery = true
    )
    void deleteRandom();

    @Modifying(flushAutomatically = true)
    @Transactional
    void deleteByDateAfter(String date);

    @Query(
            value = "SELECT DISTINCT share_id FROM daily_price",
            nativeQuery = true
    )
    Optional<List<Long>> findAllShareIds();

    @Query(
            value = "SELECT COUNT(DISTINCT share_id) FROM daily_price",
            nativeQuery = true
    )
    Optional<Integer> countAllShareIds();
}