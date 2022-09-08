package com.lkws.ttt.repository;

import com.lkws.ttt.model.HourlyPrice;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;


import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Optional;

public interface HourlyPriceRepository extends CrudRepository<HourlyPrice, Long> {


    Optional<List<HourlyPrice>> findHourlyPriceByShareId(long id);

    Optional<List<HourlyPrice>> findHourlyPriceByShareIdAndDateTimeAfter(@NotNull(message = "shareId not found") long shareId, String dateTime);

    @Query(
            value = "SELECT date_time FROM hourly_price ORDER BY date_time LIMIT 1",
            nativeQuery = true
    )
    Optional<String> getFirstDate();

    @Query(
            value = "SELECT max(date_time) FROM hourly_price GROUP BY share_id",
            nativeQuery = true)
    Optional<List<String>> findNewestDates();

    @Query(
            value = "SELECT min(date_time) FROM hourly_price WHERE date_time IN (SELECT max(date_time) FROM hourly_price GROUP BY share_id)",
            nativeQuery = true)
    Optional<String> findFillUpDate();

   @Query (
           value ="SELECT * FROM hourly_price WHERE share_id = :searchId AND date_time >= :dateParam",
           nativeQuery = true
   )
   Optional<List<HourlyPrice>> findAllDatesFromAndAfterForShare(@Param("dateParam") String dateParam, @Param("searchId") Long searchId);

    @Query (
            value = "SELECT DISTINCT share_id FROM hourly_price",
            nativeQuery = true
    )
    Optional<List<Long>> findAllShareIds();

    @Query (
            value = "SELECT COUNT(DISTINCT share_id) FROM hourly_price",
            nativeQuery = true
    )
    Optional<Integer> countAllShareIds();
}