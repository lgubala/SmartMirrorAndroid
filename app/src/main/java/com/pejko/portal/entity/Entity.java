package com.pejko.portal.entity;

/**
 * Základné trieda pre všetky entity.
 */
public class Entity
{
    public static final int NO_ID = 0;

    private int id = NO_ID;

    /**
     * Vráti jednoznačný identifikátor entity.
     */
    public int getId()
    {
        return id;
    }

    /**
     * Nastaví jednoznačný identifikátor entity.
     */
    public void setId(int id)
    {
        this.id = id;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (obj == null) {
            return false;
        }

        if (obj == this) {
            return true;
        }

        if (this.isNew()) {
            return false;
        }

        if (!(obj instanceof Entity)) {
            return false;
        }

        Entity rhs = (Entity) obj;

        if (rhs.isNew()) {
            return false;
        }

        return this.id == rhs.id;
    }

    public boolean isNew()
    {
        return id == NO_ID;
    }

}
