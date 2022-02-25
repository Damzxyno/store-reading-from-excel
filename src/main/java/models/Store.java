package models;

import lombok.*;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Setter
@Getter
@ToString

public class Store {
    private final String storeName;
    private double storeAccount = 0.0;
    private final List <Staff> staffList = new ArrayList<>();
    private final List <Applicant> applicantList = new ArrayList<>();
    private final Storage goods = new Storage();
}
