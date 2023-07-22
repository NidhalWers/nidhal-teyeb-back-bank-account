package exalt.kata.application.contracts;

public class PublicErrorMessages
{
    public static String ParameterIsRequiredMessage(String param)
    {
        return String.format("The %s field is required", param);
    }
}
