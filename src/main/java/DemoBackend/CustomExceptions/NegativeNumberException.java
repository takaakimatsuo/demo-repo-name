package DemoBackend.CustomExceptions;

public class NegativeNumberException extends Exception {
    public NegativeNumberException(String errorMessage){
        super(errorMessage);
    }
}
