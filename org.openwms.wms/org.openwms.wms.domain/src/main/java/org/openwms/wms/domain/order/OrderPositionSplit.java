/*
 * openwms.org, the Open Warehouse Management System.
 * Copyright (C) 2014 Heiko Scherrer
 *
 * This file is part of openwms.org.
 *
 * openwms.org is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation; either version 2 of the
 * License, or (at your option) any later version.
 *
 * openwms.org is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this software. If not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.openwms.wms.domain.order;

import org.openwms.common.domain.values.Problem;
import org.openwms.core.domain.AbstractEntity;
import org.openwms.core.domain.values.CoreTypeDefinitions;
import org.openwms.core.domain.values.UnitType;
import org.openwms.core.util.validation.AssertUtils;
import org.openwms.wms.domain.inventory.Product;

import javax.persistence.*;
import java.io.Serializable;

/**
 * An OrderPositionSplit.
 * 
 * @author <a href="mailto:scherrer@openwms.org">Heiko Scherrer</a>
 * @version $Revision: $
 * @since 0.1
 */
@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "C_TYPE")
@Table(name = "WMS_ORDER_POS_SPLIT", uniqueConstraints = @UniqueConstraint(columnNames = { "C_ORDER_ID_K",
        "C_POSITION_NO_K", "C_SPLIT_NO_K" }))
public class OrderPositionSplit<T extends AbstractOrder<T, U>, U extends OrderPosition<T, U>> extends AbstractEntity<Long> implements Serializable {

    private static final long serialVersionUID = -1444350691553190L;

    /** Unique technical key. */
    @Id
    @Column(name = "C_ID")
    @GeneratedValue
    private Long id;

    @ManyToOne(targetEntity = OrderPosition.class)
    @JoinColumn(name = "C_ID", referencedColumnName = "C_ID", insertable = false, updatable = false)
    private U position;

    /** Business key. */
    @Embedded
    private OrderPositionSplitKey splitId;

    /** Current priority of the split. */
    @Column(name = "C_PRIORITY")
    private int priority;

    /** The quantity that was originally demanded. */
    @org.hibernate.annotations.Type(type = "org.openwms.persistence.ext.hibernate.UnitUserType")
    @org.hibernate.annotations.Columns(columns = { @Column(name = "C_QUANTITY_TYPE", length = 255, nullable = false),
            @Column(name = "C_QTY", length = CoreTypeDefinitions.QUANTITY_LENGTH, nullable = false) })
    private UnitType qty;

    /** The ordered {@link Product}. */
    @ManyToOne
    @JoinColumn(name = "C_PRODUCT", referencedColumnName = "C_SKU")
    private Product product;

    /** Latest problem that is occurred on this OrderPositionSplit. */
    @Embedded
    private Problem problem;

    /** Version field. */
    @Version
    @Column(name = "C_VERSION")
    private long version;

    /**
     * Used by the JPA provider.
     */
    protected OrderPositionSplit() {
        super();
    }

    /**
     * Create a new OrderPositionSplit.
     * 
     * @param position
     *            The OrderPosition this Split belongs to
     * @param sId
     *            The unique id of this Split
     */
    public OrderPositionSplit(U position, Integer sId) {
        AssertUtils.notNull(position);
        AssertUtils.notNull(sId);
        this.splitId = new OrderPositionSplitKey(position, sId);
        this.priority = position.getPriority();
    }

    /**
     * Create a new OrderPositionSplit.
     * 
     * @param sId
     *            The unique id of this Split
     */
    public OrderPositionSplit(OrderPositionSplitKey sId) {
        AssertUtils.notNull(sId);
        this.splitId = sId;
        this.priority = position.getPriority();
    }

    /**
     * After data was loaded, initialize some fields with transient values.
     */
    @PostLoad
    protected void postLoad() {
        this.position = this.getPosition();
    }

    /**
     * @see org.openwms.core.domain.DomainObject#isNew()
     */
    @Override
    public boolean isNew() {
        return this.id == null;
    }

    /**
     * @see org.openwms.core.domain.DomainObject#getVersion()
     */
    @Override
    public long getVersion() {
        return this.version;
    }

    /**
     * @see org.openwms.core.domain.DomainObject#getId()
     */
    @Override
    public Long getId() {
        return id;
    }

    /**
     * Get the position.
     * 
     * @return the position.
     */
    public U getPosition() {
        return position;
    }

    /**
     * Get the splitId.
     * 
     * @return the splitId.
     */
    public OrderPositionSplitKey getSplitId() {
        return splitId;
    }

    /**
     * Get the priority.
     * 
     * @return the priority.
     */
    public int getPriority() {
        return priority;
    }

    /**
     * Get the qty.
     * 
     * @return the qty.
     */
    public UnitType getQty() {
        return qty;
    }

    /**
     * Get the product.
     * 
     * @return the product.
     */
    public Product getProduct() {
        return product;
    }

    /**
     * Get the problem.
     * 
     * @return the problem.
     */
    public Problem getProblem() {
        return problem;
    }
}