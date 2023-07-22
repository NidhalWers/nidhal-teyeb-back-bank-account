package exalt.kata;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@Import(ApplicationConfiguration.class)
public class BankAccountApplication
{
    public static void main(String[] args)
    {
        SpringApplication.run(BankAccountApplication.class, args);
    }
}
