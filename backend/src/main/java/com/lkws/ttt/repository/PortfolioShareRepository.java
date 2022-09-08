package com.lkws.ttt.repository;

import com.lkws.ttt.model.PortfolioShare;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface PortfolioShareRepository extends CrudRepository<PortfolioShare, Long> {

    Optional<List<PortfolioShare>> findPortfolioSharesByUserId(long userId);
}
