package org.alfresco.po.share.user.profile;

public class CompanyDetails
{
    private String name;
    private String address1;
    private String address2;
    private String address3;
    private String postCode;
    private String telephone;
    private String fax;
    private String email;

    public CompanyDetails(String name, String address1, String address2, String address3, String postCode,
                          String telephone, String fax, String email)
    {
        this.setName(name);
        this.setAddress1(address1);
        this.setAddress2(address2);
        this.setAddress3(address3);
        this.setPostCode(postCode);
        this.setTelephone(telephone);
        this.setFax(fax);
        this.setEmail(email);
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getAddress1()
    {
        return address1;
    }

    public void setAddress1(String address1)
    {
        this.address1 = address1;
    }

    public String getAddress2()
    {
        return address2;
    }

    public void setAddress2(String address2)
    {
        this.address2 = address2;
    }

    public String getAddress3()
    {
        return address3;
    }

    public void setAddress3(String address3)
    {
        this.address3 = address3;
    }

    public String getPostCode()
    {
        return postCode;
    }

    public void setPostCode(String postCode)
    {
        this.postCode = postCode;
    }

    public String getTelephone()
    {
        return telephone;
    }

    public void setTelephone(String telephone)
    {
        this.telephone = telephone;
    }

    public String getFax()
    {
        return fax;
    }

    public void setFax(String fax)
    {
        this.fax = fax;
    }

    public String getEmail()
    {
        return email;
    }

    public void setEmail(String email)
    {
        this.email = email;
    }
}
