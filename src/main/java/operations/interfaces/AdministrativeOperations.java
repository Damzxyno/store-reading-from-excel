package operations.interfaces;

import exceptions.InvalidOperationException;
import exceptions.NotAuthorizedException;
import exceptions.OutOfStockException;
import models.*;

public interface AdministrativeOperations extends CommonOperations {
    void loadProductsFromExcelFile(Store company, Staff staff, String filePath) throws NotAuthorizedException;
    void sellProductsInCart(Store company, Staff staff, Customer customer) throws OutOfStockException, NotAuthorizedException, InvalidOperationException;
    void hireCashier(Store store, Staff staff, Applicant applicant) throws NotAuthorizedException, InvalidOperationException;
}
