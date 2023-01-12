package quarkus.transaction.object;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class Transaction {

	@GeneratedValue
	@Id
	private Long id;

	@Column(name = "account_number")
	private Long accountNumber;

	private BigDecimal amount;

	private TransactionStatus status = TransactionStatus.PENDING;

	private LocalDateTime created = LocalDateTime.now();

	private LocalDateTime updated;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getAccountNumber() {
		return accountNumber;
	}

	public void setAccountNumber(Long accountNumber) {
		this.accountNumber = accountNumber;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public TransactionStatus getStatus() {
		return status;
	}

	public void setStatus(TransactionStatus status) {
		this.status = status;
	}

	public LocalDateTime getCreated() {
		return created;
	}

	public void setCreated(LocalDateTime created) {
		this.created = created;
	}

	public LocalDateTime getUpdated() {
		return updated;
	}

	public void setUpdated(LocalDateTime updated) {
		this.updated = updated;
	}

	public void makeSucces() {
		setStatus(TransactionStatus.SUCCESS);
		setUpdated(LocalDateTime.now());
	}

	public void makeError() {
		setStatus(TransactionStatus.ERROR);
		setUpdated(LocalDateTime.now());
	}
}
