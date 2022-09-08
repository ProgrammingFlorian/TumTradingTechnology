package com.lkws.ttt.repository;

import com.lkws.ttt.model.ShareInfo;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface ShareInfoRepository extends CrudRepository<ShareInfo, Long> {

    Optional<ShareInfo> findBySymbol(String symbol);

    Optional<ShareInfo> findShareInfoByIsin(String isin);

    @Query(
            value = "SELECT * FROM share_info ORDER BY symbol ASC",
            nativeQuery = true
    )
    Optional<List<ShareInfo>> findAllSharesOrderedBySymbol();

    @Query(
            value = "SELECT * FROM share_info ORDER BY price_change DESC LIMIT 1",
            nativeQuery = true
    )
    Optional<ShareInfo> findTopShare();

    @Query(
            value = "SELECT * FROM share_info ORDER BY price_change ASC LIMIT 1",
            nativeQuery = true
    )
    Optional<ShareInfo> findFlopShare();

}
