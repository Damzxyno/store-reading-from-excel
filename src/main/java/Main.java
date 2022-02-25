import enums.Designation;
import enums.Gender;
import exceptions.*;
import models.Customer;
import models.Staff;
import models.Store;
import operations.implementations.AdministrativeOperationsImpl;
import operations.implementations.CustomerOperationsImpl;
import operations.interfaces.AdministrativeOperations;

public class Main {
    public static void main(String[] args) throws NotAuthorizedException, OutOfStockException, StockDoesNotExistException, InsufficientFundException, InvalidOperationException {
        AdministrativeOperations administrativeOperations = new AdministrativeOperationsImpl();
        CustomerOperationsImpl customerOperations = new CustomerOperationsImpl();
        Store store = new Store("Damzxyno Food Store");
        Staff cashier = new Staff("Mary", "Ololade", Gender.FEMALE, Designation.CASHIER);
        Staff manager = new Staff("Wilfred", "Omokpo", Gender.MALE, Designation.MANAGER);
        Customer customer = new Customer("Sade", "Oluwaseyi", Gender.FEMALE);
        String excelFilePath = "src/main/resources/excel_files/damzxyno_food_store.xlsx";

        customerOperations.fundWallet(customer, 3_000_000);


        administrativeOperations.loadProductsFromExcelFile(store, manager, excelFilePath);
        customerOperations.addProductToCart(store, customer, "DAMFS-CER-001", 15);
        customerOperations.purchaseGoodsInCart(customer);
        administrativeOperations.sellProductsInCart(store, cashier, customer);
    }
}
