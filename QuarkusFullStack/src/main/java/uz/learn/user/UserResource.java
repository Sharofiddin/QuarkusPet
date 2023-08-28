package uz.learn.user;

import java.util.List;

import org.jboss.resteasy.reactive.ResponseStatus;

import io.smallrye.mutiny.Uni;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.core.MediaType;

@Path("/api/v1/users")
public class UserResource {
  private final UserService userService;
  
  @Inject
  public UserResource(UserService userService) {
	  this.userService = userService;
  }
  
  @GET
  public Uni<List<User>> get(){
	  return userService.list();
  }
  
  @POST
  @Consumes(MediaType.APPLICATION_JSON)
  @ResponseStatus(201)
  public Uni<User> create(User user){
	  return userService.create(user);
  }
  
  
  @GET
  @Path("{id}")
  public Uni<User> get(@PathParam("id") long id){
	  return userService.findById(id);
  }
  
  @PUT
  @Consumes(MediaType.APPLICATION_JSON)
  @Path("{id}")
  public Uni<User> update(@PathParam("id") Long id, User user){
	  user.id = id;
	  return userService.update(user);
  }
}
