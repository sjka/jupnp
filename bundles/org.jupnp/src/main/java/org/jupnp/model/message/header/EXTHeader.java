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

package org.jupnp.model.message.header;

/**
 * @author Christian Bauer
 */
public class EXTHeader extends UpnpHeader<String> {

    // That's just an empty header! Isn't that great...
    public final static String DEFAULT_VALUE = "";

    public EXTHeader() {
        setValue(DEFAULT_VALUE);
    }

    public void setString(String s) throws InvalidHeaderException {
        if (s != null && s.length() > 0) {
            throw new InvalidHeaderException("Invalid EXT header, it has no value: " + s);
        }
    }

    public String getString() {
        return getValue();
    }
}
