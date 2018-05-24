package com.qcloud.cos.model;

import com.qcloud.cos.exception.CosClientException;
import com.qcloud.cos.internal.XmlWriter;

/**
 * Factory for creating XML fragments from AccessControlList objects that can be
 * sent to COS as part of requests.
 */
public class AclXmlFactory {

    /**
     * Converts the specified AccessControlList object to an XML fragment that
     * can be sent to COS.
     *
     * @param acl
     *            The AccessControlList to convert to XML.
     *
     * @return an XML representation of the Access Control List object, suitable
     *         to send in a request to COS.
     */
    public byte[] convertToXmlByteArray(AccessControlList acl) throws CosClientException {
        Owner owner = acl.getOwner();
        if (owner == null) {
            throw new CosClientException("Invalid AccessControlList: missing an COS Owner");
        }

        XmlWriter xml = new XmlWriter();
        xml.start("AccessControlPolicy");
        xml.start("Owner");
        if (owner.getId() != null) {
            xml.start("ID").value(owner.getId()).end();
        }
        if (owner.getDisplayName() != null) {
            xml.start("DisplayName").value(owner.getDisplayName()).end();
        }
        xml.end();
        xml.start("AccessControlList");
        for (Grant grant : acl.getGrantsAsList()) {
            xml.start("Grant");
            convertToXml(grant.getGrantee(), xml);
            xml.start("Permission").value(grant.getPermission().toString()).end();
            xml.end();
        }
        xml.end();
        xml.end();

        return xml.getBytes();
    }

    /**
     * Returns an XML fragment representing the specified Grantee.
     *
     * @param grantee
     *            The grantee to convert to an XML representation that can be
     *            sent to COS as part of a request.
     * @param xml
     *            The XmlWriter to which to concatenate this node to.
     *
     * @return The given XmlWriter containing the specified grantee.
     *
     * @throws CosClientException
     *             If the specified grantee type isn't recognized.
     */
    protected XmlWriter convertToXml(Grantee grantee, XmlWriter xml) throws CosClientException {
        if (grantee instanceof UinGrantee) {
            return convertToXml((UinGrantee)grantee, xml);
        } else {
            throw new CosClientException("Unknown Grantee type: " + grantee.getClass().getName());
        }
    }

    /**
     * Returns an XML fragment representing the specified canonical grantee.
     *
     * @param grantee
     *            The canonical grantee to convert to an XML representation that
     *            can be sent to COS as part of request.
     * @param xml
     *            The XmlWriter to which to concatenate this node to.
     *
     * @return The given XmlWriter containing the specified canonical grantee.
     */
    protected XmlWriter convertToXml(UinGrantee grantee, XmlWriter xml) {
        xml.start("Grantee", new String[] {"xmlns:xsi" , "xsi:type"},
                new String[] {"http://www.w3.org/2001/XMLSchema-instance", "RootAccount"});
        xml.start("ID").value(grantee.getIdentifier()).end();
        xml.end();

        return xml;
    }

}