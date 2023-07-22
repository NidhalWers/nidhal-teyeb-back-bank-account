package exalt.kata.infrastructure.persistence.implementations.database.bankAccount;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
public class BankAccountTable
{
    @Id
    private UUID id;
    private UUID externalId;
    private UUID clientExternalId;
    private LocalDateTime creationDate;
    private String status;
    private Long amount;
    private String currency;
    private String tag;
    private Long overdrawnAuthorized;
    private LocalDateTime lastModificationDate;

    public BankAccountTable()
    {
    }

    public UUID getId()
    {
        return id;
    }

    public BankAccountTable setId(UUID id)
    {
        this.id = id;
        return this;
    }

    public UUID getExternalId()
    {
        return externalId;
    }

    public BankAccountTable setExternalId(UUID externalId)
    {
        this.externalId = externalId;
        return this;
    }

    public UUID getClientExternalId()
    {
        return clientExternalId;
    }

    public BankAccountTable setClientExternalId(UUID clientExternalId)
    {
        this.clientExternalId = clientExternalId;
        return this;
    }

    public LocalDateTime getCreationDate()
    {
        return creationDate;
    }

    public BankAccountTable setCreationDate(LocalDateTime creationDate)
    {
        this.creationDate = creationDate;
        return this;
    }

    public String getStatus()
    {
        return status;
    }

    public BankAccountTable setStatus(String status)
    {
        this.status = status;
        return this;
    }

    public Long getAmount()
    {
        return amount;
    }

    public BankAccountTable setAmount(Long amount)
    {
        this.amount = amount;
        return this;
    }

    public String getCurrency()
    {
        return currency;
    }

    public BankAccountTable setCurrency(String currency)
    {
        this.currency = currency;
        return this;
    }

    public String getTag()
    {
        return tag;
    }

    public BankAccountTable setTag(String tag)
    {
        this.tag = tag;
        return this;
    }

    public Long getOverdrawnAuthorized()
    {
        return overdrawnAuthorized;
    }

    public BankAccountTable setOverdrawnAuthorized(Long overdrawnAuthorized)
    {
        this.overdrawnAuthorized = overdrawnAuthorized;
        return this;
    }

    public LocalDateTime getLastModificationDate()
    {
        return lastModificationDate;
    }

    public BankAccountTable setLastModificationDate(LocalDateTime lastModificationDate)
    {
        this.lastModificationDate = lastModificationDate;
        return this;
    }
}
