// Copyright 2024 Broadcom. All Rights Reserved.

package javaobject;

import java.io.Serializable;
import java.security.Principal;

/**
 * An implementation of {@link Principal} class for a simple user name.
 * 
 */
public class UsernamePrincipal implements Principal, Serializable {

  private final String userName;

  public UsernamePrincipal(String userName) {
    this.userName = userName;
  }

  public String getName() {
    return this.userName;
  }

  @Override
  public String toString() {
    return this.userName;
  }

}
