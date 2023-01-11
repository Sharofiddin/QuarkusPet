package quarkus.transaction.repository;

import javax.enterprise.context.ApplicationScoped;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import quarkus.transaction.object.Transaction;

@ApplicationScoped
public class TransactionRepository implements PanacheRepository<Transaction>{

}
