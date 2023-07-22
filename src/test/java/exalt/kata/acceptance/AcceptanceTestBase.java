package exalt.kata.acceptance;

import exalt.kata.BankAccountApplication;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = BankAccountApplication.class)
@ActiveProfiles("acceptance-test")
public class AcceptanceTestBase
{
}
