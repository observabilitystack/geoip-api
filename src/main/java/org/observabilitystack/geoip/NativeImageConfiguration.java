package org.observabilitystack.geoip;

import java.util.ArrayList;

import org.springframework.aot.hint.MemberCategory;
import org.springframework.aot.hint.RuntimeHints;
import org.springframework.aot.hint.RuntimeHintsRegistrar;

import com.maxmind.db.Metadata;
import com.maxmind.db.Network;
import com.maxmind.geoip2.NetworkDeserializer;
import com.maxmind.geoip2.model.AsnResponse;
import com.maxmind.geoip2.model.CityResponse;
import com.maxmind.geoip2.model.IspResponse;
import com.maxmind.geoip2.record.City;
import com.maxmind.geoip2.record.Continent;
import com.maxmind.geoip2.record.Country;
import com.maxmind.geoip2.record.Location;
import com.maxmind.geoip2.record.Postal;
import com.maxmind.geoip2.record.RepresentedCountry;
import com.maxmind.geoip2.record.Subdivision;
import com.maxmind.geoip2.record.Traits;

public class NativeImageConfiguration implements RuntimeHintsRegistrar {

    @Override
    public void registerHints(RuntimeHints hints, ClassLoader classLoader) {
        // maxmind lib
        hints.reflection().registerType(Metadata.class, MemberCategory.INVOKE_PUBLIC_CONSTRUCTORS);
        hints.reflection().registerType(CityResponse.class, MemberCategory.INVOKE_PUBLIC_CONSTRUCTORS);
        hints.reflection().registerType(IspResponse.class, MemberCategory.INVOKE_PUBLIC_CONSTRUCTORS);
        hints.reflection().registerType(AsnResponse.class, MemberCategory.INVOKE_PUBLIC_CONSTRUCTORS);
        hints.reflection().registerType(Continent.class, MemberCategory.INVOKE_PUBLIC_CONSTRUCTORS);
        hints.reflection().registerType(Location.class, MemberCategory.INVOKE_PUBLIC_CONSTRUCTORS);
        hints.reflection().registerType(Postal.class, MemberCategory.INVOKE_PUBLIC_CONSTRUCTORS);
        hints.reflection().registerType(Country.class, MemberCategory.INVOKE_PUBLIC_CONSTRUCTORS);
        hints.reflection().registerType(City.class, MemberCategory.INVOKE_PUBLIC_CONSTRUCTORS);
        hints.reflection().registerType(RepresentedCountry.class, MemberCategory.INVOKE_PUBLIC_CONSTRUCTORS);
        hints.reflection().registerType(Subdivision.class, MemberCategory.INVOKE_PUBLIC_CONSTRUCTORS);
        hints.reflection().registerType(Traits.class, MemberCategory.INVOKE_PUBLIC_CONSTRUCTORS);
        hints.reflection().registerType(NetworkDeserializer.class, MemberCategory.INVOKE_PUBLIC_CONSTRUCTORS);
        hints.reflection().registerType(Network.class, MemberCategory.INVOKE_PUBLIC_CONSTRUCTORS);
        hints.reflection().registerType(GeoIpEntry.class, MemberCategory.INVOKE_PUBLIC_CONSTRUCTORS);

        // also used by maxmind lib
        hints.reflection().registerType(ArrayList.class, MemberCategory.INVOKE_PUBLIC_CONSTRUCTORS);
    }
}
