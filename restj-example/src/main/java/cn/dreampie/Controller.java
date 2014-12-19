package cn.dreampie;

import cn.dreampie.serfj.annotations.DELETE;
import cn.dreampie.serfj.annotations.GET;
import cn.dreampie.serfj.annotations.POST;
import cn.dreampie.serfj.annotations.PUT;
import cn.dreampie.serfj.RestController;

/**
 * Created by ice on 14-12-19.
 */
public class Controller extends RestController {
  @GET
  public void index() {
    // By default, this action redirects to index.jsp (or index.html or index.htm)
  }

  @GET
  public void show() {
    // Gets ID from URL /banks/1
    String id = this.getId("bank");

    // Gets account's ID from URL /banks/1/accounts/2
    String accountId = this.getId("account");

    // By default, this action redirects to show.jsp (or show.html or show.htm)
  }

  @GET
  public void newResource() {
    // By default, this action redirects to new.jsp (or new.html or new.htm)
  }

  @GET
  public void edit() {
    // By default, this action redirects to edit.jsp (or edit.html or edit.htm)
  }

  @POST
  public void create() {
    // By default, this action redirects to create.jsp (or create.html or create.htm)
  }

  @PUT
  public void update() {
    // Gets bank's ID
    String id = this.getId("bank");

    // Updating the bank
    // ... Code that updates the bank's information

    // By default, this action redirects to update.jsp (or update.html or update.htm)
  }

  @DELETE
  public void delete() {
    // By default, this action redirects to delete.jsp (or delete.html or delete.htm)
  }

  @GET
  public void someAction() {
    // By default, this action redirects to someAction.jsp (or someAction.html or someAction.htm)
  }

}
