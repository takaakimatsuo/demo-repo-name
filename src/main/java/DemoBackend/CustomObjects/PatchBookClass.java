package DemoBackend.CustomObjects;

public class PatchBookClass {
    String borrower;
    int status;
    public void setBorrower(String borrower){
        this.borrower = borrower;
    }
    public String getBorrower(){
        return borrower;
    }

    public void setStatus(int id){
        this.status = status;
    }
    public int getStatus(){
        return status;
    }
}
