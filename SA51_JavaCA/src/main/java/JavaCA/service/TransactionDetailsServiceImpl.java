package JavaCA.service;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import JavaCA.model.TransactionDetail;
import JavaCA.repo.TransactionDetailRepository;

@Service
@Transactional
public class TransactionDetailsServiceImpl implements TransactionDetailsService {
	
	@Autowired
	private TransactionDetailRepository transDRepo;
	
	@Override
	public ArrayList<TransactionDetail> findTransactionDetailsByProductId(long productId) {
		return (ArrayList<TransactionDetail>) transDRepo.findTransactionDetailsByProductId(productId);
	}
	
	@Override
	public void saveTransactionDetail(TransactionDetail transactionDetail) {
		transDRepo.save(transactionDetail);		
	}
	
	@Override
	public void deleteTransactionDetail(TransactionDetail transactionDetail) {
		transDRepo.delete(transactionDetail);
	}

}
