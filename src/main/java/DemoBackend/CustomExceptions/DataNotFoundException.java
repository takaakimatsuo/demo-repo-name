package DemoBackend.CustomExceptions;

public class DataNotFoundException extends Exception {
    public DataNotFoundException(String errorMessage){
        super(errorMessage);
    }
}
