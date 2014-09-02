/**
 * Copyright (C) 2014 4th Line GmbH, Switzerland and others
 *
 * The contents of this file are subject to the terms of either the
 * Common Development and Distribution License Version 1 or later
 * ("CDDL") (collectively, the "License"). You may not use this file
 * except in compliance with the License. See LICENSE.txt for more
 * information.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 */

package org.jupnp.model.types;

import org.jupnp.model.ModelUtil;

import java.util.UUID;
import java.security.MessageDigest;
import java.math.BigInteger;
import java.util.logging.Logger;

/**
 * A unique device name.
 * <p>
 * UDA 1.0 does not specify a UUID format, however, UDA 1.1 specifies a format that is compatible
 * with <tt>java.util.UUID</tt> variant 4. You can use any identifier string you like.
 * </p>
 * <p>
 * You'll most likely need the {@link #uniqueSystemIdentifier(String)} method sooner or later.
 * </p>
 *
 * @author Christian Bauer
 */
public class UDN {

    final private static Logger log = Logger.getLogger(UDN.class.getName());

    public static final String PREFIX = "uuid:";

    private String identifierString;

    /**
     * @param identifierString The identifier string without the "uuid:" prefix.
     */
    public UDN(String identifierString) {
        this.identifierString = identifierString;
    }

    public UDN(UUID uuid) {
        this.identifierString = uuid.toString();
    }

    public boolean isUDA11Compliant() {
        try {
            UUID.fromString(identifierString);
            return true;
        } catch (IllegalArgumentException ex) {
            return false;
        }
    }

    public String getIdentifierString() {
        return identifierString;
    }

    public static UDN valueOf(String udnString) {
        return new UDN(udnString.startsWith(PREFIX) ? udnString.substring(PREFIX.length()) : udnString);
    }

    /**
     * Generates a global unique identifier that is the same every time this method is invoked on the same machine.
     * <p>
     * This method discovers various pieces of information about the local system such
     * as hostname, MAC address, OS name and version. It then combines this information with the
     * given salt to generate a globally unique identifier. In other words, every time you
     * call this method with the same salt on the same machine, you get the same identifier.
     * If you use the same salt on a different machine, a different identifier will be generated.
     * </p>
     * <p>
     * Note for Android users: This method does not generate unique identifiers on Android devices and will
     * throw an exception. We can't get details such as the hostname or MAC address on Android. Instead,
     * construct a UDN with <code>new UDN(UUID)</code>. When your application is first started, generate all
     * UUIDs needed for your UPnP devices and store them in your Android preferences. Then, use the stored
     * UUID to create a UDN every time your application starts.
     * </p>
     * <p>
     * Control points can remember your device's identifier, it will and should be the same every time
     * your device is powered up.
     * </p>
     *
     * @param salt An arbitrary string that uniquely identifies the devices on the current system, e.g. "MyMediaServer".
     * @return A global unique identifier, stable for the current system and salt.
     */
    public static UDN uniqueSystemIdentifier(String salt) {
        StringBuilder systemSalt = new StringBuilder();

        // Bug: I've seen InetAddress.getLocalHost() block *forever* on Android, until device is rebooted
        // Bug: On Android, NetworkInterface.isLoopback() isn't implemented
        if (!ModelUtil.ANDROID_RUNTIME) {
            try {
                java.net.InetAddress i = java.net.InetAddress.getLocalHost();
                systemSalt.append(i.getHostName()).append(i.getHostAddress());
            } catch (Exception ex) {
                // Could not find local host name, try to get the MAC address of loopback interface
                try {
                    systemSalt.append(new String(ModelUtil.getFirstNetworkInterfaceHardwareAddress()));
                } catch (Throwable ex1) {
                    // Ignore, we did everything we can
                    log.severe(
                        "Couldn't get host/network interface information on this machine, " +
                            "generated UDN might not be unique!"
                    );
                }
            }
        } else {
            throw new RuntimeException(
                "This method does not create a unique identifier on Android, see the Javadoc and " +
                    "use new UDN(UUID) instead!"
            );
        }

        systemSalt.append(System.getProperty("os.name"));
        systemSalt.append(System.getProperty("os.version"));
        try {
            byte[] hash = MessageDigest.getInstance("MD5").digest(systemSalt.toString().getBytes());
            return new UDN(
                    new UUID(
                            new BigInteger(-1, hash).longValue(),
                            salt.hashCode()
                    )
            );
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public String toString() {
        return PREFIX + getIdentifierString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || !(o instanceof UDN)) return false;
        UDN udn = (UDN) o;
        return identifierString.equals(udn.identifierString);
    }

    @Override
    public int hashCode() {
        return identifierString.hashCode();
    }

}
