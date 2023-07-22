package exalt.kata.domain;

public class OperationResult
{
    private String code;
    private String message;
    public static final OperationResult Success = new OperationResult("00000", "Success");
    public static final OperationResult GenericOperationError = new OperationResult("01001", "Generic Operation error");
    public static final OperationResult AuthorizationKyCError = new OperationResult("01002", "Client not authorized & compliant");

    private OperationResult(String code, String message)
    {
        this.code = code;
        this.message = message;
    }

    public static OperationResult createFromDatabase(String code, String message)
    {
        return new OperationResult(code, message);
    }

    public String code()
    {
        return code;
    }

    public String message()
    {
        return message;
    }
}
