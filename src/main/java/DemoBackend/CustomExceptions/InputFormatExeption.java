package DemoBackend.CustomExceptions;

public class InputFormatExeption extends Exception {
    public InputFormatExeption(String errorMessage){
        super(errorMessage);
    }
}
