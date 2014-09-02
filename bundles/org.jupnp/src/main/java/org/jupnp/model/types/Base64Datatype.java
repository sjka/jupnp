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

import org.jupnp.util.io.Base64Coder;

/**
 * @author Christian Bauer
 */
public class Base64Datatype extends AbstractDatatype<byte[]> {

    public Base64Datatype() {
    }

    public Class<byte[]> getValueType() {
        return byte[].class;
    }

    public byte[] valueOf(String s) throws InvalidValueException {
        if (s.equals("")) return null;
        try {
            return Base64Coder.decode(s);
        } catch (Exception ex) {
            throw new InvalidValueException(ex.getMessage(), ex);
        }
    }

    @Override
    public String getString(byte[] value) throws InvalidValueException {
        if (value == null) return "";
        try {
            return new String(Base64Coder.encode(value));
        } catch (Exception ex) {
            throw new InvalidValueException(ex.getMessage(), ex);
        }
    }

}
