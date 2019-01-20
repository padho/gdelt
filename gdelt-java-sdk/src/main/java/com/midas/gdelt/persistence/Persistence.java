package com.midas.gdelt.persistence;

import java.util.Map;

public interface Persistence {

    public boolean persist(Map<String, Object> data) throws Exception;

}
