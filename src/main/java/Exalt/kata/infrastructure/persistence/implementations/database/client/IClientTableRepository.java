package exalt.kata.infrastructure.persistence.implementations.database.client;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface IClientTableRepository extends JpaRepository<ClientTable, UUID>
{
}
