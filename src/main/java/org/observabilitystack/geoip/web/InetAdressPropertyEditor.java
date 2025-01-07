package org.observabilitystack.geoip.web;

import java.beans.PropertyEditorSupport;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.net.InetAddresses;

import static com.google.common.base.Preconditions.checkNotNull;

public class InetAdressPropertyEditor extends PropertyEditorSupport {

   private final Logger logger = LoggerFactory.getLogger(getClass());

   @Override
   public void setAsText(String text) {
      checkNotNull(text, "Pre-condition violated: text must not be null.");

      try {
         setValue(InetAddresses.forString(text));
      } catch (IllegalArgumentException e) {
         logger.info("Invalid IP address given: {}", e.getMessage());
         throw new InvalidIpAddressException("Invalid IP address given");
      }
   }
}
