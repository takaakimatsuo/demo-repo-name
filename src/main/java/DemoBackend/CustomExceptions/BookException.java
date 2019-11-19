package DemoBackend.CustomExceptions;

public class BookException extends Exception {
    public BookException(String errorMessage){
        super(errorMessage);
    }
}
