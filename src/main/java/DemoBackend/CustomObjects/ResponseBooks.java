package DemoBackend.CustomObjects;

import java.util.List;



public class ResponseBooks {


    private List<BookClass> books;
    private ResponseHeader msg;


    public ResponseBooks(List<BookClass> books,ResponseHeader msg){
        this.books = books;
        this.msg = msg;
    }

    public ResponseBooks(List<BookClass> books){
        this.books = books;
    }

    public ResponseBooks(ResponseHeader msg){
        this.msg = msg;
    }

    public ResponseBooks(){
    }


    public List<BookClass> getBooks(){
        return books;
    }
    public void setBooks(List<BookClass> b){
        books = b;
    }
    public ResponseHeader getResponseHeader(){
        return msg;
    }
    public void setResponseHeader(ResponseHeader m){
        this.msg = m;
    }
}
