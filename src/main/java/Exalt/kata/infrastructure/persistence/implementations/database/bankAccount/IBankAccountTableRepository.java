package exalt.kata.infrastructure.persistence.implementations.database.bankAccount;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface IBankAccountTableRepository extends JpaRepository<BankAccountTable, UUID>
{
}
