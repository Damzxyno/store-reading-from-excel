package operations.interfaces;

import models.Product;
import models.Store;
import java.util.ArrayList;
import java.util.List;

public interface CommonOperations {

    default List<Product> viewProductByCategory(Store store, String category){
        List<Product> productArrayList = new ArrayList<>();
            for(int i = 0; i< store.getGoods().size(); i++){
                Product product = store.getGoods().get(i);
                if(product.getProductCategory().equalsIgnoreCase(category))
                    productArrayList.add(product);
            }
            return productArrayList;
    }
}
