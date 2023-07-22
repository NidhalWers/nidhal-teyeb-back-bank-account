package exalt.kata.infrastructure.persistence.implementations.database.accounting;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
public class AccountingTransactionTable
{
    @Id
    private UUID id;
    private UUID externalId;
    private UUID clientExternalId;
    private UUID bankAccountExternalId;
    private LocalDateTime creationDate;
    private LocalDateTime executionDate;
    private String status;
    private Long amount;
    private String currency;
    private String type;
    private String tag;
    private String operationCode;
    private String operationMessage;

    public AccountingTransactionTable()
    {
    }

    public UUID getId()
    {
        return id;
    }

    public AccountingTransactionTable setId(UUID id)
    {
        this.id = id;
        return this;
    }

    public UUID getExternalId()
    {
        return externalId;
    }

    public AccountingTransactionTable setExternalId(UUID externalId)
    {
        this.externalId = externalId;
        return this;
    }

    public UUID getClientExternalId()
    {
        return clientExternalId;
    }

    public AccountingTransactionTable setClientExternalId(UUID clientExternalId)
    {
        this.clientExternalId = clientExternalId;
        return this;
    }

    public UUID getBankAccountExternalId()
    {
        return bankAccountExternalId;
    }

    public AccountingTransactionTable setBankAccountExternalId(UUID bankAccountExternalId)
    {
        this.bankAccountExternalId = bankAccountExternalId;
        return this;
    }

    public LocalDateTime getCreationDate()
    {
        return creationDate;
    }

    public AccountingTransactionTable setCreationDate(LocalDateTime creationDate)
    {
        this.creationDate = creationDate;
        return this;
    }

    public LocalDateTime getExecutionDate()
    {
        return executionDate;
    }

    public AccountingTransactionTable setExecutionDate(LocalDateTime executionDate)
    {
        this.executionDate = executionDate;
        return this;
    }

    public String getStatus()
    {
        return status;
    }

    public AccountingTransactionTable setStatus(String status)
    {
        this.status = status;
        return this;
    }

    public Long getAmount()
    {
        return amount;
    }

    public AccountingTransactionTable setAmount(Long amount)
    {
        this.amount = amount;
        return this;
    }

    public String getCurrency()
    {
        return currency;
    }

    public AccountingTransactionTable setCurrency(String currency)
    {
        this.currency = currency;
        return this;
    }

    public String getType()
    {
        return type;
    }

    public AccountingTransactionTable setType(String type)
    {
        this.type = type;
        return this;
    }

    public String getTag()
    {
        return tag;
    }

    public AccountingTransactionTable setTag(String tag)
    {
        this.tag = tag;
        return this;
    }

    public String getOperationCode()
    {
        return operationCode;
    }

    public AccountingTransactionTable setOperationCode(String operationCode)
    {
        this.operationCode = operationCode;
        return this;
    }

    public String getOperationMessage()
    {
        return operationMessage;
    }

    public AccountingTransactionTable setOperationMessage(String operationMessage)
    {
        this.operationMessage = operationMessage;
        return this;
    }
}
