package DemoBackend;

public class UpdateBookStatus {

    private int book_id;
    private int status;
    private String phone_number;

    public void setBook_id(int book_id){
        this.book_id = book_id;
    }
    public int getBook_id(){
        return book_id;
    }

    public void setStatus(int status){
        this.status = status;
    }
    public int getStatus(){
        return status;
    }

    public String getPhone_number() {
        return phone_number;
    }
    public void setPhone_number(String phone_number) {
        this.phone_number = phone_number;
    }


}
