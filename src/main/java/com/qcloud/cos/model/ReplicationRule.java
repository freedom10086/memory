package com.qcloud.cos.model;

import com.qcloud.cos.utils.Jackson;

import java.io.Serializable;

/**
 * Rule that specifies the replication configuration.
 */
public class ReplicationRule implements Serializable {

    /**
     * The QCloud object prefix for the replication rule. This rule will be
     * applied if an QCloud object matches this prefix.
     */
    private String prefix;

    /**
     * The status of this replication rule. Valid values are Enabled, Disabled.
     * The rule will be applied only if the status is Enabled.
     */
    private String status;

    /**
     * Destination configuration for the replication rule.
     */
    private ReplicationDestinationConfig destinationConfig;

    /**
     * Returns the prefix associated with the replication rule.
     */
    public String getPrefix() {
        return prefix;
    }

    /**
     * Sets the QCloud Object prefix for the replication rule.
     *
     * @throws IllegalArgumentException
     *             if the prefix is null.
     */
    public void setPrefix(String prefix) {
        if (prefix == null) {
            throw new IllegalArgumentException(
                    "Prefix cannot be null for a replication rule");
        }
        this.prefix = prefix;
    }

    /**
     * Sets the QCloud Object prefix for the replication rule. Returns the
     * updated object.
     *
     * @return the updated {@link ReplicationRule} object.
     * @throws IllegalArgumentException
     *             if the prefix is null.
     */
    public ReplicationRule withPrefix(String prefix) {
        setPrefix(prefix);
        return this;
    }

    /**
     * Returns the status of the replication rule.
     */
    public String getStatus() {
        return status;
    }

    /**
     * Sets the status of this replication rule. Valid values are Enabled,
     * Disabled. The rule will be applied only if the status is Enabled.
     *
     * @param status
     *            the status of the replication rule.
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * Sets the status of this replication rule. Valid values are Enabled,
     * Disabled. The rule will be applied only if the status is Enabled.
     *
     * @param status
     *            the status of replication rule.
     *
     * @return the updated {@link ReplicationRule} object.
     */
    public ReplicationRule withStatus(String status) {
        setStatus(status);
        return this;
    }

    /**
     * Sets the status of this replication rule. Valid values are Enabled,
     * Disabled. The rule will be applied only if the status is Enabled.
     *
     * @param status
     *            the status of the replication rule.
     */
    public void setStatus(ReplicationRuleStatus status) {
        setStatus(status.getStatus());
    }

    /**
     * Sets the status of this replication rule. Valid values are Enabled,
     * Disabled. The rule will be applied only if the status is Enabled.
     *
     * @param status
     *            the status of replication rule.
     *
     * @return the updated {@link ReplicationRule} object.
     */
    public ReplicationRule withStatus(ReplicationRuleStatus status) {
        setStatus(status.getStatus());
        return this;
    }

    /**
     * Returns the destination configuration for the replication rule.
     */
    public ReplicationDestinationConfig getDestinationConfig() {
        return destinationConfig;
    }

    /**
     * Sets the destination configuration for the replication rule.
     *
     * @throws IllegalArgumentException
     *             if the destinationConfig is null.
     */
    public void setDestinationConfig(
            ReplicationDestinationConfig destinationConfig) {
        if (destinationConfig == null) {
            throw new IllegalArgumentException(
                    "Destination cannot be null in the replication rule");
        }
        this.destinationConfig = destinationConfig;
    }

    /**
     * Sets the destination configuration for the replication rule.Returns the
     * updated object.
     *
     * @throws IllegalArgumentException
     *             if the destinationConfig is null.
     * @return the updated {@link ReplicationRule} object.
     */
    public ReplicationRule withDestinationConfig(
            ReplicationDestinationConfig destinationConfig) {
        setDestinationConfig(destinationConfig);
        return this;
    }

    @Override
    public String toString() {
        return Jackson.toJsonString(this);
    }
}
