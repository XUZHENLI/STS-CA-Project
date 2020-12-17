package JavaCA.service;

import java.util.ArrayList;
import java.util.List;

import JavaCA.model.TransactionDetail;

public interface TransactionDetailsService {

	ArrayList<TransactionDetail> findTransactionDetailsByProductId(long productId);
	void saveTransactionDetail(TransactionDetail transactionDetail);
	void deleteTransactionDetail(TransactionDetail transactionDetail);
	List<TransactionDetail> findAllTransactionDetails();
}
