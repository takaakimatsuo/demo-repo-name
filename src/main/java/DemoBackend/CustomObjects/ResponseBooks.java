package DemoBackend.CustomObjects;

import java.util.ArrayList;
import java.util.List;

public class ResponseBooks {

    private List<BookClass> books = new ArrayList<>();
    private ResponseHeader msg = new ResponseHeader();

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
