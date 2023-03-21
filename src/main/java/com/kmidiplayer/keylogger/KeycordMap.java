package com.kmidiplayer.keylogger;

import java.util.Map;

import java.util.HashMap;

public class KeycordMap {
    private static int returnKey = 65;
    private static Map<String, Integer> keyCodeMap = new HashMap<String, Integer>(){ {
            // Code (仮想キーコード)から
            // reference : https://kts.sakaiweb.com/virtualkeycodes.html
        put ("0",48);
        put ("1",49);
        put ("2",50);
        put ("3",51);
        put ("4",52);
        put ("5",53);
        put ("6",54);
        put ("7",55);
        put ("8",56);
        put ("9",57);

        put ("A",65);
        put ("B",66);
        put ("C",67);
        put ("D",68);
        put ("E",69);
        put ("F",70);
        put ("G",71);
        put ("H",72);
        put ("I",73);
        put ("J",74);
        put ("K",75);
        put ("L",76);
        put ("M",77);
        put ("N",78);
        put ("O",79);
        put ("P",80);
        put ("Q",81);
        put ("R",82);
        put ("S",83);
        put ("T",84);
        put ("U",85);
        put ("V",86);
        put ("W",87);
        put ("X",88);
        put ("Y",89);
        put ("Z",90);
    } };
    
    public static int GetVKcode(String key){
        // key(str) => vkcode(int)に変換
        if(keyCodeMap.get(key) == null){
            returnKey = 65;
        } else {
            returnKey = (int) keyCodeMap.get(key);
        }
        return returnKey;
    }
}