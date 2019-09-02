package soderstrand.martin.filereader.model;

public class Result {

    private String fileName;
    private Integer numberOfClients;
    private Integer numberOfSellers;
    private Integer salesIdOfBiggestSale;
    private String nameOfWorstSeller;

    public Result() {
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public Integer getNumberOfClients() {
        return numberOfClients;
    }

    public void setNumberOfClients(Integer numberOfClients) {
        this.numberOfClients = numberOfClients;
    }

    public Integer getNumberOfSellers() {
        return numberOfSellers;
    }

    public void setNumberOfSellers(Integer numberOfSellers) {
        this.numberOfSellers = numberOfSellers;
    }

    public Integer getSalesIdOfBiggestSale() {
        return salesIdOfBiggestSale;
    }

    public void setSalesIdOfBiggestSale(Integer salesIdOfBiggestSale) {
        this.salesIdOfBiggestSale = salesIdOfBiggestSale;
    }

    public String getNameOfWorstSeller() {
        return nameOfWorstSeller;
    }

    public void setNameOfWorstSeller(String nameOfWorstSeller) {
        this.nameOfWorstSeller = nameOfWorstSeller;
    }
}
