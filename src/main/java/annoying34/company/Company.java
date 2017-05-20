package annoying34.company;


public class Company {

    private String name;
    private String email;
    private String imageURL = "";
    private boolean selected = false;

    public Company(String name, String email, String imageURL, boolean selected) {
        this.name = name;
        this.email = email;
        this.imageURL = imageURL;
        this.selected = selected;
    }

    public Company(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Company company = (Company) o;

        if (selected != company.selected) return false;
        if (name != null ? !name.equals(company.name) : company.name != null) return false;
        if (email != null ? !email.equals(company.email) : company.email != null) return false;
        return imageURL != null ? imageURL.equals(company.imageURL) : company.imageURL == null;
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (email != null ? email.hashCode() : 0);
        result = 31 * result + (imageURL != null ? imageURL.hashCode() : 0);
        result = 31 * result + (selected ? 1 : 0);
        return result;
    }
}
