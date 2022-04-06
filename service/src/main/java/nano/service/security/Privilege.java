package nano.service.security;

import nano.service.nano.entity.NanoToken;

/**
 * Token privilege
 *
 * @see Authorized
 * @see NanoToken
 */
public abstract class Privilege {
   public static final String BASIC = "BASIC";
   public static final String NANO_API = "NANO_API";
   public static final String MAIL = "MAIL";
}
