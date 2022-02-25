package operations.implementations;

import enums.Designation;
import enums.Qualification;
import exceptions.InvalidOperationException;
import exceptions.NotAuthorizedException;
import models.*;
import operations.interfaces.AdministrativeOperations;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;

public class AdministrativeOperationsImpl implements AdministrativeOperations {

    @Override
    public void loadProductsFromExcelFile(Store company, Staff staff, String filePath) throws NotAuthorizedException {
        if (staff.getDesignation().equals(Designation.MANAGER)) {
            try {
                XSSFWorkbook workbook = new XSSFWorkbook(filePath);
                XSSFSheet xssfSheet = workbook.getSheetAt(0);
                int lastRowIndex = xssfSheet.getLastRowNum();

                for (int i = 1; i <= lastRowIndex; i++) {
                    XSSFRow row = xssfSheet.getRow(i);
                    company.getGoods().add( new Product(
                                row.getCell(1).getStringCellValue(),
                                row.getCell(2).getStringCellValue(),
                                row.getCell(3).getStringCellValue(),
                                row.getCell(4).getNumericCellValue(),
                                (int) row.getCell(5).getNumericCellValue()
                    ));
                }
            } catch (IOException e) {e.printStackTrace();}
        } else throw new NotAuthorizedException ("Only Manager can load product into store!");
    }

    @Override
    public void sellProductsInCart(Store company, Staff staff, Customer customer) throws NotAuthorizedException, InvalidOperationException {
        StringBuilder receiptBody = new StringBuilder();
        if (!staff.getDesignation().equals(Designation.CASHIER)) throw new NotAuthorizedException("Only Cashiers can sell and dispense receipts to customers!");
        else if (!customer.isCheckOut()) throw new InvalidOperationException("Customer has not checked out!");
        else {
            String heading = "\t -----------" + company.getStoreName() + "-----------"
                    + "\n Product name ---- " + "Price ---- "
                    + "Units ---- " + " Total Price";
            receiptBody.append(heading);
            int snNum = 1;

            for (var product : customer.getCart().entrySet()) {
                String productID = product.getKey();
                int quantityBought = product.getValue();
                Product companyProduct = company.getGoods().get(productID);
                reduceCompanyProduct(companyProduct, quantityBought);
                receiptBody.append("\n")
                        .append(snNum)
                        .append(". ")
                        .append(generateReceiptRow(companyProduct, quantityBought));
            }


            customer.setCheckOut(false);
            customer.setWallet(customer.getWallet() - customer.getTotalGoodsPrice());
            company.setStoreAccount(customer.getTotalGoodsPrice() + company.getStoreAccount());
            createAFileToSaveData("receipt" + new Date().getTime() + ".txt", receiptBody.toString());
            customer.getCart().clear();
        }

    }

    @Override
    public void hireCashier(Store store, Staff staff, Applicant applicant) throws NotAuthorizedException, InvalidOperationException {
        if (!staff.getDesignation().equals(Designation.MANAGER))
            throw new NotAuthorizedException ("Only the Manager can hire an applicant!");
        if (!store.getApplicantList().contains(applicant))
            throw new InvalidOperationException("You can't hire applicant that has not applied!");

        if (applicant.getQualification().equals(Qualification.SSCE))
            store.getStaffList().add(new Staff(applicant.getLastName(),
                                                applicant.getFirstName(),
                                                applicant.getGender(),
                                                Designation.CASHIER));
            store.getApplicantList().remove(applicant);


    }

    private void reduceCompanyProduct(Product companyProduct, int quantityBought){
        companyProduct.setProductQuantity(companyProduct.getProductQuantity() - quantityBought);

    }

    private void createAFileToSaveData(String filePath, String fileContent){
        try {
            FileWriter fileWriter = new FileWriter(filePath);
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
            bufferedWriter.write(fileContent);
            bufferedWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String generateReceiptRow(Product product,  int quantityBought){
        return product.getProductName() + "\t | "
                +  product.getProductPrice() + "\t | "
                + quantityBought + "\t |  "
                + product.getProductPrice() * quantityBought;
    }

}


