package com.tiger.cores.generators;

import java.io.Serializable;

import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.IdentifierGenerator;

import com.tiger.common.ulid.UlidCreator;

public class UlidGenerator implements IdentifierGenerator {

    @Override
    public Serializable generate(SharedSessionContractImplementor session, Object object) {
        // Tạo ULID
        return UlidCreator.getUlid();
    }
}
