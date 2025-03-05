package com.groupChatV2.Leandro.Utils;

public final class Validations {

    public static boolean isMaxClientCountExpensive(int maxClients){
        int cpuCores = Runtime.getRuntime().availableProcessors();
        if(maxClients > (cpuCores * 2)){
            return true;
        }
        return false;
    }


}
