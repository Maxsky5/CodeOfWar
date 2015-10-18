package com.maxsky5.codeofwar.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.RandomAccess;

public class AwesomeList<E> extends ArrayList<E> implements List<E>, RandomAccess, Cloneable, java.io.Serializable {

    public E getLastElement() {
        return get(size() - 1);
    }
}
