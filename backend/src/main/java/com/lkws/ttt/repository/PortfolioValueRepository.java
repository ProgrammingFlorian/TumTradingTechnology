package com.lkws.ttt.repository;

import com.lkws.ttt.model.PortfolioValue;
import org.springframework.data.repository.CrudRepository;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Optional;

public interface PortfolioValueRepository extends CrudRepository<PortfolioValue, Long> {

    Optional<List<PortfolioValue>> findPortfolioValueByUserId(@NotNull(message = "userId not found") long userId);

    Optional<List<PortfolioValue>> findPortfolioValueByUserIdAndDateTimeAfter(@NotNull(message = "userId not found") long userId, String dateTime);
}
