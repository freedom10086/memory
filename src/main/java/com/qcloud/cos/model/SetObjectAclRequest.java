package com.qcloud.cos.model;

import com.qcloud.cos.internal.CosServiceRequest;

import java.io.Serializable;

public class SetObjectAclRequest extends CosServiceRequest implements Serializable {

    /** The name of the bucket containing the object whose ACL is being set. */
    private final String bucketName;

    /** The name of the object whose ACL is being set. */
    private final String key;

    /** The version ID of the object version whose ACL is being set. */
    /** Currently, COS doesn't support set acl for special version object **/
    private final String versionId;

    /** The custom ACL to apply to the specified object. */
    private final AccessControlList acl;

    /** The canned ACL to apply to the specified object. */
    private final CannedAccessControlList cannedAcl;

    /**
     * Constructs a new SetObjectAclRequest object, ready to set the specified ACL on the specified
     * object when this request is executed.
     *
     * @param bucketName The name of the bucket containing the object whose ACL is being set.
     * @param key The name of the object whose ACL is being set.
     * @param acl The custom Access Control List containing the access rules to apply to the
     *        specified bucket when this request is executed.
     */
    public SetObjectAclRequest(String bucketName, String key, AccessControlList acl) {
        this.bucketName = bucketName;
        this.key = key;
        this.versionId = null;

        this.acl = acl;
        this.cannedAcl = null;
    }

    /**
     * Constructs a new SetObjectAclRequest object, ready to set the specified ACL on the specified
     * object when this request is executed.
     *
     * @param bucketName The name of the bucket containing the object whose ACL is being set.
     * @param key The name of the object whose ACL is being set.
     * @param acl The Canned Access Control List to apply to the specified bucket when this request
     *        is executed.
     */
    public SetObjectAclRequest(String bucketName, String key, CannedAccessControlList acl) {
        this.bucketName = bucketName;
        this.key = key;
        this.versionId = null;

        this.acl = null;
        this.cannedAcl = acl;
    }

    /**
     * Returns the name of the bucket containing the object whose ACL is being set.
     *
     * @return The name of the bucket containing the object whose ACL is being set.
     */
    public String getBucketName() {
        return bucketName;
    }

    /**
     * Returns the name of the object whose ACL is being set.
     *
     * @return The name of the object whose ACL is being set.
     */
    public String getKey() {
        return key;
    }

    /**
     * Returns the version ID of the object version whose ACL is being set.
     *
     * @return The version ID of the object version whose ACL is being set.
     */
    public String getVersionId() {
        return versionId;
    }

    /**
     * Returns the custom ACL to be applied to the specified object when this request is executed. A
     * request can use either a custom ACL or a canned ACL, but not both.
     *
     * @return The custom ACL to be applied to the specified bucket when this request is executed,
     *         or null if the request is to be executed with a canned ACL.
     */
    public AccessControlList getAcl() {
        return acl;
    }

    /**
     * Returns the canned ACL to be applied to the specified object when this request is executed. A
     * request can use either a custom ACL or a canned ACL, but not both.
     *
     * @return The canned ACL to be applied to the specified bucket when this request is executed,
     *         or null if the request is to be executed with a custom ACL.
     */
    public CannedAccessControlList getCannedAcl() {
        return cannedAcl;
    }

}
