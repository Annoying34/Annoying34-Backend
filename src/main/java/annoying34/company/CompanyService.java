package annoying34.company;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class CompanyService {

    private CompanyDao companyDao;

    @Autowired
    public CompanyService(CompanyDao companyDao) {
        this.companyDao = companyDao;
    }

    public List<Company> getCompanies() {
        List<Company> companyList = new ArrayList<>();

        companyDao.findAll().forEach( company -> companyList.add(company));

        //TODO init data elsewhere
        if (companyList.isEmpty()) {
            createDemoCompanyData();
            companyDao.findAll().forEach( company -> companyList.add(company));
        }

        return companyList;
    }

    //TODO implement search :-)
    public List<Company> getCompanies(CompanySearch search) {
        List<Company> companyList = new ArrayList<>();
        companyDao.findAll().forEach( company -> companyList.add(company));
        companyList.add(new Company("fizzbuzz", search.getEmail(), "", true));

        return companyList;
    }

    private void createDemoCompanyData() {
        List<Company> companyList = new ArrayList<>();
        companyList.add(new Company("Amazon Europe Core S.à r.l.(DE)", "impressum@amazon.de", "", false));
        companyList.add(new Company("Heise Medien GmbH & Co. KG", "webmaster@heise.de", "", false));
        companyList.add(new Company("1&1 Mail & Media GmbH", "gmx@gmxnet.de", "", false));
        companyList.add(new Company("MediaMarkt E-Business GmbH", "onlineshop@mediamarkt.de", "", false));
        companyList.add(new Company("Telekom Deutschland GmbH", "impressum@telekom.de", "", false));

        companyDao.save(companyList);
    }
}