package JavaCA.controller;

import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import JavaCA.model.Product;
import JavaCA.model.Supplier;
import JavaCA.model.TransactionDetail;
import JavaCA.service.ProductServiceImpl;
import JavaCA.service.SupplierService;
import JavaCA.service.SupplierServiceImpl;
import JavaCA.service.TransactionDetailsService;

@Controller
@RequestMapping("/report")
public class ReportController 
{
	private ProductServiceImpl pservice;
	private TransactionDetailsService tdservice;
	private SupplierService sservice;
		
	@Autowired
	public void setServices(ProductServiceImpl pservice, TransactionDetailsService tdservice, SupplierServiceImpl sservice) 
	{
		this.pservice = pservice;
		this.tdservice = tdservice;
		this.sservice = sservice;
	}
	
	@RequestMapping(value={"/usage"}, method=RequestMethod.GET)
	public String usageReportForProduct(Model model)
	{
		//model.addAttribute("product", new Product());
		return "report/usage";
	}
	
	@RequestMapping(value={"/search"}, method=RequestMethod.POST)
	public String searchUsageReportForProduct(RedirectAttributes model, @RequestParam long id, @RequestParam String fromDate, 
						   @RequestParam String toDate)
	{
		if (pservice.findProduct(id) == null)
			model.addFlashAttribute("errorMsgId", "There is no product with this Id");
		else
			model.addFlashAttribute("id", id);
		if (!TransactionDetailsService.isValidDateFormat(fromDate))
			model.addFlashAttribute("errorMsgFromDate", "Input must be in the format of yyyy-MM-dd");
		else
		{
			if (!fromDate.isBlank())
				model.addFlashAttribute("fromDate", Date.valueOf(fromDate));
		}
		if (!TransactionDetailsService.isValidDateFormat(toDate))
			model.addFlashAttribute("errorMsgToDate", "Input must be in the format of yyyy-MM-dd");
		else
		{
			if (!toDate.isBlank())
				model.addFlashAttribute("toDate", Date.valueOf(toDate));
		}
		if (pservice.findProduct(id) == null || !TransactionDetailsService.isValidDateFormat(fromDate) || !TransactionDetailsService.isValidDateFormat(toDate))
			return "redirect:/report/usage/";
		model.addFlashAttribute("search", true);
		return "redirect:/report/usage/" + id;
	}
	
	@RequestMapping(value={"/usage/{id}"}, method=RequestMethod.GET)
	public String usageReportForProductId(Model model, @PathVariable long id)
	{
		String output = "report/usage";
		List<TransactionDetail> transactionDetailsForThisProduct = tdservice.findTransactionDetailsByProductId(id);
		if (model.getAttribute("fromDate") == null && model.getAttribute("toDate") == null)
		{
			model.addAttribute("transactiondetails", transactionDetailsForThisProduct);
		}
		else
		{
			if(model.getAttribute("fromDate") != null && model.getAttribute("toDate") != null)
			{
				Date fromDate = (Date) model.getAttribute("fromDate");
				Date toDate = (Date) model.getAttribute("toDate");
				transactionDetailsForThisProduct = transactionDetailsForThisProduct.stream().filter(x -> 
																	x.getDate().compareTo(toDate) <= 0
																	&& x.getDate().compareTo(fromDate) >= 0)
														   			.collect(Collectors.toList());
				model.addAttribute("transactiondetails", transactionDetailsForThisProduct);
			}
			else if (model.getAttribute("fromDate") != null)
			{
				Date fromDate = (Date) model.getAttribute("fromDate");
				transactionDetailsForThisProduct = transactionDetailsForThisProduct.stream().filter(x -> 
																	x.getDate().compareTo(fromDate) >= 0)
																	.collect(Collectors.toList());
				model.addAttribute("transactiondetails", transactionDetailsForThisProduct);
			}
			else
			{
				Date toDate = (Date) model.getAttribute("toDate");
				transactionDetailsForThisProduct = transactionDetailsForThisProduct.stream().filter(x -> 
																	x.getDate().compareTo(toDate) <= 0)
																	.collect(Collectors.toList());
				model.addAttribute("transactiondetails", transactionDetailsForThisProduct);
			}
		}
		model.addAttribute("product", pservice.findProduct(id));
		if (model.containsAttribute("print"))
			output = "report/usagereportprint";
			model.addAttribute("timeOfReport", Date.valueOf(LocalDate.now()));
		return output;
	}
	
	@RequestMapping(value={"/usage/{id}/print"}, method=RequestMethod.GET)
	public String printUsageReportForProductId(@PathVariable long id, RedirectAttributes model)
	{
		model.addFlashAttribute("print", true);
		return "redirect:/report/usage/" + id;
	}
	
	@RequestMapping(value={"/reorder"}, method=RequestMethod.GET)
	public String reorderReport(Model model)
	{
		String output = "report/reorder";
		double grandTotal = 0;
		List<List<Product>> listOfListsOfProduct = new ArrayList<>();
		List<Supplier> suppliers = sservice.findAllSuppliers();
		for (Supplier s:suppliers)
		{
			List<Product> productsThatRequireReorderBySupplier = pservice.findAllProducts().stream()
					   .filter(x -> x.getQuantity() <= x.getReorderLevel())
					   .filter(x -> x.getSupplier().getId() == s.getId())
					   .collect(Collectors.toList());
			if (productsThatRequireReorderBySupplier.size() > 0)
			{
				listOfListsOfProduct.add(productsThatRequireReorderBySupplier);
				grandTotal = grandTotal + productsThatRequireReorderBySupplier.stream()
				.mapToDouble(x -> x.getOriginalPrice() * (x.getReorderLevel() - x.getQuantity() + x.getMinOrderQty()))
				.sum();
			}
		}
		model.addAttribute("productsThatRequireReorder", listOfListsOfProduct);
		model.addAttribute("grandTotal", grandTotal);
		if (model.containsAttribute("print"))
			output = "report/reorderreportprint";
			model.addAttribute("timeOfReport", Date.valueOf(LocalDate.now()));
		return output;
	}
	
	@RequestMapping(value={"/reorder/print"}, method=RequestMethod.GET)
	public String printReorderReport(RedirectAttributes model)
	{
		model.addFlashAttribute("print", true);
		return "redirect:/report/reorder/";
	}
}