package com.lkws.ttt.services;

import com.lkws.ttt.model.InvalidTransactionException;
import com.lkws.ttt.model.PortfolioShare;
import com.lkws.ttt.model.ShareNotFoundException;
import com.lkws.ttt.model.UserNotFoundException;
import com.lkws.ttt.repository.PortfolioShareRepository;
import com.lkws.ttt.repository.ShareInfoRepository;
import com.lkws.ttt.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.lkws.ttt.testvalues.PortfolioTestValues.*;
import static com.lkws.ttt.testvalues.UserTestValues.brokeUser;
import static com.lkws.ttt.testvalues.UserTestValues.user2;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
public class PortfolioServiceTest {

    @InjectMocks
    private PortfolioService portfolioService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PortfolioShareRepository portfolioShareRepository;

    @Mock
    private ShareInfoRepository shareInfoRepository;


    @Test
    void testBuyShares() {
        // given
        when(userRepository.findById(4321L)).thenReturn(Optional.ofNullable(user2));
        when(shareInfoRepository.findById(portfolioShareToBuy1.shareId())).thenReturn(Optional.ofNullable(dummyShareInfo));
        when(portfolioShareRepository.save(boughtPortfolioShare1)).thenReturn(boughtPortfolioShare1);
        // when
        var boughtShare = portfolioService.buyShares(portfolioShareToBuy1);
        // then
        assertEquals(boughtPortfolioShare1, boughtShare);
    }

    @Test
    void testBuySharesWithInvalidShare() {
        // given
        when(userRepository.findById(4321L)).thenReturn(Optional.ofNullable(user2));
        when(shareInfoRepository.findById(portfolioShareToBuy1.shareId())).thenReturn(Optional.empty());
        // when then
        assertThrows(ShareNotFoundException.class, () -> portfolioService.buyShares(portfolioShareToBuy1));
    }

    @Test
    void testBuySharesWithInvalidUser() {
        // given
        when(userRepository.findById(4321L)).thenReturn(Optional.empty());
        // when then
        assertThrows(UserNotFoundException.class, () -> portfolioService.buyShares(portfolioShareToBuy1));
    }

    @Test
    void testBuySharesInvalidAmount() {
        // given
        when(userRepository.findById(4321L)).thenReturn(Optional.ofNullable(user2));
        when(shareInfoRepository.findById(invalidPortfolioShareToBuy1.shareId())).thenReturn(Optional.ofNullable(dummyShareInfo));
        // when then
        assertThrows(InvalidTransactionException.class, () -> portfolioService.buyShares(invalidPortfolioShareToBuy1));
    }

    @Test
    void testBuySharesWithNotEnoughMoney() {
        // given
        when(userRepository.findById(4321L)).thenReturn(Optional.ofNullable(brokeUser));
        when(shareInfoRepository.findById(invalidPortfolioShareToBuy1.shareId())).thenReturn(Optional.ofNullable(dummyShareInfo));

        // when then
        assertThrows(InvalidTransactionException.class, () -> portfolioService.buyShares(portfolioShareToBuy1));
    }


    @Test
    void testBuySharesInvalidOrderType() {
        // given
        when(userRepository.findById(4321L)).thenReturn(Optional.ofNullable(user2));
        when(shareInfoRepository.findById(invalidPortfolioShareToBuy2.shareId())).thenReturn(Optional.ofNullable(dummyShareInfo));
        // when then
        assertThrows(InvalidTransactionException.class, () -> portfolioService.buyShares(invalidPortfolioShareToBuy2));
    }

    @Test
    void testSellShares() {
        // given
        when(userRepository.findById(4321L)).thenReturn(Optional.ofNullable(user2));
        when(shareInfoRepository.findById(portfolioShareDTOtoSell1.shareId())).thenReturn(Optional.ofNullable(dummyShareInfo));
        List<PortfolioShare> list = new ArrayList<>();
        list.add(boughtPortfolioShare1);
        when(portfolioShareRepository.findPortfolioSharesByUserId(4321L)).thenReturn(Optional.of(list));
        when(portfolioShareRepository.save(soldPortfolioShare1)).thenReturn(soldPortfolioShare1);
        // when
        var soldShare = portfolioService.sellShares(portfolioShareDTOtoSell1);
        // then
        assertEquals(soldPortfolioShare1, soldShare);
    }

    @Test
    void testSellSharesInvalid() {
        // given
        when(userRepository.findById(4321L)).thenReturn(Optional.ofNullable(user2));
        when(shareInfoRepository.findById(portfolioShareDTOtoSell1.shareId())).thenReturn(Optional.ofNullable(dummyShareInfo));
        List<PortfolioShare> list = new ArrayList<>();
        when(portfolioShareRepository.findPortfolioSharesByUserId(4321L)).thenReturn(Optional.of(list));
        when(portfolioShareRepository.save(soldPortfolioShare1)).thenReturn(soldPortfolioShare1);
        // when then
        assertThrows(InvalidTransactionException.class, () -> portfolioService.sellShares(portfolioShareDTOtoSell1));
    }
}
