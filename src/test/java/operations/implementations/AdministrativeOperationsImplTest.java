package operations.implementations;

import enums.Designation;
import enums.Gender;
import enums.Qualification;
import exceptions.*;
import models.*;
import operations.interfaces.AdministrativeOperations;
import operations.interfaces.CustomerOperations;
import org.junit.Test;

import static org.junit.Assert.*;

public class AdministrativeOperationsImplTest {
    AdministrativeOperations administrativeOperations = new AdministrativeOperationsImpl();
    CustomerOperations customerOperations = new CustomerOperationsImpl();
    Store store = new Store("Damzxyno Food Store");
    Staff cashier = new Staff("Mary", "Ololade", Gender.FEMALE, Designation.CASHIER);
    Staff manager = new Staff("Wilfred", "Omokpo", Gender.MALE, Designation.MANAGER);
    Customer customer = new Customer("Sade", "Oluwaseyi", Gender.FEMALE);
    String excelFilePath = "src/main/resources/excel_files/damzxyno_food_store.xlsx";

    @Test
    public void testForUnAuthorizedLoadingOfProductFromExcelFile() {
        Exception exception = assertThrows(NotAuthorizedException.class,
                ()->administrativeOperations.loadProductsFromExcelFile(store, cashier,excelFilePath));
        assertEquals("Only Manager can load product into store!", exception.getMessage());
    }

    @Test
    public void storeStorageShouldContainTheQuantityOfGoodsFromExcelFileAfterSuccessfulLoading() throws NotAuthorizedException {
        administrativeOperations.loadProductsFromExcelFile(store, manager, excelFilePath);
        assertEquals(16, store.getGoods().size());
    }

    @Test
    public void storeStorageShouldContainGoodsFromExcelFileAfterSuccessfulLoading() throws NotAuthorizedException {
        administrativeOperations.loadProductsFromExcelFile(store, manager, excelFilePath);
        assertTrue(store.getGoods().contains("DAMFS-CER-001"));
        assertTrue(store.getGoods().contains("DAMFS-BEV-002"));
        assertTrue(store.getGoods().contains("DAMFS-BEV-003"));
        assertTrue(store.getGoods().contains("DAMFS-BEV-004"));
        assertTrue(store.getGoods().contains("DAMFS-CER-002"));
        assertTrue(store.getGoods().contains("DAMFS-LAN-001"));
        assertTrue(store.getGoods().contains("DAMFS-LAN-002"));
        assertTrue(store.getGoods().contains("DAMFS-COS-001"));
        assertTrue(store.getGoods().contains("DAMFS-COS-001"));
        assertTrue(store.getGoods().contains("DAMFS-COS-002"));
        assertTrue(store.getGoods().contains("DAMFS-CSP-001"));
        assertTrue(store.getGoods().contains("DAMFS-CSP-002"));
        assertTrue(store.getGoods().contains("DAMFS-CSP-003"));
        assertTrue(store.getGoods().contains("DAMFS-CSP-004"));
        assertTrue(store.getGoods().contains("DAMFS-CSP-005"));
        assertTrue(store.getGoods().contains("DAMFS-CSP-006"));
    }


    @Test
    public void testForUnAuthorizedPersonSellingToCustomer() throws NotAuthorizedException, OutOfStockException, StockDoesNotExistException{
        administrativeOperations.loadProductsFromExcelFile(store, manager, excelFilePath);
        customerOperations.addProductToCart(store, customer, "DAMFS-CSP-006", 9);

        assertThrows(NotAuthorizedException.class, ()-> administrativeOperations.sellProductsInCart(store, manager, customer));
    }

    @Test
    public void testForSellingToCustomerThatHasNotCheckedOut() throws NotAuthorizedException, OutOfStockException, StockDoesNotExistException{
        administrativeOperations.loadProductsFromExcelFile(store, manager, excelFilePath);
        customerOperations.addProductToCart(store, customer, "DAMFS-CSP-006", 9);

        assertThrows(InvalidOperationException.class, ()-> administrativeOperations.sellProductsInCart(store, cashier, customer));
    }

    @Test
    public void companyProductShouldReduceAfterPurchase() throws NotAuthorizedException, OutOfStockException, StockDoesNotExistException, InsufficientFundException, InvalidOperationException {
        administrativeOperations.loadProductsFromExcelFile(store, manager, excelFilePath);
        assertEquals(13, store.getGoods().get("DAMFS-CSP-006").getProductQuantity());
        customerOperations.fundWallet(customer, 10_000);
        customerOperations.addProductToCart(store, customer, "DAMFS-CSP-006", 9);
        customerOperations.purchaseGoodsInCart(customer);
        administrativeOperations.sellProductsInCart(store, cashier, customer);
        assertEquals(4, store.getGoods().get("DAMFS-CSP-006").getProductQuantity());
    }

    @Test
    public void staffCanSeeGoodsByCategory() throws NotAuthorizedException {
        administrativeOperations.loadProductsFromExcelFile(store, manager, excelFilePath);
        assertEquals( 2, administrativeOperations.viewProductByCategory(store, "Cereals").size());
        assertEquals( "Cereals", administrativeOperations.viewProductByCategory(store, "Cereals").get(0).getProductCategory());
        assertEquals( "Cereals", administrativeOperations.viewProductByCategory(store, "Cereals").get(1).getProductCategory());
    }

    @Test
    public void testForUnauthorizedStaffHiringAnApplicant(){
        Applicant applicant = new Applicant("Chisom", "Uchenna", Gender.FEMALE, Qualification.SSCE);
        Exception exception = assertThrows(NotAuthorizedException.class, () -> administrativeOperations.hireCashier(store, cashier, applicant));
        assertEquals("Only the Manager can hire an applicant!", exception.getMessage());
    }

    @Test
    public void testForHiringApplicantWhoseInfoIsNotInTheApplicantListOfCompany(){
        Applicant applicant = new Applicant("Udeme", "Evangel", Gender.FEMALE, Qualification.OND);
        Exception exception = assertThrows(InvalidOperationException.class, () -> administrativeOperations.hireCashier(store, manager, applicant));
        assertEquals("Applicant object exist but not in company's record", "You can't hire applicant that has not applied!", exception.getMessage());
    }

    @Test
    public void testForSuccessfulCreationOfStaffAndPuttingInCompanyList() throws InvalidOperationException, NotAuthorizedException {
        Applicant applicant = new Applicant("Ifeoluwa", "Olaseyi", Gender.MALE, Qualification.SSCE);
        assertTrue("Initially, company does not contain any staff", store.getStaffList().isEmpty());
        new ApplicantOperationImpl().apply(store,applicant);
        administrativeOperations.hireCashier(store, manager, applicant);
        assertEquals("Now the staffList should increase by 1", 1, store.getStaffList().size());
    }
}