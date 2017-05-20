package annoying34.company;

import java.util.ArrayList;
import java.util.List;

public class CompanyService {

    public static List<Company> getCompanies() {
        List<Company> companyList = new ArrayList<>();


        /*
        //TODO JPA DB support
        load data from db
        in the moment, we dont have a db, so create demo data
         */
        companyList.addAll(getDemoCompanyData());

        return companyList;
    }

    //TODO implement search :-)
    public static List<Company> getCompanies(CompanySearch search) {
        List<Company> companyList = new ArrayList<>();
        companyList.addAll(getDemoCompanyData());
        companyList.add(new Company("fizzbuzz", search.getEmail(), "", true));

        return companyList;
    }

    private static List<Company> getDemoCompanyData() {
        List<Company> companyList = new ArrayList<>();
        companyList.add(new Company("Amazon Europe Core S.à r.l.(DE)", "impressum@amazon.de", "", false));
        companyList.add(new Company("Heise Medien GmbH & Co. KG", "webmaster@heise.de", "", false));
        companyList.add(new Company("1&1 Mail & Media GmbH", "gmx@gmxnet.de", "", false));
        companyList.add(new Company("MediaMarkt E-Business GmbH", "onlineshop@mediamarkt.de", "", false));
        companyList.add(new Company("Telekom Deutschland GmbH", "impressum@telekom.de", "", false));


        return companyList;
    }
}
