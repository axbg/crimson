package com.axbg.crimson.utility.serializable;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ByteArrayWrapper implements Serializable {
    private static final long serialVersionUID = -3061571128537091145L;

    byte[] content;
}
