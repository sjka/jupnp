/**
 * Copyright (C) 2014 4th Line GmbH, Switzerland and others
 *
 * The contents of this file are subject to the terms of the
 * Common Development and Distribution License Version 1 or later
 * ("CDDL") (collectively, the "License"). You may not use this file
 * except in compliance with the License. See LICENSE.txt for more
 * information.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 */

package org.jupnp.test.ssdp;

import org.jupnp.mock.MockUpnpService;
import org.jupnp.model.ServerClientTokens;
import org.jupnp.model.message.OutgoingDatagramMessage;
import org.jupnp.model.message.UpnpMessage;
import org.jupnp.model.message.header.UpnpHeader;
import org.jupnp.model.meta.LocalDevice;
import org.jupnp.model.types.NotificationSubtype;
import org.jupnp.protocol.async.SendingNotificationAlive;
import org.jupnp.protocol.async.SendingNotificationByebye;
import org.jupnp.test.data.SampleData;
import org.jupnp.test.data.SampleDeviceRoot;
import org.jupnp.test.data.SampleUSNHeaders;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;


public class AdvertisementTest {

    @Test
    public void sendAliveMessages() throws Exception {

        MockUpnpService upnpService = new MockUpnpService();
        upnpService.startup();

        LocalDevice rootDevice = SampleData.createLocalDevice();
        LocalDevice embeddedDevice = rootDevice.getEmbeddedDevices()[0];

        SendingNotificationAlive prot = new SendingNotificationAlive(upnpService, rootDevice);
        prot.run();

        for (OutgoingDatagramMessage msg : upnpService.getRouter().getOutgoingDatagramMessages()) {
            assertAliveMsgBasics(msg);
            //SampleData.debugMsg(msg);
        }

        SampleUSNHeaders.assertUSNHeaders(
            upnpService.getRouter().getOutgoingDatagramMessages(),
            rootDevice, embeddedDevice, UpnpHeader.Type.NT);
    }

    @Test
    public void sendByebyeMessages() throws Exception {

        MockUpnpService upnpService = new MockUpnpService();
        upnpService.startup();

        LocalDevice rootDevice = SampleData.createLocalDevice();
        LocalDevice embeddedDevice = rootDevice.getEmbeddedDevices()[0];

        SendingNotificationByebye prot = new SendingNotificationByebye(upnpService, rootDevice);
        prot.run();

        for (OutgoingDatagramMessage msg : upnpService.getRouter().getOutgoingDatagramMessages()) {
            assertByebyeMsgBasics(msg);
            //SampleData.debugMsg(msg);
        }

        SampleUSNHeaders.assertUSNHeaders(
            upnpService.getRouter().getOutgoingDatagramMessages(),
            rootDevice, embeddedDevice, UpnpHeader.Type.NT);
    }

    protected void assertAliveMsgBasics(UpnpMessage msg) {
        assertEquals(msg.getHeaders().getFirstHeader(UpnpHeader.Type.NTS).getValue(), NotificationSubtype.ALIVE);
        assertEquals(msg.getHeaders().getFirstHeader(UpnpHeader.Type.LOCATION).getValue().toString(), SampleDeviceRoot.getDeviceDescriptorURL().toString());
        assertEquals(msg.getHeaders().getFirstHeader(UpnpHeader.Type.MAX_AGE).getValue(), 1800);
        assertEquals(msg.getHeaders().getFirstHeader(UpnpHeader.Type.SERVER).getValue(), new ServerClientTokens());
    }

    protected void assertByebyeMsgBasics(UpnpMessage msg) {
        assertEquals(msg.getHeaders().getFirstHeader(UpnpHeader.Type.NTS).getValue(), NotificationSubtype.BYEBYE);
    }

}