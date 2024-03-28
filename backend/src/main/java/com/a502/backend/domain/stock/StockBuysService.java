package com.a502.backend.domain.stock;

import com.a502.backend.application.entity.*;
import com.a502.backend.global.error.BusinessException;
import com.a502.backend.global.exception.ErrorCode;
import jakarta.persistence.LockModeType;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
@Service
public class StockBuysService {
	private final StockBuysRepository stockBuysRepository;

	public StockBuy findById(int id) {
		return stockBuysRepository.findById(id).orElseThrow(
				() -> BusinessException.of(ErrorCode.API_ERROR_STOCKBUY_NOT_EXIST));
	}

	@Transactional
	public StockBuy save(User user, Stock stock, int price, int cntTotal, Code code) {
		return stockBuysRepository.save(StockBuy.builder()
				.user(user)
				.price(price)
				.cntTotal(cntTotal)
				.stock(stock)
				.price(price)
				.code(code)
				.build());
	}

	public List<StockBuy> getBuyOrderList(int id, int cnt, LocalDateTime localDateTime) {
		return stockBuysRepository.findAllByStock_IdAndCntNotGreaterThanAndCreatedAtGreaterThan(id, cnt, localDateTime);
	}
//	@Transactional
//	@Lock(LockModeType.PESSIMISTIC_WRITE)
	public List<StockBuy> findTransactionList(Stock stock, int price) {
		return stockBuysRepository.findAllByStockAndPriceOrderByCreatedAtAsc(stock, price).orElse(null);
	}

	@Transactional
//	@Lock(LockModeType.PESSIMISTIC_WRITE)
	public void stockBuy(StockBuy stockBuy, int cnt, Code code) {
//		StockBuy sb = stockBuysRepository.findById(stockBuy.getId()).orElse(null);
		int cntNot = stockBuy.getCntNot();
		if (cntNot - cnt < 0)
			throw BusinessException.of(ErrorCode.API_ERROR_STOCKBUY_STOCK_IS_NOT_ENOUGH);
		stockBuy.setCntNot(cntNot - cnt);
        if (cntNot - cnt == 0)
			stockBuy.updateCode(code);
		stockBuysRepository.saveAndFlush(stockBuy);
	}

	public List<StockBuy> getTodayTransactions(Stock stock, LocalDateTime localDateTime) {
		return stockBuysRepository.findAllByStockAndCreatedAtGreaterThan(stock, localDateTime).orElseThrow(() -> BusinessException.of(ErrorCode.API_ERROR_STOCKBUY_NOT_EXIST));
	}

	public List<StockBuy> getWaitingStockOrders(User user, Code code, LocalDateTime localDateTime, int cnt){
		return stockBuysRepository.findAllByUserAndCodeAndCreatedAtGreaterThanAndCntNotGreaterThan(user, code, localDateTime, cnt).orElseThrow(()->BusinessException.of(ErrorCode.API_ERROR_STOCKBUY_NOT_EXIST));
	}

	@Transactional
	public int getStockBuyWaitingList(User user, Stock stock, Code code){
		List<StockBuy> list = stockBuysRepository.findAllByUserAndStockAndCode(user, stock, code);
		int price = 0;
		for(StockBuy stockBuy : list){
			price += stockBuy.getPrice() * stockBuy.getCntNot();
		}
		return price;
	}

	public List<StockBuy> getStockTransListByStock(Stock stock){
		return stockBuysRepository.findAllByStock(stock);
	}
}
