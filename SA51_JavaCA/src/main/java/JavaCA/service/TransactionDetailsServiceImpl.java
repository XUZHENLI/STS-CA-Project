package JavaCA.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import JavaCA.model.Transaction;
import JavaCA.model.TransactionDetail;
import JavaCA.repo.TransactionDetailRepository;
import JavaCA.repo.TransactionRepository;

@Service
@Transactional
public class TransactionDetailsServiceImpl implements TransactionDetailsService {
	
	@Autowired
	private TransactionRepository transRepo;
	
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

	@Override
	public List<TransactionDetail> findAllTransactionDetails() {
		// TODO Auto-generated method stub
		return transDRepo.findAll();
	}

	@Override
	public TransactionDetail findTransactionDetailById(long id) {
		// TODO Auto-generated method stub
		return transDRepo.findById(id).get();
	}

}
