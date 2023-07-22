package exalt.kata.infrastructure.persistence.implementations.database.accounting;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface IAccountingTransactionTableRepository extends JpaRepository<AccountingTransactionTable, UUID>
{
}
