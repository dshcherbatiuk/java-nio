package com.nio.refactor.enums;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Optional;
import java.util.Set;

public enum Ports {
    READ_PORT (4444),
    WRITE_PORT (5555);

    private final int adress;

    Ports (int adress){
        this.adress = adress;
        fillAdress();
    }


    private static HashMap<Integer, Ports> adresses = new HashMap<>();

    private void fillAdress(){
        for (Ports port : Ports.values()){
            adresses.put(port.adress, port);
        }
    }

    public static Set<Integer> getAdress(){
        return adresses.keySet();
    }
    public static Optional<Ports> getAdressOptional(int adress){
        for (Ports port : Ports.values()){
            if (adress == port.adress){
                return Optional.of(port);
            }
        }
        return null;
    }


}
