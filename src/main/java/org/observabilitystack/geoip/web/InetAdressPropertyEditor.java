package org.observabilitystack.geoip.web;

import java.beans.PropertyEditorSupport;

import com.google.common.net.InetAddresses;

import static com.google.common.base.Preconditions.checkNotNull;

public class InetAdressPropertyEditor extends PropertyEditorSupport {

   @Override
   public void setAsText(String text) {
      checkNotNull(text, "Pre-condition violated: text must not be null.");

      try {
         setValue(InetAddresses.forString(text));
      } catch (IllegalArgumentException e) {
         throw new InvalidIpAddressException(e.getMessage());
      }
   }
}
