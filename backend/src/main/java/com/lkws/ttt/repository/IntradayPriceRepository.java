package com.lkws.ttt.repository;

import com.lkws.ttt.model.IntradayPrice;
import org.springframework.data.repository.CrudRepository;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Optional;

public interface IntradayPriceRepository extends CrudRepository<IntradayPrice, Long> {

    Optional<List<IntradayPrice>> findIntradayPriceByShareIdAndDateAfter(@NotNull(message = "shareId not found") long shareId, @NotNull() String date);

    Optional<List<IntradayPrice>> findIntradayPriceByShareId(long shareId);
}