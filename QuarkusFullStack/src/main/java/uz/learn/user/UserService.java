package uz.learn.user;

import static io.quarkus.hibernate.reactive.panache.PanacheEntityBase.find;

import java.util.List;

import org.hibernate.ObjectNotFoundException;

import io.quarkus.elytron.security.common.BcryptUtil;
import io.quarkus.hibernate.reactive.panache.PanacheEntityBase;
import io.quarkus.hibernate.reactive.panache.common.WithSession;
import io.quarkus.hibernate.reactive.panache.common.WithTransaction;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
@WithSession
public class UserService {
  Uni<User> findById(Long id){
	  return User.<User>findById(id).onItem().ifNull().failWith(()->new ObjectNotFoundException(id, "user"));
  }
  
  Uni<User> findByName(String name){
	  return find("name", name).firstResult();
  }
  
  Uni<List<User>> list(){
	  return User.listAll();
  }
  
  @WithTransaction
  public Uni<User> create(User user){
	  user.password = BcryptUtil.bcryptHash(user.getPassword());
	  return user.persistAndFlush();
  }
  
  @WithTransaction
  public Uni<User> update(User user){
	  return findById(user.id)
			  .chain(u->PanacheEntityBase.getSession())
			  .chain(s -> s.merge(user));
  }
}
